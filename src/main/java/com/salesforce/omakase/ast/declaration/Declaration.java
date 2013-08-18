/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.declaration;

import static com.salesforce.omakase.emitter.SubscribableRequirement.AUTOMATIC;

import com.google.common.base.Optional;
import com.salesforce.omakase.As;
import com.salesforce.omakase.Broadcaster;
import com.salesforce.omakase.CollectingBroadcaster;
import com.salesforce.omakase.ast.AbstractLinkable;
import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.Refinable;
import com.salesforce.omakase.ast.declaration.value.PropertyValue;
import com.salesforce.omakase.emitter.Description;
import com.salesforce.omakase.emitter.Subscribable;
import com.salesforce.omakase.parser.*;

/**
 * A CSS declaration, comprised of a property and value.
 * 
 * @author nmcwilliams
 */
@Subscribable
@Description(broadcasted = AUTOMATIC)
public class Declaration extends AbstractLinkable<Declaration> implements Refinable<RefinedDeclaration>, RefinedDeclaration {
    private static final String UNRECOGNIZED = "Unable to parse remaining declaration value";
    private static final String EXPECTED = "Expected to parse a property value!";

    private final Broadcaster broadcaster;
    private final RawSyntax rawPropertyName;
    private final RawSyntax rawPropertyValue;

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
    public RefinedDeclaration refine() {
        if (propertyName == null) {
            propertyName = Property.named(rawPropertyName.content());

            CollectingBroadcaster collector = new CollectingBroadcaster(broadcaster);
            Stream stream = new Stream(rawPropertyValue.content(), line(), column());

            // parse the contents
            Parser parser = ParserStrategy.getValueParser(propertyName);
            parser.parse(stream, collector);

            // there should be nothing left
            if (!stream.eof()) throw new ParserException(stream, UNRECOGNIZED);

            // store the parsed value
            Optional<PropertyValue> first = collector.find(PropertyValue.class);
            if (!first.isPresent()) throw new ParserException(stream, EXPECTED);
            propertyValue = first.get();
        }

        return this;
    }

    @Override
    protected Declaration self() {
        return this;
    }

    @Override
    public String toString() {
        return As.string(this)
            .indent()
            .add("syntax", super.toString())
            .add("rawProperty", rawPropertyName)
            .add("rawValue", rawPropertyValue)
            .add("refinedProperty", propertyName)
            .add("refinedValue", propertyValue)
            .toString();
    }
}
