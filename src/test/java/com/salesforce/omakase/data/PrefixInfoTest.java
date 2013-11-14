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

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link PrefixInfo}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class PrefixInfoTest {
    @Test
    public void hasPropertyTrue() {
        assertThat(PrefixInfo.hasProperty(Property.BORDER_RADIUS)).isTrue();
    }

    @Test
    public void hasPropertyFalse() {
        assertThat(PrefixInfo.hasProperty(Property.BORDER)).isFalse();
    }

    @Test
    public void lastPrefixVersion() {
        assertThat(PrefixInfo.lastPrefixedVersion(Property.BORDER_RADIUS, Browser.SAFARI)).isEqualTo(4);
    }

    @Test
    public void lastPrefixedVersionNotPresent() {
        assertThat(PrefixInfo.lastPrefixedVersion(Property.BORDER_RADIUS, Browser.IE)).isEqualTo(-1);
    }

    @Test
    public void lastPrefixedVersionForNotPrefixedProperty() {
        assertThat(PrefixInfo.lastPrefixedVersion(Property.BORDER, Browser.CHROME)).isEqualTo(-1);
    }

    @Test
    public void hasFunctionTrue() {
        assertThat(PrefixInfo.hasFunction("calc")).isTrue();
    }

    @Test
    public void hasFunctionFalse() {
        assertThat(PrefixInfo.hasFunction("blah")).isFalse();
    }

    @Test
    public void lastPrefixedVersionFunction() {
        assertThat(PrefixInfo.lastPrefixedVersion("calc", Browser.CHROME)).isEqualTo(25);
    }

    @Test
    public void lastPrefixedVersionFunctionNotPresent() {
        assertThat(PrefixInfo.lastPrefixedVersion("calc", Browser.IE)).isEqualTo(-1);
    }

    @Test
    public void lastPrefixedVersionFunctionForNotPrefixedFunction() {
        assertThat(PrefixInfo.lastPrefixedVersion("blah", Browser.IE)).isEqualTo(-1);
    }
}
