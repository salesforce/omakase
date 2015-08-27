/*
 * Copyright (C) 2015 salesforce.com, inc.
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

package com.salesforce.omakase.plugin.prefixer;

import com.salesforce.omakase.SupportMatrix;
import com.salesforce.omakase.data.Browser;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/** Unit tests for {@link PrefixBehavior}. */
@SuppressWarnings("JavaDoc")
public class PrefixBehaviorTest {
    @Test
    public void testMatchesFirst() {
        PrefixBehavior pb = new PrefixBehavior().put(Browser.CHROME, 10).put(Browser.FIREFOX, 15);
        SupportMatrix sm = new SupportMatrix().browser(Browser.CHROME, 5);
        assertThat(pb.matches(sm)).isTrue();
    }

    @Test
    public void testMatchesSecond() {
        PrefixBehavior pb = new PrefixBehavior().put(Browser.CHROME, 10).put(Browser.FIREFOX, 15);
        SupportMatrix sm = new SupportMatrix().browser(Browser.FIREFOX, 15);
        assertThat(pb.matches(sm)).isTrue();
    }

    @Test
    public void testMatchesMultiple() {
        PrefixBehavior pb = new PrefixBehavior().put(Browser.CHROME, 10).put(Browser.FIREFOX, 15);
        SupportMatrix sm = new SupportMatrix().browser(Browser.CHROME, 5).browser(Browser.FIREFOX, 15).browser(Browser.SAFARI, 6);
        assertThat(pb.matches(sm)).isTrue();
    }

    @Test
    public void testMatchesNone() {
        PrefixBehavior pb = new PrefixBehavior().put(Browser.CHROME, 10).put(Browser.FIREFOX, 15);
        SupportMatrix sm = new SupportMatrix().browser(Browser.CHROME, 11).browser(Browser.SAFARI, 6);
        assertThat(pb.matches(sm)).isFalse();
    }

    @Test
    public void testMatchesWithPrefix() {
        PrefixBehavior pb = new PrefixBehavior().put(Browser.CHROME, 10).put(Browser.FIREFOX, 15);
        SupportMatrix sm = new SupportMatrix().browser(Browser.CHROME, 5);
        assertThat(pb.matches(sm, Browser.CHROME.prefix())).isTrue();
    }

    @Test
    public void testDoesntMatchWithPrefix() {
        PrefixBehavior pb = new PrefixBehavior().put(Browser.CHROME, 10).put(Browser.FIREFOX, 15);
        SupportMatrix sm = new SupportMatrix().browser(Browser.CHROME, 5);
        assertThat(pb.matches(sm, Browser.IE.prefix())).isFalse();
    }

    @Test
    public void testDoesntMatchRegardlessOfPrefix() {
        PrefixBehavior pb = new PrefixBehavior().put(Browser.CHROME, 10).put(Browser.FIREFOX, 15);
        SupportMatrix sm = new SupportMatrix().browser(Browser.SAFARI, 5);
        assertThat(pb.matches(sm, Browser.CHROME.prefix())).isFalse();
    }
}