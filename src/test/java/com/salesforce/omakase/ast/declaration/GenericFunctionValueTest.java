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

package com.salesforce.omakase.ast.declaration;

import com.google.common.collect.Lists;
import com.salesforce.omakase.SupportMatrix;
import com.salesforce.omakase.data.Browser;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.writer.StyleWriter;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;

import static org.fest.assertions.api.Assertions.assertThat;

/** Unit tests for {@link GenericFunctionValue}. */
@SuppressWarnings("JavaDoc")
public class GenericFunctionValueTest {
    @Rule public final ExpectedException exception = ExpectedException.none();

    GenericFunctionValue value;

    @Test
    public void positioning() {
        value = new GenericFunctionValue(5, 2, "url", "home.png");
        assertThat(value.line()).isEqualTo(5);
        assertThat(value.column()).isEqualTo(2);
    }

    @Test
    public void getsName() {
        value = new GenericFunctionValue("url", "home.png");
        assertThat(value.name()).isEqualTo("url");
    }

    @Test
    public void setsName() {
        value = new GenericFunctionValue("rgb", "255,255,255");
        value.name("rgba");
        assertThat(value.name()).isEqualTo("rgba");
    }

    @Test
    public void getsArgs() {
        value = new GenericFunctionValue("url", "home.png");
        assertThat(value.args()).isEqualTo("home.png");
    }

    @Test
    public void setsArgs() {
        value = new GenericFunctionValue("rgb", "255,255,255");
        value.args("255, 255, 255, 0.5");
        assertThat(value.args()).isEqualTo("255, 255, 255, 0.5");
    }

    @Test
    public void textualValueReturnsArgs() {
        value = new GenericFunctionValue("url", "home.png");
        assertThat(value.textualValue()).isEqualTo("home.png");
    }

    @Test
    public void writeVerbose() throws IOException {
        value = new GenericFunctionValue("url", "home.png");
        assertThat(StyleWriter.verbose().writeSnippet(value)).isEqualTo("url(home.png)");
    }

    @Test
    public void writeInline() throws IOException {
        value = new GenericFunctionValue("url", "home.png");
        assertThat(StyleWriter.verbose().writeSnippet(value)).isEqualTo("url(home.png)");
    }

    @Test
    public void writeCompressed() throws IOException {
        value = GenericFunctionValue.of("url", "home.png");
        assertThat(StyleWriter.verbose().writeSnippet(value)).isEqualTo("url(home.png)");
    }

    @Test
    public void writeEmptyArgs() throws IOException {
        value = new GenericFunctionValue("xyz", "");
        assertThat(StyleWriter.verbose().writeSnippet(value)).isEqualTo("xyz()");
    }

    @Test
    public void copy() {
        value = new GenericFunctionValue("test", "args");
        value.comments(Lists.newArrayList("test"));

        GenericFunctionValue copy = (GenericFunctionValue)value.copy();
        assertThat(copy.name()).isEqualTo("test");
        assertThat(copy.args()).isEqualTo("args");
        assertThat(copy.comments()).hasSameSizeAs(value.comments());
    }

    @Test
    public void prefixRequired() {
        SupportMatrix support = new SupportMatrix();
        support.browser(Browser.CHROME, 19);
        value = new GenericFunctionValue("calc", "2px-1px");
        value.comments(Lists.newArrayList("test"));

        value.prefix(Prefix.WEBKIT, support);
        assertThat(value.name()).isEqualTo("-webkit-calc");
        assertThat(value.args()).isEqualTo("2px-1px");
    }

    @Test
    public void prefixNotRequired() {
        SupportMatrix support = new SupportMatrix();
        value = new GenericFunctionValue("calc", "2px-1px");
        value.comments(Lists.newArrayList("test"));

        value.prefix(Prefix.WEBKIT, support);
        assertThat(value.name()).isEqualTo("calc");
        assertThat(value.args()).isEqualTo("2px-1px");
    }
}
