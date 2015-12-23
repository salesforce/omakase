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

import com.google.common.base.Optional;
import com.salesforce.omakase.data.Prefix;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link Prefixes}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class PrefixesTest {
    @Rule public final ExpectedException exception = ExpectedException.none();

    @Test
    public void parsePrefixPresent() {
        Optional<Prefix> prefix = Prefixes.parsePrefix("-moz-border-radius");
        assertThat(prefix.get()).isSameAs(Prefix.MOZ);
    }

    @Test
    public void parsePrefixAbsent() {
        Optional<Prefix> prefix = Prefixes.parsePrefix("border-radius");
        assertThat(prefix.isPresent()).isFalse();
    }

    @Test
    public void parsePrefixUnknownPrefix() {
        Optional<Prefix> prefix = Prefixes.parsePrefix("-blah-border-radius");
        assertThat(prefix.isPresent()).isFalse();
    }

    @Test
    public void splitPrefixPresent() {
        Prefixes.PrefixPair pair = Prefixes.splitPrefix("-moz-border-radius");
        assertThat(pair.prefix().get()).isSameAs(Prefix.MOZ);
        assertThat(pair.unprefixed()).isEqualTo("border-radius");
    }

    @Test
    public void splitPrefixNotPresent() {
        Prefixes.PrefixPair pair = Prefixes.splitPrefix("border-radius");
        assertThat(pair.prefix().isPresent()).isFalse();
        assertThat(pair.unprefixed()).isEqualTo("border-radius");
    }

    @Test
    public void splitPrefixDashButNotPrefixed() {
        Prefixes.PrefixPair pair = Prefixes.splitPrefix("-borderradius");
        assertThat(pair.prefix().isPresent()).isFalse();
        assertThat(pair.unprefixed()).isEqualTo("-borderradius");
    }

    @Test
    public void errorIfUnknkownPrefix() {
        exception.expect(IllegalArgumentException.class);
        Prefixes.splitPrefix("-blah-border-radius");
    }

    @Test
    public void unprefixedPresent() {
        assertThat(Prefixes.unprefixed("-webkit-test")).isEqualTo("test");
    }

    @Test
    public void unprefixedAbsent() {
        assertThat(Prefixes.unprefixed("test")).isEqualTo("test");
    }

    @Test
    public void fakedOutUnprefixed() {
        assertThat(Prefixes.unprefixed("-test")).isEqualTo("-test");
    }
}
