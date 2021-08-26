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

package com.salesforce.omakase.ast.selector;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

/**
 * Represents a single keyframe selector part. For example, "from", "to", "0%', "50%", etc...
 *
 * @author nmcwilliams
 */
public final class KeyframeSelector extends AbstractSelectorPart {
    private String keyframe;

    /**
     * Creates a new instance with the given line and column numbers and keyframe.
     * <p>
     * If dynamically creating a new instance then use {@link #KeyframeSelector(String)} instead.
     *
     * @param line
     *     The line number.
     * @param column
     *     The column number.
     * @param keyframe
     *     The keyframe, e.g., "from" or "75%".
     */
    public KeyframeSelector(int line, int column, String keyframe) {
        super(line, column);
        this.keyframe = keyframe;
    }

    /**
     * Creates a new instance with no line or number specified (used for dynamically created {@link Syntax} units).
     *
     * @param keyframe
     *     The keyframe, e.g., "from" or "75%".
     */
    public KeyframeSelector(String keyframe) {
        keyframe(keyframe);
    }

    /**
     * Sets the keyframe.
     *
     * @param keyframe
     *     The keyframe, e.g., "from" or "75%".
     *
     * @return this, for chaining.
     */
    public KeyframeSelector keyframe(String keyframe) {
        this.keyframe = checkNotNull(keyframe, "keyframe cannot be null");
        return this;
    }

    /**
     * Gets the keyframe (e.g., e.g., "from" or "75%").
     *
     * @return The keyframe.
     */
    public String keyframe() {
        return keyframe;
    }

    @Override
    public SelectorPartType type() {
        return SelectorPartType.KEYFRAMES_SELECTOR;
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        appendable.append(keyframe);
    }

    @Override
    public KeyframeSelector copy() {
        return new KeyframeSelector(keyframe).copiedFrom(this);
    }
}
