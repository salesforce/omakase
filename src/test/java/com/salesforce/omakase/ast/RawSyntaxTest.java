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

import com.salesforce.omakase.writer.StyleWriter;
import org.junit.Test;

import java.io.IOException;

import static org.fest.assertions.api.Assertions.*;

/** Unit tests for {@link RawSyntax}. */
@SuppressWarnings("JavaDoc")
public class RawSyntaxTest {
    @Test
    public void line() {
        assertThat(new RawSyntax(5, 10, ".class > #id").line()).isEqualTo(5);
    }

    @Test
    public void column() {
        assertThat(new RawSyntax(5, 10, ".class > #id").column()).isEqualTo(10);
    }

    @Test
    public void writeVerbose() throws IOException {
        RawSyntax r = new RawSyntax(5, 5, ".class > #id");
        StyleWriter writer = StyleWriter.verbose();
        assertThat(writer.writeSnippet(r)).isEqualTo(".class > #id");
    }

    public void writeCompressed() {
        fail("unimplemented"); // TODO write test once functionality is there
    }
}
