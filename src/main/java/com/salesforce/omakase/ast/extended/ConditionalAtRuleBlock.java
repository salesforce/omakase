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

package com.salesforce.omakase.ast.extended;

import com.google.common.collect.ImmutableList;
import com.salesforce.omakase.ast.Statement;
import com.salesforce.omakase.ast.StatementIterable;
import com.salesforce.omakase.ast.atrule.AbstractAtRuleMember;
import com.salesforce.omakase.ast.atrule.AtRuleBlock;
import com.salesforce.omakase.ast.collection.LinkedSyntaxCollection;
import com.salesforce.omakase.ast.collection.SyntaxCollection;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.annotation.Description;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import com.salesforce.omakase.plugin.conditionals.Conditionals;
import com.salesforce.omakase.plugin.conditionals.ConditionalsConfig;
import com.salesforce.omakase.plugin.conditionals.ConditionalsRefiner;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.salesforce.omakase.broadcast.BroadcastRequirement.REFINED_AT_RULE;

/**
 * An extension to the standard CSS syntax that allows for conditional at-rules.
 * <p/>
 * Example of a conditional at-rule:
 * <pre>
 * {@code @}if(ie7) { .test{color:red} }
 * </pre>
 * <p/>
 * This block will output its inner statements if its condition is contained within the set of "true" condition strings, as
 * specified by a {@link ConditionalsConfig}. Negation may be specified on conditions using <code>!</code>. Multiple conditions
 * may be specified using <code>||</code>.
 * <p/>
 * For more information on using and configuring conditionals see the main readme file.
 *
 * @author nmcwilliams
 * @see Conditionals
 * @see ConditionalsRefiner
 */
@Subscribable
@Description(value = "conditionals", broadcasted = REFINED_AT_RULE)
public final class ConditionalAtRuleBlock extends AbstractAtRuleMember implements AtRuleBlock {
    private final ImmutableList<Conditional> conditionals;
    private final SyntaxCollection<StatementIterable, Statement> statements;
    private final ConditionalsConfig config;

    /**
     * Creates a new {@link ConditionalAtRuleBlock} instance with the given conditions, statements and config object.
     * <p/>
     * The given config contains the set of strings that are the "true" values/conditions. During output of the CSS source, this
     * block and its contents will only be written out if its condition matches what is in the config (or vice versa if the
     * negation operator is used).
     * <p/>
     * Note that this matching is case-sensitive. It is highly recommended to enforce a single case (e.g., lower-case) among the
     * given conditions and set of true conditions in order to ensure matching works properly (by default the various conditionals
     * plugin classes do this automatically).
     * <p/>
     * It is acceptable for the given config to change its set of true conditions, allowing the outputting of multiple variations
     * of the CSS source from a single parse operation. For example, set the true conditions, write out the source, change the
     * true conditions, write out the source again, etc.
     *
     * @param line
     *     The line number.
     * @param column
     *     The column number.
     * @param config
     *     The {@link ConditionalsConfig} instance.
     * @param conditionals
     *     The  list of conditionals.
     * @param statements
     *     The inner statements of the block. These will be printed out if the condition is contained within the trueConditions
     *     set.
     * @param broadcaster
     *     The {@link Broadcaster} to use for broadcasting new units.
     */
    public ConditionalAtRuleBlock(int line, int column, Iterable<Conditional> conditionals, Iterable<Statement> statements,
        ConditionalsConfig config, Broadcaster broadcaster) {
        super(line, column);
        this.config = checkNotNull(config, "config cannot be null");
        this.conditionals = ImmutableList.copyOf(checkNotNull(conditionals, "conditionals cannot be null"));
        this.statements = new LinkedSyntaxCollection<StatementIterable, Statement>(this, broadcaster);
        this.statements.appendAll(statements);
    }

    /**
     * Gets the list of {@link Conditionals} specified as arguments to this block. There may be more than one if the
     * <code>||</code> operator was used.
     *
     * @return The list of conditionals.
     */
    public ImmutableList<Conditional> conditionals() {
        return conditionals;
    }

    /**
     * Returns true if at least one of the {@link Conditionals} in this block matches any of the true conditions in the config at
     * the time of this method call.
     *
     * @return True if the condition of this block evaluates to true.
     */
    public boolean matches() {
        for (Conditional conditional : conditionals) {
            if (conditional.matches(config)) return true;
        }
        return false;
    }

    @Override
    public SyntaxCollection<StatementIterable, Statement> statements() {
        return statements;
    }

    @Override
    public Iterator<Statement> iterator() {
        return statements.iterator();
    }

    @Override
    public boolean isWritable() {
        return super.isWritable() && (config.isPassthroughMode() || matches());
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        if (config.isPassthroughMode()) {
            appendable.append("@if(");
            writer.incrementDepth();
            boolean isFirst = true;
            for (Conditional conditional : conditionals) {
                if (!isFirst) {
                    appendable.spaceIf(!writer.isCompressed());
                    appendable.append("||");
                    appendable.spaceIf(!writer.isCompressed());
                }
                writer.writeInner(conditional, appendable);
                isFirst = false;
            }
            writer.decrementDepth();
            appendable.append(')');

            appendable.spaceIf(!writer.isCompressed());
            appendable.append('{');
            appendable.newlineIf(!writer.isCompressed());
        }

        for (Statement statement : statements) {
            writer.writeInner(statement, appendable);
        }

        if (config.isPassthroughMode()) {
            appendable.newlineIf(!writer.isCompressed());
            appendable.append('}');
        }
    }

    @Override
    public void propagateBroadcast(Broadcaster broadcaster) {
        super.propagateBroadcast(broadcaster);
        statements.propagateBroadcast(broadcaster);
    }

    @Override
    public ConditionalAtRuleBlock copy() {
        List<Statement> copiedStatements = new ArrayList<>();
        for (Statement statement : statements) {
            copiedStatements.add(statement.copy());
        }
        return new ConditionalAtRuleBlock(-1, -1, conditionals, copiedStatements, config, null).copiedFrom(this);
    }
}
