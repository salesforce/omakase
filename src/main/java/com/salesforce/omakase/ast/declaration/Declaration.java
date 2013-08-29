/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.declaration;

import static com.salesforce.omakase.emitter.SubscribableRequirement.AUTOMATIC;

import java.io.IOException;
import java.util.List;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.salesforce.omakase.As;
import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.Commentable;
import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.Refinable;
import com.salesforce.omakase.ast.collection.AbstractGroupable;
import com.salesforce.omakase.ast.declaration.value.PropertyValue;
import com.salesforce.omakase.broadcaster.Broadcaster;
import com.salesforce.omakase.broadcaster.QueryableBroadcaster;
import com.salesforce.omakase.emitter.Description;
import com.salesforce.omakase.emitter.Subscribable;
import com.salesforce.omakase.parser.*;
import com.salesforce.omakase.parser.declaration.TermListParser;
import com.salesforce.omakase.parser.raw.RawDeclarationParser;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

/**
 * Represents a CSS declaration.
 * 
 * @see RawDeclarationParser
 * @see TermListParser
 * 
 * @author nmcwilliams
 */
@Subscribable
@Description(broadcasted = AUTOMATIC)
public class Declaration extends AbstractGroupable<Declaration> implements Refinable<RefinedDeclaration>, RefinedDeclaration,
        Commentable {

    private final Broadcaster broadcaster;
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
     * <p>
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
        super(rawPropertyName.line(), rawPropertyName.column());
        this.rawPropertyName = rawPropertyName;
        this.rawPropertyValue = rawPropertyValue;
        this.broadcaster = broadcaster;
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

    @Override
    public PropertyName propertyName() {
        return propertyName;
    }

    @Override
    public String filterName() {
        return (propertyName() != null) ? propertyName().getName() : rawPropertyName.filterName();
    }

    @Override
    public PropertyValue propertyValue() {
        return propertyValue;
    }

    @Override
    public boolean isRefined() {
        return propertyName != null;
    }

    @Override
    public RefinedDeclaration refine() {
        if (!isRefined()) {
            propertyName = Property.named(rawPropertyName.content());

            QueryableBroadcaster qb = new QueryableBroadcaster(broadcaster);
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
            appendable.spaceIf(writer.verbose());

            // property value
            writer.write(propertyValue, appendable);
        } else {
            // property name
            writer.write(rawPropertyName, appendable);

            // colon
            appendable.append(':');
            appendable.spaceIf(writer.verbose());

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
