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

package com.salesforce.omakase.util;

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.salesforce.omakase.ast.Rule;
import com.salesforce.omakase.ast.selector.ClassSelector;
import com.salesforce.omakase.ast.selector.Selector;

/**
 * Unit tests for {@link Actions}.
 *
 * @author nmcwilliams
 */
public class ActionsTest {
    @Test
    public void detach() {
        Rule rule = new Rule();
        Selector selector = new Selector(new ClassSelector("test"));
        rule.selectors().append(selector);

        assertThat(selector.isDestroyed()).isFalse();
        Actions.destroy().apply(Lists.newArrayList(selector));
        assertThat(selector.isDestroyed()).isTrue();
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
