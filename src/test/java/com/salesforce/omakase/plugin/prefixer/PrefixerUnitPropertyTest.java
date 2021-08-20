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

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.salesforce.omakase.Omakase;
import com.salesforce.omakase.data.Browser;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.plugin.core.AutoRefine;
import com.salesforce.omakase.writer.StyleWriter;

/**
 * Functional tests for {@link Prefixer} property replacements.
 *
 * @author nmcwilliams
 */
public class PrefixerUnitPropertyTest {
    private String process(String original, Prefixer prefixer) {
        StyleWriter writer = StyleWriter.inline();
        Omakase.source(original).use(AutoRefine.everything()).use(writer).use(prefixer).process();
        return writer.write();
    }

    private Prefixer setup(Prefix... prefixes) {
        List<Prefix> list = Lists.newArrayList(prefixes);
        boolean moz = Iterables.contains(list, Prefix.MOZ);
        boolean webkit = Iterables.contains(list, Prefix.WEBKIT);

        Prefixer prefixer = Prefixer.customBrowserSupport();

        prefixer.support().browser(Browser.FIREFOX, moz ? 3.6 : 4); // 3.6 is last prefixed
        prefixer.support().browser(Browser.SAFARI, webkit ? 4 : 5); // 4 is last prefixed
        prefixer.support().browser(Browser.IE, 10);
        prefixer.support().browser(Browser.CHROME, 25);
        prefixer.support().browser(Browser.IOS_SAFARI, 6);

        return prefixer;
    }

    // required, not present (add)
    @Test
    public void prefixPropertyTest1() {
        String original = ".test { border-radius: 2px }";
        String expected = ".test {-webkit-border-radius:2px; -moz-border-radius:2px; border-radius:2px}";
        assertThat(process(original, setup(Prefix.MOZ, Prefix.WEBKIT))).isEqualTo(expected);
    }

    // required, some present (add)
    @Test
    public void prefixPropertyTest2() {
        String original = ".test {-webkit-border-radius:2px 5px; border-radius: 2px }";
        String expected = ".test {-webkit-border-radius:2px 5px; -moz-border-radius:2px; border-radius:2px}";
        assertThat(process(original, setup(Prefix.MOZ, Prefix.WEBKIT))).isEqualTo(expected);
    }

    // required, some present, rearrange true (move/add)
    @Test
    public void prefixPropertyTest3() {
        String original = ".test { border-radius:2px; -moz-border-radius:3px; color:red }";
        String expected = ".test {-webkit-border-radius:2px; -moz-border-radius:3px; border-radius:2px; color:red}";

        Prefixer prefixer = setup(Prefix.MOZ, Prefix.WEBKIT).rearrange(true);
        assertThat(process(original, prefixer)).isEqualTo(expected);
    }

    // required, some present, rearrange false (leave/add)
    @Test
    public void prefixPropertyTest4() {
        String original = ".test { border-radius:2px; -moz-border-radius:3px; color:red }";
        String expected = ".test {-webkit-border-radius:2px; border-radius:2px; -moz-border-radius:3px; color:red}";

        Prefixer prefixer = setup(Prefix.MOZ, Prefix.WEBKIT).rearrange(false);
        assertThat(process(original, prefixer)).isEqualTo(expected);
    }

    // not required, not present (noop)
    @Test
    public void prefixPropertyTest5() {
        String original = ".test {border-radius:2px}";
        String expected = ".test {border-radius:2px}";
        assertThat(process(original, setup())).isEqualTo(expected);
    }

    // not required, present, remove true (remove)
    @Test
    public void prefixPropertyTest6() {
        String original = ".test {-webkit-border-radius:3px; -moz-border-radius:1px; -o-border-radius:2; border-radius:2px}";
        String expected = ".test {border-radius:2px}";

        Prefixer prefixer = setup().prune(true);
        assertThat(process(original, prefixer)).isEqualTo(expected);
    }

    // not required, present, remove false, rearrange true (move)
    @Test
    public void prefixPropertyTest7() {
        String original = ".test {-webkit-border-radius:3px; border-radius:2px; -moz-border-radius:1px; -o-border-radius:2px}";
        String expected = ".test {-webkit-border-radius:3px; -moz-border-radius:1px; -o-border-radius:2px; border-radius:2px}";

        Prefixer prefixer = setup().prune(false).rearrange(true);
        assertThat(process(original, prefixer)).isEqualTo(expected);
    }

    // not required, present, remove false, rearrange false (noop)
    @Test
    public void prefixPropertyTest8() {
        String original = ".test {-webkit-border-radius:3px; border-radius:2px; -moz-border-radius:1px; -o-border-radius:2px}";
        String expected = ".test {-webkit-border-radius:3px; border-radius:2px; -moz-border-radius:1px; -o-border-radius:2px}";

        Prefixer prefixer = setup().prune(false).rearrange(false);
        assertThat(process(original, prefixer)).isEqualTo(expected);
    }

    // some required, some present, remove true, rearrange true (move/remove)
    @Test
    public void prefixPropertyTest9() {
        String original = ".test {-ms-border-radius: 1px; border-radius:2px; -moz-border-radius:1px; -o-border-radius:2px}";
        String expected = ".test {-moz-border-radius:1px; border-radius:2px}";

        Prefixer prefixer = setup(Prefix.MOZ).prune(true).rearrange(true);
        assertThat(process(original, prefixer)).isEqualTo(expected);
    }

    // some required, some present, remove true, rearrange true (add/move/remove)
    @Test
    public void prefixPropertyTest10() {
        String original = ".test {-ms-border-radius:1px; border-radius:2px; -moz-border-radius:1px; -o-border-radius:2px}";
        String expected = ".test {-webkit-border-radius:2px; -moz-border-radius:1px; border-radius:2px}";

        Prefixer prefixer = setup(Prefix.MOZ, Prefix.WEBKIT).prune(true).rearrange(true);
        assertThat(process(original, prefixer)).isEqualTo(expected);
    }

    // required, multiple present, rearrange true (move)
    @Test
    public void prefixPropertyTest11() {
        String original = ".test {border-radius:2px; -moz-border-radius:3px; -moz-border-radius: 2px; color:red}";
        String expected = ".test {-moz-border-radius:3px; -moz-border-radius:2px; border-radius:2px; color:red}";

        Prefixer prefixer = setup(Prefix.MOZ).rearrange(true);
        assertThat(process(original, prefixer)).isEqualTo(expected);
    }

    // required, multiple present, remove true (remove)
    @Test
    public void prefixPropertyTest12() {
        String original = ".test {border-radius:2px; -moz-border-radius:3px; -moz-border-radius: 2px; color:red}";
        String expected = ".test {border-radius:2px; color:red}";

        Prefixer prefixer = setup().prune(true);
        assertThat(process(original, prefixer)).isEqualTo(expected);
    }
}
