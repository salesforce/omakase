/*
 * Copyright (c) 2017, salesforce.com, inc.
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

package com.salesforce.omakase.test.functional;

import com.salesforce.omakase.Omakase;
import com.salesforce.omakase.ast.RawFunction;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.annotation.Refine;
import com.salesforce.omakase.parser.Grammar;
import com.salesforce.omakase.plugin.Plugin;
import com.salesforce.omakase.plugin.core.AutoRefine;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests that name filters work.
 *
 * @author nmcwilliams
 */
@SuppressWarnings({"JavaDoc", "unused"})
public class NameFilteringTest {

    @Test
    public void testRawFunction() {
        String src = ".test { background: foo(url.png) }";

        TestFunction plugin = new TestFunction();

        Omakase.source(src).use(plugin).use(AutoRefine.everything()).process();

        assertThat(plugin.fooCalled).isTrue();
        assertThat(plugin.barCalled).isFalse();
    }

    @Test
    public void testAtRule() {
        String src = "@aFoo test {}";

        TestFunction plugin = new TestFunction();

        Omakase.source(src).use(plugin).use(AutoRefine.everything()).process();

        assertThat(plugin.aFooCalled).isTrue();
        assertThat(plugin.aBarCalled).isFalse();
    }

    @Test
    public void testDeclaration() {
        String src = ".test { dFoo: red }";

        TestFunction plugin = new TestFunction();

        Omakase.source(src).use(plugin).use(AutoRefine.everything()).process();

        assertThat(plugin.dFooCalled).isTrue();
        assertThat(plugin.dBarCalled).isFalse();
    }

    private static final class TestFunction implements Plugin {
        boolean fooCalled;
        boolean barCalled;
        boolean aFooCalled;
        boolean aBarCalled;
        boolean dFooCalled;
        boolean dBarCalled;

        @Refine("foo")
        public void correct(RawFunction f, Grammar grammar, Broadcaster broadcaster) {
            fooCalled = true;
        }

        @Refine("bar")
        public void wrong(RawFunction f, Grammar grammar, Broadcaster broadcaster) {
            barCalled = true;
        }

        @Refine("aFoo")
        public void correct(AtRule a, Grammar grammar, Broadcaster broadcaster) {
            aFooCalled = true;
        }

        @Refine("aBar")
        public void wrong(AtRule a, Grammar grammar, Broadcaster broadcaster) {
            aBarCalled = true;
        }

        @Refine("dFoo")
        public void correct(Declaration d, Grammar grammar, Broadcaster broadcaster) {
            dFooCalled = true;
        }

        @Refine("dBar")
        public void wrong(Declaration d, Grammar grammar, Broadcaster broadcaster) {
            dBarCalled = true;
        }
    }
}

