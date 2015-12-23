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

package com.salesforce.omakase.parser.token;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link CompoundToken}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class CompoundTokenTest {
    @Test
    public void matchesEither() {
        CompoundToken compound = new CompoundToken(Tokens.ALPHA, Tokens.DIGIT);
        assertThat(compound.matches('a')).isTrue();
        assertThat(compound.matches('9')).isTrue();
        assertThat(compound.matches('#')).isFalse();
    }

    @Test
    public void matchesMultipleCompounds() {
        CompoundToken compound1 = new CompoundToken(Tokens.ALPHA, Tokens.DIGIT);
        CompoundToken compound2 = new CompoundToken(Tokens.HYPHEN, Tokens.PLUS);
        CompoundToken compound3 = new CompoundToken(compound1, compound2);

        assertThat(compound3.matches('a')).isTrue();
        assertThat(compound3.matches('9')).isTrue();
        assertThat(compound3.matches('-')).isTrue();
        assertThat(compound3.matches('+')).isTrue();
        assertThat(compound3.matches('#')).isFalse();
    }

    @Test
    public void doesntMatchNull() {
        CompoundToken compound = new CompoundToken(Tokens.ALPHA, Tokens.DIGIT);
        assertThat(compound.matches('\u0000')).isFalse();
    }
}
