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

package com.salesforce.omakase.parser.raw;

import com.salesforce.omakase.broadcaster.QueryableBroadcaster;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.Stream;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Unit tests for {@link StylesheetParser}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class StylesheetParserTest {
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void testEof() {
        exception.expect(ParserException.class);
        exception.expectMessage("Extraneous text found at the end of the source");
        new StylesheetParser().parse(new Stream(".abc{color:red}   `"), new QueryableBroadcaster());
    }
}
