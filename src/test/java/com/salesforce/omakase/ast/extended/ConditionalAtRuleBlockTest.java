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

import com.google.common.collect.Lists;
import com.salesforce.omakase.ast.Rule;
import com.salesforce.omakase.ast.Statement;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.declaration.KeywordValue;
import com.salesforce.omakase.ast.declaration.PropertyValue;
import com.salesforce.omakase.ast.selector.ClassSelector;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.data.Property;
import com.salesforce.omakase.plugin.conditionals.ConditionalsConfig;
import com.salesforce.omakase.writer.StyleWriter;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests {@link ConditionalAtRuleBlock}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class ConditionalAtRuleBlockTest {
    private static final ConditionalsConfig CONFIG = new ConditionalsConfig().addTrueConditions("ie7", "webkit");
    private static final Conditional IE7 = new Conditional("ie7", false);
    private List<Statement> statements;
    private List<Conditional> conditionals;

    @Before
    public void setup() {
        statements = Lists.newArrayList();
        conditionals = Lists.newArrayList();
    }

    @Test
    public void getConditionals() {
        conditionals.add(IE7);
        ConditionalAtRuleBlock b = new ConditionalAtRuleBlock(-1, -1, conditionals, statements, CONFIG, null);
        assertThat(b.conditionals()).containsExactly(IE7);
    }

    @Test
    public void getStatements() {
        statements.add(new Rule());
        ConditionalAtRuleBlock b = new ConditionalAtRuleBlock(-1, -1, conditionals, statements, CONFIG, null);
        assertThat(b.statements()).hasSize(1);
    }

    @Test
    public void matchesOnlyConditional() {
        conditionals.add(IE7);
        ConditionalAtRuleBlock b = new ConditionalAtRuleBlock(-1, -1, conditionals, statements, CONFIG, null);
        assertThat(b.matches()).isTrue();
    }

    @Test
    public void doesntMatchOnlyConditional() {
        conditionals.add(new Conditional("ie8", false));
        ConditionalAtRuleBlock b = new ConditionalAtRuleBlock(-1, -1, conditionals, statements, CONFIG, null);
        assertThat(b.matches()).isFalse();
    }

    @Test
    public void matchesSubsequentConditional() {
        conditionals.add(new Conditional("ie8", false));
        conditionals.add(IE7);
        conditionals.add(new Conditional("ie9", false));
        ConditionalAtRuleBlock b = new ConditionalAtRuleBlock(-1, -1, conditionals, statements, CONFIG, null);
        assertThat(b.matches()).isTrue();
    }

    @Test
    public void doesntMatchAnyConditional() {
        conditionals.add(new Conditional("ie8", false));
        conditionals.add(new Conditional("ie9", false));
        conditionals.add(new Conditional("ie10", false));
        ConditionalAtRuleBlock b = new ConditionalAtRuleBlock(-1, -1, conditionals, statements, CONFIG, null);
        assertThat(b.matches()).isFalse();
    }

    @Test
    public void matchesNegation() {
        conditionals.add(new Conditional("ie10", true));
        ConditionalAtRuleBlock b = new ConditionalAtRuleBlock(-1, -1, conditionals, statements, CONFIG, null);
        assertThat(b.matches()).isTrue();
    }

    @Test
    public void isWritableMethodWhenMatches() {
        conditionals.add(IE7);
        ConditionalAtRuleBlock b = new ConditionalAtRuleBlock(-1, -1, conditionals, statements, CONFIG, null);
        assertThat(b.isWritable()).isTrue();
    }

    @Test
    public void notWritableMethodWhenNotMatches() {
        conditionals.add(new Conditional("ie8", false));
        conditionals.add(new Conditional("ie9", false));
        ConditionalAtRuleBlock b = new ConditionalAtRuleBlock(-1, -1, conditionals, statements, CONFIG, null);
        assertThat(b.isWritable()).isFalse();
    }

    @Test
    public void isWritableWhenPassthroughMode() {
        ConditionalsConfig config = new ConditionalsConfig().passthroughMode(true);
        conditionals.add(IE7);
        ConditionalAtRuleBlock b = new ConditionalAtRuleBlock(-1, -1, conditionals, statements, config, null);
        assertThat(b.isWritable()).isTrue();
    }

    @Test
    public void writeMethod() throws IOException {
        Rule rule = new Rule(5, 5, new QueryableBroadcaster());
        rule.selectors().append(new Selector(new ClassSelector("test")));
        rule.declarations().append(new Declaration(Property.DISPLAY, KeywordValue.of("none")));
        statements.add(rule);

        conditionals.add(IE7);
        ConditionalAtRuleBlock b = new ConditionalAtRuleBlock(-1, -1, conditionals, statements, CONFIG, null);
        assertThat(StyleWriter.compressed().writeSnippet(b)).isEqualTo(".test{display:none}");
    }

    @Test
    public void writeMethodPassthroughVerbose() throws IOException {
        Rule rule = new Rule(5, 5, new QueryableBroadcaster());
        rule.selectors().append(new Selector(new ClassSelector("test")));
        rule.declarations().append(new Declaration(Property.DISPLAY, KeywordValue.of("none")));
        statements.add(rule);

        conditionals.add(IE7);
        ConditionalsConfig config = new ConditionalsConfig().passthroughMode(true);
        ConditionalAtRuleBlock b = new ConditionalAtRuleBlock(-1, -1, conditionals, statements, config, null);
        assertThat(StyleWriter.verbose().writeSnippet(b)).isEqualTo("@if(ie7) {\n.test {\n  display: none;\n}\n}");
    }

    @Test
    public void writeMethodPassthroughInline() throws IOException {
        Rule rule = new Rule(5, 5, new QueryableBroadcaster());
        rule.selectors().append(new Selector(new ClassSelector("test")));
        rule.declarations().append(new Declaration(Property.DISPLAY, KeywordValue.of("none")));
        statements.add(rule);

        conditionals.add(new Conditional("webkit", false));
        ConditionalsConfig config = new ConditionalsConfig().passthroughMode(true);
        ConditionalAtRuleBlock b = new ConditionalAtRuleBlock(-1, -1, conditionals, statements, config, null);
        assertThat(StyleWriter.inline().writeSnippet(b)).isEqualTo("@if(webkit) {\n.test {display:none}\n}");
    }

    @Test
    public void writeMethodPassthroughCompressed() throws IOException {
        Rule rule = new Rule(5, 5, new QueryableBroadcaster());
        rule.selectors().append(new Selector(new ClassSelector("test")));
        rule.declarations().append(new Declaration(Property.DISPLAY, KeywordValue.of("none")));
        statements.add(rule);

        conditionals.add(IE7);
        ConditionalsConfig config = new ConditionalsConfig().passthroughMode(true);
        ConditionalAtRuleBlock b = new ConditionalAtRuleBlock(-1, -1, conditionals, statements, config, null);
        assertThat(StyleWriter.compressed().writeSnippet(b)).isEqualTo("@if(ie7){.test{display:none}}");
    }

    @Test
    public void writesLogicalOrVerbose() {
        Rule rule = new Rule(5, 5, new QueryableBroadcaster());
        rule.selectors().append(new Selector(new ClassSelector("test")));
        rule.declarations().append(new Declaration(Property.DISPLAY, KeywordValue.of("none")));
        statements.add(rule);

        conditionals.add(IE7);
        conditionals.add(new Conditional("ie8", false));
        conditionals.add(new Conditional("ie9", false));
        ConditionalsConfig config = new ConditionalsConfig().passthroughMode(true);
        ConditionalAtRuleBlock b = new ConditionalAtRuleBlock(-1, -1, conditionals, statements, config, null);
        assertThat(StyleWriter.verbose().writeSnippet(b)).isEqualTo("@if(ie7 || ie8 || ie9) {\n.test {\n  display: none;\n}\n}");
    }

    @Test
    public void writeLogicalOrInline() {
        Rule rule = new Rule(5, 5, new QueryableBroadcaster());
        rule.selectors().append(new Selector(new ClassSelector("test")));
        rule.declarations().append(new Declaration(Property.DISPLAY, KeywordValue.of("none")));
        statements.add(rule);

        conditionals.add(new Conditional("ie7", false));
        conditionals.add(new Conditional("ie10", true));
        ConditionalsConfig config = new ConditionalsConfig().passthroughMode(true);
        ConditionalAtRuleBlock b = new ConditionalAtRuleBlock(-1, -1, conditionals, statements, config, null);
        assertThat(StyleWriter.inline().writeSnippet(b)).isEqualTo("@if(ie7 || !ie10) {\n.test {display:none}\n}");
    }

    @Test
    public void writesLogicalorCompressed() {
        Rule rule = new Rule(5, 5, new QueryableBroadcaster());
        rule.selectors().append(new Selector(new ClassSelector("test")));
        rule.declarations().append(new Declaration(Property.DISPLAY, KeywordValue.of("none")));
        statements.add(rule);

        conditionals.add(IE7);
        conditionals.add(new Conditional("ie8", true));
        conditionals.add(new Conditional("ie9", false));
        ConditionalsConfig config = new ConditionalsConfig().passthroughMode(true);
        ConditionalAtRuleBlock b = new ConditionalAtRuleBlock(-1, -1, conditionals, statements, config, null);
        assertThat(StyleWriter.compressed().writeSnippet(b)).isEqualTo("@if(ie7||!ie8||ie9){.test{display:none}}");
    }

    @Test
    public void propagatesBroadcast() {
        Rule rule = new Rule();
        rule.selectors().append(new Selector(new ClassSelector("test")));
        Declaration d = new Declaration(Property.DISPLAY, PropertyValue.of(KeywordValue.of("none")));
        rule.declarations().append(d);
        statements.add(rule);

        conditionals.add(IE7);
        ConditionalsConfig config = new ConditionalsConfig().passthroughMode(true);
        ConditionalAtRuleBlock b = new ConditionalAtRuleBlock(-1, -1, conditionals, statements, config, null);

        QueryableBroadcaster qb = new QueryableBroadcaster();
        b.propagateBroadcast(qb);
        assertThat(qb.find(Statement.class).get()).isSameAs(rule);
    }

    @Test
    public void testCopy() {
        Rule rule = new Rule(5, 5, new QueryableBroadcaster());
        rule.selectors().append(new Selector(new ClassSelector("test")));
        rule.declarations().append(new Declaration(Property.DISPLAY, KeywordValue.of("none")));
        statements.add(rule);

        conditionals.add(IE7);
        ConditionalsConfig config = new ConditionalsConfig().passthroughMode(true);
        ConditionalAtRuleBlock b = new ConditionalAtRuleBlock(-1, -1, conditionals, statements, config, null);

        ConditionalAtRuleBlock copy = b.copy();
        assertThat(copy.conditionals().get(0).condition()).isEqualTo("ie7");
        assertThat(copy.statements()).hasSize(1);
    }
}
