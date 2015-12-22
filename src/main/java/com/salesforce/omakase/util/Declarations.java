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
