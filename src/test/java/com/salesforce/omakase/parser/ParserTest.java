/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

import org.junit.Test;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public interface ParserTest {
    /**
     * TODO Description
     * 
     */
    @Test
    public void returnsFalseOnFailure();

    /**
     * TODO Description
     * 
     */
    @Test
    public void returnsTrueOnSuccess();

    /**
     * TODO Description
     * 
     */
    @Test
    public void matchesExpectedBroadcastCount();

    /**
     * TODO Description
     * 
     */
    @Test
    public void matchesExpectedBroadcastContent();

    /**
     * TODO Description
     * 
     */
    @Test
    public void noChangeToStreamOnFailure();

    /**
     * TODO Description
     * 
     */
    @Test
    public void expectedStreamPositionOnSuccess();
}
