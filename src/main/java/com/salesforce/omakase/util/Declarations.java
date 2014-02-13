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
import com.google.common.collect.Lists;
import com.salesforce.omakase.ast.Statement;
import com.salesforce.omakase.ast.StatementIterable;
import com.salesforce.omakase.ast.declaration.Declaration;

import java.util.List;

/**
 * TESTME
 * <p/>
 * TODO description
 *
 * @author nmcwilliams
 */
public final class Declarations {
    private Declarations() {}

    public static Iterable<Declaration> within(StatementIterable iterable) {
        return within(iterable, true);
    }

    public static Iterable<Declaration> within(StatementIterable iterable, boolean recurse) {
        List<Iterable<Declaration>> iterables = Lists.newArrayList();

        for (Statement statement : iterable.statements()) {
            if (statement.asRule().isPresent()) {
                iterables.add(statement.asRule().get().declarations());
            } else if (recurse && statement.asAtRule().isPresent() && statement.asAtRule().get().block().isPresent()) {
                iterables.add(within(statement.asAtRule().get().block().get(), recurse));
            }
        }

        return Iterables.concat(iterables);
    }

}
