/**
 * ADD LICENSE
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
            assertThat(tokens.matches(null)).isFalse();
        }
    }
}
