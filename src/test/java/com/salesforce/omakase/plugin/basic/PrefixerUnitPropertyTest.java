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

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Functional tests for {@link Prefixer} property replacements.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class PrefixerUnitPropertyTest {
    private String process(String original, Prefixer prefixer) {
        StyleWriter writer = StyleWriter.inline();
        Omakase.source(original).use(AutoRefiner.refineEverything()).use(writer).use(prefixer).process();
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
