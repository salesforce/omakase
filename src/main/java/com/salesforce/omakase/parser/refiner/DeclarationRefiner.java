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

package com.salesforce.omakase.parser.refiner;

import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.declaration.PropertyName;
import com.salesforce.omakase.ast.declaration.PropertyValue;
import com.salesforce.omakase.broadcast.Broadcastable;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.plugin.SyntaxPlugin;

/**
 * Represents a strategy for refining an {@link Declaration} object. This allows you to add custom syntax with a structure similar
 * to standard declarations. This works in tandem with {@link SyntaxPlugin}s.
 *
 * @author nmcwilliams
 */
public interface DeclarationRefiner extends Refiner {
    /**
     * Refines a {@link Declaration}.
     * <p/>
     * The information in the given {@link Declaration} can be used to determine if the declaration is applicable to your custom
     * syntax (e.g., checking {@link Declaration#rawPropertyValue()}.
     * <p/>
     * Utilize the {@link Declaration#rawPropertyValue()} to get the raw, unrefined property value syntax. Note that it is not
     * expected for you to refine the property name, although you can do that if you check the {@link
     * Declaration#rawPropertyName()} method and set the {@link Declaration#propertyName(PropertyName)} as appropriate.
     * <p/>
     * Parse the information into a new {@link PropertyValue} object, adding all applicable terms and operators. Be sure to
     * actually apply the new object using the {@link Declaration #propertyValue(PropertyValue)} method.
     *
     * @param declaration
     *     The {@link Declaration} to refine.
     * @param broadcaster
     *     Used to broadcast any {@link Broadcastable} objects.
     * @param refiner
     *     Pass this refiner to any parser methods that require one.
     *
     * @return One of the {@link Refinement} values.
     */
    Refinement refine(Declaration declaration, Broadcaster broadcaster, MasterRefiner refiner);
}
