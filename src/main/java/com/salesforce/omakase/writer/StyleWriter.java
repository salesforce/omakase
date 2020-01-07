/*
 * Copyright (c) 2015, salesforce.com, inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 *
 *    Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *    following disclaimer.
 *
 *    Redistributions in binary form must reproduce the above copyright notice, this list of conditions and
 *    the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 *    Neither the name of salesforce.com, inc. nor the names of its contributors may be used to endorse or
 *    promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.salesforce.omakase.writer;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.salesforce.omakase.PluginRegistry;
import com.salesforce.omakase.ast.Comment;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.plugin.DependentPlugin;
import com.salesforce.omakase.plugin.Plugin;
import com.salesforce.omakase.plugin.core.SyntaxTree;
import com.salesforce.omakase.util.As;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;

import static com.google.common.base.Preconditions.*;

/**
 * The main class for writing processed CSS content.
 * <p>
 * To use, add an instance of this class via {@link com.salesforce.omakase.Omakase.Request#use(Plugin...)}.
 * <p>
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
 * String classSelector = StyleWriter.inline().writeSingle(new ClassSelector("test"));
 * </code></pre>
 * <p>
 * Unless otherwise specified, {@link WriterMode#INLINE} will be used.
 * <p>
 * By default this will not write out CSS comments, however you can change that behavior with {@link #writeAllComments(boolean)}.
 *
 * @author nmcwilliams
 */
public final class StyleWriter implements DependentPlugin {
    private WriterMode mode;
    private SyntaxTree tree;
    private Multimap<Class<? extends Writable>, CustomWriter<?>> overrides;

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
    public <T extends Writable> StyleWriter addCustomWriter(Class<T> writable, CustomWriter<T> writer) {
        if (overrides == null) {
            overrides = ArrayListMultimap.create();
        }
        overrides.put(writable, writer);
        return this;
    }

    /**
     * Writes the entire processed stylesheet to a string.
     *
     * @return The CSS output.
     */
    public String write() {
        checkState(tree != null, "syntax tree not set (did you add this writer plugin before parsing?)");

        StyleAppendable appendable = new StyleAppendable();
        try {
            writeInner(tree.stylesheet(), appendable);
        } catch (IOException e) {
            throw new AssertionError("Using a StringBuilder shouldn't cause an IOException.", e);
        }
        return appendable.toString();
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
        checkNotNull(appendable, "appendable cannot be null");
        checkState(tree != null, "syntax tree not set (did you add this writer plugin before parsing?)");
        writeInner(tree.stylesheet(), new StyleAppendable(appendable));
    }

    /**
     * Writes the given syntax unit to the given {@link StyleAppendable}, taking into account any {@link CustomWriter} overrides
     * specified on this {@link StyleWriter}.
     * <p>
     * Note that the unit will only be written if {@link Writable#isWritable()} returns true. This may be false if the unit is
     * detached/destroyed, for example.
     * <p>
     * This is usually used within implementations of {@link Writable#write(StyleWriter, StyleAppendable)} to write inner units.
     * To write a single, arbitrary syntax unit use {@link #writeSingle(Writable)} instead. Do <em>not</em> call this method from
     * within a {@link CustomWriter}.
     * <p>
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
        writeInner(writable, appendable, true);
    }

    /**
     * Writes the given syntax unit to the given {@link StyleAppendable}.
     * <p>
     * Note that the unit will only be written if {@link Writable#isWritable()} returns true. This may be false if the unit is
     * detached/destroyed, for example.
     * <p>
     * This method can be used by {@link CustomWriter}s to write out the unit as normal. To write a single, isolated object use
     * {@link #writeSingle(Writable)} instead.
     * <p>
     * This will automatically handle writing out comments and orphaned comments if the given writable is a {@link Syntax} unit,
     * if applicable according to the current options.
     *
     * @param writable
     *     The unit to write.
     * @param appendable
     *     Write the unit's output to this {@link StyleAppendable}.
     * @param useOverrides
     *     If true, custom writers can alter the output behavior. Must always be false if calling this method from a custom
     *     writer.
     * @param <T>
     *     Type of the unit to write.
     *
     * @throws IOException
     *     If an I/O error occurs.
     */
    public <T extends Writable> void writeInner(T writable, StyleAppendable appendable, boolean useOverrides) throws IOException {
        incrementDepth();

        Class<? extends Writable> klass = writable.getClass();

        if (writable.isWritable()) {
            boolean handled = false;

            if (useOverrides && overrides != null && overrides.containsKey(klass)) {
                Collection<CustomWriter<?>> writers = overrides.get(klass);
                Iterator<CustomWriter<?>> iterator = writers.iterator();

                while (!handled && iterator.hasNext()) {
                    // cast is safe as long as the map is guarded by #override
                    @SuppressWarnings("unchecked")
                    CustomWriter<T> writer = (CustomWriter<T>)iterator.next();
                    handled = writer.write(writable, this, appendable);
                }
            }

            if (!handled) {
                if (writable instanceof Syntax) {
                    Syntax syntax = (Syntax)writable;
                    if (!syntax.writesOwnComments()) {
                        appendComments(syntax.comments(), appendable);
                    }
                    syntax.write(this, appendable);
                    if (!syntax.writesOwnOrphanedComments()) {
                        appendComments(syntax.orphanedComments(), appendable);
                    }
                } else {
                    writable.write(this, appendable);
                }

                // keep track of how many syntax units written at this depth
                stack.peek().incrementPeerCountAtDepth();
            }
        }

        decrementDepth();
    }

    /**
     * The easiest way to get the output of a single {@link Writable} instance.
     * <p>
     * This method is for writing disjoint units only. Examples would be for usage in test classes or in cases where you are
     * operating on a single, isolated CSS unit as opposed to a whole CSS source.
     * <p>
     * If you are implementing {@link Writable#write(StyleWriter, StyleAppendable)} then most of the time the method you want to
     * use is {@link #writeInner(Writable, StyleAppendable)}, passing in the same {@link StyleAppendable} that you were given.
     *
     * @param writable
     *     The {@link Writable} instance, e.g., a {@link Syntax} unit.
     *
     * @return The output CSS code for the given unit.
     */
    public String writeSingle(Writable writable) {
        StyleAppendable appendable = new StyleAppendable();

        try {
            writeInner(writable, appendable, true);
        } catch (IOException e) {
            // we don't expect an IO error because we know our appendable is using a string builder.
            throw new AssertionError("Using a StringBuilder shouldn't cause an IOException.", e);
        }

        return appendable.toString();
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

    /**
     * Increments the stack depth.
     * <p>
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
     * <p>
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
     * <p>
     * The depth level is incremented every time {@link #writeInner(Writable, StyleAppendable)} inner is called. For example, two
     * rules within a stylesheet are at the same depth level, two selectors within a rule, two declarations within a rule, etc...
     * <p>
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
