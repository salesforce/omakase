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
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.fest.assertions.api.Assertions.*;

/** Unit tests for {@link AbstractSelectorPart}. */
@SuppressWarnings("JavaDoc")
public class AbstractSelectorPartTest {
    private TestSelectorPart obj;
    private Selector selector;

    @Before
    public void setup() {
        obj = new TestSelectorPart();
        selector = new Selector(obj);
    }

    @Test
    public void parentSelector() {
        assertThat(obj.parentSelector().get()).isSameAs(selector);
    }

    @Test
    public void parentSelectorWhenDetached() {
        assertThat(new TestSelectorPart().parentSelector().isPresent()).isFalse();
    }

    @Test
    public void commentsWhenDetached() {
        TestSelectorPart tsp = new TestSelectorPart();
        tsp.comments(Lists.newArrayList("test"));
        assertThat(tsp.comments()).isNotEmpty();
    }

    @Test
    public void commentsWhenAttached() {
        selector.comments(Lists.newArrayList("test"));
        obj.comments(Lists.newArrayList("test"));
        assertThat(obj.comments()).hasSize(2);
    }

    @Test
    public void writableWhenAttached() {
        assertThat(obj.isWritable()).isTrue();
    }

    @Test
    public void notWritableWhenDetached() {
        assertThat(new TestSelectorPart().isWritable()).isFalse();
    }

    public static final class TestSelectorPart extends AbstractSelectorPart {
        @Override
        protected SelectorPart self() {
            return this;
        }

        @Override
        public boolean isSelector() {
            return false;
        }

        @Override
        public boolean isCombinator() {
            return false;
        }

        @Override
        public SelectorPartType type() {
            return null;
        }

        @Override
        public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        }
    }
}
