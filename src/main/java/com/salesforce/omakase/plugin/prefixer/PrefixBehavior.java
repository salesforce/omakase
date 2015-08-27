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
import com.salesforce.omakase.data.Prefix;

import java.util.EnumMap;
import java.util.Map;

/**
 * Represents a bundle of browsers that have the same special prefix behavior (e.g., based on the same version of a spec). Each
 * browser is mapped to a browser version, which represents the last version of the browser to use this behavior (all previous
 * versions are also included).
 *
 * @author nmcwilliams
 */
@SuppressWarnings("AutoBoxing")
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
