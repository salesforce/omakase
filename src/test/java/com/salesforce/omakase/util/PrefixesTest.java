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
