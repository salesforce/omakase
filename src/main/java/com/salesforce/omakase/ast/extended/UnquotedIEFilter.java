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

package com.salesforce.omakase.ast.extended;

import com.salesforce.omakase.SupportMatrix;
import com.salesforce.omakase.ast.declaration.AbstractTerm;
import com.salesforce.omakase.broadcast.annotation.Description;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;

import static com.salesforce.omakase.broadcast.BroadcastRequirement.REFINED_DECLARATION;

/**
 * MS filter junk.
 * <p/>
 * Example:
 * <pre>
 * {@code filter: progid:DXImageTransform.Microsoft.gradient(startColorStr='#444444', EndColorStr='#999999');}
 * </pre>
 * <p/>
 * Note: this will <em>not</em> be used for filters encased in strings (hence the name "unquoted"), e.g.,
 * <pre>
 * {@code -ms-filter: "progid:DXImageTransform.Microsoft.gradient(startColorStr='#444444', EndColorStr='#999999')";}
 * </pre>
 *
 * @author nmcwilliams
 */
@Subscribable
@Description(value = "proprietary microsoft filter", broadcasted = REFINED_DECLARATION)
public final class UnquotedIEFilter extends AbstractTerm {
    private final String content;

    /**
     * Creates a new {@link UnquotedIEFilter} instance.
     *
     * @param line
     *     The line number.
     * @param column
     *     The column number.
     * @param content
     *     The filter value.
     */
    public UnquotedIEFilter(int line, int column, String content) {
        super(line, column);
        this.content = content;
    }

    /**
     * Gets the content.
     *
     * @return The content.
     */
    public String content() {
        return content;
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        appendable.append(content);
    }

    @Override
    protected UnquotedIEFilter makeCopy(Prefix prefix, SupportMatrix support) {
        // TESTME
        return new UnquotedIEFilter(-1, -1, content);
    }
}
