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

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableMap;
import com.salesforce.omakase.plugin.conditionals.Conditionals;
import com.salesforce.omakase.plugin.conditionals.ConditionalsCollector;
import com.salesforce.omakase.plugin.conditionals.ConditionalsValidator;
import com.salesforce.omakase.plugin.core.StandardValidation;
import com.salesforce.omakase.plugin.core.SyntaxTree;
import com.salesforce.omakase.plugin.prefixer.PrefixCleaner;
import com.salesforce.omakase.plugin.prefixer.Prefixer;
import com.salesforce.omakase.plugin.syntax.DeclarationPlugin;
import com.salesforce.omakase.plugin.syntax.FontFacePlugin;
import com.salesforce.omakase.plugin.syntax.KeyframesPlugin;
import com.salesforce.omakase.plugin.syntax.LinearGradientPlugin;
import com.salesforce.omakase.plugin.syntax.MediaPlugin;
import com.salesforce.omakase.plugin.syntax.SelectorPlugin;
import com.salesforce.omakase.plugin.syntax.SupportsPlugin;
import com.salesforce.omakase.plugin.syntax.UrlPlugin;
import com.salesforce.omakase.plugin.validator.PseudoElementValidator;

/**
 * Helper for creating instances of library-provided {@link Plugin}s.
 *
 * @author nmcwilliams
 */
final class Suppliers {
    /** map of suppliers for all library-provided plugins. This map isn't type safe... so don't screw it up */
    private static final Map<Class<?>, Supplier<?>> map = ImmutableMap.<Class<?>, Supplier<?>>builder()
        .put(SyntaxTree.class, SyntaxTree::new)
        .put(Prefixer.class, Prefixer::defaultBrowserSupport)
        .put(PrefixCleaner.class, PrefixCleaner::new)
        .put(Conditionals.class, Conditionals::new)
        .put(ConditionalsCollector.class, ConditionalsCollector::new)
        .put(ConditionalsValidator.class, ConditionalsValidator::new)
        .put(StandardValidation.class, StandardValidation::new)
        .put(PseudoElementValidator.class, PseudoElementValidator::new)
        .put(SelectorPlugin.class, SelectorPlugin::new)
        .put(DeclarationPlugin.class, DeclarationPlugin::new)
        .put(UrlPlugin.class, UrlPlugin::new)
        .put(LinearGradientPlugin.class, LinearGradientPlugin::new)
        .put(MediaPlugin.class, MediaPlugin::new)
        .put(KeyframesPlugin.class, KeyframesPlugin::new)
        .put(FontFacePlugin.class, FontFacePlugin::new)
        .put(SupportsPlugin.class, SupportsPlugin::new)
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
     * @return The supplier for the class, otherwise an empty optional.
     */
    @SuppressWarnings("unchecked")
    public static <T> Optional<Supplier<T>> get(Class<T> klass) {
        // cast is safe as long as the internal map is correctly formed
        return Optional.ofNullable((Supplier<T>)map.get(klass));
    }
}
