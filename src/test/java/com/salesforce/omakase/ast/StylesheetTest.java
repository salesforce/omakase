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

import com.google.common.collect.Iterators;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.data.Property;
import com.salesforce.omakase.data.Keyword;
import com.salesforce.omakase.ast.declaration.KeywordValue;
import com.salesforce.omakase.ast.selector.ClassSelector;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.util.Util;
import com.salesforce.omakase.writer.StyleWriter;
import org.junit.Test;

import java.io.IOException;

import static org.fest.assertions.api.Assertions.assertThat;

/** Unit tests for {@link Stylesheet}. */
@SuppressWarnings("JavaDoc")
public class StylesheetTest {
    @Test
    public void appendStatement() {
        Stylesheet sheet = new Stylesheet();
        sheet.append(new Rule());
        assertThat(sheet.statements()).hasSize(1);
    }

    @Test
    public void iteratorTest() {
        Stylesheet sheet = new Stylesheet();
        sheet.append(new Rule());
        assertThat(Iterators.size(sheet.iterator())).isEqualTo(1);
    }

    @Test
    public void orphanedCommentsAbsent() {
        Stylesheet sheet = new Stylesheet();
        assertThat(sheet.orphanedComments()).isEmpty();
    }

    @Test
    public void orphanedCommentsPresent() {
        Stylesheet sheet = new Stylesheet();
        sheet.orphanedComment(new Comment("s"));
        assertThat(sheet.orphanedComments()).hasSize(1);
    }

    @Test
    public void write() throws IOException {
        Stylesheet sheet = new Stylesheet();
        Rule rule = new Rule();
        rule.selectors().append(new Selector(new ClassSelector("class")));
        rule.declarations().append(new Declaration(Property.DISPLAY, KeywordValue.of(Keyword.NONE)));
        sheet.append(rule);
        assertThat(sheet.statements()).hasSize(1);

        assertThat(StyleWriter.compressed().writeSnippet(rule)).isEqualTo(".class{display:none}");
    }

    @Test
    public void toStringTest() {
        Stylesheet sheet = new Stylesheet();
        assertThat(sheet.toString()).isNotEqualTo(Util.originalToString(sheet));
    }
}
