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

package com.salesforce.omakase.plugin.prefixer;

import com.salesforce.omakase.data.Browser;
import com.salesforce.omakase.util.SupportMatrix;
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
