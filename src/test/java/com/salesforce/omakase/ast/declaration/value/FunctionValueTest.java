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

package com.salesforce.omakase.ast.declaration.value;

import com.salesforce.omakase.test.util.Util;
import com.salesforce.omakase.writer.StyleWriter;
import org.junit.Test;

import java.io.IOException;

import static org.fest.assertions.api.Assertions.assertThat;

/** Unit tests for {@link FunctionValue}. */
@SuppressWarnings("JavaDoc")
public class FunctionValueTest {
    FunctionValue value;

    @Test
    public void positioning() {
        value = new FunctionValue(5, 2, "url", "home.png");
        assertThat(value.line()).isEqualTo(5);
        assertThat(value.column()).isEqualTo(2);
    }

    @Test
    public void getsName() {
        value = new FunctionValue("url", "home.png");
        assertThat(value.name()).isEqualTo("url");
    }

    @Test
    public void setsName() {
        value = new FunctionValue("rgb", "255,255,255");
        value.name("rgba");
        assertThat(value.name()).isEqualTo("rgba");
    }

    @Test
    public void getsArgs() {
        value = new FunctionValue("url", "home.png");
        assertThat(value.args()).isEqualTo("home.png");
    }

    @Test
    public void setsArgs() {
        value = new FunctionValue("rgb", "255,255,255");
        value.args("255, 255, 255, 0.5");
        assertThat(value.args()).isEqualTo("255, 255, 255, 0.5");
    }

    @Test
    public void writeVerbose() throws IOException {
        value = new FunctionValue("url", "home.png");
        StyleWriter writer = StyleWriter.verbose();
        assertThat(writer.writeSnippet(value)).isEqualTo("url(home.png)");
    }

    @Test
    public void writeInline() throws IOException {
        value = new FunctionValue("url", "home.png");
        StyleWriter writer = StyleWriter.verbose();
        assertThat(writer.writeSnippet(value)).isEqualTo("url(home.png)");
    }

    @Test
    public void writeCompressed() throws IOException {
        value = FunctionValue.of("url", "home.png");
        StyleWriter writer = StyleWriter.verbose();
        assertThat(writer.writeSnippet(value)).isEqualTo("url(home.png)");
    }

    @Test
    public void writeEmptyArgs() throws IOException {
        value = new FunctionValue("xyz", "");
        StyleWriter writer = StyleWriter.verbose();
        assertThat(writer.writeSnippet(value)).isEqualTo("xyz()");
    }

    @Test
    public void toStringTest() {
        value = new FunctionValue("xyz", "home.png");
        assertThat(value.toString()).isNotEqualTo(Util.originalToString(value));
    }
}
