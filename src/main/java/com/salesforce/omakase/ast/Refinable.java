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

import com.salesforce.omakase.emitter.Description;
import com.salesforce.omakase.emitter.Subscribable;

/**
 * Designates that an {@link Syntax} unit is <em>refinable</em> to a more specified or detailed representation.
 * <p/>
 * This is primarily used with high-level {@link Syntax} units. CSS is parsed into unrefined {@link Syntax} units for performance
 * reasons, where each unrefined object can be further refined on demand to obtain and work with the more detailed representation
 * as applicable.
 * <p/>
 * It's important to remember that <em>before being refined</em> refinable objects may actually contain invalid CSS. Simply
 * refining the object will verify it's grammatical compliance, which can be coupled with custom validation to ensure correct
 * usage.
 *
 * @param <T>
 *     Refine to this Type of object.
 *
 * @author nmcwilliams
 * @see Syntax
 */
@Subscribable
@Description("raw syntax that can be further refined")
public interface Refinable<T> extends Syntax {
    /**
     * Refines the object to its more specific and detailed state or representation.
     * <p/>
     * <b>Important</b>: for implementations, this operation must be <em>idempotent</em>.
     *
     * @return The refined object.
     */
    T refine();

    /**
     * Gets whether this unit is refined.
     *
     * @return True if this unit is refined.
     */
    boolean isRefined();
}
