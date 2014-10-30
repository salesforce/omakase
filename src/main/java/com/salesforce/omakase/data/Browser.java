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

import com.google.common.collect.ImmutableList;
import java.util.List;

/**
 * Enum of browsers.
 * <p/>
 * THIS FILE IS GENERATED. DO NOT EDIT DIRECTLY.
 * <p/>
 * See class com.salesforce.omakase.test.util.tool.GenerateBrowserEnum for instructions on updating.
 */
@SuppressWarnings("AutoBoxing")
public enum Browser {
    /** The 'Internet Explorer' browser */
    IE("ie", "Internet Explorer", Prefix.MS, ImmutableList.of(11.0,10.0,9.0,8.0,7.0,6.0,5.5)),

    /** The 'Opera' browser */
    OPERA("opera", "Opera", Prefix.O, ImmutableList.of(25.0,24.0,23.0,22.0,21.0,20.0,19.0,18.0,17.0,16.0,15.0,12.1,12.0,11.6,11.5,11.1,11.0,10.6,10.5,10.1,10.0,9.6,9.5)),

    /** The 'Chrome' browser */
    CHROME("chrome", "Chrome", Prefix.WEBKIT, ImmutableList.of(38.0,37.0,36.0,35.0,34.0,33.0,32.0,31.0,30.0,29.0,28.0,27.0,26.0,25.0,24.0,23.0,22.0,21.0,20.0,19.0,18.0,17.0,16.0,15.0,14.0,13.0,12.0,11.0,10.0,9.0,8.0,7.0,6.0,5.0,4.0)),

    /** The 'Safari' browser */
    SAFARI("safari", "Safari", Prefix.WEBKIT, ImmutableList.of(8.0,7.1,7.0,6.1,6.0,5.1,5.0,4.0,3.2,3.1)),

    /** The 'Firefox' browser */
    FIREFOX("firefox", "Firefox", Prefix.MOZ, ImmutableList.of(33.0,32.0,31.0,30.0,29.0,28.0,27.0,26.0,25.0,24.0,23.0,22.0,21.0,20.0,19.0,18.0,17.0,16.0,15.0,14.0,13.0,12.0,11.0,10.0,9.0,8.0,7.0,6.0,5.0,4.0,3.6,3.5,3.0,2.0)),

    /** The 'Android Browser' browser */
    ANDROID("android", "Android Browser", Prefix.WEBKIT, ImmutableList.of(4.4,4.3,4.2,4.1,4.0,3.0,2.3,2.2,2.1)),

    /** The 'IE Mobile' browser */
    IE_MOBILE("ie_mob", "IE Mobile", Prefix.MS, ImmutableList.of(10.0)),

    /** The 'Safari on iOS' browser */
    IOS_SAFARI("ios_saf", "Safari on iOS", Prefix.WEBKIT, ImmutableList.of(8.1,8.0,7.1,7.0,6.1,6.0,5.1,5.0,4.3,4.2,4.1,4.0,3.2)),

    /** The 'Opera Mini' browser */
    OPERA_MINI("op_mini", "Opera Mini", Prefix.O, ImmutableList.of(8.0,5.0)),

    ;

    private final String key;
    private final String name;
    private final List<Double> versions;
    private final Prefix prefix;

    Browser(String key, String name, Prefix prefix, List<Double> versions) {
        this.key = key;
        this.name = name;
        this.prefix = prefix;
        this.versions = versions;
    }

    /**
     * Gets the name of the browser.
     *
     * @return The name of the browser.
     */
    public String browserName() {
        return name;
    }

    /**
     * Gets the prefix used by this browser.
     *
     * @return The prefix used by this browser.
     */
    public Prefix prefix() {
        return prefix;
    }

    /**
     * Gets the caniuse.com browser key.
     *
     * @return The browser key.
     */
    public String key() {
        return key;
    }

    /**
     * Gets the list of browser versions, descending order (latest first).
     *
     * @return The list of browser versions.
     */
    public List<Double> versions() {
        return versions;
    }

    @Override
    public String toString() {
        return browserName();
    }
}
