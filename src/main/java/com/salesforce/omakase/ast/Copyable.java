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

package com.salesforce.omakase.ast;

import com.salesforce.omakase.SupportMatrix;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.declaration.Term;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.util.Copy;

/**
 * Represents something that can be copied (cloned).
 * <p/>
 * Note that in many cases copying is not the preferred solution. For example, say you wanted to create a {@link Declaration} that
 * is the same as an existing one, but with a different property. If you used the same {@link Declaration#propertyValue()}
 * reference instead of a copy, any changes made to the original property value would then be reflected in the new declaration as
 * well.
 * <p/>
 * As of this writing, copying is only supported on {@link Declaration} and {@link Term} instances. If applicable, it could later
 * be added to other AST objects as well.
 *
 * @param <T>
 *     Type of object the copy creates.
 *
 * @author nmcwilliams
 * @see Copy
 */
public interface Copyable<T> {
    /**
     * Performs a deep copy of the instance.
     *
     * @return The new instance.
     */
    T copy();

    /**
     * Performs a deep copy of the instance.
     * <p/>
     * If applicable and required by the supported browser versions (as specified in the given {@link SupportMatrix}), this will
     * also prefix certain values and members as part of the copy.
     * <p/>
     * Take the following for example:
     * <pre><code>
     * PropertyName pn = PropertyName.using("border-radius");
     * PropertyName copy = PropertyName.copyWithPrefix(Prefix.WEBKIT, support);
     * </code></pre>
     * <p/>
     * Assuming that a version of Chrome was added to the {@link SupportMatrix} that requires a prefix for the {@code
     * border-radius} property, the copy will have the webkit prefix, e.g., {@code -webkit-border-radius}.
     * <p/>
     * This should also cascade to any inner or child {@link Copyable} instances. For example, if calling on a {@link Declaration}
     * instance, both the property name and also any applicable parts of the declaration value should get prefixed.
     *
     * @param prefix
     *     Apply this {@link Prefix} is applicable.
     * @param support
     *     Represents the supported browser versions.
     *
     * @return The new instance.
     */
    T copyWithPrefix(Prefix prefix, SupportMatrix support);
}
