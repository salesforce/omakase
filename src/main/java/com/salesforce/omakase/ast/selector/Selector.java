/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.selector;

import static com.salesforce.omakase.emitter.SubscribableRequirement.AUTOMATIC;

import com.google.common.base.Optional;
import com.salesforce.omakase.As;
import com.salesforce.omakase.Broadcaster;
import com.salesforce.omakase.CollectingBroadcaster;
import com.salesforce.omakase.ast.*;
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
 * example, in
 * 
 * <pre>.class, .class #id</pre>
 * there are two selectors,
 * 
 * <pre>.class</pre>
 * and
 * 
 * <pre>.class #id</pre>
 * 
 * @author nmcwilliams
 */
@Subscribable
@Description(broadcasted = AUTOMATIC)
public class Selector extends AbstractLinkable<Selector> implements Refinable<RefinedSelector>, RefinedSelector {
    private static final String EXPECTED = "Expected to find a selector!";
    private static final String UNRECOGNIZED = "Unable to parse remaining selector content (Check that the selector is valid and is allowed here)";

    private final Broadcaster broadcaster;
    private final RawSyntax rawContent;
    private SelectorPart head;

    /**
     * Creates a new instance of a {@link Selector} with the given raw content. This selector can be further refined to
     * the individual {@link SelectorPart}s by using {@link #refine()}.
     * 
     * @param rawContent
     *            The selector content.
     * @param broadcaster
     *            TODO
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
    public LinkableCollection<SelectorPart> parts() {
        return LinkableCollection.of(head);
    }

    @Override
    public RefinedSelector refine() {
        if (head == null) {
            CollectingBroadcaster collector = new CollectingBroadcaster(broadcaster);
            Stream stream = new Stream(rawContent.content(), line(), column());

            // parse the contents
            ParserFactory.refinedSelectorParser().parse(stream, collector);

            // there should be nothing left
            if (!stream.eof()) throw new ParserException(stream, UNRECOGNIZED);

            // store the parsed selector parts
            Optional<SelectorPart> first = collector.find(SelectorPart.class);
            if (!first.isPresent()) throw new ParserException(stream, EXPECTED);
            head = first.get();
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
