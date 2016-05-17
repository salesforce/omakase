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

package com.salesforce.omakase.plugin.prefixer;

import com.google.common.base.Optional;
import com.google.common.collect.Multimap;
import com.salesforce.omakase.util.SupportMatrix;
import com.salesforce.omakase.ast.Named;
import com.salesforce.omakase.ast.Rule;
import com.salesforce.omakase.ast.Statement;
import com.salesforce.omakase.ast.selector.PseudoClassSelector;
import com.salesforce.omakase.ast.selector.PseudoElementSelector;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.data.Browser;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.util.Equivalents;
import com.salesforce.omakase.util.Selectors;

import java.util.Set;

/**
 * Handles the very very special placeholder pseudo element selector.
 * <code><pre>
 * ::-webkit-input-placeholder {}
 * ::-moz-placeholder { Firefox 19+ }
 *  :-ms-input-placeholder {}
 *  :-moz-placeholder { Firefox 18- }
 *  </pre></code>
 *
 * @author nmcwilliams
 */
final class HandlePlaceholder extends AbstractHandler<PseudoElementSelector, Statement> {
    // general known issues:
    // 1: this might improperly rearrange the v19 moz syntax after the newer one.
    // 2: If the newer or older moz syntax are present the other one won't be added.

    @Override
    protected boolean applicable(PseudoElementSelector instance, SupportMatrix support) {
        return instance.name().equals("placeholder");
    }

    @Override
    protected Rule subject(PseudoElementSelector instance) {
        return instance.parent().parent();
    }

    @Override
    protected Set<Prefix> required(PseudoElementSelector instance, SupportMatrix support) {
        return support.prefixesForSelector("placeholder");
    }

    @Override
    protected Multimap<Prefix, ? extends Statement> equivalents(PseudoElementSelector instance) {
        // this custom walker is based on the fact that the standard pseudo walkers will stop at the first non-match (walkAll
        // returns false). We want to to continue walking as long as any one of the following four permutations match.
        Equivalents.EquivalentWalker<Rule, Named> walker = new Equivalents.RuleBase<Named>() {
            @Override
            public Named locate(Rule peer, Named unprefixed) {
                // find prefixed equivalents to ::placeholder
                Named located = Equivalents.PSEUDO_ELEMENTS.locate(peer, new PseudoElementSelector("placeholder"));

                // find prefixed equivalents to ::input-placeholder
                if (located == null) {
                    located = Equivalents.PSEUDO_ELEMENTS.locate(peer, new PseudoElementSelector("input-placeholder"));
                }

                // find prefixed equivalents to :placeholder
                if (located == null) {
                    located = Equivalents.PSEUDO_CLASSES.locate(peer, new PseudoClassSelector("placeholder"));
                }

                // find prefixed equivalents to :input-placeholder
                if (located == null) {
                    located = Equivalents.PSEUDO_CLASSES.locate(peer, new PseudoClassSelector("input-placeholder"));
                }

                return located;
            }
        };

        return Equivalents.prefixes(subject(instance), instance, walker);
    }

    @Override
    protected void copy(Statement original, Prefix prefix, SupportMatrix support) {
        // make the copy
        Rule copy = (Rule)original.copy();

        // special cases-- we need to switch the names/and or to a pseudo class for certain vendors
        for (Selector selector : copy.selectors()) {
            Optional<PseudoElementSelector> placeholder = Selectors.findPseudoElementSelector(selector, "placeholder", false);
            if (placeholder.isPresent()) {
                if (prefix == Prefix.WEBKIT) {
                    placeholder.get().name("-webkit-input-placeholder");
                } else if (prefix == Prefix.MS) {
                    placeholder.get().replaceWith(new PseudoClassSelector("-ms-input-placeholder"));
                } else if (prefix == Prefix.MOZ) {
                    placeholder.get().name("-moz-placeholder");
                }
            }
        }

        original.prepend(copy);

        // firefox special case where version 19 and below uses a pseudo class
        if (prefix == Prefix.MOZ && support.lowestSupportedVersion(Browser.FIREFOX) <= 19) {
            Rule oldMozCopy = copy.copy();
            for (Selector selector : oldMozCopy.selectors()) {
                Optional<PseudoElementSelector> placeholder = Selectors.findPseudoElementSelector(selector, "placeholder", false);
                if (placeholder.isPresent()) {
                    placeholder.get().replaceWith(new PseudoClassSelector("-moz-placeholder"));
                }
            }
            copy.prepend(oldMozCopy);
        }
    }
}
