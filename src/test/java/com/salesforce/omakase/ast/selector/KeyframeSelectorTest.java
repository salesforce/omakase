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

import com.salesforce.omakase.writer.StyleWriter;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link KeyframeSelector}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class KeyframeSelectorTest {
    @Test
    public void getKeyframe() {
        KeyframeSelector sel = new KeyframeSelector(1, 1, "75%");
        assertThat(sel.keyframe()).isEqualTo("75%");
    }

    @Test
    public void setKeyframe() {
        KeyframeSelector sel = new KeyframeSelector("75%");
        sel.keyframe("80%");
        assertThat(sel.keyframe()).isEqualTo("80%");
    }

    @Test
    public void write() {
        assertThat(StyleWriter.writeSingle(new KeyframeSelector("75%"))).isEqualTo("75%");
    }

    @Test
    public void copy() {
        assertThat(((KeyframeSelector)new KeyframeSelector("70%").copy()).keyframe()).isEqualTo("70%");
    }
}
