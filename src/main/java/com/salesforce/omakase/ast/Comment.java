package com.salesforce.omakase.ast;

import com.salesforce.omakase.As;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;
import com.salesforce.omakase.writer.Writable;
import com.salesforce.omakase.writer.WriterMode;

import java.io.IOException;

/**
 * TESTME
 * <p/>
 * Represents a CSS comment.
 * <p/>
 * By default, comments are not written out except for in {@link WriterMode#VERBOSE}.
 */

public class Comment implements Writable {
    private final String content;

    /**
     * Creates a new {@link Comment} with the given content.
     *
     * @param content
     *     The content.
     */
    public Comment(String content) {
        this.content = content;
    }

    /**
     * Gets the content of the comment.
     *
     * @return The content.
     */
    public String content() {
        return content;
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        if (writer.isVerbose()) {
            appendable.append("/*").append(content).append("*/");
        }
    }

    @Override
    public String toString() {
        return As.string(this).add("content", content).toString();
    }
}
