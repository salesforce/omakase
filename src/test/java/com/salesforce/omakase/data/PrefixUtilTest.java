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
 * Unit tests for {@link PrefixUtil}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class PrefixUtilTest {
    @Test
    public void hasPropertyTrue() {
        assertThat(PrefixUtil.isPrefixibleProperty(Property.BORDER_RADIUS)).isTrue();
    }

    @Test
    public void hasPropertyFalse() {
        assertThat(PrefixUtil.isPrefixibleProperty(Property.BORDER)).isFalse();
    }

    @Test
    public void hasFunctionTrue() {
        assertThat(PrefixUtil.isPrefixibleFunction("calc")).isTrue();
    }

    @Test
    public void hasFunctionFalse() {
        assertThat(PrefixUtil.isPrefixibleFunction("blah")).isFalse();
    }

    @Test
    public void hasAtRule() {
        assertThat(PrefixUtil.isPrefixibleAtRule("keyframes")).isTrue();
    }

    @Test
    public void hasAtRuleFalse() {
        assertThat(PrefixUtil.isPrefixibleAtRule("blah")).isFalse();
    }

    @Test
    public void hasSelector() {
        assertThat(PrefixUtil.isPrefixibleSelector("selection")).isTrue();
    }

    @Test
    public void hasSelectorFalse() {
        assertThat(PrefixUtil.isPrefixibleSelector("blah")).isFalse();
    }

    @Test
    public void lastVersionPropertyIsPrefixed() {
        assertThat(PrefixUtil.lastVersionPropertyIsPrefixed(Property.BORDER_RADIUS, Browser.SAFARI)).isEqualTo(4);
    }

    @Test
    public void lastVersionPropertyIsPrefixedCached() {
        assertThat(PrefixUtil.lastVersionPropertyIsPrefixed(Property.BORDER_RADIUS, Browser.SAFARI)).isEqualTo(4);
        assertThat(PrefixUtil.lastVersionPropertyIsPrefixed(Property.BORDER_RADIUS, Browser.SAFARI)).isEqualTo(4);
    }

    @Test
    public void lastVersionPropertyIsPrefixedNotPresent() {
        assertThat(PrefixUtil.lastVersionPropertyIsPrefixed(Property.BORDER_RADIUS, Browser.IE)).isEqualTo(-1);
    }

    @Test
    public void lastPrefixedVersionForNotPrefixedProperty() {
        assertThat(PrefixUtil.lastVersionPropertyIsPrefixed(Property.BORDER, Browser.CHROME)).isEqualTo(-1);
    }

    @Test
    public void lastVersionFunctionIsPrefixed() {
        assertThat(PrefixUtil.lastVersionFunctionIsPrefixed("calc", Browser.CHROME)).isEqualTo(25);
    }

    @Test
    public void lastVersionFunctionIsPrefixedNotPresent() {
        assertThat(PrefixUtil.lastVersionFunctionIsPrefixed("calc", Browser.IE)).isEqualTo(-1);
    }

    @Test
    public void lastPrefixedVersionFunctionForNotPrefixedFunction() {
        assertThat(PrefixUtil.lastVersionFunctionIsPrefixed("blah", Browser.IE)).isEqualTo(-1);
    }

    @Test
    public void lastVersionAtRuleIsPrefixed() {
        assertThat(PrefixUtil.lastVersionAtRuleIsPrefixed("keyframes", Browser.CHROME)).isGreaterThan(30);
    }

    @Test
    public void lastVersionAtRuleIsPrefixedNotPresent() {
        assertThat(PrefixUtil.lastVersionAtRuleIsPrefixed("keyframes", Browser.IE)).isEqualTo(-1);
    }

    @Test
    public void lastVersionAtRuleIsPrefixedForNotPrefixedAtRule() {
        assertThat(PrefixUtil.lastVersionAtRuleIsPrefixed("media", Browser.IE)).isEqualTo(-1);
    }

    @Test
    public void lastVersionSelectorWasPrefixed() {
        assertThat(PrefixUtil.lastVersionSelectorIsPrefixed("selection", Browser.FIREFOX)).isGreaterThan(24);
    }

    @Test
    public void lastVersionSelectorWasPrefixedNotPresent() {
        assertThat(PrefixUtil.lastVersionSelectorIsPrefixed("selection", Browser.IE)).isEqualTo(-1);
    }

    @Test
    public void lastVersionSelectorWasPrefixedForNotPrefixedSelector() {
        assertThat(PrefixUtil.lastVersionSelectorIsPrefixed("before", Browser.IE)).isEqualTo(-1);
    }
}
