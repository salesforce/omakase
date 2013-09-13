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

import com.salesforce.omakase.plugin.basic.AutoRefiner;
import com.salesforce.omakase.plugin.basic.SyntaxTree;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for Suppliers.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class SuppliersTest {
    @Test
    public void testSyntaxTree() {
        assertThat(Suppliers.get(SyntaxTree.class).isPresent()).isTrue();
    }

    @Test
    public void testAutoRefiner() {
        assertThat(Suppliers.get(AutoRefiner.class).isPresent()).isTrue();
    }
}
