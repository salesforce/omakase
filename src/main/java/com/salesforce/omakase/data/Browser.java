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

package com.salesforce.omakase.data;

import java.util.List;

import com.google.common.collect.ImmutableList;

/**
 * Enum of browsers.
 * <p>
 * The *Browser data* in this file is retrieved from caniuse.com and
 * licensed under CC-BY-4.0 (http://creativecommons.org/licenses/by/4.0).
 * <p>
 * THIS FILE IS GENERATED. DO NOT EDIT DIRECTLY.
 * <p>
 * See class com.salesforce.omakase.tools.GenerateBrowserEnum for instructions on updating.
 */
public enum Browser {
    /** The 'Internet Explorer' browser */
    IE("ie", "Internet Explorer", Prefix.MS, ImmutableList.of(11.0,10.0,9.0,8.0,7.0,6.0,5.5)),

    /** The 'Microsoft Edge' browser */
    EDGE("edge", "Microsoft Edge", Prefix.MS, ImmutableList.of(17.0,16.0,15.0,14.0,13.0,12.0)),

    /** The 'Opera' browser */
    OPERA("opera", "Opera", Prefix.WEBKIT, ImmutableList.of(53.0,52.0,51.0,50.0,49.0,48.0,47.0,46.0,45.0,44.0,43.0,42.0,41.0,40.0,39.0,38.0,37.0,36.0,35.0,34.0,33.0,32.0,31.0,30.0,29.0,28.0,27.0,26.0,25.0,24.0,23.0,22.0,21.0,20.0,19.0,18.0,17.0,16.0,15.0,12.1,12.0,11.6,11.5,11.1,11.0,10.6,10.5,10.1,10.0,9.6,9.5,9.0)),

    /** The 'Google Chrome' browser */
    CHROME("chrome", "Google Chrome", Prefix.WEBKIT, ImmutableList.of(68.0,67.0,66.0,65.0,64.0,63.0,62.0,61.0,60.0,59.0,58.0,57.0,56.0,55.0,54.0,53.0,52.0,51.0,50.0,49.0,48.0,47.0,46.0,45.0,44.0,43.0,42.0,41.0,40.0,39.0,38.0,37.0,36.0,35.0,34.0,33.0,32.0,31.0,30.0,29.0,28.0,27.0,26.0,25.0,24.0,23.0,22.0,21.0,20.0,19.0,18.0,17.0,16.0,15.0,14.0,13.0,12.0,11.0,10.0,9.0,8.0,7.0,6.0,5.0,4.0)),

    /** The 'Safari' browser */
    SAFARI("safari", "Safari", Prefix.WEBKIT, ImmutableList.of(11.1,11.0,10.1,10.0,9.1,9.0,8.0,7.1,7.0,6.1,6.0,5.1,5.0,4.0,3.2,3.1)),

    /** The 'Firefox' browser */
    FIREFOX("firefox", "Firefox", Prefix.MOZ, ImmutableList.of(61.0,60.0,59.0,58.0,57.0,56.0,55.0,54.0,53.0,52.0,51.0,50.0,49.0,48.0,47.0,46.0,45.0,44.0,43.0,42.0,41.0,40.0,39.0,38.0,37.0,36.0,35.0,34.0,33.0,32.0,31.0,30.0,29.0,28.0,27.0,26.0,25.0,24.0,23.0,22.0,21.0,20.0,19.0,18.0,17.0,16.0,15.0,14.0,13.0,12.0,11.0,10.0,9.0,8.0,7.0,6.0,5.0,4.0,3.6,3.5,3.0,2.0)),

    /** The 'Android Browser' browser */
    ANDROID("android", "Android Browser", Prefix.WEBKIT, ImmutableList.of(67.0,4.4,4.3,4.2,4.1,4.0,3.0,2.3,2.2,2.1)),

    /** The 'IE Mobile' browser */
    IE_MOBILE("ie_mob", "IE Mobile", Prefix.MS, ImmutableList.of(11.0,10.0)),

    /** The 'Safari on iOS' browser */
    IOS_SAFARI("ios_saf", "Safari on iOS", Prefix.WEBKIT, ImmutableList.of(11.4,11.3,11.2,11.0,10.3,10.2,10.0,9.3,9.2,9.0,8.4,8.1,8.0,7.1,7.0,6.1,6.0,5.1,5.0,4.3,4.2,4.1,4.0,3.2)),

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
