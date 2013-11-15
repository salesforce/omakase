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

import com.salesforce.omakase.Omakase;
import com.salesforce.omakase.SupportMatrix;
import com.salesforce.omakase.data.Browser;
import com.salesforce.omakase.writer.StyleWriter;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.*;

/**
 * Targeted functional tests for {@link Prefixer}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class PrefixerTargetedTest {
    private String process(String original, Prefixer prefixer) {
        StyleWriter writer = StyleWriter.inline();
        Omakase.source(original).request(new AutoRefiner().all()).request(writer).request(prefixer).process();
        return writer.write();
    }

    private Prefixer borderRadiusSetup() {
        return Prefixer.customBrowserSupport(new SupportMatrix().browser(Browser.FIREFOX, 3.6));
    }

    @Test
    public void borderRadius() {
        String original = ".test {border-radius: 3px}";
        String expected = ".test {-moz-border-radius:3px; border-radius:3px}";
        assertThat(process(original, borderRadiusSetup())).isEqualTo(expected);
    }

    @Test
    public void borderTopRightRadius() {
        String original = ".test {border-top-right-radius: 3px}";
        String expected = ".test {-moz-border-top-right-radius:3px; border-top-right-radius:3px}";
        assertThat(process(original, borderRadiusSetup())).isEqualTo(expected);
    }

    @Test
    public void borderTopLeftRadius() {
        String original = ".test {border-top-left-radius: 3px}";
        String expected = ".test {-moz-border-top-left-radius:3px; border-top-left-radius:3px}";
        assertThat(process(original, borderRadiusSetup())).isEqualTo(expected);
    }

    @Test
    public void borderBottomRightRadius() {
        String original = ".test {border-bottom-right-radius: 3px}";
        String expected = ".test {-moz-border-bottom-right-radius:3px; border-bottom-right-radius:3px}";
        assertThat(process(original, borderRadiusSetup())).isEqualTo(expected);
    }

    @Test
    public void borderBottomLeftRadius() {
        String original = ".test {border-bottom-left-radius: 3px}";
        String expected = ".test {-moz-border-bottom-left-radius:3px; border-bottom-left-radius:3px}";
        assertThat(process(original, borderRadiusSetup())).isEqualTo(expected);
    }

    @Test
    public void calc() {
        String original = ".test {width:calc(100% - 80px)}";
        String expected = ".test {width:-moz-calc(100% - 80px); width:calc(100% - 80px)}";
        Prefixer prefixer = Prefixer.customBrowserSupport(new SupportMatrix().browser(Browser.FIREFOX, 15));
        assertThat(process(original, prefixer)).isEqualTo(expected);
    }

    @Test
    public void boxShadow() {
        String original = ".test {box-shadow:0 8px 6px -6px black}";
        String expected = ".test {-moz-box-shadow:0 8px 6px -6px black; box-shadow:0 8px 6px -6px black}";
        Prefixer prefixer = Prefixer.customBrowserSupport(new SupportMatrix().browser(Browser.FIREFOX, 3.6));
        assertThat(process(original, prefixer)).isEqualTo(expected);
    }

    @Test
    public void boxSizing() {
        String original = ".test {box-sizing:border-box}";
        String expected = ".test {-moz-box-sizing:border-box; box-sizing:border-box}";
        Prefixer prefixer = Prefixer.customBrowserSupport(new SupportMatrix().browser(Browser.FIREFOX, 25));
        assertThat(process(original, prefixer)).isEqualTo(expected);
    }

    private Prefixer transformSetup() {
        return Prefixer.customBrowserSupport(new SupportMatrix().browser(Browser.FIREFOX, 15));
    }

    @Test
    public void transform() {
        String original = ".test {transform:translateX(2em)}";
        String expected = ".test {-moz-transform:translateX(2em); transform:translateX(2em)}";
        assertThat(process(original, transformSetup())).isEqualTo(expected);
    }

    @Test
    public void transformStyle() {
        String original = ".test {transform-style:flat}";
        String expected = ".test {-moz-transform-style:flat; transform-style:flat}";
        assertThat(process(original, transformSetup())).isEqualTo(expected);
    }

    @Test
    public void transformOrigin() {
        String original = ".test {transform-origin:100% 100%;}";
        String expected = ".test {-moz-transform-origin:100% 100%; transform-origin:100% 100%}";
        assertThat(process(original, transformSetup())).isEqualTo(expected);
    }

    @Test
    public void perspective() {
        String original = ".test {perspective:none}";
        String expected = ".test {-moz-perspective:none; perspective:none}";
        assertThat(process(original, transformSetup())).isEqualTo(expected);
    }

    @Test
    public void perspectiveOrigin() {
        String original = ".test {perspective-origin:left}";
        String expected = ".test {-moz-perspective-origin:left; perspective-origin:left}";
        assertThat(process(original, transformSetup())).isEqualTo(expected);
    }

    @Test
    public void backfaceVisibility() {
        String original = ".test {backface-visibility:visible}";
        String expected = ".test {-moz-backface-visibility:visible; backface-visibility:visible}";
        assertThat(process(original, transformSetup())).isEqualTo(expected);
    }
}
