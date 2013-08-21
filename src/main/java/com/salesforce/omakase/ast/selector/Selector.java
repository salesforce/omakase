/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.selector;

import static com.salesforce.omakase.emitter.SubscribableRequirement.AUTOMATIC;

import com.salesforce.omakase.*;
import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.Refinable;
import com.salesforce.omakase.ast.collection.AbstractGroupable;
import com.salesforce.omakase.ast.collection.StandardSyntaxCollection;
import com.salesforce.omakase.ast.collection.SyntaxCollection;
import com.salesforce.omakase.emitter.Description;
import com.salesforce.omakase.emitter.Subscribable;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.ParserFactory;
import com.salesforce.omakase.parser.Stream;

/**
 * Represents a CSS selector.
 * 
 * <p>
 * {@link Selector}s are lists of {@link SelectorPart}s. individual {@link Selector}s are separated by commas. For
 * example, in <code>.class, .class #id</code> there are two selectors, <code>.class</code> and <code>.class #id</code>.
 * 
 * @author nmcwilliams
 */
@Subscribable
@Description(broadcasted = AUTOMATIC)
public class Selector extends AbstractGroupable<Selector> implements Refinable<RefinedSelector>, RefinedSelector {
    private final SyntaxCollection<SelectorPart> parts = StandardSyntaxCollection.create();
    private final Broadcaster broadcaster;
    private final RawSyntax rawContent;

    /**
     * Creates a new instance of a {@link Selector} with the given raw content. This selector can be further refined to
     * the individual {@link SelectorPart}s by using {@link #refine()}.
     * 
     * @param rawContent
     *            The selector content.
     * @param broadcaster
     *            The {@link Broadcaster} to use when {@link #refine()} is called.
     */
    public Selector(RawSyntax rawContent, Broadcaster broadcaster) {
        super(rawContent.line(), rawContent.column());
        this.rawContent = rawContent;
        this.broadcaster = broadcaster;
    }

    /**
     * Gets the original, raw, non-validated selector content.
     * 
     * @return The raw selector content.
     */
    public RawSyntax rawContent() {
        return rawContent;
    }

    @Override
    public SyntaxCollection<SelectorPart> parts() {
        return parts;
    }

    @Override
    public RefinedSelector refine() {
        if (parts.isEmpty()) {
            CollectingBroadcaster collector = new CollectingBroadcaster(broadcaster);
            Stream stream = new Stream(rawContent.content(), line(), column());

            // parse the contents
            ParserFactory.refinedSelectorParser().parse(stream, collector);

            // there should be nothing left
            if (!stream.eof()) throw new ParserException(stream, Message.UNPARSABLE_SELECTOR);

            // store the parsed selector parts
            parts.appendAll(collector.filter(SelectorPart.class));
        }

        return this;
    }

    @Override
    protected Selector self() {
        return this;
    }

    @Override
    public String toString() {
        return As.string(this)
            .indent()
            .add("syntax", super.toString())
            .add("raw", rawContent)
            .add("parts", parts())
            .toString();
    }
}
