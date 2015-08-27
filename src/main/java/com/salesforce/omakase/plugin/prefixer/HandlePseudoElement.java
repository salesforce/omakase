/*
 * Copyright (C) 2015 salesforce.com, inc.
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