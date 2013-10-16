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

package com.salesforce.omakase.ast;

import com.salesforce.omakase.As;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;
import com.salesforce.omakase.writer.Writable;
import com.salesforce.omakase.writer.WriterMode;

import java.io.IOException;

/**
 * Represents a CSS comment.
 * <p/>
 * By default, comments are not written out except for in {@link WriterMode#VERBOSE}.
 */

public final class Comment implements Writable {
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
    public boolean isWritable() {
        return true;
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
