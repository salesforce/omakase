/*
 * Copyright (C) 2014 salesforce.com, inc.
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

package com.salesforce.omakase.ast.atrule;

import com.salesforce.omakase.SupportMatrix;
import com.salesforce.omakase.ast.Named;
import com.salesforce.omakase.ast.Status;
import com.salesforce.omakase.ast.collection.AbstractGroupable;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.declaration.PropertyName;
import com.salesforce.omakase.ast.declaration.PropertyValue;
import com.salesforce.omakase.ast.declaration.Term;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.annotation.Description;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.data.Property;
import com.salesforce.omakase.parser.refiner.FontFaceRefiner;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.salesforce.omakase.broadcast.BroadcastRequirement.REFINED_AT_RULE;

/**
 * Represents one of the {@link FontFaceBlock} font-descriptors (src, font-weight, font-style, etc...).
 * <p/>
 * For reasons why this is distinct from {@link Declaration}, see {@link FontFaceRefiner}.
 *
 * @author nmcwilliams
 * @see FontFaceBlock
 * @see FontFaceRefiner
 */
@Subscribable
@Description(value = "font descriptor within @font-face", broadcasted = REFINED_AT_RULE)
public final class FontDescriptor extends AbstractGroupable<FontFaceBlock, FontDescriptor> implements Named {
    private transient Broadcaster broadcaster;

    private final PropertyName propertyName;
    private PropertyValue propertyValue;

    /**
     * Creates a new {@link FontDescriptor} instance with the given property name and value.
     *
     * @param propertyName
     *     The name of this font-descriptor.
     * @param propertyValue
     *     The value of this font-descriptor.
     */
    public FontDescriptor(PropertyName propertyName, PropertyValue propertyValue) {
        this.propertyName = checkNotNull(propertyName, "propertyName cannot be null");
        this.propertyValue = checkNotNull(propertyValue, "propertyValue cannot be null");
    }

    /**
     * Creates a new {@link FontDescriptor} instance using the given {@link Declaration}s {@link PropertyName} and {@link
     * PropertyValue}, as well as comments.
     * <p/>
     * Note that this will result in refinement of the {@link Declaration} if not already done so.
     *
     * @param source
     *     The source {@link Declaration}
     */
    public FontDescriptor(Declaration source) {
        this.propertyName = checkNotNull(source.propertyName(), "the source declaration must have a property name");
        this.propertyValue = checkNotNull(source.propertyValue(), "the source declaration must have a property value");
        this.comments(source); // copy comments
    }

    /**
     * Gets the property name.
     *
     * @return The property name.
     */
    public PropertyName propertyName() {
        return propertyName;
    }

    /**
     * Gets whether this {@link FontDescriptor} has the given property name. Prefer to use {@link #isProperty(Property)} instead.
     * <p/>
     * Example:
     * <pre>
     * <code>if (descriptor.isProperty("font-family") {...}</code>
     * </pre>
     *
     * @param property
     *     Name of the property.
     *
     * @return True if this {@link FontDescriptor} has the given property name.
     */
    public boolean isProperty(String property) {
        return propertyName().matches(property);
    }

    /**
     * Gets whether this {@link FontDescriptor} has the given {@link Property} name.
     * <p/>
     * Example:
     * <pre>
     * <code>if (descriptor.isProperty(Property.FONT_FAMILY)) {...}</code>
     * </pre>
     *
     * @param property
     *     The {@link Property}.
     *
     * @return True of this {@link FontDescriptor} has the given property name.
     */
    public boolean isProperty(Property property) {
        return propertyName().matches(property);
    }

    @Override
    public String name() {
        return propertyName.name();
    }

    /**
     * Sets a new property value.
     *
     * @param singleTerm
     *     The single {@link Term}.
     *
     * @return this, for chaining.
     */
    public FontDescriptor propertyValue(Term singleTerm) {
        return propertyValue(PropertyValue.of(singleTerm));
    }

    /**
     * Sets a new property value.
     *
     * @param propertyValue
     *     The new property value.
     *
     * @return this, for chaining.
     */
    public FontDescriptor propertyValue(PropertyValue propertyValue) {
        if (this.propertyValue != null) {
            this.propertyValue.status(Status.NEVER_EMIT); //  don't emit detached property values
        }

        this.propertyValue = checkNotNull(propertyValue, "propertyValue cannot be null");

        if (broadcaster != null && propertyValue.status() == Status.UNBROADCASTED) {
            propertyValue.propagateBroadcast(broadcaster);
        }
        return this;
    }

    /**
     * Gets the property value.
     *
     * @return The property value.
     */
    public PropertyValue propertyValue() {
        return propertyValue;
    }

    @Override
    public void propagateBroadcast(Broadcaster broadcaster) {
        propertyValue.propagateBroadcast(broadcaster);
        super.propagateBroadcast(broadcaster);

        // necessary for cases when we are already attached but a new property value hasn't been broadcasted.
        this.broadcaster = broadcaster;
    }

    @Override
    protected FontDescriptor self() {
        return this;
    }

    @Override
    public boolean isWritable() {
        return super.isWritable() && propertyName.isWritable() && propertyValue.isWritable();
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        writer.writeInner(propertyName, appendable);
        appendable.append(':').spaceIf(writer.isVerbose());
        writer.writeInner(propertyValue, appendable);
    }

    @Override
    protected FontDescriptor makeCopy(Prefix prefix, SupportMatrix support) {
        return new FontDescriptor(propertyName.copy(prefix, support), propertyValue.copy(prefix, support));
    }
}
