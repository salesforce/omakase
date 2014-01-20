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

import com.salesforce.omakase.SupportMatrix;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.parser.raw.RawAtRuleParser;
import com.salesforce.omakase.parser.raw.RawDeclarationParser;
import com.salesforce.omakase.parser.raw.RawSelectorParser;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;

/**
 * Represents raw, non-validated content. Usually used by {@link Refinable}s.
 *
 * @author nmcwilliams
 * @see RawDeclarationParser
 * @see RawSelectorParser
 * @see RawAtRuleParser
 */
public final class RawSyntax extends AbstractSyntax<RawSyntax> {
    private final String content;

    /**
     * Creates an instance with the given line and column number and content.
     *
     * @param line
     *     The line number.
     * @param column
     *     The column number.
     * @param content
     *     The raw content.
     */
    public RawSyntax(int line, int column, String content) {
        super(line, column);
        this.content = content;
        status(Status.NEVER_EMIT);
    }

    /**
     * Gets the raw content.
     *
     * @return The raw content.
     */
    public String content() {
        return content;
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        appendable.append(content);
    }

    @Override
    protected RawSyntax makeCopy(Prefix prefix, SupportMatrix support) {
        return new RawSyntax(-1, -1, content);
    }
}
