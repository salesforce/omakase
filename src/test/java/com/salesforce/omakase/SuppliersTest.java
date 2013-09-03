/**
 * ADD LICENSE
 */
package com.salesforce.omakase;

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Test;

import com.salesforce.omakase.plugin.basic.AutoRefiner;
import com.salesforce.omakase.plugin.basic.SyntaxTree;

/**
 * Unit tests for Suppliers.
 * 
 * @author nmcwilliams
 */
@SuppressWarnings("javadoc")
public class SuppliersTest {
    @Test
    public void testSyntaxTree() {
        assertThat(Suppliers.get(SyntaxTree.class).isPresent()).isTrue();
    }

    @Test
    public void testAutoRefiner() {
        assertThat(Suppliers.get(AutoRefiner.class).isPresent()).isTrue();
    }
}
