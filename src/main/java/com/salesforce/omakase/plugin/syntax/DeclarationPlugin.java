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

import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.declaration.KeywordValue;
import com.salesforce.omakase.ast.declaration.PropertyValue;
import com.salesforce.omakase.ast.declaration.PropertyValueMember;
import com.salesforce.omakase.ast.declaration.UrlFunctionValue;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.annotation.Refine;
import com.salesforce.omakase.parser.Grammar;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.plugin.Plugin;

/**
 * Refines {@link Declaration}s.
 * <p>
 * This plugin is necessary to parse raw, unrefined declarations into into the more specific parts such as {@link
 * UrlFunctionValue} and {@link KeywordValue}. These more specific parts will not be delivered to subscription methods until the
 * parent {@link Declaration} is first refined. See the main readme file for more information on refinement.
 * <p>
 * In custom refiner plugins, you can reuse the logic from this class to parse declarations with the {@link
 * #delegateRefinement(Declaration, Grammar, Broadcaster)} method. For example, to check if the raw declaration content contains a
 * certain string and if so to refine it.
 *
 * @author nmcwilliams
 */
public final class DeclarationPlugin implements Plugin {
    private static final DeclarationPlugin DELEGATE = new DeclarationPlugin();

    /**
     * Refines the given {@link Declaration}.
     * <p>
     * If refinement is successful then a single {@link PropertyValue} (plus one or more {@link PropertyValueMember}s) will be
     * broadcasted via the given {@link Broadcaster}.
     *
     * @param declaration
     *     The declaration.
     * @param grammar
     *     The grammar.
     * @param broadcaster
     *     The broadcaster.
     */
    @Refine
    public void refine(Declaration declaration, Grammar grammar, Broadcaster broadcaster) {
        // parse inner content
        Source source = new Source(declaration.rawPropertyValue().get());
        grammar.parser().propertyValueParser().parse(source, grammar, broadcaster);

        // grab orphaned comments
        declaration.orphanedComments(source.collectComments().flushComments());

        // there should be nothing left
        if (!source.eof()) throw new ParserException(source, Message.UNPARSABLE_DECLARATION_VALUE, source.remaining());
    }

    /**
     * A convenience method to delegate refinement of a {@link Declaration} to this class.
     * <p>
     * This is mainly used by {@link Refine} subscription methods.
     * <p>
     * If refinement is successful then a single {@link PropertyValue} (plus one or more {@link PropertyValueMember}s) will be
     * broadcasted via the given {@link Broadcaster}.
     *
     * @param declaration
     *     The declaration.
     * @param grammar
     *     The grammar.
     * @param broadcaster
     *     The broadcaster.
     */
    public static void delegateRefinement(Declaration declaration, Grammar grammar, Broadcaster broadcaster) {
        DELEGATE.refine(declaration, grammar, broadcaster);
    }
}
