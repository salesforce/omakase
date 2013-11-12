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

import com.google.common.base.Objects;
import com.salesforce.omakase.data.Browser;
import com.salesforce.omakase.util.As;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Represents a specific {@link Browser} version.
 *
 * @author nmcwilliams
 */
public final class BrowserVersion {
    private final Browser browser;
    private final double version;

    /**
     * Constructs a new instance with the given browser and version.
     *
     * @param browser
     *     The {@link Browser}.
     * @param version
     *     The version number.
     */
    public BrowserVersion(Browser browser, int version) {
        this(browser, (double)version);
    }

    /**
     * Constructs a new instance with the given browser and version.
     *
     * @param browser
     *     The {@link Browser}.
     * @param version
     *     The version number.
     */
    public BrowserVersion(Browser browser, double version) {
        checkArgument(browser.versions().contains(version), "invalid version for specified browser");
        this.browser = browser;
        this.version = version;
    }

    /**
     * Gets the browser.
     *
     * @return The browser.
     */
    public Browser browser() {
        return browser;
    }

    /**
     * Gets the version number.
     *
     * @return The version number.
     */
    public double version() {
        return version;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(browser, version);
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof BrowserVersion) {
            BrowserVersion other = (BrowserVersion)object;
            return Objects.equal(browser, other.browser) && Objects.equal(version, other.version);
        }
        return false;
    }

    @Override
    public String toString() {
        return As.string(this).add("browser", browser).add("version", version).toString();
    }
}
