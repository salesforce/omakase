/*
 * Copyright (c) 2015, salesforce.com, inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 *
 *    Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *    following disclaimer.
 *
 *    Redistributions in binary form must reproduce the above copyright notice, this list of conditions and
 *    the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 *    Neither the name of salesforce.com, inc. nor the names of its contributors may be used to endorse or
 *    promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.salesforce.omakase.ast.extended;

import static com.salesforce.omakase.broadcast.BroadcastRequirement.REFINED_DECLARATION;

import java.io.IOException;

import com.salesforce.omakase.ast.declaration.AbstractTerm;
import com.salesforce.omakase.broadcast.annotation.Description;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

/**
 * MS filter junk.
 * <p>
 * Example:
 * <pre>
 * {@code filter: progid:DXImageTransform.Microsoft.gradient(startColorStr='#444444', EndColorStr='#999999');}
 * </pre>
 * <p>
 * Note: this will <em>not</em> be used for filters encased in strings (hence the name "unquoted"), e.g.,
 * <pre>
 * {@code -ms-filter: "progid:DXImageTransform.Microsoft.gradient(startColorStr='#444444', EndColorStr='#999999')";}
 * </pre>
 *
 * @author nmcwilliams
 * @see UnquotedIEFilterPlugin
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

    /**
     * Gets the content. Prefer to use {@link #content()}, which is identical to this method.
     *
     * @return The content.
     */
    @Override
    public String textualValue() {
        return content();
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        appendable.append(content);
    }

    @Override
    public UnquotedIEFilter copy() {
        return new UnquotedIEFilter(-1, -1, content).copiedFrom(this);
    }
}
