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

package com.salesforce.omakase.perf;

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
