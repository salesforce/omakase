/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

import static com.google.common.base.Preconditions.checkNotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.CharMatcher;
import com.salesforce.omakase.parser.token.Token;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public final class Stream {
    private static final Logger logger = LoggerFactory.getLogger(Stream.class);
    private static final CharMatcher NEWLINE = CharMatcher.is('\n');

    private final String source;

    private final StringBuilder tmp = new StringBuilder();

    /** current position in the source */
    private int index = 0;
    /** current line in the source */
    private int line = 1;
    /** current column in the source */
    private int column = 1;

    /**
     * @param source
     *            TODO
     */
    public Stream(CharSequence source) {
        checkNotNull(source, "source");
        this.source = source.toString();
    }

    public int line() {
        return line;
    }

    public int column() {
        return column;
    }

    public Character next() {
        // if we are at the end then return null
        if (eof()) return null;

        // update positioning
        if (NEWLINE.matches(current())) {
            line += 1;
            column = 1;
        } else {
            column += 1;
        }

        // increment position
        index += 1;

        return current();
    }

    public Character peek() {
        return peek(1);
    }

    public Character peek(int characters) {
        return source.charAt(index + characters);
    }

    public boolean eof() {
        return this.index == source.length() - 1;
    }

    public Character current() {
        return source.charAt(index);
    }

    /**
     * TODO Description
     * 
     */
    public void skipWhitepace() {
        if (eof()) return;

        while (CharMatcher.WHITESPACE.matches(peek())) {
            logger.debug("Skipping whitespace character");
            next();
        }

    }

    /**
     * TODO Description
     * 
     * @param token
     *            TODO
     */
    public void expect(Token token) {
        if (!token.matches(peek())) {
            String msg = String.format("Expected to find %s", token.description());
            throw new ParserException(msg, line, column, source);
        }
        next();
    }

    /**
     * TODO Description
     * 
     * @param token
     *            TODO
     * @return TODO
     */
    public boolean optional(Token token) {
        if (token.matches(peek())) {
            next();
            return true;
        }
        return false;
    }

    /**
     * TODO Description
     * 
     * @param token
     *            TODO
     * @return TODO
     */
    public String until(Token token) {
        // if we are already at the end then there is no content to return
        if (eof()) return "";

        String sequence; // to hold the sequence of characters to return

        // find the first character that matches the given token starting from the current position
        int found = token.matcher().indexIn(source, index + 1);

        if (found > -1) {
            // from the current position up to and excluding the matched token
            sequence = source.substring(index, found);

            // move the index to character before the matched token
            forward(found - 1);
        } else {
            // from the current position until the end of the source
            sequence = source.substring(index);

            // move the index straight to the end
            index = source.length();
        }

        return sequence;
    }

    /**
     * TODO Description
     * 
     * @param token
     *            TODO
     * @return TODO
     */
    public String chomp(Token token) {
        if (eof()) { return ""; }

        tmp.setLength(0);
        CharMatcher matcher = token.matcher();

        while (matcher.matches(peek())) {
            tmp.append(next());
        }

        return tmp.toString();
    }

    /**
     * TODO Description
     * 
     * @param newIndex
     *            TODO
     */
    public void forward(int newIndex) {
        while (newIndex > index) {
            next();
        }
    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public String source() {
        return source;
    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public String remaining() {
        return source.substring(index);
    }
}
