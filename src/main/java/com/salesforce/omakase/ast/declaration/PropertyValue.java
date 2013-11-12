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
import com.salesforce.omakase.ast.Copyable;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.broadcast.annotation.Description;
import com.salesforce.omakase.broadcast.annotation.Subscribable;

import static com.salesforce.omakase.broadcast.BroadcastRequirement.REFINED_DECLARATION;

/**
 * The value of a property in a {@link Declaration}.
 *
 * @author nmcwilliams
 */
@Subscribable
@Description(value = "interface for all property values", broadcasted = REFINED_DECLARATION)
public interface PropertyValue extends Syntax, Copyable<PropertyValue> {
    /**
     * Gets whether this {@link PropertyValue} is marked as "!important".
     *
     * @return True if this property value is marked as important.
     */
    boolean isImportant();

    /**
     * Sets whether this {@link PropertyValue} is marked as "!important".
     *
     * @param important
     *     Whether the value is "!important".
     *
     * @return this, for chaining.
     */
    PropertyValue important(boolean important);

    /**
     * Sets the parent {@link Declaration}. Generally this is handled automatically when this property value is set on the {@link
     * Declaration}, so it is not recommended to call this method manually. If you do, results may be unexpected.
     *
     * @param parent
     *     The {@link Declaration} that contains this property.
     */
    void parentDeclaration(Declaration parent);

    /**
     * Gets the parent {@link Declaration} that owns this property, or absent if not set. This will not be set for dynamically
     * created property values not yet added to a {@link Declaration} instance.
     *
     * @return The parent {@link Declaration}, or {@link Optional#absent()} if not set.
     */
    Optional<Declaration> parentDeclaration();
}
