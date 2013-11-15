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

package com.salesforce.omakase.util;

import com.google.common.collect.Lists;
import com.salesforce.omakase.ast.Rule;
import com.salesforce.omakase.ast.selector.ClassSelector;
import com.salesforce.omakase.ast.selector.Selector;
import org.junit.Test;

import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link Actions}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class ActionsTest {
    @Test
    public void detach() {
        Rule rule = new Rule();
        Selector selector = new Selector(new ClassSelector("test"));
        rule.selectors().append(selector);

        assertThat(selector.isDetached()).isFalse();
        Actions.detach().apply(Lists.newArrayList(selector));
        assertThat(selector.isDetached()).isTrue();
    }

    @Test
    public void moveBefore() {
        Rule rule = new Rule();
        Selector s1 = new Selector(new ClassSelector("test1"));
        Selector s2 = new Selector(new ClassSelector("test2"));
        Selector s3 = new Selector(new ClassSelector("test3"));
        Selector s4 = new Selector(new ClassSelector("test4"));
        rule.selectors().append(s1).append(s2).append(s3).append(s4);

        List<Selector> toMove = Lists.newArrayList(s2, s3);
        Actions.<Selector>moveBefore().apply(s1, toMove);

        assertThat(rule.selectors()).containsExactly(s2, s3, s1, s4);
    }

    @Test
    public void moveAfter() {
        Rule rule = new Rule();
        Selector s1 = new Selector(new ClassSelector("test1"));
        Selector s2 = new Selector(new ClassSelector("test2"));
        Selector s3 = new Selector(new ClassSelector("test3"));
        Selector s4 = new Selector(new ClassSelector("test4"));
        rule.selectors().append(s1).append(s2).append(s3).append(s4);

        List<Selector> toMove = Lists.newArrayList(s1, s2);
        Actions.<Selector>moveAfter().apply(s4, toMove);

        assertThat(rule.selectors()).containsExactly(s3, s4, s1, s2);
    }
}
