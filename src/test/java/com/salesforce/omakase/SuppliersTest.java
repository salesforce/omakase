/**
 * ADD LICENSE
 */
package com.salesforce.omakase;

import com.salesforce.omakase.plugin.basic.AutoRefiner;
import com.salesforce.omakase.plugin.basic.SyntaxTree;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for Suppliers.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
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
