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
import com.salesforce.omakase.plugin.prefixer.Prefixer;
import com.salesforce.omakase.writer.StyleWriter;
import org.junit.Test;

import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Functional tests for {@link Prefixer} function name replacements.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class PrefixerUnitFunctionTest {
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

        prefixer.support().browser(Browser.FIREFOX, moz ? 15 : 16); // 15 is last prefixed
        prefixer.support().browser(Browser.CHROME, webkit ? 25 : 26); // 25 is last prefixed
        prefixer.support().browser(Browser.IE, 10);
        prefixer.support().browser(Browser.IOS_SAFARI, 7);
        prefixer.support().browser(Browser.SAFARI, 6.1);

        return prefixer;
    }

    // required, not present (add)
    @Test
    public void functionPropertyTest1() {
        String original = ".test {width:calc(100% - 80px)}";
        String expected = ".test {width:-webkit-calc(100% - 80px); width:-moz-calc(100% - 80px); width:calc(100% - 80px)}";
        assertThat(process(original, setup(Prefix.MOZ, Prefix.WEBKIT))).isEqualTo(expected);
    }

    // required, some present (add)
    @Test
    public void functionPropertyTest2() {
        String original = ".test {width:-webkit-calc(100% - 80px); width:calc(100% - 80px)}";
        String expected = ".test {width:-webkit-calc(100% - 80px); width:-moz-calc(100% - 80px); width:calc(100% - 80px)}";
        assertThat(process(original, setup(Prefix.MOZ, Prefix.WEBKIT))).isEqualTo(expected);
    }

    // required, some present, rearrange true (move/add)
    @Test
    public void functionPropertyTest3() {
        String original = ".test {width:calc(80px); width:-moz-calc(80px); color: red}";
        String expected = ".test {width:-webkit-calc(80px); width:-moz-calc(80px); width:calc(80px); color:red}";

        Prefixer prefixer = setup(Prefix.MOZ, Prefix.WEBKIT).rearrange(true);
        assertThat(process(original, prefixer)).isEqualTo(expected);
    }

    // required, some present, rearrange false (leave/add)
    @Test
    public void functionPropertyTest4() {
        String original = ".test {width:calc(80px); width:-moz-calc(80px); color: red}";
        String expected = ".test {width:-webkit-calc(80px); width:calc(80px); width:-moz-calc(80px); color:red}";

        Prefixer prefixer = setup(Prefix.MOZ, Prefix.WEBKIT).rearrange(false);
        assertThat(process(original, prefixer)).isEqualTo(expected);
    }

    // not required, not present (noop)
    @Test
    public void functionPropertyTest5() {
        String original = ".test {width:-moz-calc(100% - 80px)}";
        String expected = ".test {width:-moz-calc(100% - 80px)}";
        assertThat(process(original, setup())).isEqualTo(expected);
    }

    // not required, present, remove true (remove)
    @Test
    public void functionPropertyTest6() {
        String original = ".test {width:-webkit-calc(80px); width:-moz-calc(80px); width:-ms-calc(80px); width:calc(80px)}";
        String expected = ".test {width:calc(80px)}";

        Prefixer prefixer = setup().prune(true);
        assertThat(process(original, prefixer)).isEqualTo(expected);
    }

    // not required, present, remove false, rearrange true (move)
    @Test
    public void functionPropertyTest7() {
        String original = ".test {width:-webkit-calc(80px); width:calc(80px); width:-moz-calc(80px); width:-ms-calc(80px)}";
        String expected = ".test {width:-webkit-calc(80px); width:-moz-calc(80px); width:-ms-calc(80px); width:calc(80px)}";

        Prefixer prefixer = setup().prune(false).rearrange(true);
        assertThat(process(original, prefixer)).isEqualTo(expected);
    }

    // not required, present, remove false, rearrange false (noop)
    @Test
    public void functionPropertyTest8() {
        String original = ".test {width:-webkit-calc(80px); width:calc(80px); width:-moz-calc(80px); width:-ms-calc(80px)}";
        String expected = ".test {width:-webkit-calc(80px); width:calc(80px); width:-moz-calc(80px); width:-ms-calc(80px)}";

        Prefixer prefixer = setup().prune(false).rearrange(false);
        assertThat(process(original, prefixer)).isEqualTo(expected);
    }

    // some required, some present, remove true, rearrange true (move/remove)
    @Test
    public void functionPropertyTest9() {
        String original = ".test {width:-ms-calc(80px); width:calc(80px); width:-moz-calc(30px); width:-o-calc(3px)}";
        String expected = ".test {width:-moz-calc(30px); width:calc(80px)}";

        Prefixer prefixer = setup(Prefix.MOZ).prune(true).rearrange(true);
        assertThat(process(original, prefixer)).isEqualTo(expected);
    }

    // some required, some present, remove true, rearrange true (add/move/remove)
    @Test
    public void functionPropertyTest10() {
        String original = ".test {width:-ms-calc(80px); width:calc(80px); width:-moz-calc(30px); width:-o-calc(3px)}";
        String expected = ".test {width:-webkit-calc(80px); width:-moz-calc(30px); width:calc(80px)}";

        Prefixer prefixer = setup(Prefix.MOZ, Prefix.WEBKIT).prune(true).rearrange(true);
        assertThat(process(original, prefixer)).isEqualTo(expected);
    }

    // required, multiple present, rearrange true (move)
    @Test
    public void functionPropertyTest11() {
        String original = ".test {width:calc(80px); width:-moz-calc(80px); color: red; width:-moz-calc(80px)}";
        String expected = ".test {width:-moz-calc(80px); width:-moz-calc(80px); width:calc(80px); color:red}";

        Prefixer prefixer = setup(Prefix.MOZ).rearrange(true);
        assertThat(process(original, prefixer)).isEqualTo(expected);
    }

    // required, multiple present, remove true (remove)
    @Test
    public void functionPropertyTest12() {
        String original = ".test {width:calc(80px); width:-moz-calc(80px); color: red; width:-moz-calc(80px)}";
        String expected = ".test {width:calc(80px); color:red}";

        Prefixer prefixer = setup().prune(true);
        assertThat(process(original, prefixer)).isEqualTo(expected);
    }

    // multiple functions in property value (add)
    @Test
    public void functionPropertyTest13() {
        String original = ".test {margin:calc(100%-20px) calc(100%-10px) calc(20px-5%)}";
        String expected = ".test {margin:-moz-calc(100%-20px) -moz-calc(100%-10px) -moz-calc(20px-5%); margin:calc(100%-20px) calc(100%-10px) calc(20px-5%)}";

        Prefixer prefixer = setup(Prefix.MOZ);
        assertThat(process(original, prefixer)).isEqualTo(expected);
    }

    // has mixed terms (function and others) (add)
    @Test
    public void functionPropertyTest14() {
        String original = ".test {margin:calc(20%-10px) 10px}";
        String expected = ".test {margin:-moz-calc(20%-10px) 10px; margin:calc(20%-10px) 10px}";

        Prefixer prefixer = setup(Prefix.MOZ);
        assertThat(process(original, prefixer)).isEqualTo(expected);
    }

    // doesn't rearrange if different properties (add)
    @Test
    public void functionPropertyTest15() {
        String original = ".test {width:calc(1px); height:-moz-calc(1px)}";
        String expected = ".test {width:-moz-calc(1px); width:calc(1px); height:-moz-calc(1px)}";

        Prefixer prefixer = setup(Prefix.MOZ).prune(true).rearrange(true);
        assertThat(process(original, prefixer)).isEqualTo(expected);
    }

    // doesn't remove if different property (noop)
    @Test
    public void functionPropertyTest16() {
        String original = ".test {width:calc(1px); height:-moz-calc(1px)}";
        String expected = ".test {width:calc(1px); height:-moz-calc(1px)}";

        Prefixer prefixer = setup().prune(true).rearrange(true);
        assertThat(process(original, prefixer)).isEqualTo(expected);
    }

    // handles different properties with same function name (move/add/remove)
    @Test
    public void functionPropertyTest17() {
        String original = ".test {height:-moz-calc(2px); width:calc(1px); width:-webkit-calc(2px); height:calc(1px); height:-o-calc(3px)}";
        String expected = ".test {width:-webkit-calc(2px); width:-moz-calc(1px); width:calc(1px); height:-webkit-calc(1px); height:-moz-calc(2px); height:calc(1px)}";

        Prefixer prefixer = setup(Prefix.WEBKIT, Prefix.MOZ).prune(true).rearrange(true);
        assertThat(process(original, prefixer)).isEqualTo(expected);
    }
}
