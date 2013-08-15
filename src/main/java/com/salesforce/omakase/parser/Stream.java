/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

import static com.google.common.base.Preconditions.*;
import static com.salesforce.omakase.parser.token.Tokens.NEWLINE;

import java.util.ArrayDeque;
import java.util.Deque;

import com.google.common.base.CharMatcher;
import com.google.common.base.Optional;
import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.declaration.value.FunctionValue;
import com.salesforce.omakase.ast.declaration.value.NumericalValue;
import com.salesforce.omakase.ast.declaration.value.NumericalValue.Sign;
import com.salesforce.omakase.parser.token.Token;
import com.salesforce.omakase.parser.token.Tokens;

/**
 * A tool for reading a String source one character at a time.
 * 
 * <p>
 * This provides methods for navigating through the source, matching against expected {@link Token}s, and keeps track of
 * the current line and column positions.
 * 
 * TODO review
 * 
 * FIXME this (or somewhere) needs to normalize newline chars
 * (http://dev.w3.org/csswg/css-syntax/#preprocessing-the-input-stream)
 * 
 * @author nmcwilliams
 */
public final class Stream {
    private static final String EXPECTED = "Expected to find '%s'";
    private static final String EXPECTED_CLOSING = "Expected to find closing '%s'";
    private static final String INVALID_HEX = "Expected a hex color of length 3 or 6, but found '%s'";

    /** the source to process */
    private final String source;

    /** cached length of the source */
    private final int length;

    /** current position in the source */
    private int index = 0;

    /** current line in the source */
    private int line = 1;

    /** current column in the source */
    private int column = 1;

    /** line from the original source from which this sub-stream was derived */
    private final int anchorLine;

    /** column from the original source from which this sub-stream was derived */
    private final int anchorColumn;

    /** if we are inside of a comment */
    private boolean inComment = false;

    /** if we are inside of a string */
    private boolean inString = false;

    /** snapshots of a current index position and the associated line and column numbers */
    private final Deque<Snapshot> snapshots = new ArrayDeque<Snapshot>();

    /**
     * Creates a new instance of a {@link Stream}, to be used for reading one character at a time from the given source.
     * 
     * @param source
     *            The source to read.
     */
    public Stream(CharSequence source) {
        this(source, 1, 1);
    }

    /**
     * Creates a new instance of a {@link Stream}, to be used for reading one character at a time from the given source.
     * This will use the line and column from the given {@link RawSyntax} as the anchor/starting point.
     * 
     * @param raw
     *            The {@link RawSyntax} containing the source.
     */
    public Stream(RawSyntax raw) {
        this(raw.content(), raw.line(), raw.column());
    }

    /**
     * Creates a new instance of a {@link Stream}, to be used for reading one character at a time from the given source.
     * This will use the given starting line and column.
     * 
     * @param source
     *            The source to read.
     * @param anchorLine
     *            The starting line.
     * @param anchorColumn
     *            The starting column.
     */
    public Stream(CharSequence source, int anchorLine, int anchorColumn) {
        checkNotNull(source, "source cannot be null");
        this.source = source.toString();
        this.length = source.length();
        this.anchorLine = anchorLine;
        this.anchorColumn = anchorColumn;
    }

    /**
     * Gets the current line number.
     * 
     * @return The current line number.
     */
    public int line() {
        return line;
    }

    /**
     * Gets the current column position.
     * 
     * @return The current column position.
     */
    public int column() {
        return column;
    }

    /**
     * Gets the original line of this {@link Stream} within the original source. This is mainly useful for sub-sequences
     * (sequences created from a substring of the original source).
     * 
     * @return The line number of the start of this stream in the original source.
     */
    public int anchorLine() {
        return anchorLine;
    }

    /**
     * Gets the original column of this {@link Stream} within the original source. This is mainly useful for
     * sub-sequences (sequences created from a substring of the original source).
     * 
     * @return The column number of the start of this stream in the original source.
     */
    public int anchorColumn() {
        return anchorColumn;
    }

    /**
     * Gets a string description of the position of this {@link Stream} within the original source.
     * 
     * @return The message.
     */
    public StringBuilder anchorPositionMessage() {
        return new StringBuilder(64).append("(starting from line ")
            .append(anchorLine)
            .append(", column ")
            .append(anchorColumn)
            .append(" in original source)");
    }

    /**
     * Gets whether this a sub-sequence.
     * 
     * @return True if either the {@link #anchorLine()} or {@link #anchorColumn()} is greater than 1.
     */
    public boolean isSubStream() {
        return anchorLine != 1 || anchorColumn != 1;
    }

    /**
     * Gets the character at the current position.
     * 
     * @return The character at the current position.
     */
    public Character current() {
        return eof() ? null : source.charAt(index);
    }

    /**
     * Advance to the next character.
     * 
     * @return The next character (i.e., the character at the current position after the result of this call), or null
     *         if at the end of the stream.
     */
    public Character next() {
        // if we are at the end then return null
        if (eof()) return null;

        // update line and column info
        if (NEWLINE.matches(current())) {
            line += 1;
            column = 1;
        } else {
            column += 1;
        }

        // increment index position
        index += 1;

        // return the current character
        return eof() ? null : current();
    }

    /**
     * Advance the current position to the given index. The index must not be longer than the total length of the
     * source. If the given index is less than the current index then the index will remain unchanged.
     * 
     * @param newIndex
     *            Advance to this position.
     */
    public void forward(int newIndex) {
        checkArgument(newIndex <= length, "index out of range");
        while (newIndex > index) {
            next();
        }
    }

    /**
     * Gets the next character without advancing the current position.
     * 
     * @return The next character, or null if at the end of the stream.
     */
    public Character peek() {
        return peek(1);
    }

    /**
     * Gets the character at the given number of characters forward without advancing the current position.
     * 
     * @param numCharacters
     *            The number of characters ahead to peak.
     * @return The character, or null if the end of the stream occurs first.
     */
    public Character peek(int numCharacters) {
        return (index + numCharacters < length) ? source.charAt(index + numCharacters) : null;
    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public Character peekPrevious() {
        return (index > 0) ? source.charAt(index - 1) : null;
    }

    /**
     * If the current character is whitespace then skip it along with all subsequent whitespace characters.
     */
    public void skipWhitepace() {
        if (eof()) return;

        // skip characters until the current character is not whitespace
        while (CharMatcher.WHITESPACE.matches(current())) {
            next();
        }
    }

    /**
     * Similar to {@link #next()}, this will advance to the next character, <b>but only</b> if the current character
     * matches the given {@link Token}. If the current character does not match then the current index will remain
     * unchanged.
     * 
     * @param token
     *            The token to match.
     * @return True if there was a match, false otherwise.
     */
    public Optional<Character> optional(Token token) {
        // if the current character doesn't match then don't advance
        if (!token.matches(current())) return Optional.absent();

        // advance to the next character
        return Optional.of(next());
    }

    /**
     * TODO Description
     * 
     * @param token
     *            TODO
     * @return TODO
     */
    public boolean optionallyPresent(Token token) {
        return optional(token).isPresent();
    }

    /**
     * Similar to {@link #next()}, except it will enforce that <b>current</b> character matches the given {@link Token}
     * before advancing, otherwise an error will be thrown.
     * 
     * @param token
     *            Ensure that the current token matches this {@link Token} before we advance.
     */
    public void expect(Token token) {
        if (!token.matches(current())) {
            String msg = EXPECTED;
            throw new ParserException(this, String.format(msg, token.description()));
        }
        next();
    }

    /**
     * Advances the current character position until the current character matches the given {@link Token}. If the given
     * {@link Token} is never matched then this will advance to the end of the stream.
     * 
     * @param token
     *            The token to match.
     * @return A string containing all characters that were matched, excluding the character that matched the given
     *         {@link Token}.
     */
    public String until(Token token) {
        // if we are already at the end then there is nothing to do
        if (eof()) return "";

        String sequence; // to hold the sequence of characters to return

        // find the first character that matches the given token starting from the current position
        int found = token.matcher().indexIn(source, index);

        if (found > -1) {
            // grab the characters from the current position up to and excluding the matched token
            sequence = source.substring(index, found);

            // move the index to character that matched
            forward(found);
        } else {
            // from the current position until the end of the source
            sequence = source.substring(index);

            // move the index straight to the end
            index = length;
        }

        return sequence;
    }

    /**
     * Opposite of {@link #until(Token)}, this will advance past the current character and all subsequent characters for
     * as long as they match the given {@link Token}.
     * 
     * @param token
     *            The token to match.
     * @return A string containing all characters that were matched. If nothing matched then an empty string is
     *         returned.
     */
    public String chomp(Token token) {
        if (eof()) return "";

        int start = index;

        // advance past all characters that match the token
        while (token.matches(current())) {
            next();
        }

        return source.substring(start, index);
    }

    /**
     * TODO Description
     * 
     * @param openingToken
     *            TODO
     * @param closing
     *            TODO
     * @return TODO
     */
    public String chompEnclosedValue(Token openingToken, Token closing) {
        expect(openingToken);

        int start = index;
        int level = 1;

        while (!eof()) {
            // if we are in a string continue until we are out of it
            if (inString()) {
                next();
            }

            if (openingToken.matches(current())) {
                if (!Tokens.ESCAPE.matches(peekPrevious())) {
                    level++;
                }
            } else if (closing.matches(current())) {
                if (!Tokens.ESCAPE.matches(peekPrevious())) {
                    level--;
                    if (level == 0) {
                        // skip closing token
                        next();
                        return source.substring(start, index - 1);
                    }
                }
            }

            next();
        }
        throw new ParserException(this, String.format(EXPECTED_CLOSING, closing.description()));
    }

    /**
     * Gets the original source.
     * 
     * @return The full original source.
     */
    public String source() {
        return source;
    }

    /**
     * Gets the remaining text in the source, including the current character. This does not advance the current
     * position.
     * 
     * @return A substring of the source from the current position to the end of the source.
     */
    public String remaining() {
        return source.substring(index);
    }

    /**
     * Gets the length of the source.
     * 
     * @return The number of characters in the source.
     */
    public int length() {
        return length;
    }

    /**
     * Whether we are currently inside of a comment block.
     * 
     * @return True if we are in a comment block.
     */
    public boolean inComment() {
        return inComment;
    }

    /**
     * Whether we are currently inside of a string.
     * 
     * @return True if we are inside of a string.
     */
    public boolean inString() {
        return inString;
    }

    /**
     * Gets whether we are at the end of the source.
     * 
     * @return True of we are at the end of the source.
     */
    public boolean eof() {
        return index == length;
    }

    /**
     * TODO Description
     * 
     */
    public void snapshot() {
        Snapshot snapshot = new Snapshot();
        snapshot.index = index;
        snapshot.line = line;
        snapshot.column = column;
        snapshot.inString = inString;
        snapshot.inComment = inComment;
        snapshots.add(snapshot);
    }

    /**
     * TODO Description
     * 
     * @return always returns <b>false</b> (convenience for inlining return statements in methods)
     * 
     * @throws IllegalStateException
     *             If no snapshot exists.
     */
    public boolean rollback() {
        checkState(snapshots.size() > 0, "no snapshots currently exist");
        Snapshot snapshot = snapshots.pop();
        this.index = snapshot.index;
        this.line = snapshot.line;
        this.column = snapshot.column;
        this.inString = snapshot.inString;
        this.inComment = snapshot.inComment;
        return false;
    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public Optional<String> readIdent() {
        if (Tokens.NMSTART.matches(current())) {
            String ident = chomp(Tokens.NMCHAR);
            return Optional.of(ident);
        }
        return Optional.absent();
    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public Optional<NumericalValue> readNumber() {
        snapshot();

        // parse the optional sign
        Optional<Character> sign = optional(Tokens.SIGN);

        // integer value
        String integerValue = chomp(Tokens.DIGIT);
        Integer integer = integerValue.isEmpty() ? null : Integer.valueOf(integerValue);

        // decimal
        Integer decimal = null;
        if (optionallyPresent(Tokens.DOT)) {
            String decimalValue = chomp(Tokens.DIGIT);
            decimal = decimalValue.isEmpty() ? null : Integer.valueOf(decimalValue);
        }

        // integer value or decimal must be present
        if (integer == null && decimal == null) {
            rollback();
            return Optional.absent();
        }

        NumericalValue value = new NumericalValue(integer == null ? 0 : integer);

        if (decimal != null) {
            value.decimalValue(decimal);
        }

        if (sign.isPresent()) {
            value.explicitSign(sign.get().equals('-') ? Sign.NEGATIVE : Sign.POSITIVE);
        }

        // discard our snapshot
        snapshots.pop();

        // return the numerical value
        return Optional.of(value);
    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public Optional<String> readHexColor() {
        if (Tokens.HASH.matches(current()) && Tokens.HEX_COLOR.matches(peek())) {
            // skip the has mark
            next();

            // get the color value
            String value = chomp(Tokens.HEX_COLOR);
            if (value.length() != 6 && value.length() != 3) {
                error(String.format(INVALID_HEX, value));
            }
            return Optional.of(value);
        }
        return Optional.absent();
    }

    /**
     * TODO Description
     * 
     * @param msg
     * @throws ParserException
     */
    private void error(String msg) {
        throw new ParserException(this, msg);
    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public Optional<FunctionValue> readFunction() {
        snapshot();

        // read the function name
        Optional<String> name = readIdent();

        if (!name.isPresent()) {
            rollback();
            return Optional.absent();
        }

        // must be an open parenthesis
        if (!Tokens.OPEN_PAREN.matches(current())) {
            rollback();
            return Optional.absent();
        }

        // read the arguments. This behavior itself differs from the spec a little. We aren't validating what's inside
        // the arguments. The more specifically typed function values will be responsible for validating their own args.
        String args = chompEnclosedValue(Tokens.OPEN_PAREN, Tokens.CLOSE_PAREN);

        // discard the snapshot
        snapshots.pop();

        // return a new function value instance
        return Optional.of(new FunctionValue(name.get(), args));
    }

    @Override
    public String toString() {
        return String.format("%s»%s", source.substring(0, index), source.substring(index));
    }

    private static final class Snapshot {
        int index;
        int line;
        int column;
        boolean inString;
        boolean inComment;
    }
}
