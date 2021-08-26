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

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.salesforce.omakase.Omakase;

/**
 * Unit tests for {@link Prefixer}.
 *
 * @author nmcwilliams
 */
public class PrefixerUnitTest {
    @SuppressWarnings("deprecation")
    @Rule public final ExpectedException exception = ExpectedException.none();

    @Test
    public void customShouldNotSupportAnythingByDefault() {
        assertThat(Prefixer.customBrowserSupport().support().supportedBrowsers()).isEmpty();
    }

    @Test
    public void defaultRearrangedFalse() {
        assertThat(Prefixer.defaultBrowserSupport().rearrange()).isFalse();
    }

    @Test
    public void setRearrange() {
        Prefixer prefixer = Prefixer.defaultBrowserSupport().rearrange(true);
        assertThat(prefixer.rearrange()).isTrue();
    }

    @Test
    public void defaultPruneFalse() {
        assertThat(Prefixer.defaultBrowserSupport().prune()).isFalse();
    }

    @Test
    public void setPrune() {
        Prefixer prefixer = Prefixer.defaultBrowserSupport().prune(true);
        assertThat(prefixer.prune()).isTrue();
    }

    @Test
    public void throwsErrorIfPrefixPrunerAlreadyRegistered() {
        exception.expect(IllegalStateException.class);
        exception.expectMessage("plugin should be registered AFTER");
        Omakase.source("").use(PrefixCleaner.mismatchedPrefixedUnits()).use(Prefixer.defaultBrowserSupport()).process();
    }
}
