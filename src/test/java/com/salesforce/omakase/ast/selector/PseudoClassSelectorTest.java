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

package com.salesforce.omakase.ast.selector;

import com.salesforce.omakase.writer.StyleWriter;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;

import static org.fest.assertions.api.Assertions.assertThat;

/** Unit tests for {@link PseudoClassSelector}. */
@SuppressWarnings("JavaDoc")
public class PseudoClassSelectorTest {
    @Rule public final ExpectedException exception = ExpectedException.none();

    @Test
    public void argsAbsentByDefault() {
        PseudoClassSelector s = new PseudoClassSelector("hover");
        assertThat(s.args().isPresent()).isFalse();
    }

    @Test
    public void constructorWithBothNameAndArgs() {
        PseudoClassSelector s = new PseudoClassSelector("nth-child", "2n+1");
        assertThat(s.name()).isEqualTo("nth-child");
        assertThat(s.args().get()).isEqualTo("2n+1");
    }

    @Test
    public void getName() {
        PseudoClassSelector s = new PseudoClassSelector("hover");
        assertThat(s.name()).isEqualTo("hover");
    }

    @Test
    public void setName() {
        PseudoClassSelector s = new PseudoClassSelector("hover");
        s.name("visited");
        assertThat(s.name()).isEqualTo("visited");
    }

    @Test
    public void errorsIfNameIsPseudoElement() {
        PseudoClassSelector s = new PseudoClassSelector("hover");
        exception.expect(IllegalArgumentException.class);
        s.name("before");
    }

    @Test
    public void getArgs() {
        PseudoClassSelector s = new PseudoClassSelector(5, 5, "nth-child", "-2n+1");
        assertThat(s.args().get()).isEqualTo("-2n+1");
    }

    @Test
    public void setArgs() {
        PseudoClassSelector s = new PseudoClassSelector("nth-child", "-2n+1");
        s.args("even");
        assertThat(s.args().get()).isEqualTo("even");
    }

    @Test
    public void removeArgs() {
        PseudoClassSelector s = new PseudoClassSelector("nth-child", "-2n+1");
        s.args(null);
        assertThat(s.args().isPresent()).isFalse();
    }

    @Test
    public void type() {
        assertThat(new PseudoClassSelector("hover").type()).isSameAs(SelectorPartType.PSEUDO_CLASS_SELECTOR);
    }

    @Test
    public void write() throws IOException {
        PseudoClassSelector s = new PseudoClassSelector("hover");
        assertThat(StyleWriter.compressed().writeSingle(s)).isEqualTo(":hover");
    }

    @Test
    public void writeWithArgs() throws IOException {
        PseudoClassSelector s = new PseudoClassSelector("nth-child", "2n+1");
        assertThat(StyleWriter.compressed().writeSingle(s)).isEqualTo(":nth-child(2n+1)");
    }

    @Test
    public void copyWithArgs() {
        PseudoClassSelector s = new PseudoClassSelector("nth-child", "-2n+1");
        PseudoClassSelector copy = (PseudoClassSelector)s.copy();
        assertThat(copy.name()).isEqualTo(s.name());
        assertThat(copy.args().get()).isEqualTo(s.args().get());
    }

    @Test
    public void copyNoArgs() {
        PseudoClassSelector s = new PseudoClassSelector("hover");
        PseudoClassSelector copy = (PseudoClassSelector)s.copy();
        assertThat(copy.name()).isEqualTo(s.name());
        assertThat(copy.args().isPresent()).isFalse();
    }
}
