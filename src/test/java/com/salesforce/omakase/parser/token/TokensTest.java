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

package com.salesforce.omakase.parser.token;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit test for {@link Tokens}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class TokensTest {
    @Test
    public void testMatches() {
        assertThat(Tokens.ALPHA.matches('a')).isTrue();
        assertThat(Tokens.ALPHA.matches('A')).isTrue();
        assertThat(Tokens.ALPHA.matches('8')).isFalse();
        assertThat(Tokens.DIGIT.matches('0')).isTrue();
        assertThat(Tokens.DIGIT.matches('9')).isTrue();
        assertThat(Tokens.DIGIT.matches('_')).isFalse();
        assertThat(Tokens.NMCHAR.matches('9')).isTrue();
        assertThat(Tokens.NMCHAR.matches('a')).isTrue();
        assertThat(Tokens.NMCHAR.matches('$')).isFalse();
    }

    @Test
    public void doesntMatchNull() {
        for (Tokens tokens : Tokens.values()) {
            assertThat(tokens.matches('\u0000')).isFalse();
        }
    }
}
