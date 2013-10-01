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

package com.salesforce.omakase.ast.extended;

import com.salesforce.omakase.ast.AbstractSyntax;
import com.salesforce.omakase.ast.Statement;
import com.salesforce.omakase.ast.Stylesheet;
import com.salesforce.omakase.ast.atrule.AtRuleBlock;
import com.salesforce.omakase.ast.collection.SyntaxCollection;
import com.salesforce.omakase.broadcast.annotation.Description;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.salesforce.omakase.broadcast.BroadcastRequirement.REFINED_AT_RULE;

/**
 * TODO description
 *
 * @author nmcwilliams
 */
@Subscribable
@Description(value = "condtionals", broadcasted = REFINED_AT_RULE)
public class ConditionalAtRuleBlock extends AbstractSyntax implements AtRuleBlock {
    private final SyntaxCollection<Stylesheet, Statement> statements;
    private final Set<String> trueConditions;
    private final String condition;

    public ConditionalAtRuleBlock(Set<String> trueConditions, String condition, SyntaxCollection<Stylesheet, Statement> statements) {
        this.trueConditions = checkNotNull(trueConditions, "trueConditions cannot be null");
        this.condition = checkNotNull(condition, "condition cannot be null");
        this.statements = checkNotNull(statements, "statements cannot be null");
    }

    public String condition() {
        return condition;
    }

    public SyntaxCollection<Stylesheet, Statement> statements() {
        return statements;
    }

    @Override
    public boolean isWritable() {
        return trueConditions.contains(condition);
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        for (Statement statement : statements) {
            writer.write(statement, appendable);
        }
    }
}
