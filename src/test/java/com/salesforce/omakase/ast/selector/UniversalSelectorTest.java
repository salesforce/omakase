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

import com.salesforce.omakase.writer.StyleWriter;
import org.junit.Test;

import java.io.IOException;

import static org.fest.assertions.api.Assertions.assertThat;

/** Unit tests for {@link UniversalSelector}. */
@SuppressWarnings("JavaDoc")
public class UniversalSelectorTest {

    @Test
    public void positioning() {
        assertThat(new UniversalSelector(5, 6).line()).isEqualTo(5);
        assertThat(new UniversalSelector(5, 6).column()).isEqualTo(6);
    }

    @Test
    public void isSelector() {
        assertThat(new UniversalSelector().isSelector()).isTrue();
    }

    @Test
    public void isCombinator() {
        assertThat(new UniversalSelector().isCombinator()).isFalse();
    }

    @Test
    public void type() {
        assertThat(new UniversalSelector().type()).isSameAs(SelectorPartType.UNIVERSAL_SELECTOR);
    }

    @Test
    public void write() throws IOException {
        assertThat(StyleWriter.compressed().writeSnippet(new UniversalSelector())).isEqualTo("*");
    }
}
