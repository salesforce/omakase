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

import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.test.util.Util;
import com.salesforce.omakase.writer.StyleWriter;
import org.junit.Test;

import java.io.IOException;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link Operator}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class OperatorTest {
    @Test
    public void testGetType() {
        Operator operator = new Operator(OperatorType.COMMA);
        assertThat(operator.type()).isSameAs(OperatorType.COMMA);
    }

    @Test
    public void testWrite() throws IOException {
        Operator operator = new Operator(OperatorType.COMMA);
        assertThat(StyleWriter.compressed().writeSnippet(operator)).isEqualTo(",");
    }

    @Test
    public void copyTest() {
        Operator operator = new Operator(OperatorType.COMMA);
        assertThat(operator.copy().type()).isSameAs(operator.type());
    }

    @Test
    public void copyWithPrefixTest() {
        Operator operator = new Operator(OperatorType.COMMA);
        assertThat(operator.copyWithPrefix(Prefix.MOZ, null).type()).isEqualTo(operator.type());
    }

    @Test
    public void toStringTest() {
        Operator operator = new Operator(OperatorType.SLASH);
        assertThat(operator.toString()).isNotEqualTo(Util.originalToString(operator));
    }
}
