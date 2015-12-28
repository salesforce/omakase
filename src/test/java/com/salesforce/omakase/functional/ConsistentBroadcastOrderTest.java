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

package com.salesforce.omakase.functional;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.salesforce.omakase.Omakase;
import com.salesforce.omakase.ast.Statement;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.broadcast.annotation.Observe;
import com.salesforce.omakase.broadcast.annotation.Rework;
import com.salesforce.omakase.plugin.Plugin;
import com.salesforce.omakase.plugin.basic.AutoRefiner;
import org.junit.Test;

import java.util.List;
import java.util.Set;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests that the order of broadcasts for dynamically created units is the same as the order of broadcasts for parsed units.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class ConsistentBroadcastOrderTest {
    public static final String SRC = "@media all and (min-width: 300px) {\n" +
        "    #id, .class {\n" +
        "        color: red;\n" +
        "        margin: 10px;\n" +
        "    }\n" +
        "}";

    @Test
    public void testConsistentOrdering() throws Exception {
        final List<Syntax> parsed = Lists.newArrayList();
        final List<Syntax> copied = Lists.newArrayList();

        Omakase.source(SRC)
            .use(new AutoRefiner().all())
            .use(new Plugin() {
                private boolean madeCopy;

                @Observe
                public void observe(Syntax syntax) {
                    if (!madeCopy) {
                        parsed.add(syntax);
                    } else {
                        copied.add(syntax);
                    }

                    if (syntax.getClass() == AtRule.class) {
                        madeCopy = true;
                    }
                }
            })
            .use(new Plugin() {
                private final Set<Statement> copies = Sets.newHashSet();

                @Rework
                public void rework(AtRule atRule) {
                    if (copies.contains(atRule)) return;

                    Statement copy = atRule.copy();
                    copies.add(copy);
                    atRule.prepend(copy);
                }
            })
            .process();

        for (int i = 0; i < parsed.size(); i++) {
            assertThat(copied.get(i)).hasSameClassAs(parsed.get(i));
        }
    }
}
