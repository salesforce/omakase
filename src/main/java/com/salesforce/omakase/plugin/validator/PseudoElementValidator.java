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

package com.salesforce.omakase.plugin.validator;

import com.google.common.base.Optional;
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
import com.salesforce.omakase.plugin.basic.AutoRefiner;

/**
 * Validates that {@link PseudoElementSelector}s are last within a selector sequence (the last {@link SelectorPart} within a
 * {@link Selector}).
 * <p/>
 * "A selector is a chain of one or more sequences of simple selectors separated by combinators. <b>One pseudo-element may be
 * appended to the last sequence of simple selectors in a selector.</b>" (http://www.w3 .org/TR/css3-selectors/#selector-syntax).
 *
 * @author nmcwilliams
 */
public class PseudoElementValidator implements DependentPlugin {
    @Override
    public void dependencies(PluginRegistry registry) {
        registry.require(AutoRefiner.class).selectors();
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
                    em.report(ErrorLevel.FATAL, selector, Message.PSEUDO_ELEMENT_LAST.message(selector.toString(false)));
                }
                next = next.get().next();
            }
        }
    }
}
