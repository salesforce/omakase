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

package com.salesforce.omakase.plugin.basic;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.salesforce.omakase.SupportMatrix;
import com.salesforce.omakase.ast.Statement;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.declaration.FunctionValue;
import com.salesforce.omakase.ast.declaration.KeywordValue;
import com.salesforce.omakase.ast.declaration.TermList;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.data.Property;
import com.salesforce.omakase.util.Actions;
import com.salesforce.omakase.util.Equivalents;
import com.salesforce.omakase.util.Values;

import java.util.Collection;
import java.util.Set;

/**
 * TODO move to Prefixer class
 * Collection of {@link PrefixerStep}s.
 *
 * @author nmcwilliams
 * @see Prefixer
 */
final class PrefixerSteps {
    /** standard behavior for prefixing properties in the declaration value */
    static final class HandleStandardProperty implements PrefixerStep {
        @Override
        public void process(PrefixerCtx ctx) {
            final Declaration declaration = ctx.declaration;
            final SupportMatrix support = ctx.support;
            final Property property = ctx.property;

            // gather all required prefixes for the property name
            Set<Prefix> required = support.prefixesForProperty(property);

            // find all prefixed declarations in the rule for the same property
            Multimap<Prefix, Declaration> equivalents = Equivalents.prefixedDeclarations(declaration);

            for (Prefix prefix : required) {
                Collection<Declaration> matches = equivalents.get(prefix);
                if (!matches.isEmpty()) {
                    if (ctx.rearrange) Actions.<Declaration>moveBefore().apply(declaration, matches);
                    equivalents.removeAll(prefix);
                } else {
                    declaration.prepend(declaration.copy(prefix, support));
                }
            }

            // any left over equivalents are unnecessary. remove or rearrange them if allowed
            if (!equivalents.isEmpty()) {
                if (ctx.prune) {
                    Actions.detach().apply(equivalents.values());
                } else if (ctx.rearrange) {
                    Actions.<Declaration>moveBefore().apply(declaration, equivalents.values());
                }
            }

            if (!required.isEmpty()) ctx.handled = true;
        }
    }

    /** standard behavior for prefixing functions in the declaration value */
    static final class HandleStandardFunction implements PrefixerStep {
        @Override
        public void process(PrefixerCtx ctx) {
            final FunctionValue function = ctx.function;
            final SupportMatrix support = ctx.support;
            final Declaration declaration = ctx.declaration;

            // gather all required prefixes for the function name
            Set<Prefix> required = support.prefixesForFunction(function.name());

            // find all prefixed declarations in the rule for the same property
            Multimap<Prefix, Declaration> equivalents = Equivalents.prefixedFunctions(declaration, function.name());

            for (Prefix prefix : required) {
                Collection<Declaration> matches = equivalents.get(prefix);
                if (!matches.isEmpty()) {
                    if (ctx.rearrange) Actions.<Declaration>moveBefore().apply(declaration, matches);
                    equivalents.removeAll(prefix);
                } else {
                    declaration.prepend(declaration.copy(prefix, support));
                }
            }

            // any left over equivalents are unnecessary. remove or rearrange them if allowed
            if (!equivalents.isEmpty()) {
                if (ctx.prune) {
                    Actions.detach().apply(equivalents.values());
                } else if (ctx.rearrange) {
                    Actions.<Declaration>moveBefore().apply(declaration, equivalents.values());
                }
            }

            if (!required.isEmpty()) ctx.handled = true;
        }
    }

    static final class HandleStandardAtRule implements PrefixerStep {
        @Override
        public void process(PrefixerCtx ctx) {
            final SupportMatrix support = ctx.support;
            final AtRule atRule = ctx.atRule;

            // gather all required prefixes for the at-rule
            Set<Prefix> required = support.prefixesForFunction(atRule.name());

            // find all prefixed at-rules in the stylesheet for the same name
            Multimap<Prefix, AtRule> equivalents = Equivalents.prefixedAtRules(atRule);

            for (Prefix prefix : required) {
                Collection<AtRule> matches = equivalents.get(prefix);
                if (!matches.isEmpty()) {
                    if (ctx.rearrange) Actions.<Statement>moveBefore().apply(atRule, matches);
                    equivalents.removeAll(prefix);
                } else {
                    atRule.prepend(atRule.copy(prefix, support));
                }
            }

            // any left over equivalents are unnecessary. remove or rearrange them if allowed
            if (!equivalents.isEmpty()) {
                if (ctx.prune) {
                    Actions.detach().apply(equivalents.values());
                } else if (ctx.rearrange) {
                    Actions.<Statement>moveBefore().apply(atRule, equivalents.values());
                }
            }

            if (!required.isEmpty()) ctx.handled = true;
        }
    }

    /** handles when transition/transition-property is not prefixed but a property in the declaration value must be prefixed */
    static final class HandleTransitionSpecial implements PrefixerStep {
        @Override
        public void process(PrefixerCtx ctx) {
            if (ctx.handled) return;

            // only care about transition and transition-property
            if (ctx.property != Property.TRANSITION && ctx.property != Property.TRANSITION_PROPERTY) return;

            Declaration declaration = ctx.declaration;
            Optional<TermList> termList = Values.asTermList(declaration.propertyValue());
            if (!termList.isPresent()) return;

            // try to find the first prefixed property name
            for (KeywordValue keyword : Iterables.filter(termList.get().members(), KeywordValue.class)) {
                // check if the keyword is a property
                Property keywordAsProperty = Property.lookup(keyword.keyword());
                if (keywordAsProperty == null) continue;

                // check if this property needs a prefix
                Set<Prefix> prefixes = ctx.support.prefixesForProperty(keywordAsProperty);
                if (prefixes.isEmpty()) continue;

                // prepend a copy of the declaration for each required prefix
                for (Prefix prefix : prefixes) {
                    declaration.prepend(declaration.copy(prefix, ctx.support));
                }
                return;
            }
        }
    }
}
