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

/** Unit tests for {@link IdSelector}. */
@SuppressWarnings("JavaDoc")
public class IdSelectorTest {
    @Test
    public void getName() {
        IdSelector id = new IdSelector(1, 1, "test");
        assertThat(id.name()).isEqualTo("test");
    }

    @Test
    public void setName() {
        IdSelector id = new IdSelector(1, 1, "test");
        id.name("test2");
        assertThat(id.name()).isEqualTo("test2");
    }

    @Test
    public void type() {
        assertThat(new IdSelector("test").type()).isSameAs(SelectorPartType.ID_SELECTOR);
    }

    @Test
    public void write() throws IOException {
        IdSelector id = new IdSelector(1, 1, "test");
        assertThat(StyleWriter.compressed().writeSnippet(id)).isEqualTo("#test");
    }

    @Test
    public void toStringTest() {
        IdSelector id = new IdSelector(1, 1, "test");
        assertThat(id.toString()).isNotEqualTo(Util.originalToString(id));
    }
}
