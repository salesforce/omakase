/*
 * Copyright (c) 2017, salesforce.com, inc.
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

package com.salesforce.omakase.plugin.syntax;

import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.declaration.PropertyValue;
import com.salesforce.omakase.ast.extended.UnquotedIEFilter;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.annotation.Refine;
import com.salesforce.omakase.broadcast.annotation.Rework;
import com.salesforce.omakase.broadcast.annotation.Validate;
import com.salesforce.omakase.parser.Grammar;
import com.salesforce.omakase.plugin.Plugin;

import java.util.Optional;

/**
 * This plugin enables unquoted IE proprietary filters.
 * <p>
 * For example:
 * <pre>
 * {@code filter: progid:DXImageTransform.Microsoft.Shadow(color='#969696', Direction=145, Strength=3);}
 * </pre>
 * <p>
 * See http://msdn.microsoft.com/en-us/library/ms532847(v=vs.85).aspx for more information on filters.
 * <p>
 * Note that this is <em>not needed</em> for quoted filters, commonly used with the {@code -ms-filter} property instead of {@code
 * filter}. For example:
 * <pre>
 *  {@code -ms-filter: "progid:DXImageTransform.Microsoft.Shadow(color='#969696', Direction=145, Strength=3)";}
 * </pre>
 * <p>
 * Enabling this plugin will allow the parser to "understand" this proprietary syntax. The whole property-value will be output
 * as-is once it discovers that it starts with the special "progid:" prefix. You can subscribe to created {@link UnquotedIEFilter}
 * objects as you would other standard syntax units using {@link Rework}, {@link Validate}, etc...
 * <p>
 * Example usage:
 * <pre>
 * <code>Omakase.source(input).use(new UnquotedIEFilterPlugin()).(...).process()</code>
 * </pre>
 *
 * @author nmcwilliams
 * @see UnquotedIEFilter
 */
public final class UnquotedIEFilterPlugin implements Plugin {
    /**
     * Refines unquoted IE proprietary filters.
     * <p>
     * If refinement is successful a new {@link PropertyValue} containing the {@link UnquotedIEFilter} will be broadcasted via the
     * given {@link Broadcaster}.
     *
     * @param declaration
     *     The declaration to refine.
     * @param grammar
     *     The grammar.
     * @param broadcaster
     *     The broadcaster.
     */
    @Refine
    public void refine(Declaration declaration, Grammar grammar, Broadcaster broadcaster) {
        Optional<RawSyntax> raw = declaration.rawPropertyValue();
        if (raw.isPresent() && raw.get().content().startsWith("progid:")) {
            UnquotedIEFilter t = new UnquotedIEFilter(raw.get().line(), raw.get().column(), raw.get().content());
            broadcaster.broadcast(PropertyValue.of(t));
        }
    }
}
