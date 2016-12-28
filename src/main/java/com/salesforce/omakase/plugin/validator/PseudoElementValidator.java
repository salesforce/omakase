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

package com.salesforce.omakase.plugin.validator;

import com.salesforce.omakase.Message;
import com.salesforce.omakase.PluginRegistry;
import com.salesforce.omakase.ast.selector.PseudoElementSelector;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.ast.selector.SelectorPart;
import com.salesforce.omakase.ast.selector.SelectorPartType;
import com.salesforce.omakase.broadcast.annotation.Validate;
import com.salesforce.omakase.error.ErrorLevel;
import com.salesforce.omakase.error.ErrorManager;
import com.salesforce.omakase.plugin.DependentPlugin;
import com.salesforce.omakase.plugin.syntax.SelectorPlugin;

import java.util.Optional;

/**
 * Validates that {@link PseudoElementSelector}s are last within a selector sequence (the last {@link SelectorPart} within a
 * {@link Selector}).
 * <p>
 * "A selector is a chain of one or more sequences of simple selectors separated by combinators. <b>One pseudo-element may be
 * appended to the last sequence of simple selectors in a selector.</b>" (http://www.w3 .org/TR/css3-selectors/#selector-syntax).
 *
 * @author nmcwilliams
 */
public final class PseudoElementValidator implements DependentPlugin {
    @Override
    public void dependencies(PluginRegistry registry) {
        registry.require(SelectorPlugin.class);
    }

    /**
     * Validates that the pseudo element is always last, as according to the CSS spec.
     *
     * @param selector
     *     The pseudo element.
     * @param em
     *     The error manager.
     */
    @Validate
    public void validate(PseudoElementSelector selector, ErrorManager em) {
        // Selectors Level 3 spec does not allow anything after the pseudo.
        // Selectors Level 4 draft now allows it to be followed by a "user-action pseudo class"
        if (!selector.isLast()) {
            Optional<SelectorPart> next = selector.next();
            while (next.isPresent()) {
                if (next.get().type() != SelectorPartType.PSEUDO_CLASS_SELECTOR) {
                    em.report(ErrorLevel.FATAL, selector, Message.fmt(Message.PSEUDO_ELEMENT_LAST, selector.toString(false)));
                }
                next = next.get().next();
            }
        }
    }
}
