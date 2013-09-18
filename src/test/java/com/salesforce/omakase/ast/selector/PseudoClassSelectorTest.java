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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;

import static org.fest.assertions.api.Assertions.*;

/** Unit tests for {@link PseudoClassSelector}. */
@SuppressWarnings("JavaDoc")
public class PseudoClassSelectorTest {
    @Rule public final ExpectedException exception = ExpectedException.none();

    @Test
    public void getName() {
        PseudoClassSelector s = new PseudoClassSelector("hover");
        assertThat(s.name()).isEqualTo("hover");
    }

    @Test
    public void setName() {
        PseudoClassSelector s = new PseudoClassSelector("hover");
        s.name("visited");
        assertThat(s.name()).isEqualTo("visited");
    }

    @Test
    public void errorsIfNameIsPseudoElement() {
        PseudoClassSelector s = new PseudoClassSelector("hover");
        exception.expect(IllegalArgumentException.class);
        s.name("before");
    }

    @Test
    public void isSelector() {
        assertThat(new PseudoClassSelector("hover").isSelector()).isTrue();
    }

    @Test
    public void isCombinator() {
        assertThat(new PseudoClassSelector("hover").isCombinator()).isFalse();
    }

    @Test
    public void type() {
        assertThat(new PseudoClassSelector("hover").type()).isSameAs(SelectorPartType.PSEUDO_CLASS_SELECTOR);
    }

    @Test
    public void write() throws IOException {
        PseudoClassSelector s = new PseudoClassSelector("hover");
        assertThat(StyleWriter.compressed().writeSnippet(s)).isEqualTo(":hover");
    }
}
