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

package com.salesforce.omakase.ast.atrule;

import com.salesforce.omakase.writer.StyleWriter;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link GenericAtRuleExpression}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class GenericAtRuleExpressionTest {
    @Test
    public void getExpression() {
        GenericAtRuleExpression expr = new GenericAtRuleExpression(1, 1, "test");
        assertThat(expr.expression()).isEqualTo("test");
    }

    @Test
    public void setExpression() {
        GenericAtRuleExpression expr = new GenericAtRuleExpression("test");
        expr.expression("test2");
        assertThat(expr.expression()).isEqualTo("test2");
    }

    @Test
    public void write() {
        assertThat(StyleWriter.writeSingle(new GenericAtRuleExpression("test"))).isEqualTo("test");
    }

    @Test
    public void copy() {
        GenericAtRuleExpression expr = new GenericAtRuleExpression("test");
        assertThat(((GenericAtRuleExpression)expr.copy()).expression()).isEqualTo("test");
    }
}
