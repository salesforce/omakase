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

package com.salesforce.omakase.plugin.prefixer;

import com.salesforce.omakase.SupportMatrix;

/**
 * Handles prefixing a type of AST object.
 *
 * @param <T>
 *     (Type) of AST object to prefix.
 *
 * @author nmcwilliams
 */
interface Handler<T> {
    /**
     * Handle prefixing for the AST object.
     *
     * @param instance
     *     The instance that might need prefixing.
     * @param rearrange
     *     If true, existing prefixes may be rearranged.
     * @param prune
     *     If true, existing prefixes may be removed.
     * @param support
     *     Browser support data.
     *
     * @return True if the object was "handled" and should not be passed on to another handler.
     */
    boolean handle(T instance, boolean rearrange, boolean prune, SupportMatrix support);
}
