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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.salesforce.omakase.broadcast.BroadcastRequirement.AUTOMATIC;

import java.io.IOException;
import java.util.Optional;

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
import com.salesforce.omakase.broadcast.emitter.SubscriptionPhase;
import com.salesforce.omakase.data.Property;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

/**
 * Represents a CSS declaration.
 * <p>
 * See the notes on {@link Refinable}.
 *
 * @author nmcwilliams
 * @see DeclarationParser
 * @see PropertyValueParser
 */
@Subscribable
@Description(broadcasted = AUTOMATIC)
public final class Declaration extends AbstractGroupable<Rule, Declaration> implements Refinable, Named {
    /* unrefined */
    private final RawSyntax rawName;
    private final RawSyntax rawValue;

    /* refined */
    private PropertyName propertyName;
    private PropertyValue propertyValue;

    private transient Broadcaster propagatingBroadcaster;

    /**
     * Creates a new {@link Declaration} with the given rawProperty (property name) and rawValue (property value).
     *
     * @param rawName
     *     The raw property name.
     * @param rawValue
     *     The raw property value.
     */
    public Declaration(RawSyntax rawName, RawSyntax rawValue) {
        super(rawName.line(), rawName.column());
        this.rawName = rawName;
        this.rawValue = rawValue;
        status(Status.RAW);
        propertyValue(new PropertyValue());
    }

    /**
     * Creates a new {@link Declaration} with the given {@link PropertyName} and {@link PropertyValue}.
     * <p>
     * This should be used for dynamically created declarations.
     * <p>
     * Example:
     * <pre>
     * {@code NumericalValue px10 = NumericalValue.of(10, "px");
     *   NumericalValue em5 = NumericalValue.of(5, "em");
     *   PropertyValue value = PropertyValue.ofTerms(OperatorType.SPACE, px10, em5);
     *   new Declaration(Property.BORDER_RADIUS, value)}
     * </pre>
     * <p>
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
     * <p>
     * This should be used for dynamically created declarations.
     * <p>
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
     * <p>
     * This should be used for dynamically created declarations.
     * <p>
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
     * <p>
     * This should be used for dynamically created declarations.
     * <p>
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
        this.rawName = null;
        this.rawValue = null;
        propertyName(propertyName);
        propertyValue(propertyValue);
    }

    /**
     * Gets the original, raw, non-validated property name.
     *
     * @return The raw property name, or an empty {@link Optional} if the raw property name is not set (e.g., a dynamically
     * created unit).
     */
    public Optional<RawSyntax> rawPropertyName() {
        return Optional.ofNullable(rawName);
    }

    /**
     * Gets the original, raw, non-validated property value.
     *
     * @return The raw property value, or an empty {@link Optional} if the raw property value is not set (e.g., a dynamically
     * created unit).
     */
    public Optional<RawSyntax> rawPropertyValue() {
        return Optional.ofNullable(rawValue);
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

    @Override
    public String name() {
        return propertyName().name();
    }

    /**
     * Gets the property name. This automatically refines the property name if not already done so.
     *
     * @return The property name.
     */
    public PropertyName propertyName() {
        if (propertyName == null) {
            propertyName = PropertyName.of(rawName.line(), rawName.column(), rawName.content());
        }
        return propertyName;
    }

    /**
     * Gets whether this {@link Declaration} has the given property name. Prefer to use {@link #isProperty(Property)} instead.
     * <p>
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
     * <p>
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
        checkNotNull(propertyValue, "propertyValue cannot be null");
        checkArgument(propertyValue.status() != Status.RAW, "propertyValue cannot have RAW status");
        checkArgument(propertyValue.status() != Status.NEVER_EMIT, "propertyValue cannot have NEVER_EMIT status");

        if (this.propertyValue != null) {
            this.propertyValue.declaration(null);
            this.propertyValue.status(Status.NEVER_EMIT); //  don't emit detached property values
        }

        this.propertyValue = propertyValue;
        this.propertyValue.declaration(this);

        if (propagatingBroadcaster != null) {
            this.propertyValue.propagateBroadcast(propagatingBroadcaster, Status.PARSED);
        }
        return this;
    }

    /**
     * Gets the property value.
     * <p>
     * <b>Important:</b> this may be an <b>empty</b> property value if this declaration is unrefined! See the main readme file for
     * more information on refinement.
     * <p>
     * For basic use cases, to ensure this is always refined and properly set use {@link AutoRefine} or {@link StandardValidation}
     * during parsing. For reasons why you would <em>not</em> want to do that see the main readme file.
     *
     * @return The property value.
     */
    public PropertyValue propertyValue() {
        return propertyValue;
    }

    /**
     * Similar to {@link #parent()}, except this will return the parent's containing {@link AtRule}.
     * <p>
     * This is only applicable for declarations directly within a {@link Rule}, directly within an {@link AtRuleBlock}, directly
     * within an {@link AtRule}.
     *
     * @return The parent {@link AtRule}, or an empty {@link Optional} if not present or if the parent hierarchy doesn't match as
     * described above.
     */
    public Optional<AtRule> parentAtRule() {
        Rule rule = parent();

        if (rule != null) {
            StatementIterable parent = rule.parent();
            if (parent instanceof AtRuleBlock) {
                return Optional.ofNullable(((AtRuleBlock)parent).parent());
            }
        }

        return Optional.empty();
    }

    @Override
    public boolean isRefined() {
        return rawValue == null || !propertyValue.members().isEmpty();
    }

    @Override
    public boolean shouldBreakBroadcast(SubscriptionPhase phase) {
        return super.shouldBreakBroadcast(phase) || (phase == SubscriptionPhase.REFINE && isRefined());
    }

    @Override
    public void propagateBroadcast(Broadcaster broadcaster, Status status) {
        if (status() == status) {
            propagatingBroadcaster = broadcaster;
            propertyValue.propagateBroadcast(broadcaster, status);
            super.propagateBroadcast(broadcaster, status);
        }
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
        return super.isWritable() && (!isRefined() || propertyValue.isWritable());
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
            writer.writeInner(propertyName(), appendable);
            appendable.append(':').spaceIf(writer.isVerbose());
            writer.writeInner(propertyValue, appendable);
        } else {
            writer.writeInner(rawName, appendable);
            appendable.append(':').spaceIf(writer.isVerbose());
            writer.writeInner(rawValue, appendable);
        }
    }

    @Override
    public Declaration copy() {
        if (isRefined()) {
            return new Declaration(propertyName().copy(), propertyValue.copy()).copiedFrom(this);
        } else {
            return new Declaration(rawName.copy(), rawValue.copy()).copiedFrom(this);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        if (this.propertyValue != null) {
            propertyValue.members().destroyAll();
        }
    }
}
