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

package ${package};

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;
import com.salesforce.omakase.BrowserVersion;

/**
 * Contains the last version of a browser that requires a prefix for various CSS properties.
 * <p/>
 * THIS FILE IS GENERATED. DO NOT EDIT DIRECTLY.
 * <p/>
 * See ${generator} for instructions on updating.
 */
public final class PrefixInfo {
    private static final Multimap<Property, BrowserVersion> PROPERTIES;
    private static final Multimap<String, BrowserVersion> FUNCTIONS;
    private static final Multimap<String, BrowserVersion> AT_RULES;

    static {
        ImmutableSetMultimap.Builder<Property, BrowserVersion> builder = ImmutableSetMultimap.builder();

        <#list properties as p>
        builder.put(Property.${p.property}, new BrowserVersion(Browser.${p.browser}, ${p.version}));
        </#list>

        PROPERTIES = builder.build();
    }

    static {
        ImmutableSetMultimap.Builder<String, BrowserVersion> builder = ImmutableSetMultimap.builder();

        <#list functions as f>
        builder.put("${f.name}", new BrowserVersion(Browser.${f.browser}, ${f.version}));
        </#list>

        FUNCTIONS = builder.build();
    }

    static {
        ImmutableSetMultimap.Builder<String, BrowserVersion> builder = ImmutableSetMultimap.builder();

        <#list atRules as a>
        builder.put("${a.name}", new BrowserVersion(Browser.${a.browser}, ${a.version}));
        </#list>

        AT_RULES = builder.build();
    }

    private PrefixInfo() {}

    /**
     * Gets whether prefix info exists for the given {@link Property}.
     *
     * @param property
     *     Check if prefix info exists for this property.
     *
     * @return True of prefix info exists for the given property.
     */
    public static boolean hasProperty(Property property) {
        return PROPERTIES.containsKey(property);
    }

    /**
     * Gets the last version of the given browser that requires a prefix for the given property.
     *
     * @param property
     *     The property.
     * @param browser
     *     The browser.
     *
     * @return The last version, or -1 if all known versions of the browser supports the property unprefixed.
     */
    public static double lastPrefixedVersion(Property property, Browser browser) {
        for (BrowserVersion browserVersion : PROPERTIES.get(property)) {
            if (browserVersion.browser() == browser) return browserVersion.version();
        }
        return -1d;
    }

    /**
     * Gets whether prefix info exists for the given function name.
     *
     * @param function
     *     Check if prefix info exists for this function name.
     *
     * @return True of prefix info exists for the given function name.
     */
    public static boolean hasFunction(String function) {
        return FUNCTIONS.containsKey(function);
    }

    /**
     * Gets the last version of the given browser that requires a prefix for the given function name.
     *
     * @param function
     *     The function name.
     * @param browser
     *     The browser.
     *
     * @return The last version, or -1 if all known versions of the browser supports the function name unprefixed.
     */
    public static double functionLastPrefixedVersion(String function, Browser browser) {
        for (BrowserVersion browserVersion : FUNCTIONS.get(function)) {
            if (browserVersion.browser() == browser) return browserVersion.version();
        }
        return -1d;
    }

    /**
     * Gets whether prefix info exists for the given at-rule.
     *
     * @param name
     *     Check if prefix info exists for this at-rule.
     *
     * @return True of prefix info exists for the given at-rule.
     */
    public static boolean hasAtRule(String name) {
        return AT_RULES.containsKey(name);
    }

    /**
     * Gets the last version of the given browser that requires a prefix for the given at-rule.
     *
     * @param name
     *     The at-rule name.
     * @param browser
     *     The browser.
     *
     * @return The last version, or -1 if all known versions of the browser supports the at-rule unprefixed.
     */
    public static double atRuleLastPrefixedVersion(String name, Browser browser) {
        for (BrowserVersion browserVersion : AT_RULES.get(name)) {
            if (browserVersion.browser() == browser) return browserVersion.version();
        }
        return -1d;
    }
}
