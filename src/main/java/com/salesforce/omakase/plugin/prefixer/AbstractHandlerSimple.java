/*
 * Copyright (C) 2015 salesforce.com, inc.
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

package com.salesforce.omakase.plugin.prefixer;

import com.salesforce.omakase.SupportMatrix;
import com.salesforce.omakase.ast.collection.Groupable;
import com.salesforce.omakase.data.Prefix;

/**
 * Base class for {@link Handler} implementations, providing some default copying behavior.
 *
 * @author nmcwilliams
 * @see AbstractHandler
 */
abstract class AbstractHandlerSimple<T, G extends Groupable<?, G>> extends AbstractHandler<T, G> {
    @Override
    @SuppressWarnings("unchecked")
    protected void copy(G original, Prefix prefix, SupportMatrix support) {
        G copy = (G)original.copy();
        prefix(copy, prefix, support);
        original.prepend(copy);
    }

    /** should handle prefixing the copied instance */
    protected abstract void prefix(G copied, Prefix prefix, SupportMatrix support);
}
