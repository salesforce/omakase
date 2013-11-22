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

import com.salesforce.omakase.writer.StyleWriter;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link RawFunction}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class RawFunctionTest {
    @Rule public final ExpectedException exception = ExpectedException.none();

    @Test
    public void testName() {
        RawFunction raw = new RawFunction(1, 1, "name", "args args");
        assertThat(raw.name()).isEqualTo("name");

        raw.name("changed");
        assertThat(raw.name()).isEqualTo("changed");
    }

    @Test
    public void testArgs() {
        RawFunction raw = new RawFunction(1, 1, "name", "args args");
        assertThat(raw.args()).isEqualTo("args args");

        raw.args("changed");
        assertThat(raw.args()).isEqualTo("changed");
    }

    @Test
    public void writeNotSupported() {
        exception.expect(UnsupportedOperationException.class);
        StyleWriter.writeSingle(new RawFunction(1, 1, "name", "args args"));
    }

    @Test
    public void copyNotSupported() {
        exception.expect(UnsupportedOperationException.class);
        new RawFunction(1, 1, "name", "args args").copy();
    }
}
