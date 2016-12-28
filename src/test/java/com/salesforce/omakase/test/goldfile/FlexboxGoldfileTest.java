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

package com.salesforce.omakase.test.goldfile;

import com.google.common.collect.ImmutableSet;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.annotation.Refine;
import com.salesforce.omakase.data.Browser;
import com.salesforce.omakase.data.Property;
import com.salesforce.omakase.parser.Grammar;
import com.salesforce.omakase.plugin.Plugin;
import com.salesforce.omakase.plugin.prefixer.Prefixer;
import com.salesforce.omakase.plugin.syntax.DeclarationPlugin;

/**
 * Goldfile auto-prefixing flexbox.
 *
 * @author nmcwilliams
 */
public class FlexboxGoldfileTest extends AbstractGoldfileTest {
    @Override
    public String name() {
        return "flexbox";
    }

    @Override
    protected Iterable<Plugin> plugins() {
        Prefixer prefixer = Prefixer.customBrowserSupport();
        prefixer.support().all(Browser.FIREFOX);
        prefixer.support().all(Browser.IE);
        prefixer.support().all(Browser.CHROME);
        prefixer.support().all(Browser.SAFARI);
        prefixer.support().all(Browser.IOS_SAFARI);
        prefixer.support().all(Browser.OPERA);
        prefixer.rearrange(true);

        // when auto refine is false, only these declarations should get prefixed
        Plugin singleRefine = new Plugin() {
            @Refine
            public void refine(Declaration declaration, Grammar refiner, Broadcaster broadcaster) {
                if (declaration.isProperty(Property.ALIGN_ITEMS)) {
                    DeclarationPlugin.delegateRefinement(declaration, refiner, broadcaster);
                }
            }
        };

        return ImmutableSet.of(singleRefine, prefixer);
    }
}
