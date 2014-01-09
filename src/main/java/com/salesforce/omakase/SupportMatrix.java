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
import com.google.common.collect.Sets;
import com.google.common.collect.TreeMultimap;
import com.salesforce.omakase.data.Browser;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.data.PrefixUtil;
import com.salesforce.omakase.data.Property;
import com.salesforce.omakase.plugin.basic.Prefixer;
import com.salesforce.omakase.util.As;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Represents a set of supported browser versions.
 *
 * @author nmcwilliams
 * @see Browser
 * @see Prefixer
 */
public final class SupportMatrix {
    /* using tree so that getting browser versions are in ascending order */
    private final Multimap<Browser, Double> supported = TreeMultimap.create();

    private final Map<Property, Set<Prefix>> propertyCache = new EnumMap<Property, Set<Prefix>>(Property.class);
    private final Map<String, Set<Prefix>> functionCache = new HashMap<String, Set<Prefix>>(8);
    private final Map<String, Set<Prefix>> atRuleCache = new HashMap<String, Set<Prefix>>(2);
    private final Map<String, Set<Prefix>> selectorCache = new HashMap<String, Set<Prefix>>(2);

    /**
     * Designate support for the given {@link Browser} and version.
     * <p/>
     * Example:
     * <pre>
     * <code>support.browser(Browser.CHROME, 25);</code>
     * </pre>
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
     * <p/>
     * Example:
     * <pre>
     * <code>support.browser(Browser.SAFARI, 6.1);</code>
     * </pre>
     *
     * @param browser
     *     The {@link Browser}.
     * @param version
     *     The version.
     *
     * @return this, for chaining.
     */
    public SupportMatrix browser(Browser browser, double version) {
        checkArgument(browser.versions().contains(version), "version does not exist for browser");
        supported.put(browser, version);
        return this;
    }

    /**
     * Designate support for the latest version of the given {@link Browser}.
     * <p/>
     * Example:
     * <pre>
     * <code>support.browser(Browser.CHROME);</code>
     * </pre>
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
     * <p/>
     * Example:
     * <pre>
     * <code>support.browser(Browser.CHROME, 2); // last 2 versions</code>
     * </pre>
     *
     * @param browser
     *     The {@link Browser}.
     * @param numVersions
     *     The number of versions to support, counting back from the current version.
     *
     * @return this, for chaining.
     */
    public SupportMatrix last(Browser browser, int numVersions) {
        checkArgument(numVersions <= browser.versions().size(), "numVersions out of range");
        for (int i = 0; i < numVersions; i++) {
            supported.put(browser, browser.versions().get(i));
        }
        return this;
    }

    /**
     * Support all versions of the given {@link Browser}. Generally not preferable.
     *
     * @param browser
     *     Support all versions of this {@link Browser}.
     *
     * @return this, for chaining.
     */
    public SupportMatrix all(Browser browser) {
        for (Double version : browser.versions()) {
            supported.put(browser, version);
        }
        return this;
    }

    /**
     * Gets whether any version of the given {@link Browser} is supported.
     *
     * @param browser
     *     The {@link Browser}.
     *
     * @return True if any version of the browser is supported.
     */
    public boolean supportsBrowser(Browser browser) {
        return supported.containsKey(browser);
    }

    /**
     * Gets whether the specified version of the given {@link Browser} is supported.
     *
     * @param browser
     *     The {@link Browser}.
     * @param version
     *     The specific version.
     *
     * @return True if the specified version of the browser is supported.
     */
    public boolean supportsVersion(Browser browser, int version) {
        return supportsVersion(browser, (double)version);
    }

    /**
     * Gets whether the specified version of the given {@link Browser} is supported.
     *
     * @param browser
     *     The {@link Browser}.
     * @param version
     *     The specific version.
     *
     * @return True if the specified version of the browser is supported.
     */
    public boolean supportsVersion(Browser browser, double version) {
        return supported.get(browser).contains(version);
    }

    /**
     * Gets all supported versions of the given {@link Browser}.
     *
     * @param browser
     *     Get all supported versions of this {@link Browser}.
     *
     * @return The set of all versions, or an empty set if no versions are supported.
     */
    public Set<Double> allSupportedVersions(Browser browser) {
        return ImmutableSet.copyOf(supported.get(browser));
    }

    /**
     * Gets the lowest version of the given {@link Browser} designated as supported.
     *
     * @param browser
     *     Get the lowest version supported of this {@link Browser}.
     *
     * @return The lowest supported version, or -1 if no versions are supported.
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

    /**
     * Gets all prefixes required for the given {@link Property} according to the supported browser versions.
     *
     * @param property
     *     Get required prefixes for this {@link Property}.
     *
     * @return The set of required prefixes.
     */
    public Set<Prefix> prefixesForProperty(Property property) {
        Set<Prefix> cached = propertyCache.get(property);

        if (cached == null) {
            Set<Prefix> required = Sets.newHashSet();

            for (Browser browser : supported.keySet()) {
                Double lastPrefixed = PrefixUtil.lastVersionPropertyIsPrefixed(property, browser);
                if (lowestSupportedVersion(browser) <= lastPrefixed) required.add(browser.prefix());
            }

            cached = immutable(required);
            propertyCache.put(property, cached);
        }

        return cached;
    }

    /**
     * Gets all prefixes required for the given function name (e.g., "calc" or "linear-gradient"), according to the supported
     * browser versions.
     *
     * @param name
     *     Get required prefixes for functions with this name.
     *
     * @return The set of required prefixes.
     */
    public Set<Prefix> prefixesForFunction(String name) {
        Set<Prefix> cached = functionCache.get(name);

        if (cached == null) {
            Set<Prefix> required = Sets.newHashSet();

            for (Browser browser : supported.keySet()) {
                Double lastPrefixed = PrefixUtil.lastVersionFunctionIsPrefixed(name, browser);
                if (lowestSupportedVersion(browser) <= lastPrefixed) required.add(browser.prefix());
            }

            cached = immutable(required);
            functionCache.put(name, cached);
        }

        return cached;
    }

    /**
     * Gets all prefixes required for the given at-rule (e.g., "keyframes"), according to the supported browser versions.
     *
     * @param name
     *     Get required prefixes for at-rules with this name.
     *
     * @return The set of required prefixes.
     */
    public Set<Prefix> prefixesForAtRule(String name) {
        Set<Prefix> cached = atRuleCache.get(name);

        if (cached == null) {
            Set<Prefix> required = Sets.newHashSet();

            for (Browser browser : supported.keySet()) {
                Double lastPrefixed = PrefixUtil.lastVersionAtRuleIsPrefixed(name, browser);
                if (lowestSupportedVersion(browser) <= lastPrefixed) required.add(browser.prefix());
            }

            cached = immutable(required);
            atRuleCache.put(name, cached);
        }

        return cached;
    }

    /**
     * Gets all prefixes required for the given selector (e.g., "selection"), according to the supported browser versions.
     *
     * @param name
     *     Get required prefixes for selectors with this name.
     *
     * @return The set of required prefixes.
     */
    public Set<Prefix> prefixesForSelector(String name) {
        Set<Prefix> cached = selectorCache.get(name);

        if (cached == null) {
            Set<Prefix> required = Sets.newHashSet();

            for (Browser browser : supported.keySet()) {
                Double lastPrefixed = PrefixUtil.lastVersionSelectorIsPrefixed(name, browser);
                if (lowestSupportedVersion(browser) <= lastPrefixed) required.add(browser.prefix());
            }

            cached = immutable(required);
            selectorCache.put(name, cached);
        }

        return cached;
    }

    /**
     * Gets whether the given {@link Prefix} is required for the given {@link Property}, according to the supported browser
     * versions.
     * <p/>
     * To get the set of all prefixes required by the property, use {@link #prefixesForProperty(Property)} instead.
     *
     * @param prefix
     *     The {@link Prefix}.
     * @param property
     *     The {@link Property}.
     *
     * @return True if the property requires the given prefix.
     */

    public boolean requiresPrefixForProperty(Prefix prefix, Property property) {
        return PrefixUtil.isPrefixableProperty(property) && prefixesForProperty(property).contains(prefix);
    }

    /**
     * Gets whether the given {@link Prefix} is required for the given function name, according to the supported browser
     * versions.
     *
     * @param prefix
     *     The {@link Prefix}.
     * @param function
     *     The function name, e.g., "linear-gradient".
     *
     * @return True if the function name requires the given prefix.
     */
    public boolean requiresPrefixForFunction(Prefix prefix, String function) {
        return PrefixUtil.isPrefixableFunction(function) && prefixesForFunction(function).contains(prefix);
    }

    /**
     * Gets whether the given {@link Prefix} is required for the given at-rule, according to the supported browser versions.
     *
     * @param prefix
     *     The {@link Prefix}.
     * @param name
     *     The at-rule name, e.g., "keyframes".
     *
     * @return True if the at-rule requires the given prefix.
     */
    public boolean requiresPrefixForAtRule(Prefix prefix, String name) {
        return PrefixUtil.isPrefixableAtRule(name) && prefixesForAtRule(name).contains(prefix);
    }

    /**
     * Gets whether the given {@link Prefix} is required for the given selector name, according to the supported browser
     * versions.
     *
     * @param prefix
     *     The {@link Prefix}.
     * @param name
     *     The selector name, e.g., "selection".
     *
     * @return True if the selector requires the given prefix.
     */
    public boolean requiresPrefixForSelector(Prefix prefix, String name) {
        return PrefixUtil.isPrefixableSelector(name) && prefixesForSelector(name).contains(prefix);
    }

    @Override
    public String toString() {
        return As.string(this).fields().toString();
    }

    private static Set<Prefix> immutable(Set<Prefix> required) {
        switch (required.size()) {
        case 0:
            return ImmutableSet.of();
        case 1:
            return ImmutableSet.of(required.iterator().next());
        default:
            return Sets.newEnumSet(required, Prefix.class); // enum set maintains consistent ordinal-based iteration order
        }
    }
}
