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

package com.salesforce.omakase.util;

import com.salesforce.omakase.ast.selector.IdSelector;
import com.salesforce.omakase.parser.ParserFactory;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link Parsers}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class ParsersTest {
    @Test
    public void parseNumericalAbsent() {
        assertThat(Parsers.parseNumerical("blah").isPresent()).isFalse();
    }

    @Test
    public void parseNumericalPresent() {
        assertThat(Parsers.parseNumerical("10px blah").isPresent()).isTrue();
    }

    @Test
    public void parseSimpleAbsent() {
        assertThat(Parsers.parseSimple("blah", ParserFactory.idSelectorParser(), IdSelector.class).isPresent()).isFalse();
    }

    @Test
    public void parseSimplePresent() {
        assertThat(Parsers.parseSimple("#id", ParserFactory.idSelectorParser(), IdSelector.class).isPresent()).isTrue();
    }
}
