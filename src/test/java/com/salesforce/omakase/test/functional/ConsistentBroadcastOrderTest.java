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

package com.salesforce.omakase.test.functional;

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
            .request(new AutoRefiner().all())
            .request(new Plugin() {
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
            .request(new Plugin() {
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
