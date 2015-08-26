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

package com.salesforce.omakase;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.salesforce.omakase.plugin.Plugin;
import com.salesforce.omakase.plugin.basic.AutoRefiner;
import com.salesforce.omakase.plugin.conditionals.Conditionals;
import com.salesforce.omakase.plugin.conditionals.ConditionalsCollector;
import com.salesforce.omakase.plugin.conditionals.ConditionalsValidator;
import com.salesforce.omakase.plugin.prefixer.PrefixCleaner;
import com.salesforce.omakase.plugin.prefixer.Prefixer;
import com.salesforce.omakase.plugin.basic.SyntaxTree;
import com.salesforce.omakase.plugin.validator.PseudoElementValidator;
import com.salesforce.omakase.plugin.validator.StandardValidation;

import java.util.Map;

/**
 * Helper for creating instances of library-provided {@link Plugin}s.
 *
 * @author nmcwilliams
 */
final class Suppliers {
    /** map of suppliers for all library-provided plugins. This map isn't type safe... so don't screw it up */
    private static final Map<Class<?>, Supplier<?>> map = ImmutableMap.<Class<?>, Supplier<?>>builder()
        .put(SyntaxTree.class, new Supplier<SyntaxTree>() {
            @Override
            public SyntaxTree get() {
                return new SyntaxTree();
            }
        })
        .put(AutoRefiner.class, new Supplier<AutoRefiner>() {
            @Override
            public AutoRefiner get() {
                return new AutoRefiner();
            }
        })
        .put(Prefixer.class, new Supplier<Prefixer>() {
            @Override
            public Prefixer get() {
                return Prefixer.defaultBrowserSupport();
            }
        })
        .put(PrefixCleaner.class, new Supplier<PrefixCleaner>() {
            @Override
            public PrefixCleaner get() {
                return new PrefixCleaner();
            }
        })
        .put(Conditionals.class, new Supplier<Conditionals>() {
            @Override
            public Conditionals get() {
                return new Conditionals();
            }
        })
        .put(ConditionalsCollector.class, new Supplier<ConditionalsCollector>() {
            @Override
            public ConditionalsCollector get() {
                return new ConditionalsCollector();
            }
        })
        .put(ConditionalsValidator.class, new Supplier<ConditionalsValidator>() {
            @Override
            public ConditionalsValidator get() {
                return new ConditionalsValidator();
            }
        })
        .put(StandardValidation.class, new Supplier<StandardValidation>() {
            @Override
            public StandardValidation get() {
                return new StandardValidation();
            }
        })
        .put(PseudoElementValidator.class, new Supplier<PseudoElementValidator>() {
            @Override
            public PseudoElementValidator get() {
                return new PseudoElementValidator();
            }
        })
        .build();

    /** do not construct */
    private Suppliers() {}

    /**
     * Gets the supplier for the given class.
     *
     * @param <T>
     *     Type of class/supplier.
     * @param klass
     *     Get a supplier for this class.
     *
     * @return The supplier for the class, or {@link Optional#absent()} if not present.
     */
    @SuppressWarnings("unchecked")
    public static <T> Optional<Supplier<T>> get(Class<T> klass) {
        Supplier<?> found = map.get(klass);
        if (found != null) {
            /** cast is safe as long as the internal map is correctly formed */
            return Optional.of((Supplier<T>)found);
        }
        return Optional.absent();
    }
}
