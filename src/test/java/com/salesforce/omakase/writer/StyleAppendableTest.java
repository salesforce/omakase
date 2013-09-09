/**
 * ADD LICENSE
 */
package com.salesforce.omakase.writer;

import org.junit.Test;

import java.io.IOException;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link StyleAppendable}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("javadoc")
public class StyleAppendableTest {
    @Test
    public void appendChar() throws IOException {
        StyleAppendable sa = new StyleAppendable();
        sa.append('a');
        assertThat(sa.toString()).isEqualTo("a");
    }

    @Test
    public void appendString() throws IOException {
        StyleAppendable sa = new StyleAppendable();
        sa.append("abc");
        assertThat(sa.toString()).isEqualTo("abc");
    }

    @Test
    public void newline() throws IOException {
        StyleAppendable sa = new StyleAppendable();
        sa.newline();
        assertThat(sa.toString()).isEqualTo("\n");
    }

    @Test
    public void newlineIf() throws IOException {
        StyleAppendable sa = new StyleAppendable();
        sa.newlineIf(true);
        assertThat(sa.toString()).isEqualTo("\n");
    }

    @Test
    public void newlineIfFalse() throws IOException {
        StyleAppendable sa = new StyleAppendable();
        sa.newlineIf(false);
        assertThat(sa.toString()).isEqualTo("");
    }

    @Test
    public void space() throws IOException {
        StyleAppendable sa = new StyleAppendable();
        sa.space();
        assertThat(sa.toString()).isEqualTo(" ");
    }

    @Test
    public void spaceIf() throws IOException {
        StyleAppendable sa = new StyleAppendable();
        sa.spaceIf(true);
        assertThat(sa.toString()).isEqualTo(" ");
    }

    @Test
    public void spaceIfFalse() throws IOException {
        StyleAppendable sa = new StyleAppendable();
        sa.spaceIf(false);
        assertThat(sa.toString()).isEqualTo("");
    }

    @Test
    public void indent() throws IOException {
        StyleAppendable sa = new StyleAppendable();
        sa.indent();
        assertThat(sa.toString()).isEqualTo("  ");
    }

    @Test
    public void indentIf() throws IOException {
        StyleAppendable sa = new StyleAppendable();
        sa.indentIf(true);
        assertThat(sa.toString()).isEqualTo("  ");
    }

    @Test
    public void indentIfFalse() throws IOException {
        StyleAppendable sa = new StyleAppendable();
        sa.indentIf(false);
        assertThat(sa.toString()).isEqualTo("");
    }

    @Test
    public void appendToGiven() throws IOException {
        StringBuilder b = new StringBuilder();
        StyleAppendable sa = new StyleAppendable(b);
        sa.append('c');
        assertThat(b.toString()).isEqualTo("c");
    }
}
