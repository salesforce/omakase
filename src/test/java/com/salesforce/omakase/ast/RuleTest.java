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

package com.salesforce.omakase.ast;

import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.declaration.Property;
import com.salesforce.omakase.ast.declaration.value.Keyword;
import com.salesforce.omakase.ast.declaration.value.KeywordValue;
import com.salesforce.omakase.ast.declaration.value.NumericalValue;
import com.salesforce.omakase.ast.selector.ClassSelector;
import com.salesforce.omakase.ast.selector.IdSelector;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.test.util.Util;
import com.salesforce.omakase.writer.StyleWriter;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;

import static org.fest.assertions.api.Assertions.assertThat;

/** Unit tests for Rule. */
@SuppressWarnings("JavaDoc")
public class RuleTest {
    @org.junit.Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void canAddSelector() {
        Rule rule = new Rule();
        assertThat(rule.selectors()).isEmpty();
        rule.selectors().append(new Selector(new ClassSelector("name")));
        assertThat(rule.selectors()).hasSize(1);
    }

    @Test
    public void canAddDeclaration() {
        Rule rule = new Rule();
        assertThat(rule.declarations()).isEmpty();
        rule.declarations().append(new Declaration(Property.DISPLAY, KeywordValue.of(Keyword.NONE)));
        assertThat(rule.declarations()).hasSize(1);
    }

    @Test
    public void orphanedCommentsAbsent() {
        Rule rule = new Rule();
        assertThat(rule.orphanedComments()).isEmpty();
    }

    @Test
    public void orphanedCommentsPresent() {
        Rule rule = new Rule();
        rule.orphanedComment(new Comment("test"));
        assertThat(rule.orphanedComments()).hasSize(1);
    }

    @Test
    public void asRuleAlwaysPresent() {
        Rule rule = new Rule();
        assertThat(rule.asRule().isPresent()).isTrue();
    }

    @Test
    public void asAtRuleAlwaysAbsent() {
        Rule rule = new Rule();
        assertThat(rule.asAtRule().isPresent()).isFalse();
    }

    @Test
    public void writeVerbose() throws IOException {
        Rule rule = new Rule();
        rule.selectors().append(new Selector(new ClassSelector("class")));
        rule.selectors().append(new Selector(new IdSelector("id")));
        rule.declarations().append(new Declaration(Property.DISPLAY, KeywordValue.of(Keyword.NONE)));
        rule.declarations().append(new Declaration(Property.MARGIN, NumericalValue.of(5, "px")));

        StyleWriter writer = StyleWriter.verbose();

        assertThat(writer.writeSnippet(rule)).isEqualTo(".class, #id {\n  display: none;\n  margin: 5px;\n}");
    }

    @Test
    public void writeInline() throws IOException {
        Rule rule = new Rule();
        rule.selectors().append(new Selector(new ClassSelector("class")));
        rule.selectors().append(new Selector(new IdSelector("id")));
        rule.declarations().append(new Declaration(Property.DISPLAY, KeywordValue.of(Keyword.NONE)));
        rule.declarations().append(new Declaration(Property.MARGIN, NumericalValue.of(5, "px")));

        StyleWriter writer = StyleWriter.inline();

        assertThat(writer.writeSnippet(rule)).isEqualTo(".class, #id {display:none; margin:5px}");
    }

    @Test
    public void writeCompressed() throws IOException {
        Rule rule = new Rule();
        rule.selectors().append(new Selector(new ClassSelector("class")));
        rule.selectors().append(new Selector(new IdSelector("id")));
        rule.declarations().append(new Declaration(Property.DISPLAY, KeywordValue.of(Keyword.NONE)));
        rule.declarations().append(new Declaration(Property.MARGIN, NumericalValue.of(5, "px")));

        StyleWriter writer = StyleWriter.compressed();

        assertThat(writer.writeSnippet(rule)).isEqualTo(".class,#id{display:none;margin:5px}");
    }

    @Test
    public void writeWhenDetached() throws IOException {
        Stylesheet stylesheet = new Stylesheet();

        Rule rule = new Rule();
        rule.selectors().append(new Selector(new ClassSelector("class")));
        rule.selectors().append(new Selector(new IdSelector("id")));
        rule.declarations().append(new Declaration(Property.DISPLAY, KeywordValue.of(Keyword.NONE)));
        rule.declarations().append(new Declaration(Property.MARGIN, NumericalValue.of(5, "px")));
        stylesheet.append(rule);

        rule.detach();

        StyleWriter writer = StyleWriter.verbose();
        assertThat(writer.writeSnippet(stylesheet)).isEqualTo("");
    }

    @Test
    public void writeWhenAllSelectorsAreDetached() throws IOException {
        Stylesheet stylesheet = new Stylesheet();

        Rule rule = new Rule();
        Selector selector = new Selector(new ClassSelector("class"));
        rule.selectors().append(selector);
        rule.declarations().append(new Declaration(Property.DISPLAY, KeywordValue.of(Keyword.NONE)));
        stylesheet.append(rule);

        selector.detach();

        StyleWriter writer = StyleWriter.verbose();
        assertThat(writer.writeSnippet(stylesheet)).isEqualTo("");
    }

    @Test
    public void writeWhenNoDeclarations() throws IOException {
        Stylesheet stylesheet = new Stylesheet();

        Rule rule = new Rule();
        rule.selectors().append(new Selector(new ClassSelector("class")));
        rule.selectors().append(new Selector(new IdSelector("id")));
        stylesheet.append(rule);

        StyleWriter writer = StyleWriter.verbose();
        assertThat(writer.writeSnippet(stylesheet)).isEqualTo("");
    }

    @Test
    public void writeVerboseWhenAllDeclarationsDetached() throws IOException {
        Stylesheet stylesheet = new Stylesheet();

        Rule rule = new Rule();
        rule.selectors().append(new Selector(new ClassSelector("class")));
        Declaration declaration = new Declaration(Property.MARGIN, NumericalValue.of(5, "px"));
        rule.declarations().append(declaration);
        stylesheet.append(rule);

        declaration.detach();

        StyleWriter writer = StyleWriter.verbose();
        assertThat(writer.writeSnippet(stylesheet)).isEqualTo("");
    }

    @Test
    public void toStringTest() {
        Rule rule = new Rule();
        rule.selectors().append(new Selector(new ClassSelector("class")));
        Declaration declaration = new Declaration(Property.MARGIN, NumericalValue.of(5, "px"));
        rule.declarations().append(declaration);
        assertThat(rule.toString()).isNotEqualTo(Util.originalToString(rule));
    }
}
