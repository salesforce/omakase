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

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;
import com.salesforce.omakase.BrowserVersion;

/**
 * Contains the last version of a browser that requires a prefix for various CSS properties.
 * <p/>
 * THIS FILE IS GENERATED. DO NOT EDIT DIRECTLY.
 * <p/>
 * See class com.salesforce.omakase.test.util.tool.GeneratePrefixInfoClass for instructions on updating.
 */
public final class PrefixInfo {
    private static final Multimap<Property, BrowserVersion> PROPERTIES;
    private static final Multimap<String, BrowserVersion> FUNCTIONS;

    static {
        ImmutableSetMultimap.Builder<Property, BrowserVersion> builder = ImmutableSetMultimap.builder();

        builder.put(Property.BORDER_RADIUS, new BrowserVersion(Browser.CHROME, 4.0));
        builder.put(Property.BORDER_TOP_LEFT_RADIUS, new BrowserVersion(Browser.CHROME, 4.0));
        builder.put(Property.BORDER_TOP_RIGHT_RADIUS, new BrowserVersion(Browser.CHROME, 4.0));
        builder.put(Property.BORDER_BOTTOM_LEFT_RADIUS, new BrowserVersion(Browser.CHROME, 4.0));
        builder.put(Property.BORDER_BOTTOM_RIGHT_RADIUS, new BrowserVersion(Browser.CHROME, 4.0));
        builder.put(Property.BORDER_RADIUS, new BrowserVersion(Browser.SAFARI, 4.0));
        builder.put(Property.BORDER_TOP_LEFT_RADIUS, new BrowserVersion(Browser.SAFARI, 4.0));
        builder.put(Property.BORDER_TOP_RIGHT_RADIUS, new BrowserVersion(Browser.SAFARI, 4.0));
        builder.put(Property.BORDER_BOTTOM_LEFT_RADIUS, new BrowserVersion(Browser.SAFARI, 4.0));
        builder.put(Property.BORDER_BOTTOM_RIGHT_RADIUS, new BrowserVersion(Browser.SAFARI, 4.0));
        builder.put(Property.BORDER_RADIUS, new BrowserVersion(Browser.FIREFOX, 3.6));
        builder.put(Property.BORDER_TOP_LEFT_RADIUS, new BrowserVersion(Browser.FIREFOX, 3.6));
        builder.put(Property.BORDER_TOP_RIGHT_RADIUS, new BrowserVersion(Browser.FIREFOX, 3.6));
        builder.put(Property.BORDER_BOTTOM_LEFT_RADIUS, new BrowserVersion(Browser.FIREFOX, 3.6));
        builder.put(Property.BORDER_BOTTOM_RIGHT_RADIUS, new BrowserVersion(Browser.FIREFOX, 3.6));
        builder.put(Property.BORDER_RADIUS, new BrowserVersion(Browser.ANDROID, 2.1));
        builder.put(Property.BORDER_TOP_LEFT_RADIUS, new BrowserVersion(Browser.ANDROID, 2.1));
        builder.put(Property.BORDER_TOP_RIGHT_RADIUS, new BrowserVersion(Browser.ANDROID, 2.1));
        builder.put(Property.BORDER_BOTTOM_LEFT_RADIUS, new BrowserVersion(Browser.ANDROID, 2.1));
        builder.put(Property.BORDER_BOTTOM_RIGHT_RADIUS, new BrowserVersion(Browser.ANDROID, 2.1));
        builder.put(Property.BORDER_RADIUS, new BrowserVersion(Browser.IOS_SAFARI, 3.2));
        builder.put(Property.BORDER_TOP_LEFT_RADIUS, new BrowserVersion(Browser.IOS_SAFARI, 3.2));
        builder.put(Property.BORDER_TOP_RIGHT_RADIUS, new BrowserVersion(Browser.IOS_SAFARI, 3.2));
        builder.put(Property.BORDER_BOTTOM_LEFT_RADIUS, new BrowserVersion(Browser.IOS_SAFARI, 3.2));
        builder.put(Property.BORDER_BOTTOM_RIGHT_RADIUS, new BrowserVersion(Browser.IOS_SAFARI, 3.2));
        builder.put(Property.BOX_SHADOW, new BrowserVersion(Browser.CHROME, 9.0));
        builder.put(Property.BOX_SHADOW, new BrowserVersion(Browser.SAFARI, 5.0));
        builder.put(Property.BOX_SHADOW, new BrowserVersion(Browser.FIREFOX, 3.6));
        builder.put(Property.BOX_SHADOW, new BrowserVersion(Browser.ANDROID, 3.0));
        builder.put(Property.BOX_SHADOW, new BrowserVersion(Browser.IOS_SAFARI, 4.3));
        builder.put(Property.TRANSITION, new BrowserVersion(Browser.OPERA, 12.0));
        builder.put(Property.TRANSITION_PROPERTY, new BrowserVersion(Browser.OPERA, 12.0));
        builder.put(Property.TRANSITION_DURATION, new BrowserVersion(Browser.OPERA, 12.0));
        builder.put(Property.TRANSITION_DELAY, new BrowserVersion(Browser.OPERA, 12.0));
        builder.put(Property.TRANSITION_TIMING_FUNCTION, new BrowserVersion(Browser.OPERA, 12.0));
        builder.put(Property.TRANSITION, new BrowserVersion(Browser.CHROME, 25.0));
        builder.put(Property.TRANSITION_PROPERTY, new BrowserVersion(Browser.CHROME, 25.0));
        builder.put(Property.TRANSITION_DURATION, new BrowserVersion(Browser.CHROME, 25.0));
        builder.put(Property.TRANSITION_DELAY, new BrowserVersion(Browser.CHROME, 25.0));
        builder.put(Property.TRANSITION_TIMING_FUNCTION, new BrowserVersion(Browser.CHROME, 25.0));
        builder.put(Property.TRANSITION, new BrowserVersion(Browser.SAFARI, 6.0));
        builder.put(Property.TRANSITION_PROPERTY, new BrowserVersion(Browser.SAFARI, 6.0));
        builder.put(Property.TRANSITION_DURATION, new BrowserVersion(Browser.SAFARI, 6.0));
        builder.put(Property.TRANSITION_DELAY, new BrowserVersion(Browser.SAFARI, 6.0));
        builder.put(Property.TRANSITION_TIMING_FUNCTION, new BrowserVersion(Browser.SAFARI, 6.0));
        builder.put(Property.TRANSITION, new BrowserVersion(Browser.FIREFOX, 15.0));
        builder.put(Property.TRANSITION_PROPERTY, new BrowserVersion(Browser.FIREFOX, 15.0));
        builder.put(Property.TRANSITION_DURATION, new BrowserVersion(Browser.FIREFOX, 15.0));
        builder.put(Property.TRANSITION_DELAY, new BrowserVersion(Browser.FIREFOX, 15.0));
        builder.put(Property.TRANSITION_TIMING_FUNCTION, new BrowserVersion(Browser.FIREFOX, 15.0));
        builder.put(Property.TRANSITION, new BrowserVersion(Browser.ANDROID, 4.3));
        builder.put(Property.TRANSITION_PROPERTY, new BrowserVersion(Browser.ANDROID, 4.3));
        builder.put(Property.TRANSITION_DURATION, new BrowserVersion(Browser.ANDROID, 4.3));
        builder.put(Property.TRANSITION_DELAY, new BrowserVersion(Browser.ANDROID, 4.3));
        builder.put(Property.TRANSITION_TIMING_FUNCTION, new BrowserVersion(Browser.ANDROID, 4.3));
        builder.put(Property.TRANSITION, new BrowserVersion(Browser.IOS_SAFARI, 6.1));
        builder.put(Property.TRANSITION_PROPERTY, new BrowserVersion(Browser.IOS_SAFARI, 6.1));
        builder.put(Property.TRANSITION_DURATION, new BrowserVersion(Browser.IOS_SAFARI, 6.1));
        builder.put(Property.TRANSITION_DELAY, new BrowserVersion(Browser.IOS_SAFARI, 6.1));
        builder.put(Property.TRANSITION_TIMING_FUNCTION, new BrowserVersion(Browser.IOS_SAFARI, 6.1));
        builder.put(Property.TRANSFORM, new BrowserVersion(Browser.IE, 9.0));
        builder.put(Property.TRANSFORM_ORIGIN, new BrowserVersion(Browser.IE, 9.0));
        builder.put(Property.TRANSFORM_STYLE, new BrowserVersion(Browser.IE, 9.0));
        builder.put(Property.TRANSFORM, new BrowserVersion(Browser.OPERA, 17.0));
        builder.put(Property.TRANSFORM_ORIGIN, new BrowserVersion(Browser.OPERA, 17.0));
        builder.put(Property.TRANSFORM_STYLE, new BrowserVersion(Browser.OPERA, 17.0));
        builder.put(Property.TRANSFORM, new BrowserVersion(Browser.CHROME, 31.0));
        builder.put(Property.TRANSFORM_ORIGIN, new BrowserVersion(Browser.CHROME, 31.0));
        builder.put(Property.TRANSFORM_STYLE, new BrowserVersion(Browser.CHROME, 31.0));
        builder.put(Property.TRANSFORM, new BrowserVersion(Browser.SAFARI, 7.0));
        builder.put(Property.TRANSFORM_ORIGIN, new BrowserVersion(Browser.SAFARI, 7.0));
        builder.put(Property.TRANSFORM_STYLE, new BrowserVersion(Browser.SAFARI, 7.0));
        builder.put(Property.TRANSFORM, new BrowserVersion(Browser.FIREFOX, 15.0));
        builder.put(Property.TRANSFORM_ORIGIN, new BrowserVersion(Browser.FIREFOX, 15.0));
        builder.put(Property.TRANSFORM_STYLE, new BrowserVersion(Browser.FIREFOX, 15.0));
        builder.put(Property.TRANSFORM, new BrowserVersion(Browser.ANDROID, 4.3));
        builder.put(Property.TRANSFORM_ORIGIN, new BrowserVersion(Browser.ANDROID, 4.3));
        builder.put(Property.TRANSFORM_STYLE, new BrowserVersion(Browser.ANDROID, 4.3));
        builder.put(Property.TRANSFORM, new BrowserVersion(Browser.IOS_SAFARI, 7.0));
        builder.put(Property.TRANSFORM_ORIGIN, new BrowserVersion(Browser.IOS_SAFARI, 7.0));
        builder.put(Property.TRANSFORM_STYLE, new BrowserVersion(Browser.IOS_SAFARI, 7.0));
        builder.put(Property.PERSPECTIVE, new BrowserVersion(Browser.OPERA, 17.0));
        builder.put(Property.PERSPECTIVE_ORIGIN, new BrowserVersion(Browser.OPERA, 17.0));
        builder.put(Property.BACKFACE_VISIBILITY, new BrowserVersion(Browser.OPERA, 17.0));
        builder.put(Property.PERSPECTIVE, new BrowserVersion(Browser.CHROME, 31.0));
        builder.put(Property.PERSPECTIVE_ORIGIN, new BrowserVersion(Browser.CHROME, 31.0));
        builder.put(Property.BACKFACE_VISIBILITY, new BrowserVersion(Browser.CHROME, 31.0));
        builder.put(Property.PERSPECTIVE, new BrowserVersion(Browser.SAFARI, 7.0));
        builder.put(Property.PERSPECTIVE_ORIGIN, new BrowserVersion(Browser.SAFARI, 7.0));
        builder.put(Property.BACKFACE_VISIBILITY, new BrowserVersion(Browser.SAFARI, 7.0));
        builder.put(Property.PERSPECTIVE, new BrowserVersion(Browser.FIREFOX, 15.0));
        builder.put(Property.PERSPECTIVE_ORIGIN, new BrowserVersion(Browser.FIREFOX, 15.0));
        builder.put(Property.BACKFACE_VISIBILITY, new BrowserVersion(Browser.FIREFOX, 15.0));
        builder.put(Property.PERSPECTIVE, new BrowserVersion(Browser.ANDROID, 4.3));
        builder.put(Property.PERSPECTIVE_ORIGIN, new BrowserVersion(Browser.ANDROID, 4.3));
        builder.put(Property.BACKFACE_VISIBILITY, new BrowserVersion(Browser.ANDROID, 4.3));
        builder.put(Property.PERSPECTIVE, new BrowserVersion(Browser.IOS_SAFARI, 7.0));
        builder.put(Property.PERSPECTIVE_ORIGIN, new BrowserVersion(Browser.IOS_SAFARI, 7.0));
        builder.put(Property.BACKFACE_VISIBILITY, new BrowserVersion(Browser.IOS_SAFARI, 7.0));
        builder.put(Property.BOX_SIZING, new BrowserVersion(Browser.CHROME, 9.0));
        builder.put(Property.BOX_SIZING, new BrowserVersion(Browser.SAFARI, 5.0));
        builder.put(Property.BOX_SIZING, new BrowserVersion(Browser.FIREFOX, 25.0));
        builder.put(Property.BOX_SIZING, new BrowserVersion(Browser.ANDROID, 3.0));
        builder.put(Property.BOX_SIZING, new BrowserVersion(Browser.IOS_SAFARI, 4.3));

        PROPERTIES = builder.build();
    }

    static {
        ImmutableSetMultimap.Builder<String, BrowserVersion> builder = ImmutableSetMultimap.builder();

        builder.put("calc", new BrowserVersion(Browser.CHROME, 25.0));
        builder.put("calc", new BrowserVersion(Browser.SAFARI, 6.0));
        builder.put("calc", new BrowserVersion(Browser.FIREFOX, 15.0));
        builder.put("calc", new BrowserVersion(Browser.IOS_SAFARI, 6.1));

        FUNCTIONS = builder.build();
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
    public static double lastPrefixedVersion(String function, Browser browser) {
        for (BrowserVersion browserVersion : FUNCTIONS.get(function)) {
            if (browserVersion.browser() == browser) return browserVersion.version();
        }
        return -1d;
    }
}
