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

import static org.fest.assertions.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Test;

import com.salesforce.omakase.writer.StyleWriter;

/** Unit tests for {@link Combinator}. */
public class CombinatorTest {
    @Test
    public void typeWhenDescendant() {
        Combinator c = Combinator.descendant();
        assertThat(c.type()).isSameAs(SelectorPartType.DESCENDANT_COMBINATOR);
    }

    @Test
    public void typeWhenChild() {
        Combinator c = Combinator.child();
        assertThat(c.type()).isSameAs(SelectorPartType.CHILD_COMBINATOR);
    }

    @Test
    public void typeWhenAdjacent() {
        Combinator c = Combinator.adjacent();
        assertThat(c.type()).isSameAs(SelectorPartType.ADJACENT_SIBLING_COMBINATOR);
    }

    @Test
    public void typeWhenGeneral() {
        Combinator c = Combinator.general();
        assertThat(c.type()).isSameAs(SelectorPartType.GENERAL_SIBLING_COMBINATOR);
    }

    @Test
    public void writeDescendant() throws IOException {
        Combinator c = Combinator.descendant();
        assertThat(StyleWriter.compressed().writeSingle(c)).isEqualTo(" ");
    }

    @Test
    public void writeChild() throws IOException {
        Combinator c = Combinator.child();
        assertThat(StyleWriter.compressed().writeSingle(c)).isEqualTo(">");
    }

    @Test
    public void writeAdjacent() throws IOException {
        Combinator c = Combinator.adjacent();
        assertThat(StyleWriter.compressed().writeSingle(c)).isEqualTo("+");
    }

    @Test
    public void writeGeneral() throws IOException {
        Combinator c = Combinator.general();
        assertThat(StyleWriter.compressed().writeSingle(c)).isEqualTo("~");
    }

    @Test
    public void copy() {
        Combinator c = Combinator.general();
        assertThat(c.copy().type()).isSameAs(c.type());
    }
}
