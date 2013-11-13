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

import com.salesforce.omakase.ast.Copyable;
import com.salesforce.omakase.ast.Syntax;

/**
 * Utilities for creating copies of objects.
 *
 * @author nmcwilliams
 * @see Copyable
 */
public final class Copy {
    private Copy() {}

    /**
     * TESTME
     * <p/>
     * Copies comments (reference) from the original to the given copy instance.
     *
     * @param original
     *     Copy comments from this instance.
     * @param copy
     *     Copy comments to this instance.
     * @param <T>
     *     Type of object being copied from.
     *
     * @return The exact same copy instance that was given.
     */
    public static <T extends Syntax> T comments(T original, T copy) {
        copy.directComments(original.comments());
        return copy;
    }
}
