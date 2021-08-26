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

import static org.fest.assertions.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Test;

import com.salesforce.omakase.writer.StyleWriter;

/**
 * Unit tests for {@link Operator}.
 *
 * @author nmcwilliams
 */
public class OperatorTest {
    @Test
    public void testGetType() {
        Operator operator = new Operator(OperatorType.COMMA);
        assertThat(operator.type()).isSameAs(OperatorType.COMMA);
    }

    @Test
    public void testWriteCommaCompressed() throws IOException {
        Operator operator = new Operator(OperatorType.COMMA);
        assertThat(StyleWriter.compressed().writeSingle(operator)).isEqualTo(",");
    }

    @Test
    public void testWriteCommaVerbose() {
        Operator operator = new Operator(OperatorType.COMMA);
        assertThat(StyleWriter.verbose().writeSingle(operator)).isEqualTo(", ");
    }

    @Test
    public void testWriteSlashCompressed() {
        Operator operator = new Operator(OperatorType.SLASH);
        assertThat(StyleWriter.compressed().writeSingle(operator)).isEqualTo("/");
    }

    @Test
    public void testWriteSlashVerbose() {
        Operator operator = new Operator(OperatorType.SLASH);
        assertThat(StyleWriter.verbose().writeSingle(operator)).isEqualTo(" / ");
    }

    @Test
    public void copyTest() {
        Operator operator = new Operator(OperatorType.COMMA);
        assertThat(operator.copy().type()).isSameAs(operator.type());
    }
}
