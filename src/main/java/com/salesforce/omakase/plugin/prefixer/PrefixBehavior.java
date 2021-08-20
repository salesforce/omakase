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

import java.util.EnumMap;
import java.util.Map;

import com.salesforce.omakase.data.Browser;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.util.SupportMatrix;

/**
 * Represents a bundle of browsers that have the same special prefix behavior (e.g., based on the same version of a spec). Each
 * browser is mapped to a browser version, which represents the last version of the browser to use this behavior (all previous
 * versions are also included).
 *
 * @author nmcwilliams
 */
final class PrefixBehavior {
    private final EnumMap<Browser, Double> map = new EnumMap<>(Browser.class);

    /**
     * Adds a browser to this behavior.
     *
     * @param browser
     *     The browser.
     * @param maxVersion
     *     The last version of the browser to use this behavior.
     *
     * @return this, for chaining.
     */
    public PrefixBehavior put(Browser browser, int maxVersion) {
        map.put(browser, (double)maxVersion);
        return this;
    }

    /**
     * Adds a browser to this behavior.
     *
     * @param browser
     *     The browser.
     * @param maxVersion
     *     The last version of the browser to use this behavior.
     *
     * @return this, for chaining.
     */
    public PrefixBehavior put(Browser browser, double maxVersion) {
        map.put(browser, maxVersion);
        return this;
    }

    /**
     * Returns true if the given {@link SupportMatrix} supports one of the browser versions in this behavior.
     *
     * @param support
     *     The supported browser versions.
     *
     * @return true or false depending on a match.
     */
    public boolean matches(SupportMatrix support) {
        for (Map.Entry<Browser, Double> entry : map.entrySet()) {
            if (support.supportsVersionOrLower(entry.getKey(), entry.getValue())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Same as {@link #matches(SupportMatrix)}, except this will only look at Browsers within this map that use the given prefix.
     *
     * @param support
     *     The supported browser versions.
     * @param prefix
     *     Only check browsers using this prefix.
     *
     * @return true or false depending on a match.
     */
    public boolean matches(SupportMatrix support, Prefix prefix) {
        for (Map.Entry<Browser, Double> entry : map.entrySet()) {
            if (entry.getKey().prefix() == prefix && support.supportsVersionOrLower(entry.getKey(), entry.getValue())) {
                return true;
            }
        }
        return false;
    }
}
