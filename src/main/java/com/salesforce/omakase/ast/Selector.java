/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import com.google.common.base.Optional;
import com.salesforce.omakase.*;
import com.salesforce.omakase.emitter.Subscribable;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.RefinedSelectorParser;
import com.salesforce.omakase.parser.Stream;

/**
 * Represents a CSS selector.
 * 
 * <p>
 * {@link Selector}s are lists of {@link SelectorPart}s. individual {@link Selector}s are separated by commas. For
 * example, in
 * 
 * <pre>.class, .class #id</pre>
 * 
 * there are two selectors,
 * 
 * <pre>.class</pre>
 * 
 * and
 * 
 * <pre>.class #id</pre>
 * 
 * @author nmcwilliams
 */
@Subscribable
public class Selector extends AbstractLinkableSyntax<Selector> implements Refinable<RefinedSelector>, RefinedSelector {
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
            new RefinedSelectorParser().parse(stream, collector);
            if (!stream.eof()) throw new ParserException(stream, "invalid content remaining in the selector");
            Optional<SelectorPart> first = collector.find(SelectorPart.class);
            if (!first.isPresent()) throw new ParserException(stream, "does not contain any selectors!");
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
