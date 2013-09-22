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

package com.salesforce.omakase;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link As}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class AsTest {
    @Test
    public void testClass() {
        String s = As.string(this).add("1", "a").add("2", "b").toString();
        assertThat(s).isEqualTo("AsTest{1=a, 2=b}");
    }

    @Test
    public void testNamed() {
        String s = As.stringNamed("test").add("1", "a").add("2", "b").toString();
        assertThat(s).isEqualTo("test{1=a, 2=b}");
    }

    @Test
    public void testIndent() {
        String s = As.string(this).indent().add("1", "a").add("2", "b").toString();
        assertThat(s).isEqualTo("AsTest {\n  1: a\n  2: b\n}");
    }
}
