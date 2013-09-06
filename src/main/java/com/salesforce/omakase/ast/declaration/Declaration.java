/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.declaration;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.salesforce.omakase.As;
import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.Commentable;
import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.Refinable;
import com.salesforce.omakase.ast.Status;
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
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.salesforce.omakase.emitter.SubscribableRequirement.AUTOMATIC;

/**
 * TESTME Represents a CSS declaration.
 *
 * It's important to note that the raw members may contain grammatically incorrect CSS. Refining the object will perform
 * basic grammar validation. See the notes on {@link Refinable}.
 *
 * @see RawDeclarationParser
 * @see TermListParser
 *
 * @author nmcwilliams
 */
/**
 * TODO Description
 *
 * @author nmcwilliams
 */
@Subscribable
@Description(broadcasted = AUTOMATIC)
public class Declaration extends AbstractGroupable<Declaration> implements Refinable<Declaration>, Commentable {
    private List<String> comments;

    /* unrefined */
    private final RawSyntax rawPropertyName;
    private final RawSyntax rawPropertyValue;

    /* refined */
    private PropertyName propertyName;
    private PropertyValue propertyValue;

    /**
     * Creates a new instance of a {@link Declaration} with the given rawProperty (property name) and rawValue (property
     * value). The property name and value can be further refined or validated by calling {@link #refine()}.
     *
     * Note that it is called "raw" because at this point we haven't verified that either are actually valid CSS. Hence
     * really anything can technically be in there and we can't be sure it is proper formed until {@link #refine()} has
     * been called.
     *
     * @param rawPropertyName
     *            The raw property name.
     * @param rawPropertyValue
     *            The raw property value.
     * @param broadcaster
     *            The {@link Broadcaster} to use when {@link #refine()} is called.
     */
    public Declaration(RawSyntax rawPropertyName, RawSyntax rawPropertyValue, Broadcaster broadcaster) {
        super(rawPropertyName.line(), rawPropertyName.column(), broadcaster);
        this.rawPropertyName = rawPropertyName;
        this.rawPropertyValue = rawPropertyValue;
    }

    /**
     * TODO
     *
     * @param propertyName
     *            TODO
     * @param propertyValue
     *            TODO
     */
    public Declaration(Property propertyName, PropertyValue propertyValue) {
        this(PropertyName.using(propertyName), propertyValue);
    }

    /**
     * TODO
     *
     * @param propertyName
     *            TODO
     * @param singleValue
     *            TODO
     */
    public Declaration(Property propertyName, Term singleValue) {
        this(PropertyName.using(propertyName), TermList.singleValue(singleValue));
    }

    /**
     * TODO
     *
     * @param propertyName
     *            TODO
     * @param propertyValue
     *            TODO
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
     * TODO Description
     *
     * @param propertyName
     *            TODO
     * @return TODO
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
        return refinePropertyName().propertyName;
    }

    /**
     * TODO Description TODO
     *
     * @param propertyName
     *            TODO
     * @return TODO
     */
    public boolean isProperty(PropertyName propertyName) {
        return this.propertyName.equals(propertyName);
    }

    /**
     * TODO Description
     *
     * @param property
     *            TODO
     * @return TODO
     */
    public boolean isProperty(Property property) {
        return propertyName().equals(property);
    }

    /**
     * TODO Description
     *
     * @param property
     *            TODO
     * @return TODO
     */
    public boolean isProperty(String property) {
        return propertyName().equals(property);
    }

    /**
     * TODO Description
     *
     * @param propertyValue
     *            TODO
     * @return TODO
     */
    public Declaration propertyValue(PropertyValue propertyValue) {
        this.propertyValue = checkNotNull(propertyValue, "propertyValue cannot be null");

        // if the property value is new then make sure it gets broadcasted
        if (propertyValue.status() == Status.UNBROADCASTED) {
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
        return propertyValue != null;
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

    private Declaration refinePropertyName() {
        if (!isRefined()) {
            propertyName = PropertyName.using(rawPropertyName.line(), rawPropertyName.column(), rawPropertyName.content());
        }
        return this;
    }

    @Override
    public Declaration comments(Iterable<String> commentsToAdd) {
        if (comments == null) {
            comments = Lists.newArrayList();
        }
        Iterables.addAll(comments, commentsToAdd);
        return this;
    }

    @Override
    public List<String> comments() {
        return comments == null ? ImmutableList.<String>of() : ImmutableList.copyOf(comments);
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
            .add("comments", comments)
            .add("rawProperty", rawPropertyName)
            .add("rawValue", rawPropertyValue)
            .add("refinedProperty", propertyName)
            .add("refinedValue", propertyValue)
            .toString();
    }
}
