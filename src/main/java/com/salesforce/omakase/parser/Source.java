/*
 * Copyright (C) 2013 salesforce.com, inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.salesforce.omakase.parser;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.Refinable;
import com.salesforce.omakase.parser.token.ConstantEnum;
import com.salesforce.omakase.parser.token.Token;
import com.salesforce.omakase.parser.token.TokenEnum;
import com.salesforce.omakase.parser.token.Tokens;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkPositionIndex;
import static com.salesforce.omakase.parser.token.Tokens.*;

/**
 * A tool for reading a String source one character at a time. Basically a glorified wrapper around a String.
 * <p/>
 * This provides methods for navigating through the source, matching against expected {@link Token}s, and keeps track of the
 * current line and column positions.
 *
 * @author nmcwilliams
 */
public final class Source {
    /** the "null" character, this is used to represent the absence of a char value */
    public static final char NULL_CHAR = '\u0000';

    /** the source to process */
    private final char[] chars;

    /** cached length of the source */
    private final int length;

    /** current position in the source */
    private int index = 0;

    /** current line in the source */
    private int line = 1;

    /** current column in the source */
    private int column = 1;

    /** line from the original source from which this sub-source was derived */
    private final int anchorLine;

    /** column from the original source from which this sub-source was derived */
    private final int anchorColumn;

    /** last index checked, so that #skipWhitespace can be short-circuited if the index hasn't changed */
    private int lastCheckedWhitespaceIndex = -1;

    /** last index checked, so that #collectComments can be short-circuited if the index hasn't changed */
    private int lastCheckedCommentIndex = -1;

    /** if we are inside of a comment */
    private boolean inComment = false;

    /** whether we should monitor if we are in a string or not (optional for perf) */
    private final boolean checkInString;

    /** if we are inside of a string */
    private boolean inString = false;

    /** the character that opened the last string */
    private Token stringToken = null;

    /** collection of parsed CSS comments */
    private List<String> comments;

    /**
     * Creates a new instance of a {@link Source}, to be used for reading one character at a time from the given source.
     *
     * @param source
     *     The source to read.
     */
    public Source(CharSequence source) {
        this(source, 1, 1, true);
    }

    /**
     * Creates a new instance of a {@link Source}, to be used for reading one character at a time from the content in the given
     * {@link RawSyntax}. This will use the line and column from the given {@link RawSyntax} as the anchor/starting point.
     *
     * @param raw
     *     The {@link RawSyntax} containing the source.
     */
    public Source(RawSyntax raw) {
        this(raw.content(), raw.line(), raw.column(), true);
    }

    /**
     * Creates a new instance of a {@link Source}, to be used for reading one character at a time from the content in the given
     * {@link RawSyntax}. This will use the line and column from the given {@link RawSyntax} as the anchor/starting point.
     *
     * @param raw
     *     The {@link RawSyntax} containing the source.
     * @param checkInString
     *     Whether the source should keep track of whether we are in a string or not. The main reason to specify false here is for
     *     performance reasons, to avoid extra processing that we know wouldn't be relevant.
     */
    public Source(RawSyntax raw, boolean checkInString) {
        this(raw.content(), raw.line(), raw.column(), checkInString);
    }

    /**
     * Creates a new instance of a {@link Source}, to be used for reading one character at a time from the given source. This will
     * use the given starting line and column.
     *
     * @param source
     *     The source to read.
     * @param anchorLine
     *     The starting line.
     * @param anchorColumn
     *     The starting column.
     */
    public Source(CharSequence source, int anchorLine, int anchorColumn) {
        this(source, anchorLine, anchorColumn, true);
    }

    /**
     * Creates a new instance of a {@link Source}, to be used for reading one character at a time from the given source. This will
     * use the given starting line and column.
     *
     * @param source
     *     The source to read.
     * @param anchorLine
     *     The starting line.
     * @param anchorColumn
     *     The starting column.
     * @param checkInString
     *     Whether the source should keep track of whether we are in a string or not. The main reason to specify false here is for
     *     performance reasons, to avoid extra processing that we know wouldn't be relevant.
     */
    public Source(CharSequence source, int anchorLine, int anchorColumn, boolean checkInString) {
        this.chars = source.toString().toCharArray();
        this.length = chars.length;
        this.anchorLine = anchorLine;
        this.anchorColumn = anchorColumn;
        this.checkInString = checkInString;

        // check if we are in a string
        if (checkInString) {
            updateInString();
        }
    }

    /**
     * Gets the current index position within the original source. Not to be confused with the current column position, which is
     * found with {@link #column()} instead. Note that unlike the line and column number, index is 0-based.
     *
     * @return The current index position.
     */
    public int index() {
        return index;
    }

    /**
     * Gets the current line number.
     * <p/>
     * If you want to get the line number from the original source, regardless of whether this is a sub-source or not then you may
     * want to use {@link #originalLine()} instead.
     *
     * @return The current line number.
     */
    public int line() {
        return line;
    }

    /**
     * Gets the current column position.
     * <p/>
     * If you want to get the column number from the original source, regardless of whether this is a sub-source or not then you
     * may want to use {@link #originalColumn()} instead.
     *
     * @return The current column position.
     */
    public int column() {
        return column;
    }

    /**
     * Gets the original line of this {@link Source} within the original source. This is mainly useful for sub-sequences
     * (sequences created from a substring of the original source).
     *
     * @return The line number of the start of this source in the original source.
     */
    public int anchorLine() {
        return anchorLine;
    }

    /**
     * Gets the original column of this {@link Source} within the original source. This is mainly useful for sub-sequences
     * (sequences created from a substring of the original source).
     *
     * @return The column number of the start of this source in the original source.
     */
    public int anchorColumn() {
        return anchorColumn;
    }

    /**
     * Gets the original line, taking into account both the {@link #anchorLine()} and the current {@link #line()}.
     * <p/>
     * This should be used when you want to get the real line in the original source, even if this {@link Source} is a sub-source
     * from the original. This is accurate to use even if this source is not a sub-source.
     * <p/>
     * If you want the current line within this exact {@link Source} only then use {@link #line()} instead.
     *
     * @return The original line number.
     */
    public int originalLine() {
        return anchorLine + line - 1;
    }

    /**
     * Gets the original column, taking into account both the {@link #anchorColumn()} and the current {@link #column()}.
     * <p/>
     * This should be used when you want to get the real column in the original source, even if this {@link Source} is a
     * sub-source from the original. This is accurate to use even if this source is not a sub-source.
     * <p/>
     * If you want the current column within this exact {@link Source} only then use {@link #column()} instead.
     *
     * @return The original column number.
     */
    public int originalColumn() {
        return (line == 1) ? anchorColumn + column - 1 : column;
    }

    /**
     * Gets whether this a sub-sequence. In other words, this will be true if this source was created from a sub-sequence of a
     * parent source. This is commonly true for AST objects created through {@link Refinable#refine()} methods.
     *
     * @return True if either the {@link #anchorLine()} or {@link #anchorColumn()} is greater than 1.
     */
    public boolean isSubSource() {
        return anchorLine != 1 || anchorColumn != 1;
    }

    /**
     * Gets the original source.
     *
     * @return The full original source.
     */
    public String fullSource() {
        return new String(chars);
    }

    /**
     * Gets the remaining text in the source, including the current character. This does not advance the current position.
     *
     * @return A substring of the source from the current position to the end of the source.
     */
    public String remaining() {
        return new String(chars, index, length - index);
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
     * Whether we are currently inside of a string.
     *
     * @return True if we are inside of a string.
     */
    public boolean inString() {
        return inString;
    }

    /**
     * Gets whether the current character is preceded by the escape character
     *
     * @return If the current character is escaped.
     *
     * @see Tokens#ESCAPE
     */
    public boolean isEscaped() {
        return ESCAPE.matches(peekPrevious());
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
     * Gets the character at the current position.
     *
     * @return The character at the current position, or {@link #NULL_CHAR} if at the end.
     */
    public char current() {
        return eof() ? NULL_CHAR : chars[index];
    }

    /**
     * Advance to the next character. This will automatically update the current line and column number as well.
     * <p/>
     * The spec encourages normalizing new lines to a single line feed character, however we choose not to do this preprocessing
     * as it isn't necessary for correct parsing. However by not doing this, if the source does not use LF then the line/column
     * number reported by this source (e.g., in error messages) will be incorrect. This seems acceptable as that information is
     * mostly just useful for development purposes anyway. (http://dev.w3 .org/csswg/css-syntax/#preprocessing-the-input-source)
     *
     * @return The next character (i.e., the character at the current position after the result of this call), or {@link
     *         #NULL_CHAR} if at the end of the source.
     */
    public char next() {
        // if we are at the end then return null
        if (eof()) return NULL_CHAR;

        // update line and column info
        if (NEWLINE.matches(current())) {
            line += 1;
            column = 1;
        } else {
            column += 1;
        }

        // increment index position
        index += 1;

        // check if we are in a string
        if (checkInString && !inComment) {
            updateInString();
        }

        // return the current character
        return current();
    }

    /**
     * Advance the current position to the given index. The index must not be longer than the total length of the source. If the
     * given index is less than the current index then the index will remain unchanged.
     *
     * @param newIndex
     *     Advance to this position.
     */
    public void forward(int newIndex) {
        checkPositionIndex(newIndex, length);
        while (newIndex > index) {
            next();
        }
    }

    /**
     * Gets the next character without advancing the current position.
     *
     * @return The next character, or null if at the end of the source.
     */
    public char peek() {
        return peek(1);
    }

    /**
     * Gets the character at the given number of characters forward without advancing the current position.
     *
     * @param numCharacters
     *     The number of characters ahead to peak.
     *
     * @return The character, or null if the end of the source occurs first.
     */
    public char peek(int numCharacters) {
        return ((index + numCharacters) < length) ? chars[index + numCharacters] : NULL_CHAR;
    }

    /**
     * Gets the previous character.
     *
     * @return The previous character, or null if we are at the beginning.
     */
    public char peekPrevious() {
        return (index > 0) ? chars[index - 1] : NULL_CHAR;
    }

    /**
     * If the current character is whitespace then skip it along with all subsequent whitespace characters.
     * <p/>
     * This doesn't match form feed \f as per the spec because... stupid to use that.
     *
     * @return this, for chaining.
     */
    public Source skipWhitepace() {
        // don't check the same index twice
        if (lastCheckedWhitespaceIndex == index) return this;

        // store the last checked index
        lastCheckedWhitespaceIndex = index;

        // nothing to skip if we are at the end
        if (eof()) return this;

        // skip characters until the current character is not whitespace
        char current = current();
        while ('\u0020' == current || '\n' == current || '\t' == current || '\r' == current) {
            current = next();
        }
        return this;
    }

    /**
     * Similar to {@link #next()}, this will advance to the next character, <b>but only</b> if the current character matches the
     * given {@link Token}. If the current character does not match then the current index will remain unchanged. If you don't
     * need the actual value, consider {@link #optionallyPresent(Token)} instead.
     *
     * @param token
     *     The token to match.
     *
     * @return The parsed character, or {@link Optional#absent()} if not matched.
     */
    public Optional<Character> optional(Token token) {
        // if the current character doesn't match then don't advance
        if (!token.matches(current())) return Optional.absent();

        Optional<Character> value = Optional.of(current());

        // advance to the next character
        next();

        return value;
    }

    /**
     * Same as {@link #optional(Token)}, except it returns the result of {@link Optional#isPresent()}. Basically use this when you
     * don't care about keeping the actual parsed value (e.g., because it's discarded, you already know what it is, etc...)
     *
     * @param token
     *     The token to match.
     *
     * @return True if there was a match, false otherwise.
     */
    public boolean optionallyPresent(Token token) {
        return optional(token).isPresent();
    }

    /**
     * Similar to {@link #optional(Token)}, except this works with {@link TokenEnum}s, checking each member of the given enum (in
     * the declared order) for a matching token.
     * <p/>
     * As with {@link #optional(Token)}, if the current character matches the index will be advanced by one.
     *
     * @param klass
     *     Enum class.
     * @param <T>
     *     Type of the enum.
     *
     * @return The matching enum instance, or {@link Optional#absent()} if none match.
     */
    public <T extends Enum<T> & TokenEnum> Optional<T> optionalFromEnum(Class<T> klass) {
        for (T member : klass.getEnumConstants()) {
            if (optionallyPresent(member.token())) return Optional.of(member);
        }
        return Optional.absent();
    }

    /**
     * Similar to {@link #optional(Token)} and {@link #optionalFromEnum(Class)}, except this works with {@link ConstantEnum}s,
     * checking each member of the given enum (in the declared order) for a matching constant.
     * <p/>
     * The main difference between this and {@link #optionalFromEnum(Class)} is that this is for enums that have more than one
     * character to match at a time. Matching a constant as opposed to a single character is less performant, thus if possible
     * enums should implement {@link TokenEnum} over {@link ConstantEnum}.
     *
     * @param klass
     *     Enum class.
     * @param <T>
     *     Type of the enum.
     *
     * @return The matching enum instance, or {@link Optional#absent()} if none match.
     */
    public <T extends Enum<T> & ConstantEnum> Optional<T> optionalFromConstantEnum(Class<T> klass) {
        for (T member : klass.getEnumConstants()) {
            if (readConstant(member.constant())) return Optional.of(member);
            if (!member.caseSensitive() && readConstant(member.constant().toUpperCase())) return Optional.of(member);
        }
        return Optional.absent();
    }

    /**
     * Similar to {@link #next()}, except it will enforce that the <b>current</b> character matches the given {@link Token} before
     * advancing, otherwise an error will be thrown.
     *
     * @param token
     *     Ensure that the current token matches this {@link Token} before we advance.
     *
     * @return this, for chaining.
     */
    public Source expect(Token token) {
        return expect(token, Message.EXPECTED_TO_FIND, token.description());
    }

    /**
     * Similar to {@link #next()}, except it will enforce that the <b>current</b> character matches the given {@link Token} before
     * advancing, otherwise an error will be thrown.
     *
     * @param token
     *     Ensure that the current token matches this {@link Token} before we advance.
     * @param errorMessage
     *     The error message to use if there isn't a match.
     * @param args
     *     Optional error message arguments to String#format.
     *
     * @return this, for chaining.
     */
    public Source expect(Token token, Message errorMessage, Object... args) {
        if (!token.matches(current())) throw new ParserException(this, errorMessage, args);
        next();
        return this;
    }

    /**
     * Advances the current character position until the current character matches the given {@link Token}. If the given {@link
     * Token} is never matched then this will advance to the end of the source.
     * <p/>
     * This will skip over values inside parenthesis (mainly because ';' can be a valid part of a declaration value, e.g.,
     * data-uris). This will also skip over values inside of strings, but {@link #checkInString} must be turned on.
     * <p/>
     * Important: do not pass in {@link Tokens#OPEN_PAREN} or {@link Tokens#CLOSE_PAREN}. Use {@link #chomp(Token)} instead.
     *
     * @param token
     *     The token to match.
     *
     * @return A string containing all characters that were matched, excluding the character that matched the given {@link
     *         Token}.
     */
    public String until(Token token) {
        // save the current index so we can return the matched substring
        final int start = index;

        // keep track whether we are inside parenthesis
        boolean insideParens = false;

        // continually parse until we reach the token or eof
        while (!eof()) {
            char current = chars[index];

            if (!inString) {
                // check for closing parenthesis
                if (OPEN_PAREN.matches(current) && !isEscaped()) {
                    insideParens = true;
                } else if (insideParens && CLOSE_PAREN.matches(current) && !isEscaped()) {
                    insideParens = false;
                } else if (!insideParens && token.matches(current) && !isEscaped()) {
                    // if unescaped then this is the matching token
                    return new String(chars, start, index - start);
                }
            }

            // continue to the next character
            next();

        }

        // closing token wasn't found, so return the substring from the start to the end of the source
        return new String(chars, start, length - start);
    }

    /**
     * Opposite of {@link #until(Token)}, this will advance past the current character and all subsequent characters for as long
     * as they match the given {@link Token}.
     *
     * @param token
     *     The token to match.
     *
     * @return A string containing all characters that were matched. If nothing matched then an empty string is returned.
     */
    public String chomp(Token token) {
        if (eof()) return "";

        final int start = index;

        // advance past all characters that match the token
        while (token.matches(current())) {
            next();
        }

        return new String(chars, start, index - start);
    }

    /**
     * Similar to {@link #chomp(Token)}, except this expects the value to be enclosed with an opening and closing delimiter {@link
     * Token}.
     * <p/>
     * The opening token must be present at the current position of this source or an error will be thrown. In other words, don't
     * call this until you've checked that the opening token is there, and only if you expect it to be properly closed.
     * <p/>
     * The closing token will be skipped over if it is preceded by {@link Tokens#ESCAPE} (thus no need to worry about handling
     * escaping).
     *
     * @param openingToken
     *     The opening token.
     * @param closingToken
     *     The closing token.
     *
     * @return All content in between the opening and closing tokens (excluding the tokens themselves).
     */
    public String chompEnclosedValue(Token openingToken, Token closingToken) {
        // the opening token is required
        expect(openingToken);

        // save the current position
        final int start = index;

        // set initial nesting level
        int level = 1;

        // track depth (nesting), unless the opening and closing tokens are the same
        final boolean allowNesting = !openingToken.equals(closingToken);

        // unless the closing token is a string, skip over all string content
        final boolean skipString = !closingToken.equals(DOUBLE_QUOTE) && !closingToken.equals(SINGLE_QUOTE);

        // keep parsing until we find the closing token
        while (!eof()) {
            // continue past comments (mainly so that an occurrence of the end token in the comment doesn't get recognized
            collectComments();

            // if we are in a string continue until we are out of it
            if (skipString && inString) {
                next();
            } else {
                // if nesting is allowed then another occurrence of the openingToken increases the nesting level,
                // unless preceded by the escape symbol.
                if (allowNesting && openingToken.matches(current()) && !isEscaped()) {
                    level++;
                } else if (closingToken.matches(current()) && !isEscaped()) {
                    // decrement the nesting level
                    level--;

                    // once the nesting level reaches 0 then we have found the correct closing token
                    if (level == 0) {
                        next(); // move past the closing token
                        return new String(chars, start, index - start - 1); // - 1 so that we don't include the closing token
                    }
                }

                // we haven't found the correct closing token, so continue
                next();
            }
        }

        throw new ParserException(this, Message.EXPECTED_CLOSING, closingToken.description());
    }

    /**
     * Same as {@link #collectComments(boolean)}, with a default skipWhitespace value of true.
     *
     * @return this, for chaining.
     */
    public Source collectComments() {
        return collectComments(true);
    }

    /**
     * Parses all comments at the current position in the source.
     * <p/>
     * Comments can be retrieved wth {@link #flushComments()}. That method will return and remove all comments currently in the
     * buffer.
     * <p/>
     * This separation into the two methods allows for comments to be collected prematurely without needing to backtrack if the
     * parser later determines it doesn't match. The next parser can still retrieve the comments from the buffer even if another
     * parser triggered the collection of them.
     *
     * @param skipWhitespace
     *     If we should skip past whitespace before, between and after comments.
     *
     * @return this, for chaining.
     */
    public Source collectComments(boolean skipWhitespace) {
        // if we already checked at this index then don't waste time checking again
        if (lastCheckedCommentIndex == index) return this;

        // store the last checked index
        lastCheckedCommentIndex = index;

        while (!eof()) {
            // skip whitespace
            if (skipWhitespace) {
                skipWhitepace();
            }

            // try to read a comment
            String comment = readComment();

            // add the comment to the buffer if a comment was found
            if (comment != null) {
                // delayed (re)creation of the comment buffer
                if (comments == null) {
                    comments = new ArrayList<>(2);
                }
                comments.add(comment);
            } else {
                return this;
            }
        }
        return this;
    }

    /**
     * Reads a single comment.
     *
     * @return The comment, or null.
     */
    private String readComment() {
        String comment = null;

        // check for the opening comment
        if (FORWARD_SLASH.matches(current()) && STAR.matches(peek())) {
            inComment = true;

            // save the current position so we can grab the comment contents later
            final int start = index;

            // skip the opening "/*" part
            index += 2;

            // continue until we reach the end of the comment
            while (inComment) {
                if (FORWARD_SLASH.matches(current()) && STAR.matches(peekPrevious())) {
                    inComment = false;

                    // grab the comment contents (+2 to skip the opening /*, -1 to skip the previous *
                    comment = new String(chars, start + 2, index - (start + 2) - 1);
                } else {
                    if (eof()) throw new ParserException(this, Message.MISSING_COMMENT_CLOSE);
                    next();
                }
            }

            // skip the closing slash. Doing it here because there may be a comment immediately after.
            next();
        }

        return comment;
    }

    /**
     * Returns all CSS comments currently in the buffer.
     * <p/>
     * CSS comments are placed into the buffer when {@link #collectComments()} is called. After calling this method the buffer
     * will be emptied.
     *
     * @return The current list of CSS comments.
     */

    public List<String> flushComments() {
        // gather the comments from the queue
        List<String> flushed = (comments == null) ? ImmutableList.<String>of() : comments;

        // reset the queue
        comments = null;

        return flushed;
    }

    /**
     * Creates a snapshot of the current index, line, column, and other essential state information.
     * <p/>
     * Creating a snapshot allows you to parse content but then return to a previous state once it becomes clear that the content
     * does fully match as expected. To revert to the latest snapshot call {@link Snapshot#rollback()} on the snapshot returned
     * from this method.
     *
     * @return The created snapshot.
     */

    public Snapshot snapshot() {
        return new Snapshot(this, index, line, column, inString);
    }

    /**
     * Reads a constant string at the current position.
     * <p/>
     * If a match is found the source is advanced to the end of the constant value. Otherwise the current position will remain
     * unchanged. The constant must be matched exactly -- case does matter.
     * <p/>
     * If possible this method should be avoided as it's less performant than using a {@link Token} based method.
     *
     * @param constant
     *     The exact content to match.
     *
     * @return true if the constant was matched.
     */
    public boolean readConstant(String constant) {
        int constantLength = constant.length();

        // if the length is longer than what we have then we know it's not there
        if (constantLength > (length - index)) return false;

        // check if the next exact number of characters match the constant
        int offset = index;
        for (int i = 0; i < constantLength; i++) {
            if (constant.charAt(i) != chars[offset]) return false;
            offset++;
        }

        // we have a match so move the index forward
        forward(index + constantLength);

        return true;
    }

    /**
     * Same as {@link #readConstant(String)}, except this version is case-insensitive (and thus less performant).
     * <p/>
     * <b>Important:</b> the constant given MUST be lower-cased.
     *
     * @param constant
     *     The lower-cased version of the constant.
     *
     * @return true if the constant was matched.
     */
    public boolean readConstantCaseInsensitive(String constant) {
        return readConstant(constant) || readConstant(constant.toUpperCase());
    }

    /**
     * Reads an ident token. If a match is found the current position is advanced to the end of the token.
     * <p/>
     * future: the spec allows for non ascii and escaped characters here as well.
     *
     * @return The matched token, or {@link Optional#absent()} if not matched.
     */
    public Optional<String> readIdent() {
        final char current = current();

        if (NMSTART.matches(current)) {
            // spec says idents can't start with -- or -[0-9] (www.w3.org/TR/CSS21/syndata.html#value-def-identifier)
            if (HYPHEN.matches(current) && HYPHEN_OR_DIGIT.matches(peek())) return Optional.absent();

            // return the full ident token
            return Optional.of(chomp(NMCHAR));
        }
        return Optional.absent();
    }

    /**
     * Reads a value encased in either single or double quotes. If a match is found the current position is advanced to the end of
     * the string.
     *
     * @return The value, excluding the quotation marks.
     *
     * @throws ParserException
     *     if the string is not closed properly.
     */
    public Optional<String> readString() {
        // single quote string
        if (SINGLE_QUOTE.matches(current()) && !isEscaped()) {
            return Optional.of(chompEnclosedValue(SINGLE_QUOTE, SINGLE_QUOTE));
        }

        // double quote string
        if (DOUBLE_QUOTE.matches(current()) && !isEscaped()) {
            return Optional.of(chompEnclosedValue(DOUBLE_QUOTE, DOUBLE_QUOTE));
        }

        return Optional.absent();
    }

    @Override
    public String toString() {
        String source = new String(chars);
        return String.format("%s\u00BB%s", source.substring(0, index), source.substring(index));
    }

    /**
     * An alternative to {@link #toString()} that limits the returned string to 75 characters before and after the current
     * position in the source.
     *
     * @return The contextualized string.
     */
    public String toStringContextual() {
        if (length < 255) return toString();

        // ensure we stay within the index bounds
        int start = Math.max(0, index - 75);
        int end = Math.min(length, index + 75);

        // take a substring of the whole source
        String contextual = toString().substring(start, end);

        StringBuilder builder = new StringBuilder(256);
        if (start > 0) {
            builder.append("(...snipped...)");
        }

        builder.append(contextual);

        if (end < length) {
            builder.append("(...snipped...)");
        }

        return builder.toString();
    }

    /**
     * Updates the status about whether we are in a string.
     * <p/>
     * We are in a string once we encounter an unescaped {@link Tokens#DOUBLE_QUOTE} or {@link Tokens#SINGLE_QUOTE}. We remain in
     * this status until the matching quote symbol is encountered again, unescaped.
     */
    private void updateInString() {
        final char current = current();

        if (inString && stringToken.equals(DOUBLE_QUOTE)) { // the opening quote was a double quote
            if (DOUBLE_QUOTE.matches(current) && !isEscaped()) {
                // closing quote
                stringToken = null;
                inString = false;
            }
        } else if (inString) { // the opening quote was a single quote
            if (SINGLE_QUOTE.matches(current) && !isEscaped()) {
                // closing quote
                stringToken = null;
                inString = false;
            }
        } else if (DOUBLE_QUOTE.matches(current) && !isEscaped()) { // check for opening double quote
            // opening quote
            stringToken = DOUBLE_QUOTE;
            inString = true;
        } else if (SINGLE_QUOTE.matches(current) && !isEscaped()) { // check for opening single quote
            // closing quote
            stringToken = SINGLE_QUOTE;
            inString = true;
        }
    }

    /** data object */
    public static final class Snapshot {
        private final Source source;

        /** the captured index */
        public final int index;

        /** the line at the captured index */
        public final int line;

        /** the column at the captured index */
        public final int column;

        /** whether we are in a string at the captured index */
        public final boolean inString;

        /** the original source line. See {@link Source#originalLine()}. */
        public final int originalLine;

        /** the original source column. See {@link Source#originalColumn()}. */
        public final int originalColumn;

        private Snapshot(Source source, int index, int line, int column, boolean inString) {
            this.source = source;
            this.index = index;
            this.line = line;
            this.column = column;
            this.inString = inString;
            this.originalLine = source.originalLine();
            this.originalColumn = source.originalColumn();
        }

        /**
         * Reverts to the state (index, line, column, etc...) captured within this given snapshot.
         *
         * @return always returns <b>false</b> (convenience for inlining return statements in parse methods).
         */

        public boolean rollback() {
            source.index = index;
            source.line = line;
            source.column = column;
            source.inString = inString;
            return false;
        }

        /**
         * Similar to {@link #rollback()}, but this will also throw a {@link ParserException} with the given message and optional
         * message args.
         * <p/>
         * This is a convenience function to combine the common scenario of rolling back before throwing an error so that the
         * error message indicates a more accurate location of where the error occurred.
         *
         * @param message
         *     The error message.
         * @param args
         *     Optional args for the error message.
         *
         * @throws ParserException
         *     An exception with the given message.
         */
        public void rollback(Message message, Object... args) {
            rollback();
            throw new ParserException(source, message, args);
        }
    }
}
