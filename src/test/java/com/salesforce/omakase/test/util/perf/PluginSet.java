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

package com.salesforce.omakase.test.util.perf;

import com.google.common.collect.ImmutableList;
import com.salesforce.omakase.ast.Rule;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.declaration.HexColorValue;
import com.salesforce.omakase.ast.declaration.RawFunction;
import com.salesforce.omakase.ast.selector.IdSelector;
import com.salesforce.omakase.ast.selector.PseudoClassSelector;
import com.salesforce.omakase.broadcast.annotation.Observe;
import com.salesforce.omakase.broadcast.annotation.Validate;
import com.salesforce.omakase.error.ErrorManager;
import com.salesforce.omakase.plugin.Plugin;
import com.salesforce.omakase.plugin.basic.SyntaxTree;
import com.salesforce.omakase.plugin.validator.StandardValidation;

/**
 * Sets of plugins for perf tests.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("ALL")
public final class PluginSet {
    /**
     * common set of plugins simulating real world usage.
     *
     * @return the plugins.
     */
    public static Iterable<Plugin> normal() {
        return ImmutableList.<Plugin>builder()
            .add(new SyntaxTree())
            .add(new StandardValidation())
            .add(new Plugin() {
                @Observe
                public void observe(Declaration d) {}
            })
            .add(new Plugin() {
                @Observe
                public void observe(Declaration d) {}
            })
            .add(new Plugin() {
                @Observe
                public void observe(Declaration d) {}
            })
            .add(new Plugin() {
                @Observe
                public void observe(Declaration d) {}
            })
            .add(new Plugin() {
                @Observe
                public void observe(RawFunction r) {}
            })
            .add(new Plugin() {
                @Observe
                public void observe(HexColorValue h) {}
            })
            .add(new Plugin() {
                @Observe
                public void observe(Rule r) {}
            })
            .add(new Plugin() {
                @Validate
                public void observe(Declaration d, ErrorManager em) {}
            })
            .add(new Plugin() {
                @Validate
                public void observe(PseudoClassSelector s, ErrorManager em) {}
            })
            .add(new Plugin() {
                @Validate
                public void observe(IdSelector s, ErrorManager em) {}
            })
            .add(new Plugin() {
                @Validate
                public void observe(AtRule a, ErrorManager em) {}
            })
            .build();
    }
}
