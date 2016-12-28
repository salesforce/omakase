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
import com.salesforce.omakase.ast.selector.ClassSelector;
import com.salesforce.omakase.ast.selector.IdSelector;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.ast.selector.SelectorPart;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.annotation.Refine;
import com.salesforce.omakase.parser.Grammar;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.plugin.Plugin;

/**
 * Refines {@link Selector}s.
 * <p>
 * This plugin is necessary to parse raw, unrefined selectors into the more specific {@link SelectorPart}s such as {@link
 * ClassSelector} and {@link IdSelector}. These more specific parts will not be delivered to subscription methods until the parent
 * {@link Selector} is first refined. See the main readme file for more information on refinement.
 * <p>
 * In custom refiner plugins, you can reuse the logic from this class to parse selectors with the {@link
 * #delegateRefinement(Selector, Grammar, Broadcaster)} method. For example, to check if the raw selector content contains a
 * certain string and if so to refine it.
 *
 * @author nmcwilliams
 */
public final class SelectorPlugin implements Plugin {
    private static final SelectorPlugin DELEGATE = new SelectorPlugin();

    /**
     * Refines the given {@link Selector}.
     * <p>
     * If refinement is successful one or more {@link SelectorPart}s will be broadcasted via the
     * given {@link Broadcaster}.
     *
     * @param selector
     *     The selector.
     * @param grammar
     *     The grammar.
     * @param broadcaster
     *     The broadcaster.
     */
    @Refine
    public void refine(Selector selector, Grammar grammar, Broadcaster broadcaster) {
        // parse inner content
        Source source = new Source(selector.raw().get(), false);
        grammar.parser().complexSelectorParser().parse(source, grammar, broadcaster);

        // grab orphaned comments
        selector.orphanedComments(source.collectComments().flushComments());

        // there should be nothing left
        if (!source.eof()) throw new ParserException(source, Message.UNPARSABLE_SELECTOR);
    }

    /**
     * A convenience method to delegate refinement of a {@link Selector} to this class.
     * <p>
     * This is mainly used by {@link Refine} subscription methods.
     * <p>
     * If refinement is successful one or more {@link SelectorPart}s will be broadcasted via the
     * given {@link Broadcaster}.
     *
     * @param selector
     *     Refine this selector.
     * @param grammar
     *     The grammar.
     * @param broadcaster
     *     The broadcaster.
     */
    public static void delegateRefinement(Selector selector, Grammar grammar, Broadcaster broadcaster) {
        DELEGATE.refine(selector, grammar, broadcaster);
    }
}
