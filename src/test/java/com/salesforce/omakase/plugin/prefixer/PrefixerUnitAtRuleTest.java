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

package com.salesforce.omakase.plugin.prefixer;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.salesforce.omakase.Omakase;
import com.salesforce.omakase.data.Browser;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.plugin.basic.AutoRefiner;
import com.salesforce.omakase.writer.StyleWriter;
import org.junit.Test;

import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Functional tests for {@link Prefixer} at-rule replacements.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class PrefixerUnitAtRuleTest {
    private String process(String original, Prefixer prefixer) {
        StyleWriter writer = StyleWriter.inline();
        Omakase.source(original)
            .use(AutoRefiner.refineEverything())
            .use(prefixer)
            .use(PrefixCleaner.mismatchedPrefixedUnits())
            .use(writer)
            .process();
        return writer.write();
    }

    private Prefixer setup(Prefix... prefixes) {
        List<Prefix> list = Lists.newArrayList(prefixes);
        boolean moz = Iterables.contains(list, Prefix.MOZ);
        boolean webkit = Iterables.contains(list, Prefix.WEBKIT);

        Prefixer prefixer = Prefixer.customBrowserSupport();

        prefixer.support().browser(Browser.FIREFOX, moz ? 15 : 16); // 15 is last prefixed
        if (webkit) prefixer.support().browser(Browser.CHROME, 25); // all versions currently prefixed

        return prefixer;
    }

    // required, not present (add)
    @Test
    public void atRuleTest1() {
        String original = "@keyframes test {\n" +
            "  from {top: 0%}\n" +
            "  50% {top: 50%}\n" +
            "  to {top: 100%}\n" +
            "}";
        String expected = "@-webkit-keyframes test {\n" +
            "  from {top:0%}\n" +
            "  50% {top:50%}\n" +
            "  to {top:100%}\n" +
            "}\n" +
            "@-moz-keyframes test {\n" +
            "  from {top:0%}\n" +
            "  50% {top:50%}\n" +
            "  to {top:100%}\n" +
            "}\n" +
            "@keyframes test {\n" +
            "  from {top:0%}\n" +
            "  50% {top:50%}\n" +
            "  to {top:100%}\n" +
            "}";

        assertThat(process(original, setup(Prefix.MOZ, Prefix.WEBKIT))).isEqualTo(expected);
    }

    // required, some present (add)
    @Test
    public void atRuleTest2() {
        String original = "@-moz-keyframes test {\n" +
            "  from {top:0%}\n" +
            "  50% {top:50%}\n" +
            "  to {top:100%}\n" +
            "}\n" +
            "@keyframes test {\n" +
            "  from {top:0%}\n" +
            "  50% {top:50%}\n" +
            "  to {top:100%}\n" +
            "}";
        String expected = "@-moz-keyframes test {\n" +
            "  from {top:0%}\n" +
            "  50% {top:50%}\n" +
            "  to {top:100%}\n" +
            "}\n" +
            "@-webkit-keyframes test {\n" +
            "  from {top:0%}\n" +
            "  50% {top:50%}\n" +
            "  to {top:100%}\n" +
            "}\n" +
            "@keyframes test {\n" +
            "  from {top:0%}\n" +
            "  50% {top:50%}\n" +
            "  to {top:100%}\n" +
            "}";

        assertThat(process(original, setup(Prefix.MOZ, Prefix.WEBKIT))).isEqualTo(expected);
    }

    // required, some present, rearrange true (move/add)
    @Test
    public void atRuleTest3() {
        String original = "@keyframes test {\n" +
            "  from {top:0%}\n" +
            "  50% {top:50%}\n" +
            "  to {top:100%}\n" +
            "}\n" +
            "@-moz-keyframes test {\n" +
            "  from {top:0%}\n" +
            "  50% {top:50%}\n" +
            "  to {top:100%}\n" +
            "}";

        String expected = "@-webkit-keyframes test {\n" +
            "  from {top:0%}\n" +
            "  50% {top:50%}\n" +
            "  to {top:100%}\n" +
            "}\n" +
            "@-moz-keyframes test {\n" +
            "  from {top:0%}\n" +
            "  50% {top:50%}\n" +
            "  to {top:100%}\n" +
            "}\n" +
            "@keyframes test {\n" +
            "  from {top:0%}\n" +
            "  50% {top:50%}\n" +
            "  to {top:100%}\n" +
            "}";

        Prefixer prefixer = setup(Prefix.MOZ, Prefix.WEBKIT).rearrange(true);
        assertThat(process(original, prefixer)).isEqualTo(expected);
    }

    // required, some present, rearrange false (leave/add)
    @Test
    public void atRuleTest4() {
        String original = "@keyframes test {\n" +
            "  from {top:0%}\n" +
            "  50% {top:50%}\n" +
            "  to {top:100%}\n" +
            "}\n" +
            "@-moz-keyframes test {\n" +
            "  from {top:0%}\n" +
            "  50% {top:50%}\n" +
            "  to {top:100%}\n" +
            "}";

        String expected = "@-webkit-keyframes test {\n" +
            "  from {top:0%}\n" +
            "  50% {top:50%}\n" +
            "  to {top:100%}\n" +
            "}\n" +
            "@keyframes test {\n" +
            "  from {top:0%}\n" +
            "  50% {top:50%}\n" +
            "  to {top:100%}\n" +
            "}\n" +
            "@-moz-keyframes test {\n" +
            "  from {top:0%}\n" +
            "  50% {top:50%}\n" +
            "  to {top:100%}\n" +
            "}";

        Prefixer prefixer = setup(Prefix.MOZ, Prefix.WEBKIT).rearrange(false);
        assertThat(process(original, prefixer)).isEqualTo(expected);
    }

    // not required, not present (noop)
    @Test
    public void atRuleTest5() {
        String original = "@keyframes test {\n" +
            "  from {top:0%}\n" +
            "  50% {top:50%}\n" +
            "  to {top:100%}\n" +
            "}";
        String expected = "@keyframes test {\n" +
            "  from {top:0%}\n" +
            "  50% {top:50%}\n" +
            "  to {top:100%}\n" +
            "}";
        assertThat(process(original, setup())).isEqualTo(expected);
    }

    // not required, present, remove true (remove)
    @Test
    public void atRuleTest6() {
        String original = "@-webkit-keyframes test {\n" +
            "  from {top:0%}\n" +
            "  50% {top:50%}\n" +
            "  to {top:100%}\n" +
            "}\n" +
            "@keyframes test {\n" +
            "  from {top:0%}\n" +
            "  50% {top:50%}\n" +
            "  to {top:100%}\n" +
            "}\n" +
            "@-moz-keyframes test {\n" +
            "  from {top:0%}\n" +
            "  50% {top:50%}\n" +
            "  to {top:100%}\n" +
            "}";
        String expected = "@keyframes test {\n" +
            "  from {top:0%}\n" +
            "  50% {top:50%}\n" +
            "  to {top:100%}\n" +
            "}";

        Prefixer prefixer = setup().prune(true);
        assertThat(process(original, prefixer)).isEqualTo(expected);
    }

    // not required, present, remove false, rearrange true (move)
    @Test
    public void atRuleTest7() {
        String original = "@-webkit-keyframes test {\n" +
            "  from {top:0%}\n" +
            "  50% {top:50%}\n" +
            "  to {top:100%}\n" +
            "}\n" +
            "@keyframes test {\n" +
            "  from {top:0%}\n" +
            "  50% {top:50%}\n" +
            "  to {top:100%}\n" +
            "}\n" +
            "@-moz-keyframes test {\n" +
            "  from {top:0%}\n" +
            "  50% {top:50%}\n" +
            "  to {top:100%}\n" +
            "}\n" +
            "@-ms-keyframes test {\n" +
            "  from {top:0%}\n" +
            "  50% {top:50%}\n" +
            "  to {top:100%}\n" +
            "}";

        String expected = "@-webkit-keyframes test {\n" +
            "  from {top:0%}\n" +
            "  50% {top:50%}\n" +
            "  to {top:100%}\n" +
            "}\n" +
            "@-moz-keyframes test {\n" +
            "  from {top:0%}\n" +
            "  50% {top:50%}\n" +
            "  to {top:100%}\n" +
            "}\n" +
            "@-ms-keyframes test {\n" +
            "  from {top:0%}\n" +
            "  50% {top:50%}\n" +
            "  to {top:100%}\n" +
            "}\n" +
            "@keyframes test {\n" +
            "  from {top:0%}\n" +
            "  50% {top:50%}\n" +
            "  to {top:100%}\n" +
            "}";

        Prefixer prefixer = setup().prune(false).rearrange(true);
        assertThat(process(original, prefixer)).isEqualTo(expected);
    }

    // not required, present, remove false, rearrange false (noop)
    @Test
    public void atRuleTest8() {
        String original = "@-webkit-keyframes test {\n" +
            "  from {top:0%}\n" +
            "  to {top:100%}\n" +
            "}\n" +
            "@keyframes test {\n" +
            "  from {top:0%}\n" +
            "  to {top:100%}\n" +
            "}\n" +
            "@-moz-keyframes test {\n" +
            "  from {top:0%}\n" +
            "  to {top:100%}\n" +
            "}\n" +
            "@-ms-keyframes test {\n" +
            "  from {top:0%}\n" +
            "  to {top:100%}\n" +
            "}";
        String expected = "@-webkit-keyframes test {\n" +
            "  from {top:0%}\n" +
            "  to {top:100%}\n" +
            "}\n" +
            "@keyframes test {\n" +
            "  from {top:0%}\n" +
            "  to {top:100%}\n" +
            "}\n" +
            "@-moz-keyframes test {\n" +
            "  from {top:0%}\n" +
            "  to {top:100%}\n" +
            "}\n" +
            "@-ms-keyframes test {\n" +
            "  from {top:0%}\n" +
            "  to {top:100%}\n" +
            "}";

        Prefixer prefixer = setup().prune(false).rearrange(false);
        assertThat(process(original, prefixer)).isEqualTo(expected);
    }

    // some required, some present, remove true, rearrange true (move/remove)
    @Test
    public void atRuleTest9() {
        String original = "@-ms-keyframes test {\n" +
            "  from {top:0%}\n" +
            "  to {top:100%}\n" +
            "}\n" +
            "@keyframes test {\n" +
            "  from {top:0%}\n" +
            "  to {top:100%}\n" +
            "}\n" +
            "@-moz-keyframes test {\n" +
            "  from {top:0%}\n" +
            "  to {top:100%}\n" +
            "}\n" +
            "@-o-keyframes test {\n" +
            "  from {top:0%}\n" +
            "  to {top:100%}\n" +
            "}";

        String expected = "@-moz-keyframes test {\n" +
            "  from {top:0%}\n" +
            "  to {top:100%}\n" +
            "}\n" +
            "@keyframes test {\n" +
            "  from {top:0%}\n" +
            "  to {top:100%}\n" +
            "}";

        Prefixer prefixer = setup(Prefix.MOZ).prune(true).rearrange(true);
        assertThat(process(original, prefixer)).isEqualTo(expected);
    }

    // some required, some present, remove true, rearrange true (add/move/remove)
    @Test
    public void atRuleTest10() {
        String original = "@-ms-keyframes test {\n" +
            "  from {top:0%}\n" +
            "  to {top:100%}\n" +
            "}\n" +
            "@keyframes test {\n" +
            "  from {top:0%}\n" +
            "  to {top:100%}\n" +
            "}\n" +
            "@-moz-keyframes test {\n" +
            "  from {top:0%}\n" +
            "  to {top:100%}\n" +
            "}\n" +
            "@-o-keyframes test {\n" +
            "  from {top:0%}\n" +
            "  to {top:100%}\n" +
            "}";

        String expected = "@-webkit-keyframes test {\n" +
            "  from {top:0%}\n" +
            "  to {top:100%}\n" +
            "}\n" +
            "@-moz-keyframes test {\n" +
            "  from {top:0%}\n" +
            "  to {top:100%}\n" +
            "}\n" +
            "@keyframes test {\n" +
            "  from {top:0%}\n" +
            "  to {top:100%}\n" +
            "}";

        Prefixer prefixer = setup(Prefix.MOZ, Prefix.WEBKIT).prune(true).rearrange(true);
        assertThat(process(original, prefixer)).isEqualTo(expected);
    }

    // required, multiple present, rearrange true (move)
    @Test
    public void atRuleTest11() {
        String original = "@keyframes test {\n" +
            "  from {top:0%}\n" +
            "  50% {top:50%}\n" +
            "  to {top:100%}\n" +
            "}\n" +
            "@-moz-keyframes test {\n" +
            "  from {top:0%}\n" +
            "  50% {top:50%}\n" +
            "  to {top:100%}\n" +
            "}\n" +
            "@-moz-keyframes test {\n" +
            "  from {top:0%}\n" +
            "  50% {top:50%}\n" +
            "  to {top:100%}\n" +
            "}\n" +
            ".test {color:red}";

        String expected = "@-moz-keyframes test {\n" +
            "  from {top:0%}\n" +
            "  50% {top:50%}\n" +
            "  to {top:100%}\n" +
            "}\n" +
            "@-moz-keyframes test {\n" +
            "  from {top:0%}\n" +
            "  50% {top:50%}\n" +
            "  to {top:100%}\n" +
            "}\n" +
            "@keyframes test {\n" +
            "  from {top:0%}\n" +
            "  50% {top:50%}\n" +
            "  to {top:100%}\n" +
            "}\n" +
            ".test {color:red}";

        Prefixer prefixer = setup(Prefix.MOZ).rearrange(true);
        assertThat(process(original, prefixer)).isEqualTo(expected);
    }

    // required, multiple present, remove true (remove)
    @Test
    public void atRuleTest12() {
        String original = "@keyframes test {\n" +
            "  from {top:0%}\n" +
            "  50% {top:50%}\n" +
            "  to {top:100%}\n" +
            "}\n" +
            "@-moz-keyframes test {\n" +
            "  from {top:0%}\n" +
            "  50% {top:50%}\n" +
            "  to {top:100%}\n" +
            "}\n" +
            "@-moz-keyframes test {\n" +
            "  from {top:0%}\n" +
            "  50% {top:50%}\n" +
            "  to {top:100%}\n" +
            "}\n" +
            ".test {color:red}";

        String expected = "@keyframes test {\n" +
            "  from {top:0%}\n" +
            "  50% {top:50%}\n" +
            "  to {top:100%}\n" +
            "}\n" +
            ".test {color:red}";

        Prefixer prefixer = setup().prune(true);
        assertThat(process(original, prefixer)).isEqualTo(expected);
    }

    @Test
    public void atRuleWithInnerPrefixable() {
        Prefixer prefixer = Prefixer.customBrowserSupport();
        prefixer.support().all(Browser.IE);
        prefixer.support().all(Browser.CHROME);

        String original = "@keyframes test {\n" +
            "    from { transform: rotate(0deg)}\n" +
            "    to { transform: rotate(360deg)}\n" +
            "}";

        String expected = "@-webkit-keyframes test {\n" +
            "  from {-webkit-transform:rotate(0deg); transform:rotate(0deg)}\n" +
            "  to {-webkit-transform:rotate(360deg); transform:rotate(360deg)}\n" +
            "}\n" +
            "@keyframes test {\n" +
            "  from {-webkit-transform:rotate(0deg); -ms-transform:rotate(0deg); transform:rotate(0deg)}\n" +
            "  to {-webkit-transform:rotate(360deg); -ms-transform:rotate(360deg); transform:rotate(360deg)}\n" +
            "}";

        assertThat(process(original, prefixer)).isEqualTo(expected);
    }

    // from a bug, verify that all prefixes are added for contiguous at-rules
    @Test
    public void atRuleMultiple() {
        String original = "@keyframes test1 {\n" +
            "  from {top: 0%}\n" +
            "  to {top: 100%}\n" +
            "}\n" +
            "@keyframes test2 {\n" +
            "  from {top: 0%}\n" +
            "  to {top: 100%}\n" +
            "}";
        String expected = "@-webkit-keyframes test1 {\n" +
            "  from {top:0%}\n" +
            "  to {top:100%}\n" +
            "}\n" +
            "@-moz-keyframes test1 {\n" +
            "  from {top:0%}\n" +
            "  to {top:100%}\n" +
            "}\n" +
            "@keyframes test1 {\n" +
            "  from {top:0%}\n" +
            "  to {top:100%}\n" +
            "}\n" +
            "@-webkit-keyframes test2 {\n" +
            "  from {top:0%}\n" +
            "  to {top:100%}\n" +
            "}\n" +
            "@-moz-keyframes test2 {\n" +
            "  from {top:0%}\n" +
            "  to {top:100%}\n" +
            "}\n" +
            "@keyframes test2 {\n" +
            "  from {top:0%}\n" +
            "  to {top:100%}\n" +
            "}";

        assertThat(process(original, setup(Prefix.MOZ, Prefix.WEBKIT))).isEqualTo(expected);
    }
}
