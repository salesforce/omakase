/*
 * Copyright (c) 2015, salesforce.com, inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 *
 *    Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *    following disclaimer.
 *
 *    Redistributions in binary form must reproduce the above copyright notice, this list of conditions and
 *    the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 *    Neither the name of salesforce.com, inc. nor the names of its contributors may be used to endorse or
 *    promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
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
    void returnsFalseOnFailure();

    /** Tests that false is always returned when parsing is expected to fail. */
    @Test
    void returnsTrueOnSuccess();

    /** Tests that a valid source is completely parsed */
    @Test
    void eofOnValidSources();

    /** Tests the number of broadcasted syntax units is exactly as expected. */
    @Test
    void matchesExpectedBroadcastCount();

    /** Tests that the content of the broadcasted syntax units is as expected. */
    @Test
    void matchesExpectedBroadcastContent();

    /** Tests that the source's index doesn't change if parsing is not successful. */
    @Test
    void noChangeToStreamOnFailure();

    /** Tests that the source advances to the expected index if parsing is successful. */
    @Test
    void expectedStreamPositionOnSuccess();

    /** Tests that created AST objects have the correct line and column numbers. */
    @Test
    void lineAndColumnForSubStreams();
}
