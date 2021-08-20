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

package com.salesforce.omakase.ast.declaration;

import static com.salesforce.omakase.broadcast.BroadcastRequirement.REFINED_DECLARATION;

import com.salesforce.omakase.broadcast.annotation.Description;
import com.salesforce.omakase.broadcast.annotation.Subscribable;

/**
 * A {@link PropertyValueMember} within a {@link PropertyValue} representing a single segment of the {@link Declaration} value.
 * <p>
 * For example, in <code>margin: 3px 5px</code>, there are two terms, <code>3px</code> and <code>5px</code>.
 *
 * @author nmcwilliams
 */
@Subscribable
@Description(value = "a single segment of a property value", broadcasted = REFINED_DECLARATION)
public interface Term extends PropertyValueMember {
    /**
     * Shortcut method to get the parent {@link Declaration} containing this term.
     *
     * @return The parent {@link Declaration}. If working with this term before it has been properly linked then this may return
     * null. This is not the case for normal subscription methods.
     */
    Declaration declaration();

    /**
     * Gets the <em>textual</em> content of this term.
     * <p>
     * This method may be useful as a generic way of getting the value of unknown or potentially varying term types.
     * <p>
     * If you have the concrete type of the {@link Term} in hand, prefer to use the more specific getter method instead of this
     * one.
     * <p>
     * <b>Important:</b> this is not a substitute or a replica of how the term will actually be written to a stylesheet. The
     * textual content returned may not include certain tokens and outer symbols such as hashes, quotes, parenthesis, etc... . To
     * get the textual content as it would be written to a stylesheet see {@link StyleWriter#writeSingle(Writable)} instead.
     * However note that you should rarely have need for doing that outside of actually creating stylesheet output.
     * <p>
     * {@link KeywordValue}s will simply return the keyword, {@link StringValue}s will return the contents of the string <b>not
     * including quotes</b>, functions will return the content of the function not including the parenthesis, {@link
     * HexColorValue} will return the hex value without the leading '#' , and so on... See each specific {@link Term}
     * implementation for more details.
     * <p>
     * For custom {@link Term} implementations-- You should return the most appropriate string value representing the inner
     * content of your term. If this is not applicable, either throw {@link UnsupportedOperationException} or return an empty
     * string. Do not return null.
     *
     * @return The textual content.
     */
    String textualValue();

    @Override
    Term copy();
}
