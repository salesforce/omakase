/*
 * Copyright (C) 2013 salesforce.com, inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.salesforce.omakase.writer;

import com.google.common.collect.Maps;
import com.salesforce.omakase.PluginRegistry;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.plugin.DependentPlugin;
import com.salesforce.omakase.plugin.basic.SyntaxTree;

import java.io.IOException;
import java.util.Map;

import static com.google.common.base.Preconditions.*;

/**
 * The main class for writing processed CSS content.
 * <p/>
 * To use, add an instance of this class to the Omakase request.
 * <p/>
 * Examples:
 * <pre><code>
 * StyleWriter verbose = StyleWriter.verbose();
 * Omakase.source(input).request(verbose).process();
 * String css = verbose.write();
 * </code></pre>
 * <pre><code>
 * StyleWriter compressed = StyleWriter.compressed();
 * Omakase.source(input).request(compressed).process();
 * String css = compressed.write();
 * </code></pre>
 * <pre><code>
 * StyleWriter verbose = StyleWriter.verbose();
 * Omakase.source(input).request(verbose).process();
 * verbose.writeTo(System.out);
 * </code></pre>
 * <pre><code>
 * String classSelector = StyleWriter.writeSingle(new ClassSelector("test"));
 * </code></pre>
 * <p/>
 * Unless otherwise specified, {@link WriterMode#INLINE} will be used.
 *
 * @author nmcwilliams
 */
public final class StyleWriter implements DependentPlugin {
    private final Map<Class<? extends Writable>, CustomWriter<?>> overrides = Maps.newHashMap();
    private SyntaxTree tree;
    private WriterMode mode;

    /** Creates a new {@link StyleWriter} instance using {@link WriterMode#INLINE}. */
    public StyleWriter() {
        this(WriterMode.INLINE);
    }

    /**
     * Creates a new {@link StyleWriter} instance using the given {@link WriterMode}.
     *
     * @param mode
     *     The {@link WriterMode} to use.
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
     *     The new {@link WriterMode}.
     *
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
     * Overrides the writing of a unit with the given {@link CustomWriter} instance. See {@link CustomWriter} for more details on
     * overriding.
     *
     * @param <T>
     *     The Type of unit being overridden.
     * @param writable
     *     The class of the unit to override.
     * @param writer
     *     The {@link CustomWriter} override.
     *
     * @return this, for chaining.
     */
    public <T extends Writable> StyleWriter override(Class<T> writable, CustomWriter<T> writer) {
        overrides.put(writable, writer);
        return this;
    }

    /**
     * Writes the entire processed stylesheet to the given {@link Appendable}.
     *
     * @param appendable
     *     Write the processed CSS source code to this appendable.
     *
     * @throws IOException
     *     If an I/O error occurs.
     */
    public void writeTo(Appendable appendable) throws IOException {
        writeTo(new StyleAppendable(appendable));
    }

    /**
     * Writes the entire processed stylesheet to the given {@link StyleAppendable}.
     *
     * @param appendable
     *     Write the processed CSS source code to this appendable.
     *
     * @throws IOException
     *     If an I/O error occurs.
     */
    public void writeTo(StyleAppendable appendable) throws IOException {
        checkNotNull(appendable, "appendable cannot be null");
        checkState(tree != null, "syntax tree not set (did you include this writer in the request?)");
        writeInner(tree.stylesheet(), appendable);
    }

    /**
     * Writes the entire processed stylesheet to a string.
     *
     * @return The CSS output.
     */
    public String write() {
        checkState(tree != null, "syntax tree not set (did you include this writer in the request?)");

        StyleAppendable appendable = new StyleAppendable();
        try {
            writeTo(appendable);
        } catch (IOException e) {
            throw new AssertionError("Using a StringBuilder shouldn't cause an IOException.", e);
        }
        return appendable.toString();
    }

    /**
     * Writes the given syntax unit to the given {@link StyleAppendable}, taking into account any {@link CustomWriter} overrides
     * specified on this {@link StyleWriter}.
     * <p/>
     * Note that the unit will only be written if {@link Writable#isWritable()} returns true. This may be false if the unit is
     * detached, for example.
     * <p/>
     * This is usually used within implementations of {@link Writable#write(StyleWriter, StyleAppendable)} to write inner units .
     * To write a single, isolated object then use {@link #writeSnippet (Writable)} instead.
     *
     * @param <T>
     *     Type of the unit to write.
     * @param writable
     *     The unit to write.
     * @param appendable
     *     Write the unit's output to this {@link StyleAppendable}.
     *
     * @throws IOException
     *     If an I/O error occurs.
     */
    @SuppressWarnings("unchecked")
    public <T extends Writable> void writeInner(T writable, StyleAppendable appendable) throws IOException {
        Class<? extends Writable> klass = writable.getClass();

        if (overrides.containsKey(klass)) {
            // cast is safe as long as the map is guarded by #override
            ((CustomWriter<T>)overrides.get(klass)).write(writable, this, appendable);
        } else if (writable.isWritable()) {
            writable.write(this, appendable);
        }
    }

    /**
     * Writes the given syntax unit, taking into account any {@link CustomWriter} overrides specified on this {@link
     * StyleWriter}.
     * <p/>
     * <b>Important:</b> This method is for writing disjoint units only. Examples would be for usage in test classes or in cases
     * where you are operating on a single CSS snippet as opposed to a whole CSS source. If you are implementing a syntax unit's
     * write method then most of the time the method you want to use is {@link #writeInner(Writable, StyleAppendable)}, passing in the
     * same {@link StyleAppendable} that you were given.
     *
     * The difference between this and {@link #writeSingle(Writable)} is that this is a non-static method that takes into
     * account any given {@link CustomWriter} overrides.
     * <p/>
     * As this is for writing individual units, it bypasses the {@link Writable#isWritable()} check.
     *
     * @param writable
     *     The unit to write.
     * @param <T>
     *     Type of the unit to write.
     *
     * @return The output CSS code for the given unit.
     */
    @SuppressWarnings("unchecked")
    public <T extends Writable> String writeSnippet(T writable) {
        Class<? extends Writable> klass = writable.getClass();
        StyleAppendable appendable = new StyleAppendable();

        try {
            if (overrides.containsKey(klass)) {
                // cast is safe as long as the map is guarded by #override
                ((CustomWriter<T>)overrides.get(klass)).write(writable, this, appendable);
            } else {
                // skip isWritable check
                writable.write(this, appendable);
            }
        } catch (IOException e) {
            // we don't expect an IO error because we know our appendable is using a string builder.
            throw new AssertionError("unexpected IO error");
        }

        return appendable.toString();
    }

    /**
     * Creates a new {@link StyleWriter} with {@link WriterMode#VERBOSE} mode.
     *
     * @return The new {@link StyleWriter} instance.
     */
    public static StyleWriter verbose() {
        return new StyleWriter(WriterMode.VERBOSE);
    }

    /**
     * Creates a new {@link StyleWriter} with {@link WriterMode#INLINE} mode.
     *
     * @return The new {@link StyleWriter} instance.
     */
    public static StyleWriter inline() {
        return new StyleWriter(WriterMode.INLINE);
    }

    /**
     * Creates a new {@link StyleWriter} with {@link WriterMode#COMPRESSED} mode.
     *
     * @return The new {@link StyleWriter} instance.
     */
    public static StyleWriter compressed() {
        return new StyleWriter(WriterMode.COMPRESSED);
    }

    /**
     * A shortcut to {@link #writeSnippet(Writable)} that doesn't use any {@link CustomWriter} overrides. This also bypasses the
     * {@link Writable#isWritable()} check. {@link WriterMode#INLINE} is used as a default.
     * <p/>
     * This is the simplest way to get the output of a single {@link Writable} instance.
     *
     * @param writable
     *     The {@link Writable} instance, e.g., a {@link Syntax} unit.
     *
     * @return The output CSS code for the given unit.
     */
    public static String writeSingle(Writable writable) {
        return writeSingle(writable, WriterMode.INLINE);
    }

    /**
     * A shortcut to {@link #writeSnippet(Writable)} that doesn't use any {@link CustomWriter} overrides. This also bypasses the
     * {@link Writable#isWritable()} check.
     *
     * @param writable
     *     The {@link Writable} instance, e.g., a {@link Syntax} unit.
     * @param mode
     *     The {@link WriterMode}.
     *
     * @return The output CSS code for the given unit.
     */
    public static String writeSingle(Writable writable, WriterMode mode) {
        return new StyleWriter(mode).writeSnippet(writable);
    }
}
