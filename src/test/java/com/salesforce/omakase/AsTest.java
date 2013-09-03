/**
 * ADD LICENSE
 */
package com.salesforce.omakase;

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Test;

/**
 * Unit tests for {@link As}.
 * 
 * @author nmcwilliams
 */
@SuppressWarnings("javadoc")
public class AsTest {

    @Test
    public void testClass() {
        String s = As.string(this).add("1", "a").add("2", "b").toString();
        assertThat(s).isEqualTo("AsTest{1=a, 2=b}");
    }

    @Test
    public void testNamed() {
        String s = As.stringNamed("test").add("1", "a").add("2", "b").toString();
        assertThat(s).isEqualTo("test{1=a, 2=b}");
    }

    @Test
    public void testIndent() {
        String s = As.string(this).indent().add("1", "a").add("2", "b").toString();
        assertThat(s).isEqualTo("AsTest {\n  1: a\n  2: b\n}");
    }

}
