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

package com.salesforce.omakase.ast.atrule;

import com.salesforce.omakase.ast.Statement;
import com.salesforce.omakase.ast.StatementIterable;
import com.salesforce.omakase.ast.Status;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.collection.LinkedSyntaxCollection;
import com.salesforce.omakase.ast.collection.SyntaxCollection;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.plugin.syntax.FontFacePlugin;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;
import java.util.Iterator;

/**
 * Represents the block of a font-face at-rule.
 *
 * @author nmcwilliams
 * @see FontDescriptor
 * @see FontFacePlugin
 */
public final class FontFaceBlock extends AbstractAtRuleMember implements AtRuleBlock {
    private final SyntaxCollection<FontFaceBlock, FontDescriptor> fontDescriptors;

    /**
     * Creates a new instance with no line or number specified (used for dynamically created {@link Syntax} units).
     */
    public FontFaceBlock() {
        this(-1, -1);
    }

    /**
     * Constructs a new {@link FontFaceBlock} instance.
     *  @param line
     *     The line number.
     * @param column
     *     The column number.
     */
    public FontFaceBlock(int line, int column) {
        super(line, column);
        this.fontDescriptors = new LinkedSyntaxCollection<>(this);
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
    public void propagateBroadcast(Broadcaster broadcaster, Status status) {
        if (status() == status) {
            fontDescriptors.propagateBroadcast(broadcaster, status);
            super.propagateBroadcast(broadcaster, status);
        }
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
        appendable.newlineIf(writer.isVerbose());
        writer.incrementDepth();

        // font descriptors
        for (FontDescriptor descriptor : fontDescriptors) {
            writer.writeInner(descriptor, appendable);
        }
        if (writer.isVerbose()) appendable.append(';');

        // custom handling of orphaned comments if they exist, because they have to go before the closing brace
        writer.appendComments(orphanedComments(), appendable);

        // close block
        writer.decrementDepth();
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
}
