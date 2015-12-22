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

package com.salesforce.omakase.ast;

import com.salesforce.omakase.broadcast.annotation.Description;
import com.salesforce.omakase.broadcast.annotation.Restrict;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import com.salesforce.omakase.parser.refiner.MasterRefiner;

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
 *     Refine to this type of object.
 *
 * @author nmcwilliams
 * @see Syntax
 * @see MasterRefiner
 */
@Subscribable
@Description("raw syntax that can be further refined")
public interface Refinable<T> {
    /**
     * Gets whether this unit is refined.
     *
     * @return True if this unit is refined.
     */
    boolean isRefined();

    /**
     * Refines the object to its more specific and detailed state or representation.
     * <p/>
     * <b>Important</b>: for implementations, this operation must be <em>idempotent</em>.
     *
     * @return The refined object.
     */
    T refine();

    /**
     * Returns whether this unit contains raw content (i.e., content from an underlying source). For dynamically created units (e
     * .g., through java code) this will normally be false.
     *
     * @return True if this unit contains raw content.
     *
     * @see Restrict
     */
    boolean containsRawSyntax();
}
