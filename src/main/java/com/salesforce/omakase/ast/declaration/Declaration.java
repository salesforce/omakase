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
import com.salesforce.omakase.SupportMatrix;
import com.salesforce.omakase.ast.Copyable;
import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.Refinable;
import com.salesforce.omakase.ast.Rule;
import com.salesforce.omakase.ast.collection.AbstractGroupable;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.annotation.Description;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.data.Property;
import com.salesforce.omakase.parser.declaration.TermListParser;
import com.salesforce.omakase.parser.raw.RawDeclarationParser;
import com.salesforce.omakase.parser.refiner.Refiner;
import com.salesforce.omakase.util.As;
import com.salesforce.omakase.util.Copy;
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
 * <p/>
 *
 * @author nmcwilliams
 * @see RawDeclarationParser
 * @see TermListParser
 */
@Subscribable
@Description(broadcasted = AUTOMATIC)
public final class Declaration extends AbstractGroupable<Rule, Declaration> implements Refinable<Declaration>, Copyable<Declaration> {
    private final Refiner refiner;

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
     *     The {@link Refiner} to be used later during refinement of this object.
     */
    public Declaration(RawSyntax rawPropertyName, RawSyntax rawPropertyValue, Refiner refiner) {
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
     *   PropertyValue value = TermList.ofValues(OperatorType.SPACE, px10, em5);
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
        this(PropertyName.using(propertyName), propertyValue);
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
        this(PropertyName.using(propertyName), TermList.singleValue(singleValue));
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
        this(propertyName, TermList.singleValue(singleValue));
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
        propertyName(propertyName);
        propertyValue(propertyValue);
    }

    /**
     * Gets the original, raw, non-validated property name.
     *
     * @return The raw property name, or {@link Optional#absent()} if the raw property name is not set (e.g., a dynamically
     *         created unit).
     */
    public Optional<RawSyntax> rawPropertyName() {
        return Optional.fromNullable(rawPropertyName);
    }

    /**
     * Gets the original, raw, non-validated property value.
     *
     * @return The raw property value, or {@link Optional#absent()} if the raw property value is not set (e.g., a dynamically
     *         created unit).
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
        this.propertyName = PropertyName.using(checkNotNull(property, "property cannot be null"));
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
     * @param property
     *     Name of the property.
     *
     * @return True if this {@link Declaration} has the given property name.
     */
    public boolean isProperty(String property) {
        return propertyName().matches(property);
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
     * Gets whether the {@link PropertyName} is prefixed.
     *
     * @return True if the {@link PropertyName} is prefixed.
     */
    public boolean isPrefixed() {
        return propertyName().isPrefixed();
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
        return propertyValue(TermList.singleValue(singleTerm));
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
        this.propertyValue = checkNotNull(propertyValue, "propertyValue cannot be null");
        propertyValue.parentDeclaration(this);
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
        if (!isRefined() && propertyName == null) {
            propertyName = PropertyName.using(rawPropertyName.line(), rawPropertyName.column(), rawPropertyName.content());
        }
        return propertyName;
    }

    @Override
    public void propagateBroadcast(Broadcaster broadcaster) {
        super.propagateBroadcast(broadcaster);
        if (propertyValue != null) {
            propertyValue.propagateBroadcast(broadcaster);
        }
    }

    @Override
    protected Declaration self() {
        return this;
    }

    @Override
    public boolean isWritable() {
        if (isRefined()) {
            return super.isWritable() && propertyName.isWritable() && propertyValue.isWritable();
        }
        return super.isWritable();
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        if (isRefined()) {
            // property name
            writer.writeInner(propertyName, appendable);

            // colon
            appendable.append(':');
            appendable.spaceIf(writer.isVerbose());

            // property value
            writer.writeInner(propertyValue, appendable);
        } else {
            // property name
            writer.writeInner(rawPropertyName, appendable);

            // colon
            appendable.append(':');
            appendable.spaceIf(writer.isVerbose());

            // property value
            writer.writeInner(rawPropertyValue, appendable);
        }
    }

    @Override
    public Declaration copy() {
        return Copy.comments(this, new Declaration(propertyName().copy(), propertyValue().copy()));
    }

    @Override
    public Declaration copyWithPrefix(Prefix prefix, SupportMatrix support) {
        PropertyName pn = propertyName().copyWithPrefix(prefix, support);
        PropertyValue pv = propertyValue().copyWithPrefix(prefix, support);
        return Copy.comments(this, new Declaration(pn, pv));
    }

    @Override
    public String toString() {
        return As.string(this)
            .indent()
            .add("abstract", super.toString())
            .add("rawProperty", rawPropertyName)
            .add("rawValue", rawPropertyValue)
            .add("refinedProperty", propertyName)
            .add("refinedValue", propertyValue)
            .addUnlessEmpty("orphaned", orphanedComments())
            .toString();
    }
}
