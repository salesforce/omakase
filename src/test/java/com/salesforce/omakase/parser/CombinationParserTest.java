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

package com.salesforce.omakase.parser;

import com.salesforce.omakase.broadcaster.QueryableBroadcaster;
import com.salesforce.omakase.parser.declaration.KeywordValueParser;
import com.salesforce.omakase.parser.declaration.NumericalValueParser;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link CombinationParser}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class CombinationParserTest {
    @Test
    public void parsesEither() {
        CombinationParser c = new CombinationParser(new KeywordValueParser(), new NumericalValueParser());
        assertThat(c.parse(new Stream("red"), new QueryableBroadcaster())).isTrue();
        assertThat(c.parse(new Stream("3px"), new QueryableBroadcaster())).isTrue();
        assertThat(c.parse(new Stream("!"), new QueryableBroadcaster())).isFalse();
    }
}
