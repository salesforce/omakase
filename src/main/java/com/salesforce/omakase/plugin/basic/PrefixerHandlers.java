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
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.salesforce.omakase.SupportMatrix;
import com.salesforce.omakase.ast.Rule;
import com.salesforce.omakase.ast.Statement;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.declaration.FunctionValue;
import com.salesforce.omakase.ast.declaration.KeywordValue;
import com.salesforce.omakase.ast.selector.PseudoClassSelector;
import com.salesforce.omakase.ast.selector.PseudoElementSelector;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.data.Browser;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.data.PrefixTablesUtil;
import com.salesforce.omakase.data.Property;
import com.salesforce.omakase.util.Equivalents;
import com.salesforce.omakase.util.Selectors;
import com.salesforce.omakase.util.Values;

import java.util.Set;

/**
 * Standard implementations for {@link PrefixerHandler}.
 * <p/>
 * Some of the applicable methods check for isRefined because we don't want to trigger refinement on everything just to see if a
 * prefix is needed (in production / pinpointed refinement, we may want to just prefix dynamically inserted or replaced units).
 * This is fine as usually the {@link Prefixer} plugin is only enabled when full validation or auto refinement is turned on as
 * well.
 *
 * @author nmcwilliams
 * @see Prefixer
 * @see PrefixerHandlerStandard
 */
final class PrefixerHandlers {
    private PrefixerHandlers() {}

    /** handles property name prefixes */
    static final PrefixerHandler<Declaration> PROPERTY = new PrefixerHandlerStandard<Declaration, Declaration>() {
        @Override
        protected boolean applicable(Declaration instance, SupportMatrix support) {
            // don't automatically trigger refinement on every declaration just to check if a prefix is needed.
            if (!instance.isRefined() || instance.isPrefixed()) return false;
            Optional<Property> property = instance.propertyName().asProperty();
            return property.isPresent() && PrefixTablesUtil.isPrefixableProperty(property.get());
        }

        @Override
        protected Declaration subject(Declaration instance) {
            return instance;
        }

        @Override
        protected Set<Prefix> required(Declaration instance, SupportMatrix support) {
            return support.prefixesForProperty(instance.propertyName().asProperty().get());
        }

        @Override
        protected Multimap<Prefix, Declaration> equivalents(Declaration instance) {
            return Equivalents.prefixes(subject(instance), instance, Equivalents.PROPERTIES);
        }
    };

    /** handles function value prefixes */
    static final PrefixerHandler<FunctionValue> FUNCTION = new PrefixerHandlerStandard<FunctionValue, Declaration>() {
        @Override
        protected boolean applicable(FunctionValue instance, SupportMatrix support) {
            return instance.parent().get().declaration().isPresent()
                && instance.name().charAt(0) != '-' && PrefixTablesUtil.isPrefixableFunction(instance.name());
        }

        @Override
        protected Declaration subject(FunctionValue instance) {
            return instance.group().get().parent().declaration().get();
        }

        @Override
        protected Set<Prefix> required(FunctionValue instance, SupportMatrix support) {
            return support.prefixesForFunction(instance.name());
        }

        @Override
        protected Multimap<Prefix, Declaration> equivalents(FunctionValue instance) {
            return Equivalents.prefixes(subject(instance), instance, Equivalents.FUNCTION_VALUES);
        }
    };

    /** handles at-rule prefixes */
    static final PrefixerHandler<AtRule> AT_RULE = new PrefixerHandlerStandard<AtRule, Statement>() {
        @Override
        protected boolean applicable(AtRule instance, SupportMatrix support) {
            // don't automatically trigger refinement on every at rule just to check if a prefix is needed.
            return instance.isRefined() && instance.name().charAt(0) != '-' && PrefixTablesUtil.isPrefixableAtRule(instance.name());
        }

        @Override
        protected AtRule subject(AtRule instance) {
            return instance;
        }

        @Override
        protected Set<Prefix> required(AtRule instance, SupportMatrix support) {
            return support.prefixesForAtRule(instance.name());
        }

        @Override
        protected Multimap<Prefix, AtRule> equivalents(AtRule instance) {
            return Equivalents.prefixes(subject(instance), instance, Equivalents.AT_RULES);
        }
    };

    /** handles pseudo element selector prefixes */
    static final PrefixerHandler<PseudoElementSelector> PSEUDO = new PrefixerHandlerStandard<PseudoElementSelector, Statement>() {
        @Override
        protected boolean applicable(PseudoElementSelector instance, SupportMatrix support) {
            return !instance.name().startsWith("-") && PrefixTablesUtil.isPrefixableSelector(instance.name());
        }

        @Override
        protected Rule subject(PseudoElementSelector instance) {
            return instance.parent().get().parent().get();
        }

        @Override
        protected Set<Prefix> required(PseudoElementSelector instance, SupportMatrix support) {
            return support.prefixesForSelector(instance.name());
        }

        @Override
        protected Multimap<Prefix, Rule> equivalents(PseudoElementSelector instance) {
            return Equivalents.prefixes(subject(instance), instance, Equivalents.PSEUDO_ELEMENTS);
        }
    };

    /** handles special case where transition is not prefixed but an inner keyword must be prefixed */
    static final PrefixerHandler<Declaration> TRANSITION_VALUE = new PrefixerHandlerStandard<Declaration, Declaration>() {
        // known issues:
        // 1: this won't locate equivalents where the property name is unprefixed, but the value is.
        // 2: with something like "transition: transform 1s", it's possible that two browsers with the same prefix need the
        // transform prefixed, but only one browser needs the transition prefixed. Currently we will only add one copy,
        // with transition and transform prefixed. Technically though it should be two copies,
        // one with transition prefixed and one without.

        @Override
        protected boolean applicable(Declaration instance, SupportMatrix support) {
            // don't automatically trigger refinement on every declaration just to check if a prefix is needed.
            if (!instance.isRefined() || instance.isPrefixed()) return false;

            // must be the transition property
            Optional<Property> property = instance.propertyName().asProperty();
            return property.isPresent() &&
                (property.get() == Property.TRANSITION || property.get() == Property.TRANSITION_PROPERTY);
        }

        @Override
        protected Declaration subject(Declaration instance) {
            return instance;
        }

        @Override
        protected Set<Prefix> required(Declaration instance, SupportMatrix support) {
            // try to find the first prefixed property name
            for (KeywordValue keyword : Values.filter(KeywordValue.class, instance.propertyValue())) {
                // check if the keyword is a property
                Property keywordAsProperty = Property.lookup(keyword.keyword());
                if (keywordAsProperty == null) continue;

                // check if this property needs a prefix
                Set<Prefix> prefixes = support.prefixesForProperty(keywordAsProperty);
                if (!prefixes.isEmpty()) {
                    return prefixes;
                }
            }
            return ImmutableSet.of();
        }

        @Override
        protected Multimap<Prefix, ? extends Declaration> equivalents(Declaration instance) {
            return Equivalents.prefixes(subject(instance), instance, Equivalents.PROPERTIES);
        }
    };

    /** handles the very special needs case of placeholder */
    static final PrefixerHandler<PseudoElementSelector> PLACEHOLDER = new PrefixerHandlerStandard<PseudoElementSelector, Statement>() {
        // general known issues:
        // 1: this might improperly rearrange the v19 moz syntax after the newer one.
        // 2: If the newer or older moz syntax are present the other one won't be added.

        @Override
        protected boolean applicable(PseudoElementSelector instance, SupportMatrix support) {
            return instance.name().equals("placeholder");
        }

        @Override
        protected Rule subject(PseudoElementSelector instance) {
            return instance.parent().get().parent().get();
        }

        @Override
        protected Set<Prefix> required(PseudoElementSelector instance, SupportMatrix support) {
            return support.prefixesForSelector("placeholder");
        }

        @Override
        protected Multimap<Prefix, ? extends Statement> equivalents(PseudoElementSelector instance) {
            Multimap<Prefix, Rule> map = LinkedListMultimap.create();

            // find prefixed equivalents to ::placeholder
            map.putAll(Equivalents.prefixes(subject(instance), instance, Equivalents.PSEUDO_ELEMENTS));

            // find prefixed equivalents to ::input-placeholder
            map.putAll(Equivalents.prefixes(subject(instance), new PseudoElementSelector("input-placeholder"),
                Equivalents.PSEUDO_ELEMENTS));

            // find prefixed equivalents to :placeholder
            map.putAll(Equivalents.prefixes(subject(instance), new PseudoClassSelector("placeholder"),
                Equivalents.PSEUDO_CLASSES));

            // find prefixed equivalents to :input-placeholder
            map.putAll(Equivalents.prefixes(subject(instance), new PseudoClassSelector("input-placeholder"),
                Equivalents.PSEUDO_CLASSES));

            // ...yuk

            return map;
        }

        @Override
        protected void copy(Statement original, Prefix prefix, SupportMatrix support) {
            // make the copy as normal
            super.copy(original, prefix, support);

            // find the copy
            Rule copy = original.previous().get().asRule().get();

            // special cases-- we need to switch the names/and or to a pseudo class for certain vendors
            for (Selector selector : copy.selectors()) {
                Optional<PseudoElementSelector> placeholder = Selectors.findPseudoElementSelector(selector, "placeholder", false);
                if (placeholder.isPresent()) {
                    if (prefix == Prefix.WEBKIT) {
                        placeholder.get().name("-webkit-input-placeholder");
                    } else if (prefix == Prefix.MS) {
                        placeholder.get().replaceWith(new PseudoClassSelector("-ms-input-placeholder"));
                    }
                }
            }

            // firefox special case where version 19 and below uses a pseudo class
            if (prefix == Prefix.MOZ && support.lowestSupportedVersion(Browser.FIREFOX) <= 19) {
                Rule oldMozCopy = (Rule)copy.copy();
                for (Selector selector : oldMozCopy.selectors()) {
                    Optional<PseudoElementSelector> placeholder = Selectors.findPseudoElementSelector(selector, "placeholder", false);
                    if (placeholder.isPresent()) {
                        placeholder.get().replaceWith(new PseudoClassSelector("-moz-placeholder"));
                    }
                }
                copy.prepend(oldMozCopy);
            }
        }
    };
}
