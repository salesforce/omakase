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

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Test;

import com.salesforce.omakase.broadcast.NoopBroadcaster;
import com.salesforce.omakase.parser.declaration.KeywordValueParser;
import com.salesforce.omakase.parser.declaration.NumericalValueParser;

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
        assertThat(c.parse(new Source("red"), new Grammar(), new NoopBroadcaster())).isTrue();
        assertThat(c.parse(new Source("3px"), new Grammar(), new NoopBroadcaster())).isTrue();
        assertThat(c.parse(new Source("!"), new Grammar(), new NoopBroadcaster())).isFalse();
    }
    
    @Test
    public void parsesEither2() {
        CombinationParser c = new CombinationParser(new KeywordValueParser(), new NumericalValueParser());
        assertThat(c.parse(new Source("red"), new Grammar(), new NoopBroadcaster(), true)).isTrue();
        assertThat(c.parse(new Source("3px"), new Grammar(), new NoopBroadcaster(), true)).isTrue();
        assertThat(c.parse(new Source("!"), new Grammar(), new NoopBroadcaster(), true)).isFalse();
    }
}
