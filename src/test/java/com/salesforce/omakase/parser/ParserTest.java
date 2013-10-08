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

import org.junit.Test;

/**
 * Base class for testing parsers.
 *
 * @author nmcwilliams
 */
public interface ParserTest {
    /** Tests that true is always returned when parsing is expected to be successful. */
    @Test
    public void returnsFalseOnFailure();

    /** Tests that false is always returned when parsing is expected to fail. */
    @Test
    public void returnsTrueOnSuccess();

    /** Tests that a valid source is completely parsed */
    @Test
    public void eofOnValidSources();

    /** Tests the number of broadcasted syntax units is exactly as expected. */
    @Test
    public void matchesExpectedBroadcastCount();

    /** Tests that the content of the broadcasted syntax units is as expected. */
    @Test
    public void matchesExpectedBroadcastContent();

    /** Tests that the source's index doesn't change if parsing is not successful. */
    @Test
    public void noChangeToStreamOnFailure();

    /** Tests that the source advances to the expected index if parsing is successful. */
    @Test
    public void expectedStreamPositionOnSuccess();

    /** Tests that created AST objects have the correct line and column numbers. */
    @Test
    public void lineAndColumnForSubStreams();
}
