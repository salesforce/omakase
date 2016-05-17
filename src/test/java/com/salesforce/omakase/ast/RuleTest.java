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

package com.salesforce.omakase.ast;

import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.declaration.KeywordValue;
import com.salesforce.omakase.ast.declaration.NumericalValue;
import com.salesforce.omakase.ast.selector.ClassSelector;
import com.salesforce.omakase.ast.selector.IdSelector;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.data.Keyword;
import com.salesforce.omakase.data.Property;
import com.salesforce.omakase.test.StatusChangingBroadcaster;
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
    public void hasAnnotationByNameRuleFalseSelectorFalse() {
        Rule rule = new Rule();
        assertThat(rule.hasAnnotation("test")).isFalse();
    }

    @Test
    public void hasAnnotationByNameRuleTrueSelectorFalse() {
        Rule rule = new Rule();
        rule.comment("@test");

        Selector sel = new Selector(new ClassSelector("name"));
        rule.selectors().append(sel);

        assertThat(rule.hasAnnotation("test")).isTrue();
    }

    @Test
    public void hasAnnotationByNameRuleFalseSelectorTrue() {
        Rule rule = new Rule();

        Selector sel = new Selector(new ClassSelector("name"));
        sel.comment("@test");
        rule.selectors().append(sel);

        assertThat(rule.hasAnnotation("test")).isTrue();
    }

    @Test
    public void hasAnnotationByNameRuleTrueSelectorTrue() {
        Rule rule = new Rule();
        rule.comment("@test");

        Selector sel = new Selector(new ClassSelector("name"));
        sel.comment("@test");
        rule.selectors().append(sel);

        assertThat(rule.hasAnnotation("test")).isTrue();
    }

    @Test
    public void hasAnnotationByNameTrueNoSelectorsPresent() {
        Rule rule = new Rule();
        rule.comment("@test");
        assertThat(rule.hasAnnotation("test")).isTrue();
    }

    @Test
    public void hasAnnotationByNameFalseNoSelectorsPresent() {
        Rule rule = new Rule();
        assertThat(rule.hasAnnotation("test")).isFalse();
    }

    @Test
    public void hasAnnotationByObjectRuleFalseSelectorFalse() {
        CssAnnotation annotation = new CssAnnotation("test");
        Rule rule = new Rule();
        assertThat(rule.hasAnnotation(annotation)).isFalse();
    }

    @Test
    public void hasAnnotationByObjectRuleTrueSelectorFalse() {
        CssAnnotation annotation = new CssAnnotation("test");

        Rule rule = new Rule();
        rule.annotate(annotation);

        Selector sel = new Selector(new ClassSelector("name"));
        rule.selectors().append(sel);

        assertThat(rule.hasAnnotation(annotation)).isTrue();
    }

    @Test
    public void hasAnnotationByObjectRuleFalseSelectorTrue() {
        CssAnnotation annotation = new CssAnnotation("test");

        Rule rule = new Rule();

        Selector sel = new Selector(new ClassSelector("name"));
        sel.annotate(annotation);
        rule.selectors().append(sel);

        assertThat(rule.hasAnnotation(annotation)).isTrue();
    }

    @Test
    public void hasAnnotationByObjectNoSelectorsPresent() {
        CssAnnotation annotation = new CssAnnotation("test");

        Rule rule = new Rule();
        rule.annotate(annotation);

        assertThat(rule.hasAnnotation(annotation)).isTrue();
    }

    @Test
    public void getAnnotationByNameRuleFalseSelectorFalse() {
        Rule rule = new Rule();
        assertThat(rule.annotation("test").isPresent()).isFalse();
    }

    @Test
    public void getAnnotationByNameRuleTrueSelectorFalse() {
        Rule rule = new Rule();
        rule.comment("@test");

        Selector sel = new Selector(new ClassSelector("name"));
        rule.selectors().append(sel);

        assertThat(rule.annotation("test").get().name()).isEqualTo("test");
    }

    @Test
    public void getAnnotationByNameRuleFalseSelectorTrue() {
        Rule rule = new Rule();

        Selector sel = new Selector(new ClassSelector("name"));
        sel.comment("@test");
        rule.selectors().append(sel);

        assertThat(rule.annotation("test").isPresent()).isTrue();
    }

    @Test
    public void getAnnotationByNameRuleTrueSelectorTrue() {
        Rule rule = new Rule();
        rule.comment("@test foo");

        Selector sel = new Selector(new ClassSelector("name"));
        sel.comment("@test bar");
        rule.selectors().append(sel);

        assertThat(rule.annotation("test").get().rawArgs().get()).isEqualTo("foo");
    }

    @Test
    public void getAnnotationByNameNoSelectorsPresent() {
        Rule rule = new Rule();
        rule.comment("@test foo");

        assertThat(rule.annotation("test").isPresent()).isTrue();
    }

    @Test
    public void getAllAnnotationsRuleFalseSelectorFalse() {
        Rule rule = new Rule();
        assertThat(rule.annotations()).isEmpty();
    }

    @Test
    public void getAllAnnotationsRuleTrueSelectorFalse() {
        Rule rule = new Rule();
        rule.comment("@test");
        rule.comment("@test2");

        Selector sel = new Selector(new ClassSelector("name"));
        rule.selectors().append(sel);

        assertThat(rule.annotations()).containsExactly(new CssAnnotation("test"), new CssAnnotation("test2"));
    }

    @Test
    public void getAllAnnotationsRuleFalseSelectorTrue() {
        Rule rule = new Rule();


        Selector sel = new Selector(new ClassSelector("name"));
        sel.comment("@test");
        sel.comment("@test2");
        rule.selectors().append(sel);

        assertThat(rule.annotations()).containsExactly(new CssAnnotation("test"), new CssAnnotation("test2"));
    }

    @Test
    public void getAllAnnotationsRuleTrueSelectorTrue() {
        Rule rule = new Rule();
        rule.comment("@test");
        rule.comment("@test2");

        Selector sel = new Selector(new ClassSelector("name"));
        rule.selectors().append(sel);
        rule.comment("@test3");
        rule.comment("@test4");

        assertThat(rule.annotations()).containsExactly(new CssAnnotation("test"), new CssAnnotation("test2"), new CssAnnotation
            ("test3"), new CssAnnotation("test4"));
    }

    @Test
    public void getAllAnnotationsNoSelectorsPresent() {
        Rule rule = new Rule();
        rule.comment("@test");

        assertThat(rule.annotations()).containsExactly(new CssAnnotation("test"));
    }

    @Test
    public void propagatesBroadcast() {
        Rule rule = new Rule();
        Selector s = new Selector(new ClassSelector("name"));
        Declaration d = new Declaration(Property.DISPLAY, KeywordValue.of(Keyword.NONE));

        rule.selectors().append(s);
        rule.declarations().append(d);

        assertThat(s.status() == Status.UNBROADCASTED);
        assertThat(d.status() == Status.UNBROADCASTED);

        rule.propagateBroadcast(new StatusChangingBroadcaster());

        assertThat(s.status() != Status.UNBROADCASTED);
        assertThat(d.status() != Status.UNBROADCASTED);
    }

    @Test
    public void copy() {
        Rule rule = new Rule();
        Selector s = new Selector(new ClassSelector("name"));
        Declaration d = new Declaration(Property.DISPLAY, KeywordValue.of(Keyword.NONE));
        rule.selectors().append(s);
        rule.declarations().append(d);

        Rule copy = rule.copy();
        assertThat(copy.selectors()).hasSize(1);
        assertThat(copy.declarations()).hasSize(1);
    }

    @Test
    public void isWritableTrueWhenSelectorsAndDeclarationsWritable() {
        Rule rule = new Rule();
        Selector s = new Selector(new ClassSelector("name"));
        Declaration d = new Declaration(Property.DISPLAY, KeywordValue.of(Keyword.NONE));
        rule.selectors().append(s);
        rule.declarations().append(d);

        assertThat(rule.isWritable()).isTrue();
    }

    @Test
    public void isWritableFalseWhenNoSelectors() {
        Rule rule = new Rule();
        Declaration d = new Declaration(Property.DISPLAY, KeywordValue.of(Keyword.NONE));
        rule.declarations().append(d);
        assertThat(rule.isWritable()).isFalse();
    }

    @Test
    public void isWritableFalseWhenSelectorsNotWritable() {
        Rule rule = new Rule();
        Selector s = new Selector(new ClassSelector("name"));
        Declaration d = new Declaration(Property.DISPLAY, KeywordValue.of(Keyword.NONE));
        rule.selectors().append(s);
        rule.declarations().append(d);

        s.destroy();
        assertThat(rule.isWritable()).isFalse();
    }

    @Test
    public void isWritableFalseWhenNoDeclarations() {
        Rule rule = new Rule();
        Selector s = new Selector(new ClassSelector("name"));
        rule.selectors().append(s);
        assertThat(rule.isWritable()).isFalse();
    }

    @Test
    public void isWritableFalseWhenDeclarationsNotWritable() {
        Rule rule = new Rule();
        Selector s = new Selector(new ClassSelector("name"));
        Declaration d = new Declaration(Property.DISPLAY, KeywordValue.of(Keyword.NONE));
        rule.selectors().append(s);
        rule.declarations().append(d);

        d.destroy();
        assertThat(rule.isWritable()).isFalse();
    }

    @Test
    public void writeVerbose() throws IOException {
        Rule rule = new Rule();
        rule.selectors().append(new Selector(new ClassSelector("class")));
        rule.selectors().append(new Selector(new IdSelector("id")));
        rule.declarations().append(new Declaration(Property.DISPLAY, KeywordValue.of(Keyword.NONE)));
        rule.declarations().append(new Declaration(Property.MARGIN, NumericalValue.of(5, "px")));

        assertThat(StyleWriter.verbose().writeSnippet(rule)).isEqualTo(".class, #id {\n  display: none;\n  margin: 5px;\n}");
    }

    @Test
    public void writeInline() throws IOException {
        Rule rule = new Rule();
        rule.selectors().append(new Selector(new ClassSelector("class")));
        rule.selectors().append(new Selector(new IdSelector("id")));
        rule.declarations().append(new Declaration(Property.DISPLAY, KeywordValue.of(Keyword.NONE)));
        rule.declarations().append(new Declaration(Property.MARGIN, NumericalValue.of(5, "px")));

        assertThat(StyleWriter.inline().writeSnippet(rule)).isEqualTo(".class, #id {display:none; margin:5px}");
    }

    @Test
    public void writeCompressed() throws IOException {
        Rule rule = new Rule();
        rule.selectors().append(new Selector(new ClassSelector("class")));
        rule.selectors().append(new Selector(new IdSelector("id")));
        rule.declarations().append(new Declaration(Property.DISPLAY, KeywordValue.of(Keyword.NONE)));
        rule.declarations().append(new Declaration(Property.MARGIN, NumericalValue.of(5, "px")));

        assertThat(StyleWriter.compressed().writeSnippet(rule)).isEqualTo(".class,#id{display:none;margin:5px}");
    }

    @Test
    public void writeWhenDetached() throws IOException {
        Stylesheet stylesheet = new Stylesheet(null);

        Rule rule = new Rule();
        rule.selectors().append(new Selector(new ClassSelector("class")));
        rule.selectors().append(new Selector(new IdSelector("id")));
        rule.declarations().append(new Declaration(Property.DISPLAY, KeywordValue.of(Keyword.NONE)));
        rule.declarations().append(new Declaration(Property.MARGIN, NumericalValue.of(5, "px")));
        stylesheet.append(rule);

        rule.destroy();

        assertThat(StyleWriter.verbose().writeSnippet(stylesheet)).isEqualTo("");
    }

    @Test
    public void writeWhenFirstSelectorDetached() {
        Stylesheet stylesheet = new Stylesheet();

        Rule rule = new Rule();
        Selector selector = new Selector(new ClassSelector("class1"));
        Selector selector2 = new Selector(new ClassSelector("class2"));
        Selector selector3 = new Selector(new ClassSelector("class3"));
        rule.selectors().append(selector).append(selector2).append(selector3);
        rule.declarations().append(new Declaration(Property.DISPLAY, KeywordValue.of(Keyword.NONE)));
        stylesheet.append(rule);

        selector.destroy();

        assertThat(StyleWriter.inline().writeSnippet(stylesheet)).isEqualTo(".class2, .class3 {display:none}");
    }

    @Test
    public void writeWhenMiddleSelectorDetached() {
        Stylesheet stylesheet = new Stylesheet();

        Rule rule = new Rule();
        Selector selector = new Selector(new ClassSelector("class1"));
        Selector selector2 = new Selector(new ClassSelector("class2"));
        Selector selector3 = new Selector(new ClassSelector("class3"));
        rule.selectors().append(selector).append(selector2).append(selector3);
        rule.declarations().append(new Declaration(Property.DISPLAY, KeywordValue.of(Keyword.NONE)));
        stylesheet.append(rule);

        selector2.destroy();

        assertThat(StyleWriter.inline().writeSnippet(stylesheet)).isEqualTo(".class1, .class3 {display:none}");
    }

    @Test
    public void writeWhenLastSelectorDetached() {
        Stylesheet stylesheet = new Stylesheet();

        Rule rule = new Rule();
        Selector selector = new Selector(new ClassSelector("class1"));
        Selector selector2 = new Selector(new ClassSelector("class2"));
        Selector selector3 = new Selector(new ClassSelector("class3"));
        rule.selectors().append(selector).append(selector2).append(selector3);
        rule.declarations().append(new Declaration(Property.DISPLAY, KeywordValue.of(Keyword.NONE)));
        stylesheet.append(rule);

        selector3.destroy();

        assertThat(StyleWriter.inline().writeSnippet(stylesheet)).isEqualTo(".class1, .class2 {display:none}");
    }

    @Test
    public void writeWhenAllSelectorsAreDetached() throws IOException {
        Stylesheet stylesheet = new Stylesheet();

        Rule rule = new Rule();
        Selector selector = new Selector(new ClassSelector("class"));
        rule.selectors().append(selector);
        rule.declarations().append(new Declaration(Property.DISPLAY, KeywordValue.of(Keyword.NONE)));
        stylesheet.append(rule);

        selector.destroy();

        assertThat(StyleWriter.verbose().writeSnippet(stylesheet)).isEqualTo("");
    }

    @Test
    public void writeWhenFistDeclarationDetached() {
        Stylesheet stylesheet = new Stylesheet();

        Rule rule = new Rule();
        rule.selectors().append(new Selector(new ClassSelector("class")));
        Declaration declaration = new Declaration(Property.MARGIN, NumericalValue.of(1, "px"));
        Declaration declaration2 = new Declaration(Property.MARGIN, NumericalValue.of(2, "px"));
        Declaration declaration3 = new Declaration(Property.MARGIN, NumericalValue.of(3, "px"));
        rule.declarations().append(declaration).append(declaration2).append(declaration3);
        stylesheet.append(rule);

        declaration.destroy();

        assertThat(StyleWriter.verbose().writeSnippet(stylesheet)).isEqualTo(".class {\n  margin: 2px;\n  margin: 3px;\n}");
    }

    @Test
    public void writeWhenMiddleDeclarationDetached() {
        Stylesheet stylesheet = new Stylesheet();

        Rule rule = new Rule();
        rule.selectors().append(new Selector(new ClassSelector("class")));
        Declaration declaration = new Declaration(Property.MARGIN, NumericalValue.of(1, "px"));
        Declaration declaration2 = new Declaration(Property.MARGIN, NumericalValue.of(2, "px"));
        Declaration declaration3 = new Declaration(Property.MARGIN, NumericalValue.of(3, "px"));
        rule.declarations().append(declaration).append(declaration2).append(declaration3);
        stylesheet.append(rule);

        declaration2.destroy();

        assertThat(StyleWriter.verbose().writeSnippet(stylesheet)).isEqualTo(".class {\n  margin: 1px;\n  margin: 3px;\n}");
    }

    @Test
    public void writeWhenLastDeclarationDetached() {
        Stylesheet stylesheet = new Stylesheet();

        Rule rule = new Rule();
        rule.selectors().append(new Selector(new ClassSelector("class")));
        Declaration declaration = new Declaration(Property.MARGIN, NumericalValue.of(1, "px"));
        Declaration declaration2 = new Declaration(Property.MARGIN, NumericalValue.of(2, "px"));
        Declaration declaration3 = new Declaration(Property.MARGIN, NumericalValue.of(3, "px"));
        rule.declarations().append(declaration).append(declaration2).append(declaration3);
        stylesheet.append(rule);

        declaration3.destroy();

        assertThat(StyleWriter.inline().writeSnippet(stylesheet)).isEqualTo(".class {margin:1px; margin:2px}");
    }

    @Test
    public void writeWhenNoDeclarations() throws IOException {
        Stylesheet stylesheet = new Stylesheet();

        Rule rule = new Rule();
        rule.selectors().append(new Selector(new ClassSelector("class")));
        rule.selectors().append(new Selector(new IdSelector("id")));
        stylesheet.append(rule);

        assertThat(StyleWriter.verbose().writeSnippet(stylesheet)).isEqualTo("");
    }

    @Test
    public void writeVerboseWhenAllDeclarationsDetached() throws IOException {
        Stylesheet stylesheet = new Stylesheet();

        Rule rule = new Rule();
        rule.selectors().append(new Selector(new ClassSelector("class")));
        Declaration declaration = new Declaration(Property.MARGIN, NumericalValue.of(5, "px"));
        rule.declarations().append(declaration);
        stylesheet.append(rule);

        declaration.destroy();

        assertThat(StyleWriter.verbose().writeSnippet(stylesheet)).isEqualTo("");
    }
}
