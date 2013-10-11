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

package com.salesforce.omakase.ast.declaration.value;

import com.salesforce.omakase.ast.Syntax;

/**
 * A more specific, refined {@link FunctionValue}. This can represent either standard CSS functions (url, rgba, etc...) or custom
 * functions.
 *
 * @author nmcwilliams
 * @see FunctionValue
 */
public interface RefinedFunctionValue extends Syntax {
    /**
     * Gets whether this {@link RefinedFunctionValue} is a custom function, as opposed to be standard CSS.
     *
     * @return True if this is a custom function.
     */
    boolean isCustom();
}
