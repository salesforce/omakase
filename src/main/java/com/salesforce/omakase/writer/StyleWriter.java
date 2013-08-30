/**
 * ADD LICENSE
 */
package com.salesforce.omakase.writer;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.salesforce.omakase.PluginRegistry;
import com.salesforce.omakase.ast.Writable;
import com.salesforce.omakase.plugin.DependentPlugin;
import com.salesforce.omakase.plugin.basic.SyntaxTree;

/**
 * TESTME The main class for writing processed CSS content.
 * 
 * <p>
 * To use, add an instance of this class to the Omakase request. Examples:
 * 
 * <pre><code>
 * StyleWriter verbose = StyleWriter.verbose();
 * Omakase.source(input).request(verbose).process();
 * String css = verbose.write();
 * </code></pre>
 * 
 * <pre><code>
 * StyleWriter compressed = StyleWriter.compressed();
 * Omakase.source(input).request(compressed).process();
 * String css = compressed.write();
 * </code></pre>
 * 
 * <pre><code>
 * StyleWriter verbose = StyleWriter.verbose();
 * Omakase.source(input).request(verbose).process();
 * verbose.write(System.out);
 * </code></pre>
 * 
 * <p>
 * Unless specified, {@link WriterMode#INLINE} will be used.
 * 
 * @author nmcwilliams
 */
public class StyleWriter implements DependentPlugin {
    private Map<Class<? extends Writable>, CustomWriter<?>> overrides = new HashMap<>();
    private SyntaxTree tree;
    private WriterMode mode;

    /**
     * Creates a new {@link StyleWriter} instance using {@link WriterMode#INLINE}.
     */
    public StyleWriter() {
        this(WriterMode.INLINE);
    }

    /**
     * Creates a new {@link StyleWriter} instance using the given {@link WriterMode}.
     * 
     * @param mode
     *            The {@link WriterMode} to use.
     */
    public StyleWriter(WriterMode mode) {
        this.mode = mode;
    }

    @Override
    public void dependencies(PluginRegistry registry) {
        tree = registry.require(SyntaxTree.class);
    }

    /**
     * Sets the {@link WriterMode}.
     * 
     * @param mode
     *            The new {@link WriterMode}.
     * @return this, for chaining.
     */
    public StyleWriter mode(WriterMode mode) {
        this.mode = checkNotNull(mode, "mode cannot be null");
        return this;
    }

    /**
     * Gets whether the current {@link WriterMode} is {@link WriterMode#VERBOSE}.
     * 
     * @return True if the current {@link WriterMode} is verbose.
     */
    public boolean isVerbose() {
        return mode == WriterMode.VERBOSE;
    }

    /**
     * Gets whether the current {@link WriterMode} is {@link WriterMode#INLINE}.
     * 
     * @return True if the current {@link WriterMode} is inline.
     */
    public boolean isInline() {
        return mode == WriterMode.INLINE;
    }

    /**
     * Gets whether the current {@link WriterMode} is {@link WriterMode#COMPRESSED}.
     * 
     * @return True if the current {@link WriterMode} is compressed.
     */
    public boolean isCompressed() {
        return mode == WriterMode.COMPRESSED;
    }

    /**
     * Overrides the writing of a unit with the given {@link CustomWriter} instance. See {@link CustomWriter} for more
     * details on overriding.
     * 
     * @param <T>
     *            The Type of unit being overridden.
     * @param writable
     *            The class of the unit to override.
     * @param writer
     *            The {@link CustomWriter} override.
     * @return this, for chaining.
     */
    public <T extends Writable> StyleWriter override(Class<T> writable, CustomWriter<T> writer) {
        overrides.put(writable, writer);
        return this;
    }

    /**
     * Writes the processed CSS source to a string.
     * 
     * @return The CSS output.
     * @throws IOException
     *             If an I/O error occurs.
     */
    public String write() throws IOException {
        checkState(tree != null, "syntax tree not set (did you include this writer in the request?)");

        StyleAppendable appendable = new StyleAppendable();
        write(appendable);
        return appendable.toString();
    }

    /**
     * Writes the processed CSS source code to the given {@link Appendable}.
     * 
     * @param appendable
     *            Write the processed CSS source code to this appendable.
     * @throws IOException
     *             If an I/O error occurs.
     */
    public void write(Appendable appendable) throws IOException {
        write(new StyleAppendable(appendable));
    }

    /**
     * Writes the processed CSS source code to the given {@link StyleAppendable}.
     * 
     * @param appendable
     *            Write the processed CSS source code to this appendable.
     * @throws IOException
     *             If an I/O error occurs.
     */
    public void write(StyleAppendable appendable) throws IOException {
        checkNotNull(appendable, "appendable cannot be null");
        checkState(tree != null, "syntax tree not set (did you include this writer in the request?)");
        write(tree.stylesheet(), appendable);
    }

    /**
     * Writes the given syntax unit to the given {@link StyleAppendable}, taking into account any {@link CustomWriter}
     * overrides specified on this {@link StyleWriter}.
     * 
     * @param <T>
     *            Type of the unit to write.
     * @param writable
     *            The unit to write.
     * @param appendable
     *            Write the unit's output to this {@link StyleAppendable}.
     * @throws IOException
     *             If an I/O error occurs.
     */
    @SuppressWarnings("unchecked")
    public <T extends Writable> void write(T writable, StyleAppendable appendable) throws IOException {
        Class<? extends Writable> klass = writable.getClass();

        if (overrides.containsKey(klass)) {
            // cast is safe as long as the map is guarded by #override
            ((CustomWriter<T>)overrides.get(klass)).write(writable, this, appendable);
        } else {
            writable.write(this, appendable);
        }
    }
}
