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

package com.salesforce.omakase.ast.selector;

import com.salesforce.omakase.test.util.Util;
import com.salesforce.omakase.writer.StyleWriter;
import org.junit.Test;

import java.io.IOException;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link AttributeSelector}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class AttributeSelectorTest {
    private AttributeSelector selector;

    @Test
    public void positioning() {
        selector = new AttributeSelector(5, 10, "href");
        assertThat(selector.line()).isEqualTo(5);
        assertThat(selector.column()).isEqualTo(10);
    }

    @Test
    public void matcherTypeAbsentByDefault() {
        selector = new AttributeSelector("test");
        assertThat(selector.matchType().isPresent()).isFalse();
    }

    @Test
    public void valueAbsentByDefault() {
        selector = new AttributeSelector("test");
        assertThat(selector.value().isPresent()).isFalse();
    }

    @Test
    public void getAttribute() {
        selector = new AttributeSelector("test");
        assertThat(selector.attribute()).isEqualTo("test");
    }

    @Test
    public void setAttribute() {
        selector = new AttributeSelector("test");
        selector.attribute("changed");
        assertThat(selector.attribute()).isEqualTo("changed");
    }

    @Test
    public void setMatchTypeAndValue() {
        selector = new AttributeSelector("test");
        selector.match(AttributeMatchType.EQUALS, "val");
        assertThat(selector.matchType().get()).isSameAs(AttributeMatchType.EQUALS);
        assertThat(selector.value().get()).isEqualTo("val");
    }

    @Test
    public void removeMatchTypeAndValueWhenPresent() {
        selector = new AttributeSelector("test");
        selector.match(AttributeMatchType.EQUALS, "val");
        selector.matchAll();

        assertThat(selector.matchType().isPresent()).isFalse();
        assertThat(selector.value().isPresent()).isFalse();
    }

    @Test
    public void removeMatchTypeAndValueWhenAbsent() {
        selector = new AttributeSelector("test");
        selector.matchAll();

        assertThat(selector.matchType().isPresent()).isFalse();
        assertThat(selector.value().isPresent()).isFalse();
    }

    @Test
    public void isSelector() {
        assertThat(new AttributeSelector("a").isSelector()).isTrue();
    }

    @Test
    public void isCombinator() {
        assertThat(new AttributeSelector("a").isCombinator()).isFalse();
    }

    @Test
    public void type() {
        assertThat(new AttributeSelector("a").type()).isSameAs(SelectorPartType.ATTRIBUTE_SELECTOR);
    }

    @Test
    public void writeWhenAttributeOnly() throws IOException {
        selector = new AttributeSelector("class");
        assertThat(StyleWriter.compressed().writeSnippet(selector)).isEqualTo("[class]");
    }

    @Test
    public void writeWhenAtributeAndMatchTypeAndMatchValue() throws IOException {
        selector = new AttributeSelector("class");
        selector.match(AttributeMatchType.PREFIXMATCH, "prefix");
        assertThat(StyleWriter.compressed().writeSnippet(selector)).isEqualTo("[class^=prefix]");
    }

    @Test
    public void writeWhenAtributeAndMatchTypeAndMatchValue2() throws IOException {
        selector = new AttributeSelector("class");
        selector.match(AttributeMatchType.PREFIXMATCH, "abc123-_123");
        assertThat(StyleWriter.compressed().writeSnippet(selector)).isEqualTo("[class^=abc123-_123]");
    }

    @Test
    public void writeWhenValueShouldBeQuoted() throws IOException {
        selector = new AttributeSelector("class");
        selector.match(AttributeMatchType.PREFIXMATCH, "with spaces");
        assertThat(StyleWriter.compressed().writeSnippet(selector)).isEqualTo("[class^=\"with spaces\"]");
    }

    @Test
    public void writeWhenValueShouldBeQuoted2() throws IOException {
        selector = new AttributeSelector("class");
        selector.match(AttributeMatchType.PREFIXMATCH, "1number");
        assertThat(StyleWriter.compressed().writeSnippet(selector)).isEqualTo("[class^=\"1number\"]");
    }

    @Test
    public void writeWhenValueShouldBeQuoted3() throws IOException {
        selector = new AttributeSelector("class");
        selector.match(AttributeMatchType.PREFIXMATCH, "%^&$");
        assertThat(StyleWriter.compressed().writeSnippet(selector)).isEqualTo("[class^=\"%^&$\"]");
    }

    @Test
    public void toStringTest() {
        selector = new AttributeSelector("class");
        assertThat(selector.toString()).isNotEqualTo(Util.originalToString(selector));
    }
}
