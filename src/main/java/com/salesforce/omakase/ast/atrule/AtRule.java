/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.atrule;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.salesforce.omakase.emitter.SubscribableRequirement.AUTOMATIC;

import java.io.IOException;
import java.util.List;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.salesforce.omakase.As;
import com.salesforce.omakase.ast.*;
import com.salesforce.omakase.ast.collection.AbstractGroupable;
import com.salesforce.omakase.broadcaster.Broadcaster;
import com.salesforce.omakase.emitter.Description;
import com.salesforce.omakase.emitter.Subscribable;
import com.salesforce.omakase.parser.raw.RawAtRuleParser;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

/**
 * TESTME Represents one of the CSS at-rules, such as {@literal @}media, {@literal @}charset, {@literal @}keyframes,
 * etc...
 * 
 * <p>
 * It's important to note that the raw members may contain grammatically incorrect CSS. Refining the object will perform
 * basic grammar validation. See the notes on {@link Refinable}.
 * 
 * @see RawAtRuleParser
 * 
 * @author nmcwilliams
 */
@Subscribable
@Description(broadcasted = AUTOMATIC)
public class AtRule extends AbstractGroupable<Statement> implements Statement, Refinable<AtRule>, Commentable {
    private String name;
    private List<String> comments;

    // unrefined
    private final Optional<RawSyntax> rawExpression;
    private final Optional<RawSyntax> rawBlock;

    // refined
    private Optional<AtRuleExpression> expression;
    private Optional<AtRuleBlock> block;

    /**
     * Constructs a new {@link AtRule} instance.
     * 
     * @param line
     *            The line number.
     * @param column
     *            The column number.
     * @param name
     *            Name of the at-rule.
     * @param rawExpression
     *            The raw at-rule expression. If no expression is present pass in null.
     * @param rawBlock
     *            The raw at-rule block. If no block is present pass in null.
     * @param broadcaster
     *            The {@link Broadcaster} to use when {@link #refine()} is called.
     */
    public AtRule(int line, int column, String name, RawSyntax rawExpression, RawSyntax rawBlock, Broadcaster broadcaster) {
        super(line, column, broadcaster);

        this.name = name;
        this.rawExpression = Optional.fromNullable(rawExpression);
        this.rawBlock = Optional.fromNullable(rawBlock);
    }

    /**
     * TODO
     * 
     * @param name
     *            TODO
     * @param expression
     *            TODO
     * @param block
     *            TODO
     */
    public AtRule(String name, AtRuleExpression expression, AtRuleBlock block) {
        super(-1, -1);

        checkNotNull(name, "name cannot be  null");
        checkArgument(expression != null || block != null, "either the expression or the block must be present");

        this.name = name;
        this.rawExpression = Optional.absent();
        this.rawBlock = Optional.absent();
        this.expression = Optional.fromNullable(expression);
        this.block = Optional.fromNullable(block);
    }

    /**
     * Gets the name of this {@link AtRule}.
     * 
     * @return The name.
     */
    public String name() {
        return name;
    }

    /**
     * Gets the original, raw, non-validated expression if present (e.g., "utf-8", or "all and (min-width: 800px)".
     * 
     * @return The raw expression, or {@link Optional#absent()} if not present.
     */
    public Optional<RawSyntax> rawExpression() {
        return rawExpression;
    }

    /**
     * Gets the original, raw, non-validated at-rule block, if present.
     * 
     * @return The at-rule block, or {@link Optional#absent()} if not present.
     */
    public Optional<RawSyntax> rawBlock() {
        return rawBlock;
    }

    /**
     * Gets the at-rule expression, if present.
     * 
     * @return The expression, or {@link Optional#absent()} if not present.
     */
    public Optional<AtRuleExpression> expression() {
        return expression;
    }

    /**
     * Gets the at-rule block, if present.
     * 
     * @return The block, or {@link Optional#absent()} if not present.
     */
    public Optional<AtRuleBlock> block() {
        return block;
    }

    @Override
    public boolean isRefined() {
        return expression != null || block != null;
    }

    @Override
    public AtRule refine() {
        if (!isRefined()) {
            // TODO refinement
        }

        return this;
    }

    @Override
    public AtRule comments(Iterable<String> commentsToAdd) {
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
    protected Statement self() {
        return this;
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        if (isRefined()) {
            appendable.append('@');
            appendable.append(name);

        } else {
            // symbol and name
            appendable.append('@');
            appendable.append(name);
            appendable.space();

            // TODO util.compression

            // expression
            if (rawExpression.isPresent()) {
                writer.write(rawExpression.get(), appendable);
            }

            // block
            if (rawBlock.isPresent()) {
                appendable.spaceIf(!writer.isCompressed());
                appendable.append('{');
                appendable.newlineIf(writer.isVerbose());
                appendable.indentIf(writer.isVerbose());
                writer.write(rawBlock.get(), appendable);
                appendable.newlineIf(writer.isVerbose());
                appendable.append('}');
            }

            // newlines (unless last statement)
            if (!writer.isCompressed() && !isLast()) {
                appendable.newline();
                appendable.newlineIf(writer.isVerbose());
            }
        }
    }

    @Override
    public String toString() {
        return As.string(this)
            .indent()
            .add("position", super.toString())
            .add("comments", comments)
            .add("rawExpression", rawExpression)
            .add("rawBlock", rawBlock)
            .add("expression", expression)
            .add("block", block)
            .toString();
    }
}
