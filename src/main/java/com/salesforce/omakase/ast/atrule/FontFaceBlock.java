/*
 * Copyright (C) 2014 salesforce.com, inc.
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

package com.salesforce.omakase.ast.atrule;

import com.salesforce.omakase.SupportMatrix;
import com.salesforce.omakase.ast.Statement;
import com.salesforce.omakase.ast.StatementIterable;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.collection.LinkedSyntaxCollection;
import com.salesforce.omakase.ast.collection.SyntaxCollection;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.parser.refiner.FontFaceRefiner;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;
import java.util.Iterator;

/**
 * Represents the block of a font-face at-rule.
 *
 * @author nmcwilliams
 * @see FontDescriptor
 * @see FontFaceRefiner
 */
public final class FontFaceBlock extends AbstractAtRuleMember implements AtRuleBlock {
    private final SyntaxCollection<FontFaceBlock, FontDescriptor> fontDescriptors;

    /**
     * Creates a new instance with no line or number specified (used for dynamically created {@link Syntax} units).
     */
    public FontFaceBlock() {
        this(-1, -1, null);
    }

    /**
     * Constructs a new {@link FontFaceBlock} instance.
     *
     * @param line
     *     The line number.
     * @param column
     *     The column number.
     * @param broadcaster
     *     Used to broadcast new units.
     */
    public FontFaceBlock(int line, int column, Broadcaster broadcaster) {
        super(line, column);
        this.fontDescriptors = new LinkedSyntaxCollection<>(this, broadcaster);
    }

    /**
     * Gets the {@link SyntaxCollection} of {@link FontDescriptor}s within this unit.
     *
     * @return All font-descriptors.
     */
    public SyntaxCollection<FontFaceBlock, FontDescriptor> fontDescriptors() {
        return fontDescriptors;
    }

    @Override
    public SyntaxCollection<StatementIterable, Statement> statements() {
        // if we add other at-rules without statements, reconsider making AtRuleBlock implement StatementIterable
        throw new UnsupportedOperationException("font-face blocks do not contain statements/declarations, but font descriptors");
    }

    @Override
    public Iterator<Statement> iterator() {
        throw new UnsupportedOperationException("font-face blocks do not contain statements/declarations, but font descriptors");
    }

    @Override
    public void propagateBroadcast(Broadcaster broadcaster) {
        fontDescriptors.propagateBroadcast(broadcaster);
        super.propagateBroadcast(broadcaster);
    }

    @Override
    public boolean isWritable() {
        return super.isWritable() && !fontDescriptors.isEmpty();
    }

    @Override
    public boolean writesOwnOrphanedComments() {
        return true;
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        appendable.spaceIf(!writer.isCompressed());
        appendable.append('{');
        appendable.indentIf(writer.isVerbose());

        // font descriptors
        boolean wroteFirst = false;
        for (FontDescriptor descriptor : fontDescriptors) {
            if (descriptor.isWritable()) {
                if (wroteFirst) {
                    appendable.append(';');
                }

                if (writer.isVerbose()) {
                    appendable.newline();
                } else if (writer.isInline() && wroteFirst) {
                    appendable.space();
                }

                writer.writeInner(descriptor, appendable);
                wroteFirst = true;
            }
        }
        if (wroteFirst && writer.isVerbose()) appendable.append(';');

        // custom handling of orphaned comments if they exist, because they have to go before the closing brace
        StyleWriter.appendComments(orphanedComments(), writer, appendable);

        // close block
        appendable.unindentIf(writer.isVerbose());
        appendable.newlineIf(writer.isVerbose());
        appendable.append('}');
    }

    @Override
    public FontFaceBlock copy() {
        FontFaceBlock copy = new FontFaceBlock().copiedFrom(this);
        for (FontDescriptor descriptor : fontDescriptors) {
            copy.fontDescriptors().append(descriptor.copy());
        }
        return copy;
    }

    @Override
    public void prefix(Prefix prefix, SupportMatrix support, boolean deep) {
        prefixChildren(fontDescriptors, prefix, support, deep);
    }
}
