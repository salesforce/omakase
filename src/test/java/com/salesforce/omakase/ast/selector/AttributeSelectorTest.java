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

package com.salesforce.omakase.ast.selector;

import static org.fest.assertions.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Test;

import com.salesforce.omakase.writer.StyleWriter;

/**
 * Unit tests for {@link AttributeSelector}.
 *
 * @author nmcwilliams
 */
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
    public void type() {
        assertThat(new AttributeSelector("a").type()).isSameAs(SelectorPartType.ATTRIBUTE_SELECTOR);
    }

    @Test
    public void writeWhenAttributeOnly() throws IOException {
        selector = new AttributeSelector("class");
        assertThat(StyleWriter.compressed().writeSingle(selector)).isEqualTo("[class]");
    }

    @Test
    public void writeWhenAtributeAndMatchTypeAndMatchValue() throws IOException {
        selector = new AttributeSelector("class");
        selector.match(AttributeMatchType.PREFIXMATCH, "prefix");
        assertThat(StyleWriter.compressed().writeSingle(selector)).isEqualTo("[class^=prefix]");
    }

    @Test
    public void writeWhenAtributeAndMatchTypeAndMatchValue2() throws IOException {
        selector = new AttributeSelector("class");
        selector.match(AttributeMatchType.PREFIXMATCH, "abc123-_123");
        assertThat(StyleWriter.compressed().writeSingle(selector)).isEqualTo("[class^=abc123-_123]");
    }

    @Test
    public void writeWhenValueShouldBeQuoted() throws IOException {
        selector = new AttributeSelector("class");
        selector.match(AttributeMatchType.PREFIXMATCH, "with spaces");
        assertThat(StyleWriter.compressed().writeSingle(selector)).isEqualTo("[class^=\"with spaces\"]");
    }

    @Test
    public void writeWhenValueShouldBeQuoted2() throws IOException {
        selector = new AttributeSelector("class");
        selector.match(AttributeMatchType.PREFIXMATCH, "1number");
        assertThat(StyleWriter.compressed().writeSingle(selector)).isEqualTo("[class^=\"1number\"]");
    }

    @Test
    public void writeWhenValueShouldBeQuoted3() throws IOException {
        selector = new AttributeSelector("class");
        selector.match(AttributeMatchType.PREFIXMATCH, "%^&$");
        assertThat(StyleWriter.compressed().writeSingle(selector)).isEqualTo("[class^=\"%^&$\"]");
    }

    @Test
    public void copyNoMatchTypeorMatchValue() {
        selector = new AttributeSelector("class");
        AttributeSelector copy = selector.copy();
        assertThat(copy.attribute()).isEqualTo("class");
        assertThat(copy.matchType().isPresent()).isFalse();
        assertThat(copy.value().isPresent()).isFalse();
    }

    @Test
    public void copyMatchTypeAndValue() {
        selector = new AttributeSelector("class");
        selector.match(AttributeMatchType.EQUALS, "test");

        AttributeSelector copy = selector.copy();
        assertThat(copy.attribute()).isEqualTo("class");
        assertThat(copy.matchType().get()).isSameAs(AttributeMatchType.EQUALS);
        assertThat(copy.value().get()).isEqualTo("test");
    }
}
