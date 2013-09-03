/**
 * ADD LICENSE
 */
package com.salesforce.omakase.writer;

import static org.fest.assertions.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Test;

import com.salesforce.omakase.Omakase;
import com.salesforce.omakase.ast.selector.Selector;

/**
 * Unit tests for {@link CustomWriter}.
 * 
 * @author nmcwilliams
 */
@SuppressWarnings("javadoc")
public class CustomWriterTest {
    @Test
    public void testCustomWriter() {
        StyleWriter writer = StyleWriter.compressed();
        writer.override(Selector.class, new TestCustomWriter());
        Omakase.source(".class{color:red}").add(writer).process();

        assertThat(writer.write()).isEqualTo("CUSTOM.class{color:red}");
    }

    public static final class TestCustomWriter implements CustomWriter<Selector> {
        @Override
        public void write(Selector selector, StyleWriter writer, StyleAppendable appendable) throws IOException {
            appendable.append("CUSTOM");
            selector.write(writer, appendable);
        }
    }
}
