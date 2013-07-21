/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

import org.junit.Before;
import org.junit.Test;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
@SuppressWarnings("javadoc")
public class StreamTest {
    Stream stream;

    @Before
    public void before() {
        stream = new Stream(".testing #is > fun { color: red; margin: 10px 5px; }");

    }

    @Test
    public void test() {
        while (!stream.eof()) {
            System.out.print(stream.next());
        }
    }
}
