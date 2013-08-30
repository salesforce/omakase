/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser.token;

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Test;

/**
 * Unit tests for {@link CompoundToken}.
 * 
 * @author nmcwilliams
 */
@SuppressWarnings("javadoc")
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
        assertThat(compound.matches(null)).isFalse();
    }
}
