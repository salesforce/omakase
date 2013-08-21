/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

import org.junit.Test;

/**
 * Base class for testing parsers.
 * 
 * @author nmcwilliams
 */
public interface ParserTest {
    /**
     * Tests that true is always returned when parsing is expected to be successful.
     */
    @Test
    public void returnsFalseOnFailure();

    /**
     * Tests that false is always returned when parsing is expected to fail.
     */
    @Test
    public void returnsTrueOnSuccess();

    /**
     * Tests the number of broadcasted syntax units is exactly as expected.
     */
    @Test
    public void matchesExpectedBroadcastCount();

    /**
     * Tests that the content of the broadcasted syntax units is as expected.
     */
    @Test
    public void matchesExpectedBroadcastContent();

    /**
     * Tests that the stream's index doesn't change if parsing is not successful.
     */
    @Test
    public void noChangeToStreamOnFailure();

    /**
     * Tests that the stream advances to the expected index if parsing is successful.
     */
    @Test
    public void expectedStreamPositionOnSuccess();
}
