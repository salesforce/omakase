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
import com.google.common.collect.Multimap;
import com.salesforce.omakase.SupportMatrix;
import com.salesforce.omakase.ast.Rule;
import com.salesforce.omakase.ast.Statement;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.declaration.FunctionValue;
import com.salesforce.omakase.ast.declaration.KeywordValue;
import com.salesforce.omakase.ast.selector.PseudoElementSelector;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.data.PrefixInfo;
import com.salesforce.omakase.data.Property;
import com.salesforce.omakase.util.Equivalents;
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
        boolean applicable(Declaration instance, SupportMatrix support) {
            if (instance.isDetached() || !instance.isRefined() || instance.isPrefixed()) return false;
            Optional<Property> property = instance.propertyName().asProperty();
            return property.isPresent() && PrefixInfo.hasProperty(property.get());
        }

        @Override
        Declaration subject(Declaration instance) {
            return instance;
        }

        @Override
        Set<Prefix> required(Declaration instance, SupportMatrix support) {
            return support.prefixesForProperty(instance.propertyName().asProperty().get());
        }

        @Override
        Multimap<Prefix, Declaration> equivalents(Declaration instance) {
            return Equivalents.prefixedDeclarations(instance);
        }
    };

    /** handles function value prefixes */
    static final PrefixerHandler<FunctionValue> FUNCTION = new PrefixerHandlerStandard<FunctionValue, Declaration>() {
        @Override
        boolean applicable(FunctionValue instance, SupportMatrix support) {
            return !instance.isDetached() && instance.group().get().parent().declaration().isPresent()
                && !instance.name().startsWith("-") && PrefixInfo.hasFunction(instance.name());
        }

        @Override
        Declaration subject(FunctionValue instance) {
            return instance.group().get().parent().declaration().get();
        }

        @Override
        Set<Prefix> required(FunctionValue instance, SupportMatrix support) {
            return support.prefixesForFunction(instance.name());
        }

        @Override
        Multimap<Prefix, Declaration> equivalents(FunctionValue instance) {
            return Equivalents.prefixedFunctions(subject(instance), instance.name());
        }
    };

    /** handles at-rule prefixes */
    static final PrefixerHandler<AtRule> AT_RULE = new PrefixerHandlerStandard<AtRule, Statement>() {
        @Override
        boolean applicable(AtRule instance, SupportMatrix support) {
            return !instance.isDetached() && instance.isRefined() && !instance.name().startsWith("-")
                && PrefixInfo.hasAtRule(instance.name());
        }

        @Override
        AtRule subject(AtRule instance) {
            return instance;
        }

        @Override
        Set<Prefix> required(AtRule instance, SupportMatrix support) {
            return support.prefixesForAtRule(instance.name());
        }

        @Override
        Multimap<Prefix, AtRule> equivalents(AtRule instance) {
            return Equivalents.prefixedAtRules(instance);
        }
    };

    /** handles pseudo element selector prefixes */
    static final PrefixerHandler<PseudoElementSelector> PSEUDO = new PrefixerHandlerStandard<PseudoElementSelector, Statement>() {

        @Override
        boolean applicable(PseudoElementSelector instance, SupportMatrix support) {
            return !instance.isDetached() && !instance.parent().get().isDetached() && !instance.name().startsWith("-") &&
                PrefixInfo.hasSelector(instance.name());
        }

        @Override
        Rule subject(PseudoElementSelector instance) {
            return instance.parent().get().parent().get();
        }

        @Override
        Set<Prefix> required(PseudoElementSelector instance, SupportMatrix support) {
            return support.prefixesForSelector(instance.name());
        }

        @Override
        Multimap<Prefix, Rule> equivalents(PseudoElementSelector instance) {
            return Equivalents.prefixedPseudoElementSelectors(instance);
        }
    };

    /** handles special case where transition is not prefixed by an inner keyword must be prefixed */
    static final PrefixerHandler<Declaration> TRANSITION = new PrefixerHandler<Declaration>() {
        @Override
        public boolean handle(Declaration instance, boolean rearrange, boolean prune, SupportMatrix support) {
            Optional<Property> property = instance.propertyName().asProperty();
            if (!property.isPresent()) return false;

            if (property.get() == Property.TRANSITION || property.get() == Property.TRANSITION_PROPERTY) {
                // try to find the first prefixed property name
                for (KeywordValue keyword : Values.filter(KeywordValue.class, instance.propertyValue())) {
                    // check if the keyword is a property
                    Property keywordAsProperty = Property.lookup(keyword.keyword());
                    if (keywordAsProperty == null) continue;

                    // check if this property needs a prefix
                    Set<Prefix> prefixes = support.prefixesForProperty(keywordAsProperty);
                    if (prefixes.isEmpty()) continue;

                    // prepend a copy of the declaration for each required prefix
                    for (Prefix prefix : prefixes) {
                        instance.prepend(instance.copy(prefix, support));
                    }
                    return true;
                }
            }
            return false;
        }
    };
}
