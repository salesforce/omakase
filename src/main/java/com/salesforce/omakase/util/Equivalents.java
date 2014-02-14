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

package com.salesforce.omakase.util;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.salesforce.omakase.ast.Named;
import com.salesforce.omakase.ast.Rule;
import com.salesforce.omakase.ast.Statement;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.collection.Groupable;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.declaration.FunctionValue;
import com.salesforce.omakase.ast.selector.PseudoElementSelector;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.data.Prefix;

/**
 * Utilities for finding prefixed equivalents.
 *
 * @author nmcwilliams
 */
public final class Equivalents {
    private Equivalents() {}

    public static <G, P extends Named> Multimap<Prefix, G> prefixes(
        G groupable, P prefixed, EquivalentWalker<G, P> walker) {
        Multimap<Prefix, G> multimap = null;

        G previous = walker.previous(groupable, prefixed);
        while (previous != null) {
            P located = walker.locate(previous, prefixed);
            if (located != null) {
                if (multimap == null) multimap = LinkedListMultimap.create(); // perf -- delayed creation
                multimap.put(Prefixes.parsePrefix(located.name()).get(), previous);
            }
            previous = walker.previous(previous, prefixed);
        }

        // look for prefixed versions appearing after the unprefixed one
        G next = walker.next(groupable, prefixed);
        while (next != null) {
            P located = walker.locate(next, prefixed);
            if (located != null) {
                if (multimap == null) multimap = LinkedListMultimap.create(); // perf -- delayed creation
                multimap.put(Prefixes.parsePrefix(located.name()).get(), next);
            }
            next = walker.next(next, prefixed);
        }

        return multimap == null ? ImmutableMultimap.<Prefix, G>of() : multimap;
    }

    public interface EquivalentWalker<G, P extends Named> {
        P locate(G groupable, P prefixed);

        G previous(G groupable, P prefixed);

        G next(G groupable, P prefixed);
    }

    private abstract static class Base<G extends Groupable<?, G>, S extends Named> implements EquivalentWalker<G, S> {
        @Override
        public G previous(G groupable, S prefixed) {
            return groupable.previous().orNull();
        }

        @Override
        public G next(G groupable, S prefixed) {
            return groupable.next().orNull();
        }
    }

    private abstract static class RuleBase<S extends Named> implements EquivalentWalker<Rule, S> {
        @Override
        public Rule previous(Rule groupable, S prefixed) {
            Optional<Statement> previous = groupable.next();
            if (!previous.isPresent() || !previous.get().asRule().isPresent()) return null;
            return previous.get().asRule().get();
        }

        @Override
        public Rule next(Rule groupable, S prefixed) {
            Optional<Statement> next = groupable.next();
            if (!next.isPresent() || !next.get().asRule().isPresent()) return null;
            return next.get().asRule().get();
        }
    }

    private abstract static class AtRuleBase<S extends Named> implements EquivalentWalker<AtRule, S> {
        @Override
        public AtRule previous(AtRule groupable, S prefixed) {
            Optional<Statement> previous = groupable.next();
            if (!previous.isPresent() || !previous.get().asAtRule().isPresent()) return null;
            return previous.get().asAtRule().get();
        }

        @Override
        public AtRule next(AtRule groupable, S prefixed) {
            Optional<Statement> next = groupable.next();
            if (!next.isPresent() || !next.get().asAtRule().isPresent()) return null;
            return next.get().asAtRule().get();
        }
    }

    private static boolean isPrefixed(Named named) {
        return named.name().charAt(0) == '-';
    }

    public static final EquivalentWalker<Declaration, Declaration> PROPERTIES = new Base<Declaration, Declaration>() {
        @Override
        public Declaration locate(Declaration groupable, Declaration prefixed) {
            // check if the declaration has the same property name, but prefixed
            return groupable.isPrefixed() && groupable.isPropertyIgnorePrefix(prefixed.propertyName()) ? groupable : null;
        }
    };

    public static final EquivalentWalker<Declaration, FunctionValue> FUNCTION_VALUES = new Base<Declaration, FunctionValue>() {
        @Override
        public FunctionValue locate(Declaration groupable, FunctionValue prefixed) {
            // check if the declaration has the same property name as the prefixed one
            if (groupable.isProperty(prefixed.declaration().get().propertyName())) {
                // try to find a function value with the same name but prefixed
                for (FunctionValue function : Values.filter(FunctionValue.class, groupable.propertyValue())) {
                    if (isPrefixed(function) && function.name().endsWith(prefixed.name())) return function;
                }
            }
            return null;
        }
    };

    public static final EquivalentWalker<AtRule, AtRule> AT_RULES = new AtRuleBase<AtRule>() {
        @Override
        public AtRule locate(AtRule groupable, AtRule prefixed) {
            if (isPrefixed(groupable)) {
                Prefixes.PrefixPair pair = Prefixes.splitPrefix(groupable.name());
                if (pair.unprefixed().equals(prefixed.name()) && pair.prefix().isPresent()) {
                    return groupable;
                }
            }
            return null;
        }
    };

    public static final EquivalentWalker<Rule, PseudoElementSelector> PSEUDO_ELEMENTS = new RuleBase<PseudoElementSelector>() {
        @Override
        public PseudoElementSelector locate(Rule groupable, PseudoElementSelector prefixed) {
            for (Selector selector : groupable.selectors()) {
                Optional<PseudoElementSelector> f = Selectors.findPseudoElementSelector(selector, prefixed.name(), false);
                if (f.isPresent() && isPrefixed(f.get())) {
                    return f.get();
                }
            }
            return null;
        }
    };
}
