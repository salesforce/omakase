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

    public static Optional<Declaration> prefixedEquivalent(Declaration unprefixed, Prefix prefix) {
        checkArgument(!unprefixed.isDetached(), "declaration must not be detached");

        for (Declaration d : unprefixed.group().get()) {
            if (d.propertyName().hasPrefix(prefix) && d.isPropertyIgnorePrefix(unprefixed.propertyName())) {
                return Optional.of(d);
            }
        }

        return Optional.absent();
    }

    public static Iterable<Declaration> prefixedEquivalents(Declaration unprefixed) {
        checkArgument(!unprefixed.isDetached(), "declaration must not be detached");
        checkArgument(!unprefixed.isPrefixed(), "declaration must not have a prefixed property");

        PropertyName name = unprefixed.propertyName();
        Set<Declaration> matches = Sets.newHashSet();

        for (Declaration declaration : unprefixed.group().get()) {
            if (declaration.isPrefixed() && declaration.isPropertyIgnorePrefix(name)) matches.add(declaration);
        }

        return matches;
    }

    public static Optional<Declaration> equivalentWithPrefixedFunction(Declaration unprefixed, Prefix prefix,
        String functionName) {
        checkArgument(!unprefixed.isDetached(), "declaration must not be detached");
        checkArgument(!unprefixed.isPrefixed(), "declaration must not have a prefixed property");

        String expectedName = prefix.toString() + functionName;

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

    public static void apply(Iterable<Declaration> declarations, Action<? super Declaration> action) {
        for (Declaration declaration : declarations) action.apply(declaration);
    }
}
