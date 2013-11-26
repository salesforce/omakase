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
import com.salesforce.omakase.ast.Statement;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.declaration.FunctionValue;
import com.salesforce.omakase.data.Prefix;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * TESTME
 * <p/>
 * Utilities for finding prefixed equivalents.
 *
 * @author nmcwilliams
 */
public final class Equivalents {
    private Equivalents() {}

    /**
     * Finds all {@link Declaration}s within the same rule that have the same property name as the given declaration and also have
     * a vendor prefix.
     * <p/>
     * For example, given the following css:
     * <pre><code>
     *  .example {
     *      color: red;
     *      -webkit-border-radius: 3px;
     *      -moz-border-radius: 3px;
     *      border-radius: 3px;
     *  }
     * </code></pre>
     * <p/>
     * When given the last declaration in the rule, this will return both the {@code -webkit-border-radius} and the {@code
     * -moz-border-radius} declarations.
     *
     * @param unprefixed
     *     Find other declarations that are prefixed equivalents of this one.
     *
     * @return All found prefixed equivalents, or an empty immutable multimap if none are found.
     *
     * @throws IllegalArgumentException
     *     If the given declaration is detached or is prefixed itself.
     */
    public static Multimap<Prefix, Declaration> prefixedDeclarations(Declaration unprefixed) {
        checkArgument(!unprefixed.isDetached(), "declaration must not be detached");
        checkArgument(!unprefixed.isPrefixed(), "declaration must not have a prefixed property");

        Multimap<Prefix, Declaration> multimap = null;

        for (Declaration declaration : unprefixed.group().get()) {
            if (declaration.isPrefixed() && declaration.isPropertyIgnorePrefix(unprefixed.propertyName())) {
                if (multimap == null) multimap = LinkedListMultimap.create(); // perf -- delayed creation
                multimap.put(declaration.propertyName().prefix().get(), declaration);
            }
        }

        return multimap == null ? ImmutableMultimap.<Prefix, Declaration>of() : multimap;
    }

    /**
     * Finds all {@link Declaration}s within the same rule that contains a prefixed version of the given function name. The
     * property name of the declaration must be the same.
     * <p/>
     * For example, given the following css:
     * <pre><code>
     *  .example {
     *      width: -webkit-calc(2px - 1px);
     *      color: red;
     *      width: calc(2px - 1px);
     *  }
     * </code></pre>
     * When given the last declaration in the rule and a functionName of "calc", this will return the first declaration, which
     * contains the {@code -webkit-calc} function.
     *
     * @param unprefixed
     *     Match against this {@link Declaration}.
     * @param functionName
     *     The name of the function, e.g., "linear-gradient".
     *
     * @return All found prefixed equivalents, or an empty immutable multimap if none are found.
     *
     * @throws IllegalArgumentException
     *     If the given declaration is detached or is prefixed itself.
     */
    public static Multimap<Prefix, Declaration> prefixedFunctions(Declaration unprefixed, String functionName) {
        checkArgument(!unprefixed.isDetached(), "declaration must not be detached");
        checkArgument(!unprefixed.isPrefixed(), "declaration must not have a prefixed property");

        Multimap<Prefix, Declaration> multimap = null;

        for (Declaration declaration : unprefixed.group().get()) {
            // property must have the same name
            if (declaration.isProperty(unprefixed.propertyName())) {
                for (FunctionValue function : Values.filter(FunctionValue.class, declaration.propertyValue())) {
                    String name = function.name();
                    if (name.startsWith("-") && name.endsWith(functionName)) {
                        Optional<Prefix> prefix = Prefixes.parsePrefix(name);
                        if (prefix.isPresent()) {
                            if (multimap == null) multimap = LinkedListMultimap.create(); // perf -- delayed creation
                            multimap.put(prefix.get(), declaration);
                        }
                    }
                }
            }
        } //pyramid of dooooom

        return multimap == null ? ImmutableMultimap.<Prefix, Declaration>of() : multimap;
    }

    /**
     * Finds all {@link AtRule}s within the same sheet/group that have the same at-rule name as the given at-rule and also have a
     * vendor prefix.
     * <p/>
     * For example, given the following css:
     * <pre><code>
     * &#64;-webkit-keyframes {
     *   from { top: 0%}
     *   to { top: 100%}
     * }
     * <p/>
     * &#64;keyframes {
     *   from { top: 0%}
     *   to { top: 100%}
     * }
     * <p/>
     * &#64;-moz-keyframes {
     *   from { top: 0%}
     *   to { top: 100%}
     * }
     * </code></pre>
     * When given the middle, unprefixed at-rule, the at-rules before and after it will be returned.
     *
     * @param unprefixed
     *     Match against this {@link AtRule}.
     *
     * @return All found prefixed equivalents, or an empty immutable multimap if none are found.
     *
     * @throws IllegalArgumentException
     *     If the given at-rule is detached or is prefixed itself.
     */
    public static Multimap<Prefix, AtRule> prefixedAtRules(AtRule unprefixed) {
        checkArgument(!unprefixed.isDetached(), "at-rule must not be detached");
        checkArgument(!unprefixed.name().startsWith("-"), "at-rule must not have a prefixed property");

        Multimap<Prefix, AtRule> multimap = null;

        // look for prefixed versions appearing before the unprefixed one
        Optional<Statement> previous = unprefixed.previous();

        while (previous.isPresent() && previous.get().asAtRule().isPresent()) {
            AtRule atRule = previous.get().asAtRule().get();
            if (atRule.name().startsWith("-")) {
                Prefixes.PrefixPair pair = Prefixes.splitPrefix(atRule.name());
                if (pair.unprefixed().equals(unprefixed.name()) && pair.prefix().isPresent()) {
                    if (multimap == null) multimap = LinkedListMultimap.create(); // perf -- delayed creation
                    multimap.put(pair.prefix().get(), atRule);
                    previous = atRule.previous();
                }
            } else {
                previous = Optional.absent();
            }
        }

        // look for prefixed versions appearing after the unprefixed one
        Optional<Statement> next = unprefixed.next();

        while (next.isPresent() && next.get().asAtRule().isPresent()) {
            AtRule atRule = next.get().asAtRule().get();
            if (atRule.name().startsWith("-")) {
                Prefixes.PrefixPair pair = Prefixes.splitPrefix(atRule.name());
                if (pair.unprefixed().equals(unprefixed.name()) && pair.prefix().isPresent()) {
                    if (multimap == null) multimap = LinkedListMultimap.create(); // perf -- delayed creation
                    multimap.put(pair.prefix().get(), atRule);
                    next = atRule.next();
                    continue;
                }
            }
            next = Optional.absent();
        }

        return multimap == null ? ImmutableMultimap.<Prefix, AtRule>of() : multimap;
    }
}
