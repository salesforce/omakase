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

import com.salesforce.omakase.util.Util;
import com.salesforce.omakase.writer.StyleWriter;
import org.junit.Test;

import java.io.IOException;

import static org.fest.assertions.api.Assertions.assertThat;

/** Unit tests for {@link TypeSelector}. */
@SuppressWarnings("JavaDoc")
public class TypeSelectorTest {
    @Test
    public void getName() {
        TypeSelector ts = new TypeSelector(1, 1, "p");
        assertThat(ts.name()).isEqualTo("p");
    }

    @Test
    public void setName() {
        TypeSelector ts = new TypeSelector("p");
        ts.name("div");
        assertThat(ts.name()).isEqualTo("div");
    }

    @Test
    public void type() {
        assertThat(new TypeSelector(1, 1, "p").type()).isSameAs(SelectorPartType.TYPE_SELECTOR);
    }

    @Test
    public void lowerCasesName() {
        TypeSelector ts = new TypeSelector(1, 1, "P");
        assertThat(ts.name()).isEqualTo("p");

        ts.name("DIV");
        assertThat(ts.name()).isEqualTo("div");
    }

    @Test
    public void write() throws IOException {
        TypeSelector ts = new TypeSelector(1, 1, "p");
        assertThat(StyleWriter.compressed().writeSnippet(ts)).isEqualTo("p");
    }

    @Test
    public void toStringTest() {
        TypeSelector ts = new TypeSelector(1, 1, "p");
        assertThat(ts.toString()).isNotEqualTo(Util.originalToString(ts));
    }
}
