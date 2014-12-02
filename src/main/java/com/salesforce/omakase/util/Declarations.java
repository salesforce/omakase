/*
 * Copyright (C) 2014 salesforce.com, inc.
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

import com.google.common.collect.Iterables;
import com.salesforce.omakase.ast.Statement;
import com.salesforce.omakase.ast.StatementIterable;
import com.salesforce.omakase.ast.Stylesheet;
import com.salesforce.omakase.ast.atrule.AtRuleBlock;
import com.salesforce.omakase.ast.declaration.Declaration;

import java.util.ArrayList;
import java.util.List;

/**
 * Utilities for working with {@link Declaration}s.
 *
 * @author nmcwilliams
 */
public final class Declarations {
    private Declarations() {}

    /**
     * Finds all {@link Declaration}s within the given {@link StatementIterable} (e.g., a {@link Stylesheet} or {@link
     * AtRuleBlock}).
     * <p/>
     * By default this will recurse into any inner/child {@link StatementIterable}s as well, including their {@link Declaration}s.
     * If you only want {@link Declaration}s one level deep, call {@link #within(StatementIterable, boolean)} with false.
     * <p/>
     * This is optimized to not copy each {@link Declaration} into a new collection, but returns lazy {@link Iterable} instead.
     * <p/>
     * Examples:
     * <pre><code>
     * for (Declaration declaration : Declarations.within(atRule.block().get())) {
     *      ...
     * }
     * </code></pre>
     * <pre><code>
     * for (Declaration declaration : Declarations.within(stylesheet)) {
     *      ...
     * }
     * </code></pre>
     *
     * @param parent
     *     Find declarations within this {@link StatementIterable}.
     *
     * @return An {@link Iterable} over each {@link Declaration}.
     */
    public static Iterable<Declaration> within(StatementIterable parent) {
        return within(parent, true);
    }

    /**
     * Finds all {@link Declaration}s within the given {@link StatementIterable} (e.g., a {@link Stylesheet} or {@link
     * AtRuleBlock}).
     * <p/>
     * This is optimized to not copy each {@link Declaration} into a new collection, but returns lazy {@link Iterable} instead.
     * <p/>
     * Examples:
     * <pre><code>
     * for (Declaration declaration : Declarations.within(atRule.block().get(), true)) {
     *      ...
     * }
     * </code></pre>
     * <pre><code>
     * for (Declaration declaration : Declarations.within(stylesheet, false)) {
     *      ...
     * }
     * </code></pre>
     *
     * @param parent
     *     Find declarations within this {@link StatementIterable}.
     * @param recurse
     *     Whether to recursively look include any child/inner {@link StatementIterable}s. Pass false to only iterate over the
     *     {@link Declaration}s one level deep.
     *
     * @return An {@link Iterable} over each {@link Declaration}.
     */
    @SuppressWarnings("ConstantConditions")
    public static Iterable<Declaration> within(StatementIterable parent, boolean recurse) {
        List<Iterable<Declaration>> iterables = new ArrayList<>();

        for (Statement statement : parent.statements()) {
            if (statement.asRule().isPresent()) {
                iterables.add(statement.asRule().get().declarations());
            } else if (recurse && statement.asAtRule().isPresent() && statement.asAtRule().get().block().isPresent()) {
                iterables.add(within(statement.asAtRule().get().block().get(), recurse));
            }
        }

        return Iterables.concat(iterables);
    }
}
