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

package com.salesforce.omakase.ast.atrule;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.salesforce.omakase.broadcast.BroadcastRequirement.AUTOMATIC;

import java.io.IOException;
import java.util.Optional;

import com.salesforce.omakase.ast.Named;
import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.Refinable;
import com.salesforce.omakase.ast.Statement;
import com.salesforce.omakase.ast.StatementIterable;
import com.salesforce.omakase.ast.Status;
import com.salesforce.omakase.ast.collection.AbstractGroupable;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.annotation.Description;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import com.salesforce.omakase.broadcast.emitter.SubscriptionPhase;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

/**
 * Represents one of the CSS at-rules, such as {@code @media}, {@code @charset}, {@code @keyframes}, etc...
 * 
 * <p>See the notes on {@link Refinable}.</p>
 *
 * @author nmcwilliams
 * @see AtRuleParser
 */
@Subscribable
@Description(broadcasted = AUTOMATIC)
public final class AtRule extends AbstractGroupable<StatementIterable, Statement> implements Statement, Refinable, Named {
    private String name;
    private boolean shouldWriteName = true;
    private boolean isConditional = false;

    // unrefined
    private final RawSyntax rawExpression;
    private final RawSyntax rawBlock;

    // refined
    private AtRuleExpression expression;
    private AtRuleBlock block;

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
     */
    public AtRule(int line, int column, String name, RawSyntax rawExpression, RawSyntax rawBlock) {
        super(line, column);
        this.name = name;
        this.rawExpression = rawExpression;
        this.rawBlock = rawBlock;
        this.expression = null;
        this.block = null;
        status(Status.RAW);
    }

    /**
     * Creates a new instance with no line or column specified (used for dynamically created {@link Syntax} units).
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
        this.rawExpression = null;
        this.rawBlock = null;
        this.expression = expression;
        this.block = block;

        if (expression != null) {
            expression.parent(this);
        }
        if (block != null) {
            block.parent(this);
        }
    }

    /**
     * Sets the at-rule name.
     *
     * @param name
     *     The new name.
     *
     * @return this, for chaining.
     */
    public AtRule name(String name) {
        this.name = checkNotNull(name, "name cannot be null");
        return this;
    }

    @Override
    public String name() {
        return name;
    }

    /**
     * Specifies whether the name should be written out. This might be specified as false by custom refiners where the
     * name of the custom at-rule is not applicable in the final CSS source.
     *
     * @param shouldWriteName
     *     Whether the at-rule name (and @ symbol) should be written out.
     *
     * @return this, for chaining.
     *
     * @see #markAsMetadataRule()
     */
    public AtRule shouldWriteName(boolean shouldWriteName) {
        this.shouldWriteName = shouldWriteName;
        return this;
    }

    /**
     * Gets whether the name of the atRule should be written out. Default is true, but can be modified via {@link
     * #shouldWriteName(boolean)}.
     *
     * @return True if the name of the atRule should be written out.
     */
    public boolean shouldWriteName() {
        return shouldWriteName;
    }

    /**
     * Gets the original, raw, non-validated expression if present (e.g., "utf-8", or "all and (min-width: 800px)".
     *
     * @return The raw expression, or an empty {@link Optional} if not present.
     */
    public Optional<RawSyntax> rawExpression() {
        return Optional.ofNullable(rawExpression);
    }

    /**
     * Gets the original, raw, non-validated at-rule block, if present.
     *
     * @return The at-rule block, or an empty {@link Optional} if not present.
     */
    public Optional<RawSyntax> rawBlock() {
        return Optional.ofNullable(rawBlock);
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
        if (expression == null) {
            checkArgument(block != null, "cannot remove / set a null expression when the block is absent");
        } else {
            expression.parent(this);
        }
        this.expression = expression;
        return this;
    }

    /**
     * Gets the at-rule expression, if present.
     * <p>
     * <b>Important:</b> this may be {@link Optional#empty()} if this at-rule is unrefined.
     *
     * @return The expression, or an empty {@link Optional} if not present.
     */
    public Optional<AtRuleExpression> expression() {
        return Optional.ofNullable(expression);
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
        if (block == null) {
            checkState(expression != null, "cannot remove / set a null block when the expression is absent");
        } else {
            block.parent(this);
        }
        this.block = block;
        return this;
    }

    /**
     * Gets the at-rule block, if present.
     * <p>
     * <b>Important:</b> this may be {@link Optional#empty()} if this at-rule is unrefined.
     *
     * @return The block, or an empty {@link Optional} if not present.
     */
    public Optional<AtRuleBlock> block() {
        return Optional.ofNullable(block);
    }

    /**
     * Used to indicate this at-rule is for metadata purposes only and should not be written out in the output CSS.
     * <p>
     * This is mainly used for custom syntax.
     *
     * @return this, for chaining.
     */
    public AtRule markAsMetadataRule() {
        shouldWriteName(false);
        if (expression == null) {
            expression(MetadataExpression.instance());
        }
        return this;
    }

    @Override
    public boolean isRefined() {
        return expression != null || block != null;
    }
    
    /**
     * Setter to indicate this at rule is a conditional rule ({@code @if}).
     * 
     * @param isConditional True if this rule is conditional.
     * @see #isConditional()
     */
    public void setConditional(final boolean isConditional) {
        this.isConditional = isConditional;
    }
    
    /**
     * Determines if this rule is conditional ({@code @if}) or not.
     * 
     * <p>For more information on conditional at rules, see
     * {@link com.salesforce.omakase.ast.extended.ConditionalAtRuleBlock}.</p>
     * 
     * @return True if this rule is conditional, else false. This class defaults
     *      to false if not explicitly set.
     * @see #setConditional(boolean)
     */
    public boolean isConditional() {
        return isConditional;
    }

    @Override
    public boolean shouldBreakBroadcast(SubscriptionPhase phase) {
        return super.shouldBreakBroadcast(phase) || (phase == SubscriptionPhase.REFINE && isRefined());
    }

    @Override
    protected AtRule self() {
        return this;
    }

    @Override
    public void propagateBroadcast(Broadcaster broadcaster, Status status) {
        if (status() == status) {
            if (expression != null) {
                expression.propagateBroadcast(broadcaster, status);
            }
            if (block != null) {
                block.propagateBroadcast(broadcaster, status);
            }
            super.propagateBroadcast(broadcaster, status);
        }
    }

    @Override
    public boolean isWritable() {
        if (!super.isWritable()) return false;

        if (isRefined()) {
            // this logic is based on the assumed general behavior of not being writable when no contents are writable. For
            // example, if the block is not writable but the expression present and writable, writing out just the expression
            // could result in invalid css.

            // if expression is present, it must be writable
            if (expression != null && !expression.isWritable()) {
                return false;
            }

            // if block is present, it must be writable
            if (block != null && !block.isWritable()) {
                return false;
            }

            // otherwise if we have a writable name, expression or block return true
            return shouldWriteName || expression != null || block != null;
        }
        return true;
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        // newlines (unless first statement)
        if (!writer.isCompressed() && !writer.isFirstAtCurrentDepth()) {
            appendable.newline().newlineIf(writer.isVerbose());
        }

        if (isRefined()) {
            // name
            if (shouldWriteName) {
                appendable.append('@').append(name);
                appendable.spaceIf(expression != null && expression.isWritable());
            }

            // expression
            if (expression != null) {
                writer.writeInner(expression, appendable);
            }

            // block
            if (block != null) {
                writer.writeInner(block, appendable);
            }

        } else {
            // symbol and name
            appendable.append('@').append(name).space();

            // expression
            if (rawExpression != null) {
                writer.writeInner(rawExpression, appendable);
                appendable.spaceIf(rawBlock != null && !writer.isCompressed());
            }

            // block
            if (rawBlock != null) {
                appendable.append('{');
                appendable.indentIf(writer.isVerbose());
                appendable.newlineIf(writer.isVerbose());
                writer.writeInner(rawBlock, appendable);
                appendable.unindentIf(writer.isVerbose());
                appendable.newlineIf(writer.isVerbose());
                appendable.append('}');
            } else {
                appendable.append(';');
            }
        }
    }

    @Override
    public AtRule copy() {
        AtRule copy;

        if (isRefined()) {
            AtRuleExpression expressionCopy = expression != null ? expression.copy() : null;
            AtRuleBlock blockCopy = block != null ? block.copy() : null;
            copy = new AtRule(name, expressionCopy, blockCopy).copiedFrom(this);
        } else {
            RawSyntax expressionCopy = rawExpression != null ? rawExpression.copy() : null;
            RawSyntax blockCopy = rawBlock != null ? rawBlock.copy() : null;
            copy = new AtRule(-1, -1, name, expressionCopy, blockCopy).copiedFrom(this);
        }
        copy.shouldWriteName(shouldWriteName);
        return copy;
    }
}
