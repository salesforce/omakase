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

package com.salesforce.omakase;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.salesforce.omakase.plugin.Plugin;
import com.salesforce.omakase.plugin.basic.AutoRefiner;
import com.salesforce.omakase.plugin.basic.SyntaxTree;
import com.salesforce.omakase.plugin.conditionals.Conditionals;
import com.salesforce.omakase.plugin.conditionals.ConditionalsCollector;
import com.salesforce.omakase.plugin.conditionals.ConditionalsValidator;
import com.salesforce.omakase.plugin.prefixer.PrefixCleaner;
import com.salesforce.omakase.plugin.prefixer.Prefixer;
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
