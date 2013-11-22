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
