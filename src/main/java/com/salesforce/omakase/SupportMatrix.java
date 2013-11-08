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

package com.salesforce.omakase;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;
import com.salesforce.omakase.data.Browser;

import java.util.Set;

/**
 * TESTME
 * <p/>
 * TODO description
 *
 * @author nmcwilliams
 */
public final class SupportMatrix {
    /* using tree so that getting browser versions are in descending order */
    private final Multimap<Browser, Double> supported = TreeMultimap.create();

    /**
     * Designate support for the given {@link Browser} and version.
     *
     * @param browser
     *     The {@link Browser}.
     * @param version
     *     The version.
     *
     * @return this, for chaining.
     */
    public SupportMatrix browser(Browser browser, int version) {
        return browser(browser, (double)version);
    }

    /**
     * Designate support for the given {@link Browser} and version.
     *
     * @param browser
     *     The {@link Browser}.
     * @param version
     *     The version.
     *
     * @return this, for chaining.
     */
    public SupportMatrix browser(Browser browser, double version) {
        supported.put(browser, version);
        return this;
    }

    /**
     * Designate support for the latest version of the given {@link Browser}.
     *
     * @param browser
     *     The {@link Browser}.
     *
     * @return this, for chaining.
     */
    public SupportMatrix latest(Browser browser) {
        supported.put(browser, browser.versions().get(0));
        return this;
    }

    /**
     * Designate support for the last N number of versions of the given {@link Browser}, counting back from the current version.
     *
     * @param browser
     *     The {@link Browser}.
     * @param numVersions
     *     The number of versions to support, counting back from the current version.
     *
     * @return this, for chaining.
     */
    public SupportMatrix last(Browser browser, int numVersions) {
        int actualNumVersions = Math.min(numVersions, browser.versions().size());
        for (int i = 0; i < actualNumVersions; i++) {
            supported.put(browser, browser.versions().get(i));
        }
        return this;
    }

    /**
     * Gets the lowest version of the given {@link Browser} designated as supported.
     *
     * @param browser
     *     Get the lowest version supported of this {@link Browser}.
     *
     * @return this, for chaining.
     */
    public Double lowestSupportedVersion(Browser browser) {
        return Iterables.getFirst(supported.get(browser), -1d);
    }

    /**
     * Gets the list of all designated supported {@link Browser}s.
     *
     * @return this, for chaining.
     */
    public Set<Browser> supportedBrowsers() {
        return ImmutableSet.copyOf(supported.keySet());
    }

    @Override
    public String toString() {
        return As.string(this).indent().add("supported", supported).toString();
    }
}
