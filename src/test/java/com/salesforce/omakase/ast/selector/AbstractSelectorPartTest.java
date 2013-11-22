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
import com.salesforce.omakase.SupportMatrix;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.fest.assertions.api.Assertions.assertThat;

/** Unit tests for {@link AbstractSelectorPart}. */
@SuppressWarnings("JavaDoc")
public class AbstractSelectorPartTest {
    private TestSelectorPart obj1;
    private TestSelectorPart obj2;
    private TestSelectorPart obj3;
    private TestSelectorPart obj4;
    private TestSelectorPart obj5;
    private TestSelectorPart obj6;
    private Combinator combinator;
    private Selector selector;
    private Selector full;

    @Before
    public void setup() {
        obj1 = new TestSelectorPart();
        selector = new Selector(obj1);
    }

    private void fill() {
        obj2 = new TestSelectorPart();
        obj3 = new TestSelectorPart();
        obj4 = new TestSelectorPart();
        obj5 = new TestSelectorPart();
        obj6 = new TestSelectorPart();
        combinator = new Combinator(CombinatorType.DESCENDANT);
        full = selector = new Selector(obj1, obj2, obj3, combinator, obj4, obj5, obj6);
    }

    @Test
    public void parentSelector() {
        assertThat(obj1.parentSelector().get()).isSameAs(selector);
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
        obj1.comments(Lists.newArrayList("test"));
        assertThat(obj1.comments()).hasSize(2);
    }

    @Test
    public void writableWhenAttached() {
        assertThat(obj1.isWritable()).isTrue();
    }

    @Test
    public void notWritableWhenDetached() {
        assertThat(new TestSelectorPart().isWritable()).isFalse();
    }

    @Test
    public void adjoiningWhenFirst() {
        fill();
        assertThat(obj1.adjoining()).containsExactly(obj1, obj2, obj3);
    }

    @Test
    public void adjoiningWhenMiddle() {
        fill();
        assertThat(obj2.adjoining()).containsExactly(obj1, obj2, obj3);
    }

    @Test
    public void adjoiningWhenEnd() {
        fill();
        assertThat(obj3.adjoining()).containsExactly(obj1, obj2, obj3);
    }

    @Test
    public void adjoiningWhenCombinator() {
        fill();
        assertThat(combinator.adjoining()).containsExactly(combinator);
    }

    @Test
    public void adjoiningWhenDetached() {
        fill();
        obj2.detach();
        assertThat(obj2.adjoining()).containsExactly(obj2);
    }

    public static final class TestSelectorPart extends AbstractSelectorPart {
        @Override
        public SelectorPartType type() {
            return SelectorPartType.CLASS_SELECTOR;
        }

        @Override
        public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        }

        @Override
        protected SelectorPart makeCopy(Prefix prefix, SupportMatrix support) {
            throw new UnsupportedOperationException();
        }
    }
}
