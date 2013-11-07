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

import com.google.common.collect.ImmutableList;
import java.util.List;

/**
 * Enum of browsers.
 * <p/>
 * THIS FILE IS GENERATED. DO NOT EDIT DIRECTLY.
 * <p/>
 * See ${generator} for instructions on updating.
 */
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
