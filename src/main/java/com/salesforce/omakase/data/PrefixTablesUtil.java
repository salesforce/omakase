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

package com.salesforce.omakase.data;

/**
 * Utilities for working with the generated data in {@link PrefixTables}.
 *
 * @author nmcwilliams
 */
public final class PrefixTablesUtil {
    // XXX whether additional caching here improves performance is questionable, and if enabled needs to be made thread-safe
    // static caches, based on prefix tables data being immutable
    // private static final Table<Property, Browser, Double> PROPERTY_CACHE = HashBasedTable.create();
    // private static final Table<String, Browser, Double> KEYWORD_CACHE = HashBasedTable.create();
    // private static final Table<String, Browser, Double> AT_RULE_CACHE = HashBasedTable.create();
    // private static final Table<String, Browser, Double> SELECTOR_CACHE = HashBasedTable.create();
    // private static final Table<String, Browser, Double> FUNCTION_CACHE = HashBasedTable.create();

    private PrefixTablesUtil() {}

    /**
     * Gets whether prefix info exists for the given {@link Property}.
     *
     * @param property
     *     Check if prefix info exists for this property.
     *
     * @return True of prefix info exists for the given property.
     */
    public static boolean isPrefixableProperty(Property property) {
        return PrefixTables.PROPERTIES.containsRow(property);
    }

    /**
     * Gets whether prefix info exists for the given keyword.
     *
     * @param keyword
     *     Check if prefix info exists for this keyword.
     *
     * @return True of prefix info exists for the given keyword.
     */
    public static boolean isPrefixableKeyword(Keyword keyword) {
        return PrefixTables.KEYWORDS.containsRow(keyword);
    }

    /**
     * Gets whether prefix info exists for the given at-rule.
     *
     * @param name
     *     Check if prefix info exists for this at-rule.
     *
     * @return True of prefix info exists for the given at-rule.
     */
    public static boolean isPrefixableAtRule(String name) {
        return PrefixTables.AT_RULES.containsRow(name);
    }

    /**
     * Gets whether prefix info exists for the given selector name.
     *
     * @param name
     *     Check if prefix info exists for this selector name.
     *
     * @return True of prefix info exists for the given selector name.
     */
    public static boolean isPrefixableSelector(String name) {
        return PrefixTables.SELECTORS.containsRow(name);
    }

    /**
     * Gets whether prefix info exists for the given function name.
     *
     * @param function
     *     Check if prefix info exists for this function name.
     *
     * @return True of prefix info exists for the given function name.
     */
    public static boolean isPrefixableFunction(String function) {
        return PrefixTables.FUNCTIONS.containsRow(function);
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
    public static Double lastVersionPropertyIsPrefixed(Property property, Browser browser) {
        final Double val = PrefixTables.PROPERTIES.get(property, browser);
        return val != null ? val : -1d;
    }

    /**
     * Gets the last version of the given browser that requires a prefix for the given keyword.
     *
     * @param keyword
     *     The keyword.
     * @param browser
     *     The browser.
     *
     * @return The last version, or -1 if all known versions of the browser supports the keyword unprefixed.
     */
    public static Double lastVersionKeywordIsPrefixed(Keyword keyword, Browser browser) {
        final Double val = PrefixTables.KEYWORDS.get(keyword, browser);
        return val != null ? val : -1d;
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
    public static Double lastVersionAtRuleIsPrefixed(String name, Browser browser) {
        final Double val = PrefixTables.AT_RULES.get(name, browser);
        return val != null ? val : -1d;
    }

    /**
     * Gets the last version of the given browser that requires a prefix for the given selector name.
     *
     * @param name
     *     The selector name.
     * @param browser
     *     The browser.
     *
     * @return The last version, or -1 if all known versions of the browser supports the selector unprefixed.
     */
    public static Double lastVersionSelectorIsPrefixed(String name, Browser browser) {
        final Double val = PrefixTables.SELECTORS.get(name, browser);
        return val != null ? val : -1d;
    }

    /**
     * Gets the last version of the given browser that requires a prefix for the given function name.
     *
     * @param name
     *     The function name.
     * @param browser
     *     The browser.
     *
     * @return The last version, or -1 if all known versions of the browser supports the function name unprefixed.
     */
    public static Double lastVersionFunctionIsPrefixed(String name, Browser browser) {
        final Double val = PrefixTables.FUNCTIONS.get(name, browser);
        return val != null ? val : -1d;
    }
}
