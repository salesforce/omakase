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

package com.salesforce.omakase.test.sample.customselector;

import com.salesforce.omakase.ast.selector.AbstractSelectorPart;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.ast.selector.SelectorPartType;
import com.salesforce.omakase.ast.selector.SimpleSelector;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The custom AST object representing our placeholder selector.
 *
 * @author nmcwilliams
 */
@Subscribable
@SuppressWarnings("JavaDoc")
public class PlaceholderSelector extends AbstractSelectorPart implements SimpleSelector {
    private final String name;
    private final List<Selector> references = new ArrayList<>();

    public PlaceholderSelector(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    public PlaceholderSelector addReference(Selector selector) {
        references.add(selector);
        return this;
    }

    public List<Selector> references() {
        return references;
    }

    @Override
    public SelectorPartType type() {
        return SelectorPartType.CUSTOM;
    }

    @Override
    public PlaceholderSelector copy() {
        return new PlaceholderSelector(name);
    }

    @Override
    public boolean isWritable() {
        return super.isWritable() && !references.isEmpty();
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        for (Selector selector : references) {
            writer.writeInner(selector, appendable);
        }
    }
}
