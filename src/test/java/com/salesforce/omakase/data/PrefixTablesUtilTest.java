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
 * Unit tests for {@link PrefixTablesUtil}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class PrefixTablesUtilTest {
    @Test
    public void hasPropertyTrue() {
        assertThat(PrefixTablesUtil.isPrefixableProperty(Property.BORDER_RADIUS)).isTrue();
    }

    @Test
    public void hasPropertyFalse() {
        assertThat(PrefixTablesUtil.isPrefixableProperty(Property.BORDER)).isFalse();
    }

    @Test
    public void hasFunctionTrue() {
        assertThat(PrefixTablesUtil.isPrefixableFunction("calc")).isTrue();
    }

    @Test
    public void hasFunctionFalse() {
        assertThat(PrefixTablesUtil.isPrefixableFunction("blah")).isFalse();
    }

    @Test
    public void hasAtRule() {
        assertThat(PrefixTablesUtil.isPrefixableAtRule("keyframes")).isTrue();
    }

    @Test
    public void hasAtRuleFalse() {
        assertThat(PrefixTablesUtil.isPrefixableAtRule("blah")).isFalse();
    }

    @Test
    public void hasSelector() {
        assertThat(PrefixTablesUtil.isPrefixableSelector("selection")).isTrue();
    }

    @Test
    public void hasSelectorFalse() {
        assertThat(PrefixTablesUtil.isPrefixableSelector("blah")).isFalse();
    }

    @Test
    public void lastVersionPropertyIsPrefixed() {
        assertThat(PrefixTablesUtil.lastVersionPropertyIsPrefixed(Property.BORDER_RADIUS, Browser.SAFARI)).isEqualTo(4);
    }

    @Test
    public void lastVersionPropertyIsPrefixedCached() {
        assertThat(PrefixTablesUtil.lastVersionPropertyIsPrefixed(Property.BORDER_RADIUS, Browser.SAFARI)).isEqualTo(4);
        assertThat(PrefixTablesUtil.lastVersionPropertyIsPrefixed(Property.BORDER_RADIUS, Browser.SAFARI)).isEqualTo(4);
    }

    @Test
    public void lastVersionPropertyIsPrefixedNotPresent() {
        assertThat(PrefixTablesUtil.lastVersionPropertyIsPrefixed(Property.BORDER_RADIUS, Browser.IE)).isEqualTo(-1);
    }

    @Test
    public void lastPrefixedVersionForNotPrefixedProperty() {
        assertThat(PrefixTablesUtil.lastVersionPropertyIsPrefixed(Property.BORDER, Browser.CHROME)).isEqualTo(-1);
    }

    @Test
    public void lastVersionFunctionIsPrefixed() {
        assertThat(PrefixTablesUtil.lastVersionFunctionIsPrefixed("calc", Browser.CHROME)).isEqualTo(25);
    }

    @Test
    public void lastVersionFunctionIsPrefixedNotPresent() {
        assertThat(PrefixTablesUtil.lastVersionFunctionIsPrefixed("calc", Browser.IE)).isEqualTo(-1);
    }

    @Test
    public void lastPrefixedVersionFunctionForNotPrefixedFunction() {
        assertThat(PrefixTablesUtil.lastVersionFunctionIsPrefixed("blah", Browser.IE)).isEqualTo(-1);
    }

    @Test
    public void lastVersionAtRuleIsPrefixed() {
        assertThat(PrefixTablesUtil.lastVersionAtRuleIsPrefixed("keyframes", Browser.CHROME)).isGreaterThan(30);
    }

    @Test
    public void lastVersionAtRuleIsPrefixedNotPresent() {
        assertThat(PrefixTablesUtil.lastVersionAtRuleIsPrefixed("keyframes", Browser.IE)).isEqualTo(-1);
    }

    @Test
    public void lastVersionAtRuleIsPrefixedForNotPrefixedAtRule() {
        assertThat(PrefixTablesUtil.lastVersionAtRuleIsPrefixed("media", Browser.IE)).isEqualTo(-1);
    }

    @Test
    public void lastVersionSelectorWasPrefixed() {
        assertThat(PrefixTablesUtil.lastVersionSelectorIsPrefixed("selection", Browser.FIREFOX)).isGreaterThan(24);
    }

    @Test
    public void lastVersionSelectorWasPrefixedNotPresent() {
        assertThat(PrefixTablesUtil.lastVersionSelectorIsPrefixed("selection", Browser.IE)).isEqualTo(-1);
    }

    @Test
    public void lastVersionSelectorWasPrefixedForNotPrefixedSelector() {
        assertThat(PrefixTablesUtil.lastVersionSelectorIsPrefixed("before", Browser.IE)).isEqualTo(-1);
    }
}
