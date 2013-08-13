/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.salesforce.omakase.parser.token.Tokens.NEWLINE;

import com.google.common.base.CharMatcher;
import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.parser.token.MemberToken;
import com.salesforce.omakase.parser.token.Token;
import com.salesforce.omakase.parser.token.TokenSequence;

/**
 * A tool for reading a String source one character at a time.
 * 
 * <p>
 * This provides methods for navigating through the source, matching against expected {@link Token}s, and keeps track of
 * the current line and column positions.
 * 
 * @author nmcwilliams
 */
public final class Stream {
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
        return source.charAt(index);
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
        return current();
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
     * Reads the given {@link TokenSequence} from the current position in the source. If the sequence matches (all
     * required members are found in order) then the current position will be advanced to the last matched character. If
     * the sequence doesn't match then the position will remain unchanged.
     * 
     * @param sequence
     *            The {@link TokenSequence} to read.
     * @return The substring matching the {@link TokenSequence}.
     */
    public String read(TokenSequence sequence) {
        int currentIndex = index;
        int lastIndex = currentIndex;

        // attempt to match each member of the sequence. If a required member doesn't match then the whole operation
        // fails and we do not advance the current position.
        for (MemberToken member : sequence) {
            lastIndex = peek(member.token(), currentIndex);
            if (lastIndex > -1) {
                currentIndex = lastIndex;
            } else {
                if (!member.isOptional()) return "";
            }
        }

        // find the matched substring
        String matched = source.substring(index, currentIndex);

        // advance to the last position
        forward(currentIndex);

        return matched;
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
        return length <= index + numCharacters ? source.charAt(index + numCharacters) : null;
    }

    /**
     * Gets the index of the last character matching the given token without advancing the current position.
     * 
     * @param token
     *            The {@link Token} to match.
     * @param startIndex
     *            Begin searching from this (arbitrary and unrelated to the current index) position in the source.
     * @return The index of the last matching character, or -1 if the {@link Token} didn't match anything.
     */
    public int peek(Token token, int startIndex) {
        int i = startIndex;
        char c = source.charAt(startIndex);
        while (token.matches(c)) {
            if (eof()) break;
            c = source.charAt(i++);
        }

        return (i == startIndex) ? -1 : i;
    }

    /**
     * If the current character is whitespace then skip it along with all subsequent whitespace characters.
     */
    public void skipWhitepace() {
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
    public boolean optional(Token token) {
        // if the current character doesn't match then don't advance
        if (!token.matches(current())) return false;

        // advance to the next character
        next();

        return true;
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
            String msg = "Expected to find '%s'";
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
     * @return A string containing all characters that were matched.
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
        return index == (length - 1);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(source.length() + 16);
        builder.append(source.substring(0, index) + "»" + source.substring(index));
        if (isSubStream()) {
            builder.append(" ").append(anchorPositionMessage());
        }
        return builder.toString();
    }
}
