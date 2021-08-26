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

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Test;

/**
 * Unit tests for {@link PrefixTablesUtil}.
 *
 * @author nmcwilliams
 */
public class PrefixTablesUtilTest {
    @Test
    public void isPrefixableProperty() {
        assertThat(PrefixTablesUtil.isPrefixableProperty(Property.BORDER_RADIUS)).isTrue();
    }

    @Test
    public void isNotPrefixableProperty() {
        assertThat(PrefixTablesUtil.isPrefixableProperty(Property.BORDER)).isFalse();
    }

    @Test
    public void isPrefixibleKeyword() {
        assertThat(PrefixTablesUtil.isPrefixableKeyword(Keyword.FLEX)).isTrue();
    }

    @Test
    public void isNotPrefixibleKeyword() {
        assertThat(PrefixTablesUtil.isPrefixableKeyword(Keyword.ALL)).isFalse();
    }

    @Test
    public void isPrefixableAtRule() {
        assertThat(PrefixTablesUtil.isPrefixableAtRule("keyframes")).isTrue();
    }

    @Test
    public void isNotPrefixableAtRule() {
        assertThat(PrefixTablesUtil.isPrefixableAtRule("blah")).isFalse();
    }

    @Test
    public void isPrefixableSelector() {
        assertThat(PrefixTablesUtil.isPrefixableSelector("selection")).isTrue();
    }

    @Test
    public void isNotPrefixableSelector() {
        assertThat(PrefixTablesUtil.isPrefixableSelector("blah")).isFalse();
    }

    @Test
    public void isPrefixableFunction() {
        assertThat(PrefixTablesUtil.isPrefixableFunction("calc")).isTrue();
    }

    @Test
    public void isNotPrefixableFunction() {
        assertThat(PrefixTablesUtil.isPrefixableFunction("blah")).isFalse();
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
    public void lastVersionKeywordIsPrefixed() {
        assertThat(PrefixTablesUtil.lastVersionKeywordIsPrefixed(Keyword.FLEX, Browser.FIREFOX)).isEqualTo(21);
    }

    @Test
    public void lastVersionKeywordIsPrefixedCached() {
        assertThat(PrefixTablesUtil.lastVersionKeywordIsPrefixed(Keyword.FLEX, Browser.FIREFOX)).isEqualTo(21);
        assertThat(PrefixTablesUtil.lastVersionKeywordIsPrefixed(Keyword.FLEX, Browser.FIREFOX)).isEqualTo(21);
    }

    @Test
    public void lastVersionKeywordIsPrefixedNotPresent() {
        assertThat(PrefixTablesUtil.lastVersionKeywordIsPrefixed(Keyword.FLEX, Browser.EDGE)).isEqualTo(-1);
    }

    @Test
    public void lastPrefixedVersionForNotPrefixedKeyword() {
        assertThat(PrefixTablesUtil.lastVersionKeywordIsPrefixed(Keyword.ALL, Browser.CHROME)).isEqualTo(-1);
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
}
