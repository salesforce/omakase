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

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.TreeMultimap;
import com.salesforce.omakase.data.Browser;
import com.salesforce.omakase.data.Keyword;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.data.PrefixTablesUtil;
import com.salesforce.omakase.data.Property;
import com.salesforce.omakase.plugin.prefixer.Prefixer;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
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

    private final Map<Property, Set<Prefix>> propertyCache = new EnumMap<>(Property.class);
    private final Map<Keyword, Set<Prefix>> keywordCache = new EnumMap<>(Keyword.class);
    private final Map<String, Set<Prefix>> atRuleCache = new HashMap<>(2);
    private final Map<String, Set<Prefix>> selectorCache = new HashMap<>(2);
    private final Map<String, Set<Prefix>> functionCache = new HashMap<>(8);

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
     * Gets whether the specified version or lower of the browser is supported. If the browser is not supported then this returns
     * false.
     *
     * @param browser
     *     The {@link Browser}.
     * @param version
     *     The version.
     *
     * @return True if the given version or a lower one is supported
     */
    public boolean supportsVersionOrLower(Browser browser, double version) {
        Collection<Double> versions = supported.get(browser);
        if (versions.isEmpty()) return false;
        return versions.iterator().next() <= version;
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
            Set<Prefix> required = new HashSet<>();

            for (Browser browser : supported.keySet()) {
                Double lastPrefixed = PrefixTablesUtil.lastVersionPropertyIsPrefixed(property, browser);
                if (lowestSupportedVersion(browser) <= lastPrefixed) required.add(browser.prefix());
            }

            cached = immutable(required);
            propertyCache.put(property, cached);
        }

        return cached;
    }

    /**
     * Gets all prefixes required for the given {@link Keyword} according to the supported browser versions.
     *
     * @param keyword
     *     Get required prefixes for this {@link Keyword}.
     *
     * @return The set of required prefixes.
     */
    public Set<Prefix> prefixesForKeyword(Keyword keyword) {
        Set<Prefix> cached = keywordCache.get(keyword);

        if (cached == null) {
            Set<Prefix> required = new HashSet<>();

            for (Browser browser : supported.keySet()) {
                Double lastPrefixed = PrefixTablesUtil.lastVersionKeywordIsPrefixed(keyword, browser);
                if (lowestSupportedVersion(browser) <= lastPrefixed) required.add(browser.prefix());
            }

            cached = immutable(required);
            keywordCache.put(keyword, cached);
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
            Set<Prefix> required = new HashSet<>();

            for (Browser browser : supported.keySet()) {
                Double lastPrefixed = PrefixTablesUtil.lastVersionAtRuleIsPrefixed(name, browser);
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
            Set<Prefix> required = new HashSet<>();

            for (Browser browser : supported.keySet()) {
                Double lastPrefixed = PrefixTablesUtil.lastVersionSelectorIsPrefixed(name, browser);
                if (lowestSupportedVersion(browser) <= lastPrefixed) required.add(browser.prefix());
            }

            cached = immutable(required);
            selectorCache.put(name, cached);
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
            Set<Prefix> required = new HashSet<>();

            for (Browser browser : supported.keySet()) {
                Double lastPrefixed = PrefixTablesUtil.lastVersionFunctionIsPrefixed(name, browser);
                if (lowestSupportedVersion(browser) <= lastPrefixed) required.add(browser.prefix());
            }

            cached = immutable(required);
            functionCache.put(name, cached);
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
        return PrefixTablesUtil.isPrefixableProperty(property) && prefixesForProperty(property).contains(prefix);
    }

    /**
     * Gets whether the given {@link Prefix} is required for the given {@link Keyword}, according to the supported browser
     * versions.
     * <p/>
     * To get the set of all prefixes required by the keyword, use {@link #prefixesForKeyword(Keyword)} instead.
     *
     * @param prefix
     *     The {@link Prefix}.
     * @param keyword
     *     The {@link Keyword}.
     *
     * @return True if the property requires the given prefix.
     */
    public boolean requiresPrefixForKeyword(Prefix prefix, Keyword keyword) {
        return PrefixTablesUtil.isPrefixableKeyword(keyword) && prefixesForKeyword(keyword).contains(prefix);
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
        return PrefixTablesUtil.isPrefixableAtRule(name) && prefixesForAtRule(name).contains(prefix);
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
        return PrefixTablesUtil.isPrefixableSelector(name) && prefixesForSelector(name).contains(prefix);
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
        return PrefixTablesUtil.isPrefixableFunction(function) && prefixesForFunction(function).contains(prefix);
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
