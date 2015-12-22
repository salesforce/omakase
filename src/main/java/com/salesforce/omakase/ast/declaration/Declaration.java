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

import com.google.common.base.Optional;
import com.salesforce.omakase.ast.Named;
import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.Refinable;
import com.salesforce.omakase.ast.Rule;
import com.salesforce.omakase.ast.StatementIterable;
import com.salesforce.omakase.ast.Status;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.atrule.AtRuleBlock;
import com.salesforce.omakase.ast.collection.AbstractGroupable;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.annotation.Description;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import com.salesforce.omakase.data.Property;
import com.salesforce.omakase.parser.declaration.PropertyValueParser;
import com.salesforce.omakase.parser.raw.RawDeclarationParser;
import com.salesforce.omakase.parser.refiner.MasterRefiner;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.salesforce.omakase.broadcast.BroadcastRequirement.AUTOMATIC;

/**
 * Represents a CSS declaration.
 * <p/>
 * It's important to note that the raw members may contain grammatically incorrect CSS. Refining the object will perform basic
 * grammar validation. See the notes on {@link Refinable} and in the readme.
 *
 * @author nmcwilliams
 * @see RawDeclarationParser
 * @see PropertyValueParser
 */
@Subscribable
@Description(broadcasted = AUTOMATIC)
public final class Declaration extends AbstractGroupable<Rule, Declaration> implements Refinable<Declaration>, Named {
    private final transient MasterRefiner refiner;
    private transient Broadcaster broadcaster;

    /* unrefined */
    private final RawSyntax rawPropertyName;
    private final RawSyntax rawPropertyValue;

    /* refined */
    private PropertyName propertyName;
    private PropertyValue propertyValue;

    /**
     * Creates a new instance of a {@link Declaration} with the given rawProperty (property name) and rawValue (property value).
     * The property name and value can be further refined or validated by calling {@link #refine()}.
     * <p/>
     * Note that it is called "raw" because at this point we haven't verified that either are actually valid CSS. Hence really
     * anything can technically be in there and we can't be sure it is proper formed until {@link #refine()} has been called.
     *
     * @param rawPropertyName
     *     The raw property name.
     * @param rawPropertyValue
     *     The raw property value.
     * @param refiner
     *     The {@link MasterRefiner} to be used later during refinement of this object.
     */
    public Declaration(RawSyntax rawPropertyName, RawSyntax rawPropertyValue, MasterRefiner refiner) {
        super(rawPropertyName.line(), rawPropertyName.column());
        this.rawPropertyName = rawPropertyName;
        this.rawPropertyValue = rawPropertyValue;
        this.refiner = refiner;
    }

    /**
     * Creates a new instance of a {@link Declaration} with the given {@link PropertyName} and {@link PropertyValue}.
     * <p/>
     * This should be used for dynamically created declarations.
     * <p/>
     * Example:
     * <pre>
     * {@code NumericalValue px10 = NumericalValue.of(10, "px");
     *   NumericalValue em5 = NumericalValue.of(5, "em");
     *   PropertyValue value = PropertyValue.ofTerms(OperatorType.SPACE, px10, em5);
     *   new Declaration(Property.BORDER_RADIUS, value)}
     * </pre>
     * <p/>
     * If there is only a single value then use {@link #Declaration(Property, Term)} instead.
     *
     * @param propertyName
     *     The {@link Property}.
     * @param propertyValue
     *     The {@link PropertyValue}.
     */
    public Declaration(Property propertyName, PropertyValue propertyValue) {
        this(PropertyName.of(propertyName), propertyValue);
    }

    /**
     * Creates a new instance of a {@link Declaration} with the given {@link Property} and single {@link Term} value.
     * <p/>
     * This should be used for dynamically created declarations.
     * <p/>
     * Example:
     * <pre>
     * {@code new Declaration(Property.ZOOM, NumericalValue.of(1));}
     * </pre>
     *
     * @param propertyName
     *     The {@link Property}.
     * @param singleValue
     *     The single {@link Term}.
     */
    public Declaration(Property propertyName, Term singleValue) {
        this(PropertyName.of(propertyName), PropertyValue.of(singleValue));
    }

    /**
     * Creates a new instance of a {@link Declaration} with the given {@link PropertyName} and single {@link Term} value.
     * <p/>
     * This should be used for dynamically created declarations.
     * <p/>
     * Example:
     * <pre>
     * {@code PropertyName name = PropertyName.using("new-prop");}
     * {@code new Declaration(name, NumericalValue.of(1));}
     * </pre>
     *
     * @param propertyName
     *     The property name.
     * @param singleValue
     *     The single {@link Term}.
     */
    public Declaration(PropertyName propertyName, Term singleValue) {
        this(propertyName, PropertyValue.of(singleValue));
    }

    /**
     * Creates a new instance of a {@link Declaration} with the given {@link PropertyName} and {@link PropertyValue}.
     * <p/>
     * This should be used for dynamically created declarations.
     * <p/>
     * Example:
     * <pre>
     * {@code PropertyName prop = PropertyName.using(Property.BORDER_RADIUS).prefix(Prefix.WEBKIT);
     *   Declaration newDeclaration = new Declaration(prop, declaration.propertyValue());}
     * </pre>
     *
     * @param propertyName
     *     The {@link PropertyName}.
     * @param propertyValue
     *     The {@link PropertyValue}.
     */
    public Declaration(PropertyName propertyName, PropertyValue propertyValue) {
        this.refiner = null;
        this.rawPropertyName = null;
        this.rawPropertyValue = null;
        propertyName(propertyName).propertyValue(propertyValue);
    }

    /**
     * Gets the original, raw, non-validated property name.
     *
     * @return The raw property name, or {@link Optional#absent()} if the raw property name is not set (e.g., a dynamically
     * created unit).
     */
    public Optional<RawSyntax> rawPropertyName() {
        return Optional.fromNullable(rawPropertyName);
    }

    /**
     * Gets the original, raw, non-validated property value.
     *
     * @return The raw property value, or {@link Optional#absent()} if the raw property value is not set (e.g., a dynamically
     * created unit).
     */
    public Optional<RawSyntax> rawPropertyValue() {
        return Optional.fromNullable(rawPropertyValue);
    }

    /**
     * Sets a new property name. Generally, doing this should be avoided.
     *
     * @param property
     *     The new property.
     *
     * @return this, for chaining.
     */
    public Declaration propertyName(Property property) {
        this.propertyName = PropertyName.of(checkNotNull(property, "property cannot be null"));
        return this;
    }

    /**
     * Sets a new property name. Generally, doing this should be avoided.
     *
     * @param propertyName
     *     The new property name.
     *
     * @return this, for chaining.
     */
    public Declaration propertyName(PropertyName propertyName) {
        this.propertyName = checkNotNull(propertyName, "propertyName cannot be null");
        return this;
    }

    /**
     * Sets a new property name. Generally, doing this should be avoided.
     *
     * @param propertyName
     *     The new property name.
     *
     * @return this, for chaining.
     */
    public Declaration propertyName(String propertyName) {
        this.propertyName = PropertyName.of(propertyName);
        return this;
    }

    /**
     * Gets the property name. This automatically refines the property name if not already done so.
     *
     * @return The property name.
     */
    public PropertyName propertyName() {
        return refinePropertyName();
    }

    /**
     * Gets whether this {@link Declaration} has the given property name. Prefer to use {@link #isProperty(Property)} instead.
     * <p/>
     * Example:
     * <pre>
     * <code>if (declaration.isProperty("border-radius") {...}</code>
     * </pre>
     *
     * @param name
     *     Name of the property.
     *
     * @return True if this {@link Declaration} has the given property name.
     */
    public boolean isProperty(String name) {
        return propertyName().matches(name);
    }

    /**
     * Gets whether this {@link Declaration} has the given {@link Property} name.
     * <p/>
     * Example:
     * <pre>
     * <code>if (declaration.isProperty(Property.BORDER_RADIUS)) {...}</code>
     * </pre>
     *
     * @param property
     *     The {@link Property}.
     *
     * @return True of this {@link Declaration} has the given property name.
     */
    public boolean isProperty(Property property) {
        return propertyName().matches(property);
    }

    /**
     * Gets whether this {@link Declaration} has a {@link PropertyName} that matches the given one. For the definition of this,
     * see {@link PropertyName#matches(PropertyName)}.
     *
     * @param propertyName
     *     The {@link PropertyName}.
     *
     * @return True if this {@link Declaration} has a property name that matches the given one.
     *
     * @see PropertyName#matches(PropertyName)
     */
    public boolean isProperty(PropertyName propertyName) {
        return propertyName().matches(propertyName);
    }

    /**
     * Same as {@link #isProperty(Property)}, except this ignores the prefix.
     *
     * @param property
     *     The property.
     *
     * @return True if this {@link Declaration} has the given property, ignoring the prefix.
     *
     * @see PropertyName#matchesIgnorePrefix(Property)
     */
    public boolean isPropertyIgnorePrefix(Property property) {
        return propertyName().matchesIgnorePrefix(property);
    }

    /**
     * Same as {@link #isProperty(PropertyName)}, except this ignores the prefix.
     *
     * @param propertyName
     *     The {@link PropertyName}.
     *
     * @return True if this {@link Declaration} has the given property name, ignoring the prefix.
     *
     * @see PropertyName#matchesIgnorePrefix(PropertyName)
     */
    public boolean isPropertyIgnorePrefix(PropertyName propertyName) {
        return propertyName().matchesIgnorePrefix(propertyName);
    }

    /**
     * Same as {@link #isProperty(String)}, except this ignores the prefix.
     *
     * @param name
     *     The property name.
     *
     * @return True if this {@link Declaration} has the given property name, ignoring the prefix.
     *
     * @see PropertyName#matchesIgnorePrefix(String)
     */
    public boolean isPropertyIgnorePrefix(String name) {
        return propertyName().matchesIgnorePrefix(name);
    }

    /**
     * Gets whether the {@link PropertyName} is prefixed.
     *
     * @return True if the {@link PropertyName} is prefixed.
     */
    public boolean isPrefixed() {
        return propertyName().isPrefixed();
    }

    @Override
    public String name() {
        return propertyName().name();
    }

    /**
     * Sets a new property value.
     *
     * @param singleTerm
     *     The single {@link Term}.
     *
     * @return this, for chaining.
     */
    public Declaration propertyValue(Term singleTerm) {
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
    public Declaration propertyValue(PropertyValue propertyValue) {
        if (this.propertyValue != null) {
            this.propertyValue.declaration(null);
            this.propertyValue.status(Status.NEVER_EMIT); //  don't emit detached property values
        }

        this.propertyValue = checkNotNull(propertyValue, "propertyValue cannot be null");
        propertyValue.declaration(this);

        if (broadcaster != null && propertyValue.status() == Status.UNBROADCASTED) {
            propertyValue.propagateBroadcast(broadcaster);
        }
        return this;
    }

    /**
     * Gets the property value. This automatically refines the property value if not already done so.
     *
     * @return The property value.
     */
    public PropertyValue propertyValue() {
        return refine().propertyValue;
    }

    /**
     * Similar to {@link #parent()}, except this will return the parent's containing {@link AtRule}.
     * <p/>
     * This is only applicable for declarations directly within a {@link Rule}, directly within an {@link AtRuleBlock}, directly
     * within an {@link AtRule}.
     *
     * @return The parent {@link AtRule}, or {@link Optional#absent()} if not present or if the parent hierarchy doesn't match as
     * described above.
     */
    public Optional<AtRule> parentAtRule() {
        Rule rule = parent();

        if (rule != null) {
            StatementIterable parent = rule.parent();
            if (parent instanceof AtRuleBlock) {
                return Optional.fromNullable(((AtRuleBlock)parent).parent());
            }
        }

        return Optional.absent();
    }

    @Override
    public boolean isRefined() {
        return propertyName != null && propertyValue != null;
    }

    @Override
    public Declaration refine() {
        if (!isRefined() && refiner != null) {
            refinePropertyName();
            refiner.refine(this);
        }

        return this;
    }

    /** Refines just the property name */
    private PropertyName refinePropertyName() {
        if (propertyName == null && !isRefined()) {
            propertyName = PropertyName.of(rawPropertyName.line(), rawPropertyName.column(), rawPropertyName.content());
        }
        return propertyName;
    }

    @Override
    public boolean containsRawSyntax() {
        return rawPropertyName != null || rawPropertyValue != null;
    }

    @Override
    public void propagateBroadcast(Broadcaster broadcaster) {
        if (propertyValue != null) {
            propertyValue.propagateBroadcast(broadcaster);
        }
        super.propagateBroadcast(broadcaster);

        // necessary for cases when we are already attached but a new property value hasn't been broadcasted.
        this.broadcaster = broadcaster;
    }

    @Override
    protected Declaration self() {
        return this;
    }

    @Override
    public boolean writesOwnComments() {
        return true;
    }

    @Override
    public boolean isWritable() {
        return super.isWritable() && (!isRefined() || (propertyName.isWritable() && propertyValue.isWritable()));
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

        if (isRefined()) {
            writer.writeInner(propertyName, appendable);
            appendable.append(':').spaceIf(writer.isVerbose());
            writer.writeInner(propertyValue, appendable);
        } else {
            writer.writeInner(rawPropertyName, appendable);
            appendable.append(':').spaceIf(writer.isVerbose());
            writer.writeInner(rawPropertyValue, appendable);
        }
    }

    @Override
    public Declaration copy() {
        return new Declaration(propertyName().copy(), propertyValue().copy()).copiedFrom(this);
    }
}
