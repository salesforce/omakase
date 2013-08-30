/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.selector;

import static com.salesforce.omakase.emitter.SubscribableRequirement.AUTOMATIC;

import java.io.IOException;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.salesforce.omakase.As;
import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.Commentable;
import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.Refinable;
import com.salesforce.omakase.ast.collection.AbstractGroupable;
import com.salesforce.omakase.ast.collection.StandardSyntaxCollection;
import com.salesforce.omakase.ast.collection.SyntaxCollection;
import com.salesforce.omakase.broadcaster.Broadcaster;
import com.salesforce.omakase.broadcaster.QueryableBroadcaster;
import com.salesforce.omakase.emitter.Description;
import com.salesforce.omakase.emitter.Subscribable;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.ParserFactory;
import com.salesforce.omakase.parser.Stream;
import com.salesforce.omakase.parser.selector.ComplexSelectorParser;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

/**
 * TESTME Represents a CSS selector.
 * 
 * <p>
 * {@link Selector}s are lists of {@link SelectorPart}s. Individual {@link Selector}s are separated by commas. For
 * example, in <code>.class, .class #id</code> there are two selectors, <code>.class</code> and <code>.class #id</code>.
 * 
 * @see ComplexSelectorParser
 * 
 * @author nmcwilliams
 */
@Subscribable
@Description(broadcasted = AUTOMATIC)
public class Selector extends AbstractGroupable<Selector> implements Refinable<RefinedSelector>, RefinedSelector, Commentable {
    private final SyntaxCollection<SelectorPart> parts = StandardSyntaxCollection.create();
    private final Broadcaster broadcaster;
    private final RawSyntax rawContent;

    private List<String> comments;

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
    public boolean isRefined() {
        return !parts.isEmpty();
    }

    @Override
    public RefinedSelector refine() {
        if (!isRefined()) {
            QueryableBroadcaster qb = new QueryableBroadcaster(broadcaster);
            Stream stream = new Stream(rawContent.content(), line(), column()).skipInStringCheck();

            // parse the contents
            ParserFactory.complexSelectorParser().parse(stream, qb);

            // there should be nothing left
            if (!stream.eof()) throw new ParserException(stream, Message.UNPARSABLE_SELECTOR);

            // store the parsed selector parts
            parts.appendAll(qb.filter(SelectorPart.class));
        }

        return this;
    }

    @Override
    public Selector comments(Iterable<String> commentsToAdd) {
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
    protected Selector self() {
        return this;
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        if (isRefined()) {
            for (SelectorPart part : parts) {
                writer.write(part, appendable);
            }
        } else {
            writer.write(rawContent, appendable);
        }
    }

    @Override
    public String toString() {
        return As.string(this)
            .indent()
            .add("position", super.toString())
            .add("comments", comments)
            .add("raw", rawContent)
            .add("parts", parts())
            .toString();
    }
}
