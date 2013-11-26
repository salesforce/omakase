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

package com.salesforce.omakase.plugin.basic;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.salesforce.omakase.Omakase;
import com.salesforce.omakase.data.Browser;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.writer.StyleWriter;
import org.junit.Test;

import java.util.List;

import static org.fest.assertions.api.Assertions.*;

/**
 * Functional tests for {@link Prefixer} at-rule replacements.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class PrefixerUnitAtRuleTest {
    private String process(String original, Prefixer prefixer) {
        StyleWriter writer = StyleWriter.inline();
        Omakase.source(original).request(new AutoRefiner().all()).request(writer).request(prefixer).process();
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
}
