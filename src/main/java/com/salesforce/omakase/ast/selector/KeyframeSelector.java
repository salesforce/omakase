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

package com.salesforce.omakase.ast.selector;

import com.salesforce.omakase.SupportMatrix;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TESTME
 * <p/>
 * Represents a single keyframe selector part. For example, "from", "to", "0%', "50%", etc...
 *
 * @author nmcwilliams
 */
public class KeyframeSelector extends AbstractSelectorPart {
    private String keyframe;

    /**
     * Creates a new instance with the given line and column numbers and keyframe.
     * <p/>
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
    protected KeyframeSelector makeCopy(Prefix prefix, SupportMatrix support) {
        return new KeyframeSelector(keyframe);
    }
}
