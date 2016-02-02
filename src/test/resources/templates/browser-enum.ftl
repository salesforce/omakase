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

package ${package};

import com.google.common.collect.ImmutableList;
import java.util.List;

/**
 * Enum of browsers.
 * <p/>
 * The *Browser data* in this file is retrieved from caniuse.com and
 * licensed under CC-BY-4.0 (http://creativecommons.org/licenses/by/4.0).
 * <p/>
 * THIS FILE IS GENERATED. DO NOT EDIT DIRECTLY.
 * <p/>
 * See ${generator} for instructions on updating.
 */
@SuppressWarnings("AutoBoxing")
public enum Browser {
    <#list browsers as browser>
    /** The '${browser.displayName}' browser */
    ${browser.enumName}("${browser.key}", "${browser.displayName}", ${browser.prefix}, ImmutableList.of(${browser.versions})),

    </#list>
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
