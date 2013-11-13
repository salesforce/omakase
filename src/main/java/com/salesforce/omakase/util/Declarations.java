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
import com.google.common.collect.Sets;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.declaration.FunctionValue;
import com.salesforce.omakase.ast.declaration.PropertyName;
import com.salesforce.omakase.ast.declaration.TermList;
import com.salesforce.omakase.ast.declaration.TermListMember;
import com.salesforce.omakase.data.Prefix;

import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * TESTME
 * <p/>
 * Utilities for working with {@link Declaration}s.
 *
 * @author nmcwilliams
 */
public final class Declarations {
    private Declarations() {}

    /**
     * Finds the first {@link Declaration} within the same rule that has the same property name as the given declaration, but also
     * has the given {@link Prefix}.
     * <p/>
     * For example, if the given declaration has a property name of {@code border-radius} and the given prefix is {@link
     * Prefix#WEBKIT}, this will find the first declaration with a property name of {@code -webkit-border-radius}. Note that
     * searching begins at the beginning of the rule.
     *
     * @param unprefixed
     *     Find the first prefixed equivalent of this {@link Declaration}.
     * @param prefix
     *     The {@link Prefix} that the equivalent must have.
     *
     * @return The first prefixed equivalent {@link Declaration} within the same rule, or {@link Optional#absent()} if none are
     *         found.
     *
     * @throws IllegalArgumentException
     *     If the given declaration is detached or is prefixed itself.
     */
    public static Optional<Declaration> prefixedEquivalent(Declaration unprefixed, Prefix prefix) {
        checkArgument(!unprefixed.isDetached(), "declaration must not be detached");
        checkArgument(!unprefixed.isPrefixed(), "declaration must not have a prefixed property");

        for (Declaration d : unprefixed.group().get()) {
            if (d.propertyName().hasPrefix(prefix) && d.isPropertyIgnorePrefix(unprefixed.propertyName())) {
                return Optional.of(d);
            }
        }

        return Optional.absent();
    }

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
     * @return All found prefixed equivalents, or an empty collection if none are found.
     *
     * @throws IllegalArgumentException
     *     If the given declaration is detached or is prefixed itself.
     */
    public static Iterable<Declaration> prefixedEquivalents(Declaration unprefixed) {
        checkArgument(!unprefixed.isDetached(), "declaration must not be detached");
        checkArgument(!unprefixed.isPrefixed(), "declaration must not have a prefixed property");

        Set<Declaration> matches = Sets.newHashSet();

        for (Declaration d : unprefixed.group().get()) {
            if (d.isPrefixed() && d.isPropertyIgnorePrefix(unprefixed.propertyName())) matches.add(d);
        }

        return matches;
    }

    /**
     * Finds the first {@link Declaration} within the same rule that contains a prefixed function with the given {@link Prefix}
     * and functionName. The property name of the declaration must be the same, and if it has a prefix it must be the prefix that
     * is given.
     * <p/>
     * For example, given the following css:
     * <pre><code>
     *  .example {
     *      width: -webkit-calc(2px - 1px);
     *      color: red;
     *      width: calc(2px - 1px);
     *  }
     * </code></pre>
     * When given the last declaration in the rule, the {@link Prefix#WEBKIT} prefix, and a functionName of "calc", this will
     * return the first declaration, which contains the {@code -webkit-calc} function. If the first declaration had a property
     * name of {@code -webkit-width} it would also match, however if the first declaration had a property name of {@code
     * -moz-width} or {@code height} it would not match.
     *
     * @param unprefixed
     *     Match against this {@link Declaration}.
     * @param prefix
     *     The prefix the function name must have, and the property name may optionally have.
     * @param functionName
     *     The name of the function, e.g., "linear-gradient".
     *
     * @return The first found matching {@link Declaration}, or {@link Optional#absent()} if none are found.
     *
     * @throws IllegalArgumentException
     *     If the given declaration is detached or is prefixed itself.
     */
    public static Optional<Declaration> prefixedFunctionEquivalent(Declaration unprefixed, Prefix prefix, String functionName) {
        checkArgument(!unprefixed.isDetached(), "declaration must not be detached");
        checkArgument(!unprefixed.isPrefixed(), "declaration must not have a prefixed property");

        String expectedName = prefix + functionName;

        for (Declaration declaration : unprefixed.group().get()) {
            // property must have the same unprefixed name, and if it is prefixed it must be the expected prefix
            PropertyName name = declaration.propertyName();
            if (name.matchesIgnorePrefix(unprefixed.propertyName()) && (!name.isPrefixed() || name.hasPrefix(prefix))) {
                Optional<TermList> termList = Values.asTermList(declaration.propertyValue());
                if (termList.isPresent()) {
                    for (TermListMember member : termList.get().members()) {
                        if (member instanceof FunctionValue) {
                            if (((FunctionValue)member).name().equals(expectedName)) return Optional.of(declaration);
                        }
                    }
                }
            }
        }
        return Optional.absent();
    }

    /**
     * Applies an {@link Action} on each of the given {@link Declaration} instances.
     * <p/>
     * Example:
     * <pre><code>
     * Declarations.apply(myDeclarations, Actions.DETACH);
     * </code></pre>
     *
     * @param declarations
     *     Apply the {@link Action} on each of these declarations.
     * @param action
     *     The {@link Action} to apply.
     */
    public static void apply(Iterable<Declaration> declarations, Action<? super Declaration> action) {
        for (Declaration declaration : declarations) action.apply(declaration);
    }
}
