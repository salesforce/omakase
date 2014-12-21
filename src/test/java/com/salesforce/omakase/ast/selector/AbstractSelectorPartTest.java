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

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/** Unit tests for {@link AbstractSelectorPart}. */
@SuppressWarnings("JavaDoc")
public class AbstractSelectorPartTest {
    private SelectorPart part;
    private Selector selector;

    @Before
    public void setup() {
        part = new IdSelector("test");
        selector = new Selector(part);
    }

    @Test
    public void commentsWhenDetached() {
        IdSelector id = new IdSelector("2");
        id.comments(Lists.newArrayList("test"));
        assertThat(id.comments()).isNotEmpty();
    }

    @Test
    public void commentsWhenAttached() {
        // note - changed behavior to not include parent selector comments
        selector.comments(Lists.newArrayList("test"));
        part.comments(Lists.newArrayList("test"));
        assertThat(part.comments()).hasSize(1);
    }

    @Test
    public void writableWhenAttached() {
        assertThat(part.isWritable()).isTrue();
    }

}
