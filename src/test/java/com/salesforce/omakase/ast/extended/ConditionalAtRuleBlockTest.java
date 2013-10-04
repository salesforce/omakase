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

import com.google.common.collect.Sets;
import com.salesforce.omakase.ast.Rule;
import com.salesforce.omakase.ast.Statement;
import com.salesforce.omakase.ast.Stylesheet;
import com.salesforce.omakase.ast.collection.StandardSyntaxCollection;
import com.salesforce.omakase.ast.collection.SyntaxCollection;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.declaration.Property;
import com.salesforce.omakase.ast.declaration.value.KeywordValue;
import com.salesforce.omakase.ast.selector.ClassSelector;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.test.util.Util;
import com.salesforce.omakase.writer.StyleWriter;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Set;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests {@link ConditionalAtRuleBlock}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class ConditionalAtRuleBlockTest {
    private static final Set<String> TRUE_CONDITIONS = Sets.newHashSet("ie7", "webkit");
    private SyntaxCollection<Stylesheet, Statement> statements;

    @Before
    public void setup() {
        statements = StandardSyntaxCollection.create(new Stylesheet());
    }

    @Test
    public void getCondition() {
        ConditionalAtRuleBlock b = new ConditionalAtRuleBlock(TRUE_CONDITIONS, "ie7", statements);
        assertThat(b.condition()).isEqualTo("ie7");
    }

    @Test
    public void getStatements() {
        ConditionalAtRuleBlock b = new ConditionalAtRuleBlock(TRUE_CONDITIONS, "ie7", statements);
        assertThat(b.statements()).isSameAs(statements);
    }

    @Test
    public void isWritableMethodWhenMatches() {
        ConditionalAtRuleBlock b = new ConditionalAtRuleBlock(TRUE_CONDITIONS, "ie7", statements);
        assertThat(b.isWritable()).isTrue();
    }

    @Test
    public void isWritableMethodWhenNotMatches() {
        ConditionalAtRuleBlock b = new ConditionalAtRuleBlock(TRUE_CONDITIONS, "ie8", statements);
        assertThat(b.isWritable()).isFalse();
    }

    @Test
    public void writeMethod() throws IOException {
        Rule rule = new Rule(5, 5, new QueryableBroadcaster());
        rule.selectors().append(new Selector(new ClassSelector("test")));
        rule.declarations().append(new Declaration(Property.DISPLAY, KeywordValue.of("none")));
        statements.append(rule);

        ConditionalAtRuleBlock b = new ConditionalAtRuleBlock(TRUE_CONDITIONS, "ie7", statements);
        assertThat(StyleWriter.compressed().writeSnippet(b)).isEqualTo(".test{display:none}");
    }

    @Test
    public void toStringTest() {
        ConditionalAtRuleBlock b = new ConditionalAtRuleBlock(TRUE_CONDITIONS, "ie7", statements);
        assertThat(b.toString()).isNotEqualTo(Util.originalToString(b));
    }
}
