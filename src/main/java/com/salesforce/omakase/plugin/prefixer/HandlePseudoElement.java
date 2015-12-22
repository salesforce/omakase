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

import com.google.common.collect.Multimap;
import com.salesforce.omakase.SupportMatrix;
import com.salesforce.omakase.ast.Rule;
import com.salesforce.omakase.ast.Statement;
import com.salesforce.omakase.ast.selector.PseudoElementSelector;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.data.PrefixTablesUtil;
import com.salesforce.omakase.util.Equivalents;
import com.salesforce.omakase.util.Selectors;

import java.util.Set;

/**
 * Handles prefixing pseudo element selectors. In this case, when a prefix is needed the whole rule must be copied.
 *
 * @author nmcwilliams
 */
final class HandlePseudoElement extends AbstractHandlerSimple<PseudoElementSelector, Statement> {
    @Override
    protected boolean applicable(PseudoElementSelector instance, SupportMatrix support) {
        return !instance.name().startsWith("-") && PrefixTablesUtil.isPrefixableSelector(instance.name());
    }

    @Override
    protected Rule subject(PseudoElementSelector instance) {
        return instance.parent().parent();
    }

    @Override
    protected Set<Prefix> required(PseudoElementSelector instance, SupportMatrix support) {
        return support.prefixesForSelector(instance.name());
    }

    @Override
    protected Multimap<Prefix, Rule> equivalents(PseudoElementSelector instance) {
        return Equivalents.prefixes(subject(instance), instance, Equivalents.PSEUDO_ELEMENTS);
    }

    @Override
    protected void prefix(Statement copied, Prefix prefix, SupportMatrix support) {
        // find the pseudo selector
        Rule rule = copied.asRule().get();

        // rename pseudo element selectors that need the prefix
        for (Selector selector : rule.selectors()) {
            for (PseudoElementSelector pseudo : Selectors.filter(PseudoElementSelector.class, selector)) {
                if (support.requiresPrefixForSelector(prefix, pseudo.name())) {
                    pseudo.name(prefix + pseudo.name());
                }
            }
        }
    }
}
