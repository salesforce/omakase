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

package com.salesforce.omakase.writer;

import com.salesforce.omakase.Omakase;
import com.salesforce.omakase.ast.selector.Selector;
import org.junit.Test;

import java.io.IOException;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link CustomWriter}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class CustomWriterTest {
    @Test
    public void testCustomWriter() {
        StyleWriter writer = StyleWriter.compressed();
        writer.override(Selector.class, new TestCustomWriter());
        Omakase.source(".class{color:red}").add(writer).process();

        assertThat(writer.write()).isEqualTo("CUSTOM.class{color:red}");
    }

    public static final class TestCustomWriter implements CustomWriter<Selector> {
        @Override
        public void write(Selector selector, StyleWriter writer, StyleAppendable appendable) throws IOException {
            appendable.append("CUSTOM");
            selector.write(writer, appendable);
        }
    }
}
