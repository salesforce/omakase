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

package com.salesforce.omakase.util;

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Test;

import com.salesforce.omakase.ast.selector.IdSelector;
import com.salesforce.omakase.parser.factory.StandardParserFactory;

/**
 * Unit tests for {@link Parsers}.
 *
 * @author nmcwilliams
 */
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
        assertThat(Parsers.parseSimple("blah", StandardParserFactory.instance().idSelectorParser(), IdSelector.class).isPresent()).isFalse();
    }

    @Test
    public void parseSimplePresent() {
        assertThat(Parsers.parseSimple("#id", StandardParserFactory.instance().idSelectorParser(), IdSelector.class).isPresent()).isTrue();
    }
}
