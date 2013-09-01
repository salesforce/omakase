/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Test;

import com.salesforce.omakase.broadcaster.QueryableBroadcaster;
import com.salesforce.omakase.parser.declaration.KeywordValueParser;
import com.salesforce.omakase.parser.declaration.NumericalValueParser;

/**
 * Unit tests for {@link CombinationParser}.
 * 
 * @author nmcwilliams
 */
@SuppressWarnings("javadoc")
public class CombinatorParserTest {
    @Test
    public void parsesEither() {
        CombinationParser c = new CombinationParser(new KeywordValueParser(), new NumericalValueParser());
        assertThat(c.parse(new Stream("red"), new QueryableBroadcaster())).isTrue();
        assertThat(c.parse(new Stream("3px"), new QueryableBroadcaster())).isTrue();
        assertThat(c.parse(new Stream("!"), new QueryableBroadcaster())).isFalse();
    }
}
