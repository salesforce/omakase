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

package com.salesforce.omakase;

import com.google.common.collect.Iterables;
import com.salesforce.omakase.data.Browser;
import com.salesforce.omakase.data.Keyword;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.data.Property;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link SupportMatrix}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class SupportMatrixTest {
    @Rule public final ExpectedException exception = ExpectedException.none();

    private SupportMatrix support;

    @Before
    public void setup() {
        this.support = new SupportMatrix();
    }

    @Test
    public void browserInt() {
        support.browser(Browser.CHROME, 27);
        assertThat(support.supportsVersion(Browser.CHROME, 27)).isTrue();
    }

    @Test
    public void browserDouble() {
        support.browser(Browser.SAFARI, 6.1);
        assertThat(support.supportsVersion(Browser.SAFARI, 6.1)).isTrue();
    }

    @Test
    public void latestBrowser() {
        support.latest(Browser.CHROME);
        assertThat(support.allSupportedVersions(Browser.CHROME)).containsExactly(Browser.CHROME.versions().get(0));
    }

    @Test
    public void lastNVersions() {
        support.last(Browser.CHROME, 3);
        Double first = Iterables.get(Browser.CHROME.versions(), 0);
        Double second = Iterables.get(Browser.CHROME.versions(), 1);
        Double third = Iterables.get(Browser.CHROME.versions(), 2);

        assertThat(support.allSupportedVersions(Browser.CHROME)).containsOnly(first, second, third);
    }

    @Test
    public void lastNVersionsOutOfRange() {
        exception.expect(IllegalArgumentException.class);
        support.last(Browser.IE_MOBILE, 100);
    }

    @Test
    public void allBrowsers() {
        support.all(Browser.IE);
        for (double d : Browser.IE.versions()) {
            assertThat(support.supportsVersion(Browser.IE, d)).isTrue();
        }
    }

    @Test
    public void supportsWhenEmpty() {
        assertThat(support.supportsVersion(Browser.CHROME, 26)).isFalse();
    }

    @Test
    public void supportsVersionFalse() {
        support.browser(Browser.CHROME, 27);
        assertThat(support.supportsVersion(Browser.CHROME, 26)).isFalse();
    }

    @Test
    public void supportsVersionOrLower() {
        support.browser(Browser.CHROME, 7);
        support.browser(Browser.CHROME, 15);
        support.browser(Browser.CHROME, 16);
        support.browser(Browser.CHROME, 17);
        support.browser(Browser.CHROME, 18);
        support.browser(Browser.CHROME, 19);
        support.browser(Browser.CHROME, 20);
        assertThat(support.supportsVersionOrLower(Browser.CHROME, 20)).isTrue();
        assertThat(support.supportsVersionOrLower(Browser.CHROME, 18)).isTrue();
        assertThat(support.supportsVersionOrLower(Browser.CHROME, 12)).isTrue();
    }

    @Test
    public void supportsVersionOrLowerFalse() {
        support.browser(Browser.CHROME, 7);
        support.browser(Browser.CHROME, 15);
        support.browser(Browser.CHROME, 16);
        support.browser(Browser.CHROME, 17);
        support.browser(Browser.CHROME, 18);
        support.browser(Browser.CHROME, 19);
        support.browser(Browser.CHROME, 20);
        assertThat(support.supportsVersionOrLower(Browser.CHROME, 6)).isFalse();
        assertThat(support.supportsVersionOrLower(Browser.CHROME, 4)).isFalse();
    }

    @Test
    public void supportsVersionOrLowerBrowserNotSupported() {
        support.browser(Browser.CHROME, 20);
        assertThat(support.supportsVersionOrLower(Browser.FIREFOX, 22)).isFalse();
    }

    @Test
    public void supportsBrowserTrue() {
        support.browser(Browser.CHROME, 27);
        assertThat(support.supportsBrowser(Browser.CHROME)).isTrue();
    }

    @Test
    public void supportsBrowserFalse() {
        support.browser(Browser.CHROME, 27);
        assertThat(support.supportsBrowser(Browser.IE)).isFalse();
    }

    @Test
    public void allSupportedVersionsInAscendingOrder() {
        support.browser(Browser.CHROME, 27);
        support.browser(Browser.CHROME, 26);
        support.browser(Browser.CHROME, 22);
        support.browser(Browser.CHROME, 17);
        assertThat(support.allSupportedVersions(Browser.CHROME)).containsExactly(17d, 22d, 26d, 27d);
    }

    @Test
    public void lowestSupportedVersionPresent() {
        support.browser(Browser.CHROME, 27);
        support.browser(Browser.CHROME, 26);
        support.browser(Browser.CHROME, 22);
        support.browser(Browser.CHROME, 17);
        assertThat(support.lowestSupportedVersion(Browser.CHROME)).isEqualTo(17);
    }

    @Test
    public void lowestSupportedVersionNotPresent() {
        assertThat(support.lowestSupportedVersion(Browser.CHROME)).isEqualTo(-1d);
    }

    @Test
    public void supportedBrowsers() {
        support.browser(Browser.CHROME, 27);
        support.browser(Browser.SAFARI, 5);
        support.browser(Browser.FIREFOX, 13);
        assertThat(support.supportedBrowsers()).containsOnly(Browser.CHROME, Browser.SAFARI, Browser.FIREFOX);
    }

    @Test
    public void prefixesForProperty() {
        support.browser(Browser.IE, 10);
        support.browser(Browser.CHROME, 27);
        support.browser(Browser.SAFARI, 4);
        support.browser(Browser.FIREFOX, 3.6);
        support.browser(Browser.OPERA, 15);
        assertThat(support.prefixesForProperty(Property.BORDER_RADIUS)).containsOnly(Prefix.WEBKIT, Prefix.MOZ);
    }

    @Test
    public void prefixesForPropertyCached() {
        support.browser(Browser.IE, 10);
        support.browser(Browser.CHROME, 27);
        support.browser(Browser.SAFARI, 4);
        support.browser(Browser.FIREFOX, 3.6);
        support.browser(Browser.OPERA, 15);
        assertThat(support.prefixesForProperty(Property.BORDER_RADIUS)).containsOnly(Prefix.WEBKIT, Prefix.MOZ);
        assertThat(support.prefixesForProperty(Property.BORDER_RADIUS)).containsOnly(Prefix.WEBKIT, Prefix.MOZ);
    }

    @Test
    public void requiresPrefixForPropertyTrue() {
        support.browser(Browser.IE, 10);
        support.browser(Browser.CHROME, 27);
        support.browser(Browser.SAFARI, 4);
        support.browser(Browser.FIREFOX, 3.6);
        support.browser(Browser.OPERA, 15);
        assertThat(support.requiresPrefixForProperty(Prefix.WEBKIT, Property.BORDER_RADIUS)).isTrue();
    }

    @Test
    public void requiresPrefixForPropertyFalse() {
        support.browser(Browser.IE, 10);
        support.browser(Browser.CHROME, 27);
        support.browser(Browser.SAFARI, 4);
        support.browser(Browser.FIREFOX, 3.6);
        support.browser(Browser.OPERA, 15);
        assertThat(support.requiresPrefixForProperty(Prefix.MS, Property.BORDER_RADIUS)).isFalse();
    }

    @Test
    public void requiresPrefixForUnprefixablePropertyFalse() {
        support.browser(Browser.IE, 10);
        support.browser(Browser.CHROME, 27);
        support.browser(Browser.SAFARI, 4);
        support.browser(Browser.FIREFOX, 3.6);
        support.browser(Browser.OPERA, 15);
        assertThat(support.requiresPrefixForProperty(Prefix.MS, Property.MARGIN)).isFalse();
    }

    @Test
    public void prefixesForKeyword() {
        support.browser(Browser.IE, 10);
        support.browser(Browser.CHROME, 22);
        support.browser(Browser.FIREFOX, 28);
        assertThat(support.prefixesForKeyword(Keyword.FLEX)).containsOnly(Prefix.MS, Prefix.WEBKIT);
    }

    @Test
    public void prefixesForKeywordCached() {
        support.browser(Browser.IE, 10);
        support.browser(Browser.CHROME, 22);
        support.browser(Browser.FIREFOX, 28);
        assertThat(support.prefixesForKeyword(Keyword.FLEX)).containsOnly(Prefix.MS, Prefix.WEBKIT);
        assertThat(support.prefixesForKeyword(Keyword.FLEX)).containsOnly(Prefix.MS, Prefix.WEBKIT);
    }

    @Test
    public void requiresPrefixForKeywordTrue() {
        support.browser(Browser.IE, 10);
        support.browser(Browser.CHROME, 22);
        support.browser(Browser.FIREFOX, 28);
        assertThat(support.requiresPrefixForKeyword(Prefix.WEBKIT, Keyword.FLEX)).isTrue();
    }

    @Test
    public void requiresPrefixForKeywordFalse() {
        support.browser(Browser.IE, 10);
        support.browser(Browser.CHROME, 22);
        support.browser(Browser.FIREFOX, 28);
        assertThat(support.requiresPrefixForKeyword(Prefix.MOZ, Keyword.FLEX)).isFalse();
    }

    @Test
    public void requiresPrefixForUnprefixableKeywordFalse() {
        support.browser(Browser.IE, 10);
        support.browser(Browser.CHROME, 22);
        support.browser(Browser.FIREFOX, 28);
        assertThat(support.requiresPrefixForKeyword(Prefix.MS, Keyword.ABOVE)).isFalse();
    }

    @Test
    public void prefixesForAtRule() {
        support.browser(Browser.IE, 10);
        support.browser(Browser.CHROME, 20);
        support.browser(Browser.FIREFOX, 14);
        assertThat(support.prefixesForAtRule("keyframes")).containsOnly(Prefix.WEBKIT, Prefix.MOZ);
    }

    @Test
    public void prefixesForAtRuleCached() {
        support.browser(Browser.IE, 10);
        support.browser(Browser.CHROME, 20);
        support.browser(Browser.FIREFOX, 14);
        assertThat(support.prefixesForAtRule("keyframes")).containsOnly(Prefix.WEBKIT, Prefix.MOZ);
        assertThat(support.prefixesForAtRule("keyframes")).containsOnly(Prefix.WEBKIT, Prefix.MOZ);
    }

    @Test
    public void requiresPrefixForAtRuleTrue() {
        support.browser(Browser.IE, 10);
        support.browser(Browser.CHROME, 20);
        support.browser(Browser.FIREFOX, 14);
        assertThat(support.requiresPrefixForAtRule(Prefix.WEBKIT, "keyframes")).isTrue();
    }

    @Test
    public void requiresPrefixForAtRuleFalse() {
        support.browser(Browser.IE, 10);
        support.browser(Browser.CHROME, 20);
        support.browser(Browser.FIREFOX, 14);
        assertThat(support.requiresPrefixForAtRule(Prefix.WEBKIT, "bop")).isFalse();
    }

    @Test
    public void prefixesForSelector() {
        support.browser(Browser.IE, 10);
        support.browser(Browser.CHROME, 20);
        support.browser(Browser.FIREFOX, 14);
        assertThat(support.prefixesForSelector("selection")).containsOnly(Prefix.MOZ);
    }

    @Test
    public void prefixesForSelectorCached() {
        support.browser(Browser.IE, 10);
        support.browser(Browser.CHROME, 20);
        support.browser(Browser.FIREFOX, 14);
        assertThat(support.prefixesForSelector("selection")).containsOnly(Prefix.MOZ);
        assertThat(support.prefixesForSelector("selection")).containsOnly(Prefix.MOZ);
    }

    @Test
    public void requiresPrefixForSelectorTrue() {
        support.browser(Browser.IE, 10);
        support.browser(Browser.CHROME, 20);
        support.browser(Browser.FIREFOX, 14);
        assertThat(support.requiresPrefixForSelector(Prefix.MOZ, "selection")).isTrue();
    }

    @Test
    public void requiresPrefixForSelectorFalse() {
        support.browser(Browser.IE, 10);
        support.browser(Browser.CHROME, 20);
        support.browser(Browser.FIREFOX, 14);
        assertThat(support.requiresPrefixForSelector(Prefix.O, "baaahd")).isFalse();
    }

    @Test
    public void prefixesForFunction() {
        support.browser(Browser.IE, 10);
        support.browser(Browser.CHROME, 20);
        support.browser(Browser.SAFARI, 7);
        support.browser(Browser.FIREFOX, 14);
        support.browser(Browser.OPERA, 12);
        assertThat(support.prefixesForFunction("calc")).containsOnly(Prefix.WEBKIT, Prefix.MOZ);
    }

    @Test
    public void prefixesForFunctionCached() {
        support.browser(Browser.IE, 10);
        support.browser(Browser.CHROME, 20);
        support.browser(Browser.SAFARI, 7);
        support.browser(Browser.FIREFOX, 14);
        support.browser(Browser.OPERA, 12);
        assertThat(support.prefixesForFunction("calc")).containsOnly(Prefix.WEBKIT, Prefix.MOZ);
        assertThat(support.prefixesForFunction("calc")).containsOnly(Prefix.WEBKIT, Prefix.MOZ);
    }

    @Test
    public void requiresPrefixForFunctionTrue() {
        support.browser(Browser.IE, 10);
        support.browser(Browser.CHROME, 20);
        support.browser(Browser.SAFARI, 7);
        support.browser(Browser.FIREFOX, 14);
        support.browser(Browser.OPERA, 12);
        assertThat(support.requiresPrefixForFunction(Prefix.WEBKIT, "calc")).isTrue();
    }

    @Test
    public void requiresPrefixForFunctionFalse() {
        support.browser(Browser.IE, 10);
        support.browser(Browser.CHROME, 20);
        support.browser(Browser.SAFARI, 7);
        support.browser(Browser.FIREFOX, 14);
        support.browser(Browser.OPERA, 12);
        assertThat(support.requiresPrefixForFunction(Prefix.O, "calc")).isFalse();
    }
}
