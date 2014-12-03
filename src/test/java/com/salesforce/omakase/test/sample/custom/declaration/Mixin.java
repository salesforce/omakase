/*
 * Copyright (C) 2014 salesforce.com, inc.
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

package com.salesforce.omakase.test.sample.custom.declaration;

import com.google.common.collect.ImmutableList;
import com.salesforce.omakase.ast.atrule.AbstractAtRuleMember;
import com.salesforce.omakase.ast.atrule.AtRuleExpression;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The custom AST object representing our mixin definition.
 * <p/>
 * Specifically this is a custom {@link AtRuleExpression} that gets placed into the at-rule (it's not the at-rule itself).
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class Mixin extends AbstractAtRuleMember implements AtRuleExpression {
    private final String name;
    private final List<String> params;
    private final List<Declaration> declarations;

    public Mixin(String name, Iterable<String> params, Iterable<Declaration> declarations) {
        this.name = checkNotNull(name, "name cannot be null");
        this.params = ImmutableList.copyOf(params);
        this.declarations = ImmutableList.copyOf(declarations);
    }

    public String name() {
        return name;
    }

    public List<String> params() {
        return params;
    }

    public List<Declaration> declarations() {
        return declarations;
    }

    @Override
    public Mixin copy() {
        return null;
    }

    @Override
    public boolean isWritable() {
        return false;
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        throw new UnsupportedOperationException("write not supported");
    }
}
