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

package com.salesforce.omakase.ast.atrule;

import com.google.common.base.Optional;
import com.salesforce.omakase.As;
import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.Refinable;
import com.salesforce.omakase.ast.Rule;
import com.salesforce.omakase.ast.Statement;
import com.salesforce.omakase.ast.Stylesheet;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.collection.AbstractGroupable;
import com.salesforce.omakase.broadcast.annotation.Description;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import com.salesforce.omakase.parser.refiner.Refiner;
import com.salesforce.omakase.parser.raw.RawAtRuleParser;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;

import static com.google.common.base.Preconditions.*;
import static com.salesforce.omakase.broadcast.BroadcastRequirement.AUTOMATIC;

/**
 * Represents one of the CSS at-rules, such as {@literal @}media, {@literal @}charset, {@literal @}keyframes, etc...
 * <p/>
 * It's important to note that the raw members may contain grammatically incorrect CSS. Refining the object will perform basic
 * grammar validation. See the notes on {@link Refinable}.
 *
 * @author nmcwilliams
 * @see RawAtRuleParser
 */
@Subscribable
@Description(broadcasted = AUTOMATIC)
public class AtRule extends AbstractGroupable<Stylesheet, Statement> implements Statement, Refinable<AtRule> {
    private final Refiner refiner;
    private final String name;

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
     *     The line number.
     * @param column
     *     The column number.
     * @param name
     *     Name of the at-rule.
     * @param rawExpression
     *     The raw at-rule expression. If no expression is present pass in null.
     * @param rawBlock
     *     The raw at-rule block. If no block is present pass in null.
     * @param refiner
     *     The {@link Refiner} to be used later during refinement of this object.
     */
    public AtRule(int line, int column, String name, RawSyntax rawExpression, RawSyntax rawBlock, Refiner refiner) {
        super(line, column);
        this.name = name;
        this.rawExpression = Optional.fromNullable(rawExpression);
        this.rawBlock = Optional.fromNullable(rawBlock);
        this.expression = Optional.absent();
        this.block = Optional.absent();
        this.refiner = refiner;
    }

    /**
     * Creates a new instance with no line or number specified (used for dynamically created {@link Syntax} units).
     *
     * @param name
     *     Name of the at-rule.
     * @param expression
     *     The at-rule's expression, or null if not present.
     * @param block
     *     The at-rule's block, or null if not present.
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
        this.refiner = null;
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
     * Sets the {@link AtRuleExpression}.
     *
     * @param expression
     *     The expression.
     *
     * @return this, for chaining.
     */
    public AtRule expression(AtRuleExpression expression) {
        checkState(expression != null || block != null, "either the expression or the block must be present");
        this.expression = Optional.fromNullable(expression);
        return this;
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
     * Sets the {@link AtRuleBlock}.
     *
     * @param block
     *     The block.
     *
     * @return this, for chaining.
     */
    public AtRule block(AtRuleBlock block) {
        checkState(expression != null || block != null, "either the expression or the block must be present");
        this.block = Optional.fromNullable(block);
        return this;
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
        return expression.isPresent() || block.isPresent();
    }

    @Override
    public AtRule refine() {
        if (!isRefined() && refiner != null) {
            refiner.refine(this);
        }

        return this;
    }

    @Override
    public Optional<Rule> asRule() {
        return Optional.absent();
    }

    @Override
    public Optional<AtRule> asAtRule() {
        return Optional.of(this);
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

            if (expression.isPresent()) {
                writer.write(expression.get(), appendable);
            }

            if (block.isPresent()) {
                appendable.spaceIf(!writer.isCompressed());
                appendable.append('{');
                appendable.newlineIf(writer.isVerbose());
                appendable.indentIf(writer.isVerbose());
                writer.write(block.get(), appendable);
                appendable.newlineIf(writer.isVerbose());
                appendable.append('}');
            }

        } else {
            // symbol and name
            appendable.append('@');
            appendable.append(name);
            appendable.space();

            // TODO compression

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
        }

        // newlines (unless last statement)
        if (!writer.isCompressed() && !isLast()) {
            appendable.newline();
            appendable.newlineIf(writer.isVerbose());
        }
    }

    @Override
    public String toString() {
        return As.string(this)
            .indent()
            .add("abstract", super.toString())
            .add("rawExpression", rawExpression)
            .add("rawBlock", rawBlock)
            .add("expression", expression)
            .add("block", block)
            .toString();
    }
}
