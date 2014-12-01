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

package com.salesforce.omakase.test.sample;

import com.salesforce.omakase.ast.declaration.AbstractTerm;
import com.salesforce.omakase.ast.declaration.FunctionValue;
import com.salesforce.omakase.ast.declaration.PropertyValue;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;

/**
 * This is a sample custom AST object representing our custom function. The custom function represents a variable lookup.
 * <p/>
 * We extend {@link AbstractTerm} because we want this AST object to fit into {@link PropertyValue}s. We implement {@link
 * FunctionValue} so that, among other reasons, we get properly delivered to {@link FunctionValue} subscription methods.
 *
 * @author nmcwilliams
 */

@Subscribable
@SuppressWarnings("JavaDoc")
public class SampleCustomFunction extends AbstractTerm implements FunctionValue {
    public static final String NAME = "custom-var";
    private final String arg;

    public SampleCustomFunction(String arg) {
        this.arg = arg;
    }

    public String arg() {
        return arg;
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public String textualValue() {
        return arg();
    }

    @Override
    public SampleCustomFunction copy() {
        return new SampleCustomFunction(arg).copiedFrom(this);
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        appendable.append(NAME).append('(').append(arg).append(')');
    }
}
