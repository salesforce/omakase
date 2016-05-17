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

package com.salesforce.omakase.ast.declaration;

import com.google.common.collect.Lists;
import com.salesforce.omakase.writer.StyleWriter;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link UnicodeRangeValue}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class UnicodeRangeValueTest {
    private UnicodeRangeValue range;

    @Test
    public void positioning() {
        range = new UnicodeRangeValue(5, 10, "u+ff0");
        assertThat(range.line()).isEqualTo(5);
        assertThat(range.column()).isEqualTo(10);
    }

    @Test
    public void getsValue() {
        range = new UnicodeRangeValue(5, 10, "u+ff0");
        assertThat(range.value()).isEqualTo("u+ff0");
    }

    @Test
    public void setsValue() {
        range = new UnicodeRangeValue(5, 10, "u+ff0");
        range.value("u+26");
        assertThat(range.value()).isEqualTo("u+26");
    }

    @Test
    public void valueLowerCased() {
        range = new UnicodeRangeValue(5, 10, "U+0025-00FF");
        assertThat(range.value()).isEqualTo("u+0025-00ff");
    }

    @Test
    public void textualValueReturnsValue() {
        range = new UnicodeRangeValue(5, 10, "u+ff0");
        assertThat(range.textualValue()).isEqualTo("u+ff0");
    }

    @Test
    public void writeVerbose() {
        range = new UnicodeRangeValue(5, 10, "u+0025-00ff");
        StyleWriter writer = StyleWriter.verbose();
        assertThat(writer.writeSingle(range)).isEqualTo("u+0025-00ff");
    }

    @Test
    public void writeInline() {
        range = new UnicodeRangeValue(5, 10, "u+0025-00ff");
        StyleWriter writer = StyleWriter.inline();
        assertThat(writer.writeSingle(range)).isEqualTo("u+0025-00ff");
    }

    @Test
    public void writeCompressed() {
        range = new UnicodeRangeValue(5, 10, "u+0025-00ff");
        StyleWriter writer = StyleWriter.compressed();
        assertThat(writer.writeSingle(range)).isEqualTo("u+0025-00ff");
    }

    @Test
    public void copyTest() {
        range = new UnicodeRangeValue(5, 10, "u+ff0");
        range.comments(Lists.newArrayList("test"));

        UnicodeRangeValue copy = range.copy();
        assertThat(copy.value()).isEqualTo(range.value());
        assertThat(copy.comments()).hasSameSizeAs(range.comments());
    }
}
