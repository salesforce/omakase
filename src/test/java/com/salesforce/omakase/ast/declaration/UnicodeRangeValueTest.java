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

package com.salesforce.omakase.ast.declaration;

import com.google.common.collect.Lists;
import com.salesforce.omakase.writer.StyleWriter;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link UnicodeRangeValue}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class UnicodeRangeValueTest {
    private UnicodeRangeValue range;

    @Test
    public void positioning() {
        range = new UnicodeRangeValue(5, 10, "u+ff0");
        assertThat(range.line()).isEqualTo(5);
        assertThat(range.column()).isEqualTo(10);
    }

    @Test
    public void getsValue() {
        range = new UnicodeRangeValue(5, 10, "u+ff0");
        assertThat(range.value()).isEqualTo("u+ff0");
    }

    @Test
    public void setsValue() {
        range = new UnicodeRangeValue(5, 10, "u+ff0");
        range.value("u+26");
        assertThat(range.value()).isEqualTo("u+26");
    }

    @Test
    public void valueLowerCased() {
        range = new UnicodeRangeValue(5, 10, "U+0025-00FF");
        assertThat(range.value()).isEqualTo("u+0025-00ff");
    }

    @Test
    public void textualValueReturnsValue() {
        range = new UnicodeRangeValue(5, 10, "u+ff0");
        assertThat(range.textualValue()).isEqualTo("u+ff0");
    }

    @Test
    public void writeVerbose() {
        range = new UnicodeRangeValue(5, 10, "u+0025-00ff");
        StyleWriter writer = StyleWriter.verbose();
        assertThat(writer.writeSnippet(range)).isEqualTo("u+0025-00ff");
    }

    @Test
    public void writeInline() {
        range = new UnicodeRangeValue(5, 10, "u+0025-00ff");
        StyleWriter writer = StyleWriter.inline();
        assertThat(writer.writeSnippet(range)).isEqualTo("u+0025-00ff");
    }

    @Test
    public void writeCompressed() {
        range = new UnicodeRangeValue(5, 10, "u+0025-00ff");
        StyleWriter writer = StyleWriter.compressed();
        assertThat(writer.writeSnippet(range)).isEqualTo("u+0025-00ff");
    }

    @Test
    public void copyTest() {
        range = new UnicodeRangeValue(5, 10, "u+ff0");
        range.comments(Lists.newArrayList("test"));

        UnicodeRangeValue copy = (UnicodeRangeValue)range.copy();
        assertThat(copy.value()).isEqualTo(range.value());
        assertThat(copy.comments()).hasSameSizeAs(range.comments());
    }
}
