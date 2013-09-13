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
import com.salesforce.omakase.As;
import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.Refinable;
import com.salesforce.omakase.ast.Status;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.collection.AbstractGroupable;
import com.salesforce.omakase.ast.declaration.value.PropertyValue;
import com.salesforce.omakase.ast.declaration.value.Term;
import com.salesforce.omakase.ast.declaration.value.TermList;
import com.salesforce.omakase.broadcaster.Broadcaster;
import com.salesforce.omakase.broadcaster.QueryableBroadcaster;
import com.salesforce.omakase.emitter.Description;
import com.salesforce.omakase.emitter.Subscribable;
import com.salesforce.omakase.parser.Parser;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.ParserStrategy;
import com.salesforce.omakase.parser.Stream;
import com.salesforce.omakase.parser.declaration.TermListParser;
import com.salesforce.omakase.parser.raw.RawDeclarationParser;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.salesforce.omakase.emitter.SubscribableRequirement.AUTOMATIC;

/**
 * TESTME
 * <p/>
 * Represents a CSS declaration.
 * <p/>
 * It's important to note that the raw members may contain grammatically incorrect CSS. Refining the object will perform basic
 * grammar validation. See the notes on {@link Refinable}.
 *
 * @author nmcwilliams
 * @see RawDeclarationParser
 * @see TermListParser
 */
@Subscribable
@Description(broadcasted = AUTOMATIC)
public class Declaration extends AbstractGroupable<Declaration> implements Refinable<Declaration> {
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
     * @param broadcaster
     *     The {@link Broadcaster} to use when {@link #refine()} is called.
     */
    public Declaration(RawSyntax rawPropertyName, RawSyntax rawPropertyValue, Broadcaster broadcaster) {
        super(rawPropertyName.line(), rawPropertyName.column(), broadcaster);
        this.rawPropertyName = rawPropertyName;
        this.rawPropertyValue = rawPropertyValue;
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
     *   PropertyValue value = TermList.ofValues(TermOperator.SPACE, px10, em5);
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
     * Creates a new instance of a {@link Declaration} with the given {@link PropertyName} and single {@link Term} value.
     * <p/>
     * This should be used for dynamically created declarations.
     * <p/>
     * Example:
     * <pre>
     * {@code new Declaration(Property.ZOOM, NumericalValue.of(1))}
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
        this.rawPropertyName = null;
        this.rawPropertyValue = null;
        this.propertyName = checkNotNull(propertyName);
        this.propertyValue = checkNotNull(propertyValue);
    }

    /**
     * Gets the original, raw, non-validated property name.
     *
     * @return The raw property name.
     */
    public RawSyntax rawPropertyName() {
        return rawPropertyName;
    }

    /**
     * Gets the original, raw, non-validated property value.
     *
     * @return The raw property value.
     */
    public RawSyntax rawPropertyValue() {
        return rawPropertyValue;
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
     * Gets the property name.
     *
     * @return The property name.
     */
    public PropertyName propertyName() {
        return propertyName == null ? refinePropertyName() : propertyName;
    }

    /**
     * Gets whether this {@link Declaration} has the given {@link PropertyName}.
     *
     * @param propertyName
     *     The property name.
     *
     * @return True if this {@link Declaration} has the given property name.
     */
    public boolean isProperty(PropertyName propertyName) {
        return propertyName().equals(propertyName);
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
     *     The property name.
     *
     * @return True of this {@link Declaration} has the given property name.
     */
    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    public boolean isProperty(Property property) {
        return propertyName().equals(property);
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
    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    public boolean isProperty(String property) {
        return propertyName().equals(property);
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

        // if the property value is new then make sure it gets broadcasted
        if (propertyValue.status() == Status.UNBROADCASTED && broadcaster() != null) {
            broadcaster().broadcast(propertyValue);
        }

        return this;
    }

    /**
     * Gets the property value.
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
        if (!isRefined()) {
            refinePropertyName();

            QueryableBroadcaster qb = new QueryableBroadcaster(broadcaster());
            Stream stream = new Stream(rawPropertyValue.content(), line(), column());

            // parse the contents
            Parser parser = ParserStrategy.getValueParser(propertyName);
            parser.parse(stream, qb);

            // there should be nothing left
            if (!stream.eof()) throw new ParserException(stream, Message.UNPARSABLE_VALUE);

            // store the parsed value
            Optional<PropertyValue> first = qb.find(PropertyValue.class);
            if (!first.isPresent()) throw new ParserException(stream, Message.EXPECTED_VALUE);
            propertyValue = first.get();
        }

        return this;
    }

    /** Refines just the property name */
    private PropertyName refinePropertyName() {
        if (!isRefined()) {
            propertyName = PropertyName.using(rawPropertyName.line(), rawPropertyName.column(), rawPropertyName.content());
        }
        return propertyName;
    }

    @Override
    public Syntax broadcaster(Broadcaster broadcaster) {
        if (propertyValue != null) {
            propertyValue.broadcaster(broadcaster);
        }
        return super.broadcaster(broadcaster);
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
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        if (isDetached()) return;

        if (isRefined()) {
            // property name
            writer.write(propertyName, appendable);

            // colon
            appendable.append(':');
            appendable.spaceIf(writer.isVerbose());

            // property value
            writer.write(propertyValue, appendable);
        } else {
            // property name
            writer.write(rawPropertyName, appendable);

            // colon
            appendable.append(':');
            appendable.spaceIf(writer.isVerbose());

            // property value
            writer.write(rawPropertyValue, appendable);
        }
    }

    @Override
    public String toString() {
        return As.string(this)
            .indent()
            .add("position", super.toString())
            .add("rawProperty", rawPropertyName)
            .add("rawValue", rawPropertyValue)
            .add("refinedProperty", propertyName)
            .add("refinedValue", propertyValue)
            .toString();
    }
}
