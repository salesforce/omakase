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

import com.salesforce.omakase.PluginRegistry;
import com.salesforce.omakase.ast.Comment;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.plugin.DependentPlugin;
import com.salesforce.omakase.plugin.basic.SyntaxTree;
import com.salesforce.omakase.util.As;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
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
 * Omakase.source(input).use(verbose).process();
 * String css = verbose.write();
 * </code></pre>
 * <pre><code>
 * StyleWriter compressed = StyleWriter.compressed();
 * Omakase.source(input).use(compressed).process();
 * String css = compressed.write();
 * </code></pre>
 * <pre><code>
 * StyleWriter verbose = StyleWriter.verbose();
 * Omakase.source(input).use(verbose).process();
 * verbose.writeTo(System.out);
 * </code></pre>
 * <pre><code>
 * String classSelector = StyleWriter.writeSingle(new ClassSelector("test"));
 * </code></pre>
 * <p/>
 * Unless otherwise specified, {@link WriterMode#INLINE} will be used.
 * <p/>
 * By default this will not write out CSS comments, however you can change that behavior with {@link #writeAllComments(boolean)}.
 *
 * @author nmcwilliams
 */
public final class StyleWriter implements DependentPlugin {
    private final Map<Class<? extends Writable>, CustomWriter<?>> overrides = new HashMap<>();

    private SyntaxTree tree;
    private WriterMode mode;

    private boolean writeAllComments;
    private boolean writeAnnotatedComments;
    private boolean writeBangComments;

    private final Deque<StackEntry> stack = new ArrayDeque<>();

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
     * Gets the {@link WriterMode}.
     *
     * @return The writer mode.
     */
    public WriterMode mode() {
        return mode;
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
     * Sets whether all comments will be written out.
     *
     * @param writeComments
     *     Whether all comments should be written out.
     *
     * @return this, for chaining.
     */
    public StyleWriter writeAllComments(boolean writeComments) {
        this.writeAllComments = writeComments;
        return this;
    }

    /**
     * Gets whether all comments will be written out.
     *
     * @return True if comments will be written out.
     */
    public boolean shouldWriteAllComments() {
        return writeAllComments;
    }

    /**
     * Sets whether comments with annotations should be written out, even if {@link #shouldWriteAllComments()} is false. This can
     * be useful to preserve annotations across parsing operations.
     *
     * @param writeAnnotatedComments
     *     Whether comments with annotations should be written out.
     *
     * @return this, for chaining.
     */
    public StyleWriter writeAnnotatedComments(boolean writeAnnotatedComments) {
        this.writeAnnotatedComments = writeAnnotatedComments;
        return this;
    }

    /**
     * Returns whether comments with annotations should be written out, even if {@link #shouldWriteAllComments()} is false. This
     * can be useful to preserve annotations across parsing operations.
     *
     * @return True if annotated comments should always be written out.
     */
    public boolean shouldWriteAnnotatedComments() {
        return writeAllComments || writeAnnotatedComments;
    }

    /**
     * Returns whether comments starting with '!' should be written out, even if {@link #shouldWriteAllComments()} is false. There
     * must not be any whitespace between the comment open and the bang. This may be useful for preserving copyrights.
     *
     * @param writeBangComments
     *     Whether comments with bangs should be written out.
     *
     * @return this, for chaining.
     */
    public StyleWriter writeBangComments(boolean writeBangComments) {
        this.writeBangComments = writeBangComments;
        return this;
    }

    /**
     * Returns whether comments starting with '!' should be written out, even if {@link #shouldWriteAllComments()} is false. There
     * must not be any whitespace between the comment open and the bang. This may be useful for preserving copyrights.
     *
     * @return True if annotated comments should always be written out.
     */
    public boolean shouldWriteBangComments() {
        return writeAllComments || writeBangComments;
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
     * This is usually used within implementations of {@link Writable#write(StyleWriter, StyleAppendable)} to write inner units.
     * To write a single, isolated object use {@link #writeSnippet(Writable)} instead.
     * <p/>
     * This will automatically handle writing out comments and orphaned comments if the given writable is a {@link Syntax} unit,
     * if applicable according to the current options.
     *
     * @param writable
     *     The unit to write.
     * @param appendable
     *     Write the unit's output to this {@link StyleAppendable}.
     * @param <T>
     *     Type of the unit to write.
     *
     * @throws IOException
     *     If an I/O error occurs.
     */
    public <T extends Writable> void writeInner(T writable, StyleAppendable appendable) throws IOException {
        incrementDepth();

        Class<? extends Writable> klass = writable.getClass();
        Syntax syntax = (writable instanceof Syntax) ? (Syntax)writable : null;

        if (overrides.containsKey(klass)) {
            // cast is safe as long as the map is guarded by #override
            @SuppressWarnings("unchecked")
            CustomWriter<T> custom = (CustomWriter<T>)overrides.get(klass);
            custom.write(writable, this, appendable);
        } else if (writable.isWritable()) {
            // write comments
            if (syntax != null && !syntax.writesOwnComments()) {
                appendComments(syntax.comments(), appendable);
            }

            // write the object
            writable.write(this, appendable);

            // write orphaned comments
            if (syntax != null && !syntax.writesOwnOrphanedComments()) {
                appendComments(syntax.orphanedComments(), appendable);
            }

            // keep track of how many syntax units written at this depth
            stack.peek().incrementPeerCountAtDepth();
        }

        decrementDepth();
    }

    /**
     * Writes the given syntax unit, taking into account any {@link CustomWriter} overrides specified on this {@link
     * StyleWriter}.
     * <p/>
     * <b>Important:</b> This method is for writing disjoint units only. Examples would be for usage in test classes or in cases
     * where you are operating on a single CSS snippet as opposed to a whole CSS source. If you are implementing a syntax unit's
     * write method then most of the time the method you want to use is {@link #writeInner(Writable, StyleAppendable)}, passing in
     * the same {@link StyleAppendable} that you were given.
     * <p/>
     * The difference between this and {@link #writeSingle(Writable)} is that this is a non-static method that takes into account
     * any given {@link CustomWriter} overrides.
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
    public <T extends Writable> String writeSnippet(T writable) {
        StyleAppendable appendable = new StyleAppendable();

        try {
            writeInner(writable, appendable);
        } catch (IOException e) {
            // we don't expect an IO error because we know our appendable is using a string builder.
            throw new AssertionError("unexpected IO error");
        }

        return appendable.toString();
    }

    /**
     * Increments the stack depth.
     * <p/>
     * This is usually handled automatically internally, however you may want to call this when writing out inner objects at an
     * artificially deeper depth level. If you do so, be sure to call {@link #decrementDepth()} at the end.
     *
     * @return this, for chaining.
     */
    public StyleWriter incrementDepth() {
        stack.push(new StackEntry(stack.peek()));
        return this;
    }

    /**
     * Decrements the stack depth.
     * <p/>
     * This is usually handled automatically internally. See {@link #incrementDepth()} for details on when you may want to use
     * this manually.
     *
     * @return this, for chaining.
     */
    public StyleWriter decrementDepth() {
        stack.pop();
        return this;
    }

    /**
     * Gets the count of the number of peer units previously written out at the current depth level. Units that return false from
     * {@link Writable#isWritable()} are not included.
     *
     * @return The number of peers previously written out at the current depth level.
     */
    public int countAtCurrentDepth() {
        return stack.isEmpty() ? 0 : stack.peek().numberOfPreviousPeers();
    }

    /**
     * Gets whether no peer units have been written out at the current depth level.
     * <p/>
     * The depth level is incremented every time {@link #writeInner(Writable, StyleAppendable)} inner is called. For example, two
     * rules within a stylesheet are at the same depth level, two selectors within a rule, two declarations within a rule, etc...
     * <p/>
     * This is usually used when something should be written only for the first unit in a collection, or conversely for all units
     * but the first.
     *
     * @return True if no peer units have been written out at the current depth level.
     */
    public boolean isFirstAtCurrentDepth() {
        return countAtCurrentDepth() == 0;
    }

    @Override
    public String toString() {
        return As.string(this).add("mode", mode).add("writeComments", writeAllComments).toString();
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

    /**
     * Utility method for assisting with writing comments.
     *
     * @param comments
     *     The comments to write.
     * @param appendable
     *     Write the unit's output to this {@link StyleAppendable}.
     *
     * @throws IOException
     *     If an I/O error occurs.
     * @see Syntax#writesOwnComments()
     * @see Syntax#writesOwnOrphanedComments()
     */
    public void appendComments(Iterable<Comment> comments, StyleAppendable appendable) throws
        IOException {
        for (Comment comment : comments) {
            if (shouldWriteAllComments()) {
                writeInner(comment, appendable);
            } else if (shouldWriteAnnotatedComments() && comment.annotation().isPresent()) {
                writeInner(comment, appendable);
            } else if (shouldWriteBangComments() && comment.startsWithBang()) {
                writeInner(comment, appendable);
            }
        }
    }

    /** used to help keep track of how many inner units are written at a certain depth. */
    private static final class StackEntry {
        private final StackEntry parent;
        private int numChildren;

        public StackEntry(StackEntry parent) {
            this.parent = parent;
        }

        /** increment the count of children directly below this depth */
        public void incrementNumChildren() {
            numChildren++;
        }

        /** gets the number of children directly below this depth */
        public int totalChildren() {
            return numChildren;
        }

        /** increment the number of peers at this depth */
        public void incrementPeerCountAtDepth() {
            if (this.parent != null) {
                this.parent.incrementNumChildren();
            }
        }

        /** gets the number of peers at this depth */
        public int numberOfPreviousPeers() {
            return this.parent != null ? this.parent.totalChildren() : 0;
        }
    }
}
