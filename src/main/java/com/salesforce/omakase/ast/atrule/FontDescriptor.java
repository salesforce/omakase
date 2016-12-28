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

package com.salesforce.omakase.ast.atrule;

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
import com.salesforce.omakase.data.Property;
import com.salesforce.omakase.plugin.syntax.FontFacePlugin;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.salesforce.omakase.broadcast.BroadcastRequirement.REFINED_AT_RULE;

/**
 * Represents one of the {@link FontFaceBlock} font-descriptors (src, font-weight, font-style, etc...).
 *
 * @author nmcwilliams
 * @see FontFaceBlock
 * @see FontFacePlugin
 */
@Subscribable
@Description(value = "font descriptor within @font-face", broadcasted = REFINED_AT_RULE)
public final class FontDescriptor extends AbstractGroupable<FontFaceBlock, FontDescriptor> implements Named {
    private final PropertyName propertyName;
    private PropertyValue propertyValue;

    private transient Broadcaster propagatingBroadcaster;

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
     * <p>
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
     * <p>
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
     * <p>
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

        if (propagatingBroadcaster != null) {
            this.propertyValue.propagateBroadcast(propagatingBroadcaster, Status.PARSED);
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
    public void propagateBroadcast(Broadcaster broadcaster, Status status) {
        if (status() == status) {
            this.propagatingBroadcaster = broadcaster;
            propertyValue.propagateBroadcast(broadcaster, status);
            super.propagateBroadcast(broadcaster, status);
        }
    }

    @Override
    protected FontDescriptor self() {
        return this;
    }

    @Override
    public boolean writesOwnComments() {
        return true;
    }

    @Override
    public boolean isWritable() {
        return super.isWritable() && propertyName.isWritable() && propertyValue.isWritable();
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        if (!writer.isFirstAtCurrentDepth()) {
            appendable.append(';');

            if (writer.isVerbose()) {
                appendable.newline();
            } else if (writer.isInline() && !writer.isFirstAtCurrentDepth()) {
                appendable.space();
            }
        }

        writer.appendComments(comments(), appendable);

        writer.writeInner(propertyName, appendable);
        appendable.append(':').spaceIf(writer.isVerbose());
        writer.writeInner(propertyValue, appendable);
    }

    @Override
    public FontDescriptor copy() {
        return new FontDescriptor(propertyName.copy(), propertyValue.copy()).copiedFrom(this);
    }
}
