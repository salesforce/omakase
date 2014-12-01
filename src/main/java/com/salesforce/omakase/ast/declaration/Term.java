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

package com.salesforce.omakase.ast.declaration;

import com.google.common.base.Optional;
import com.salesforce.omakase.broadcast.annotation.Description;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import com.salesforce.omakase.writer.StyleWriter;
import com.salesforce.omakase.writer.Writable;

import static com.salesforce.omakase.broadcast.BroadcastRequirement.REFINED_DECLARATION;

/**
 * A {@link PropertyValueMember} within a {@link PropertyValue} representing a single segment of the {@link Declaration} value.
 * <p/>
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
     * @return The parent {@link Declaration}, or {@link Optional#absent()} if this term is detached or without a parent.
     */
    Optional<Declaration> declaration();

    /**
     * Gets the <em>textual</em> content of this term.
     * <p/>
     * This method may be useful as a generic way of getting the value of unknown or potentially varying term types.
     * <p/>
     * If you have the concrete type of the {@link Term} in hand, prefer to use the more specific getter method instead of this
     * one.
     * <p/>
     * <b>Important:</b> this is not a substitute or a replica of how the term will actually be written to a stylesheet. The
     * textual content returned may not include certain tokens and outer symbols such as hashes, quotes, parenthesis, etc... . To
     * get the textual content as it would be written to a stylesheet see {@link StyleWriter#writeSingle(Writable)} instead.
     * However note that you should rarely have need for doing that outside of actually creating stylesheet output.
     * <p/>
     * {@link KeywordValue}s will simply return the keyword, {@link StringValue}s will return the contents of the string <b>not
     * including quotes</b>, functions will return the content of the function not including the parenthesis, {@link
     * HexColorValue} will return the hex value without the leading '#' , and so on... See each specific {@link Term}
     * implementation for more details.
     * <p/>
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
