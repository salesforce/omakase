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

/** Unit tests for {@link ClassSelector]. */
@SuppressWarnings("JavaDoc")
public class ClassSelectorTest {
    @Test
    public void getName() {
        ClassSelector cs = new ClassSelector(1, 1, "test");
        assertThat(cs.name()).isEqualTo("test");
    }

    @Test
    public void setName() {
        ClassSelector cs = new ClassSelector(1, 1, "test");
        cs.name("test2");
        assertThat(cs.name()).isEqualTo("test2");
    }

    @Test
    public void isSelector() {
        ClassSelector cs = new ClassSelector("test");
        assertThat(cs.isSelector()).isTrue();
    }

    @Test
    public void isCombinator() {
        ClassSelector cs = new ClassSelector("test");
        assertThat(cs.isCombinator()).isFalse();
    }

    @Test
    public void type() {
        ClassSelector cs = new ClassSelector("test");
        assertThat(cs.type()).isSameAs(SelectorPartType.CLASS_SELECTOR);
    }

    @Test
    public void write() throws IOException {
        ClassSelector cs = new ClassSelector("test");
        StyleWriter writer = StyleWriter.compressed();
        assertThat(writer.writeSnippet(cs)).isEqualTo(".test");
    }

    @Test
    public void toStringTest() {
        ClassSelector cs = new ClassSelector("test");
        assertThat(cs.toString()).isNotEqualTo(Util.originalToString(cs));
    }
}
