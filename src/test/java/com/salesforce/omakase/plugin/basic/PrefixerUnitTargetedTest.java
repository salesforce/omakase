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
public class PrefixerUnitTargetedTest {
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

    private Prefixer transitionSetup() {
        Prefixer prefixer = Prefixer.customBrowserSupport();
        prefixer.support().browser(Browser.FIREFOX, 15);// for transition and transform
        prefixer.support().browser(Browser.FIREFOX, 3.6); // for border-radius
        return prefixer;
    }

    @Test
    public void transition() {
        String original = ".test {transition:width 1s,height 1s}";
        String expected = ".test {-moz-transition:width 1s,height 1s; transition:width 1s,height 1s}";
        assertThat(process(original, transitionSetup())).isEqualTo(expected);
    }

    @Test
    public void transitionWithPrefixableProps() {
        String original = ".test {transition:width 1s,border-radius 2px,transform 1s}";
        String expected = ".test {-moz-transition:width 1s,-moz-border-radius 2px,-moz-transform 1s; transition:width 1s,border-radius 2px,transform 1s}";
        assertThat(process(original, transitionSetup())).isEqualTo(expected);
    }

    @Test
    public void transitionNotPrefixedButValueIs() {
        String original = ".test {transition:width 1s,transform 1s}";
        String expected = ".test {transition:width 1s,-webkit-transform 1s; transition:width 1s,transform 1s}";
        Prefixer prefixer = Prefixer.customBrowserSupport();
        prefixer.support().browser(Browser.CHROME, 30); // chrome 30 has transform prefixed but not transition
        assertThat(process(original, prefixer)).isEqualTo(expected);
    }

    @Test
    public void transitionProperty() {
        String original = ".test {transition-property:width,border-radius,transform}";
        String expected = ".test {-moz-transition-property:width,-moz-border-radius,-moz-transform; transition-property:width,border-radius,transform}";
        assertThat(process(original, transitionSetup())).isEqualTo(expected);
    }

    @Test
    public void transitionPropertyWithPrefixibleProps() {
        String original = ".test {transition-property:width,transform}";
        String expected = ".test {transition-property:width,-webkit-transform; transition-property:width,transform}";
        Prefixer prefixer = Prefixer.customBrowserSupport();
        prefixer.support().browser(Browser.CHROME, 30); // chrome 30 has transform prefixed but not transition
        assertThat(process(original, prefixer)).isEqualTo(expected);
    }

    @Test
    public void transitionDuration() {
        String original = ".test {transition-duration:2s}";
        String expected = ".test {-moz-transition-duration:2s; transition-duration:2s}";
        assertThat(process(original, transitionSetup())).isEqualTo(expected);
    }

    @Test
    public void transitionDelay() {
        String original = ".test {transition-delay:2s}";
        String expected = ".test {-moz-transition-delay:2s; transition-delay:2s}";
        assertThat(process(original, transitionSetup())).isEqualTo(expected);
    }

    @Test
    public void transitionTimingFunction() {
        String original = ".test {transition-timing-function:ease}";
        String expected = ".test {-moz-transition-timing-function:ease; transition-timing-function:ease}";
        assertThat(process(original, transitionSetup())).isEqualTo(expected);
    }

    private Prefixer animationSetup() {
        Prefixer prefixer = Prefixer.customBrowserSupport();
        prefixer.support().browser(Browser.FIREFOX, 15);
        return prefixer;
    }

    @Test
    public void animation() {
        String original = ".test {animation:anim 3s linear 1s infinite alternate}";
        String expected = ".test {-moz-animation:anim 3s linear 1s infinite alternate; animation:anim 3s linear 1s infinite alternate}";
        assertThat(process(original, animationSetup())).isEqualTo(expected);
    }

    @Test
    public void animationDelay() {
        String original = ".test {animation-delay:3s}";
        String expected = ".test {-moz-animation-delay:3s; animation-delay:3s}";
        assertThat(process(original, animationSetup())).isEqualTo(expected);
    }

    @Test
    public void animationDirection() {
        String original = ".test {animation-direction:reverse}";
        String expected = ".test {-moz-animation-direction:reverse; animation-direction:reverse}";
        assertThat(process(original, animationSetup())).isEqualTo(expected);
    }

    @Test
    public void animationDuration() {
        String original = ".test {animation-duration:3s}";
        String expected = ".test {-moz-animation-duration:3s; animation-duration:3s}";
        assertThat(process(original, animationSetup())).isEqualTo(expected);
    }

    @Test
    public void animationFillMode() {
        String original = ".test {animation-fill-mode:none}";
        String expected = ".test {-moz-animation-fill-mode:none; animation-fill-mode:none}";
        assertThat(process(original, animationSetup())).isEqualTo(expected);
    }

    @Test
    public void animationIterationCount() {
        String original = ".test {animation-iteration-count:2}";
        String expected = ".test {-moz-animation-iteration-count:2; animation-iteration-count:2}";
        assertThat(process(original, animationSetup())).isEqualTo(expected);
    }

    @Test
    public void animationName() {
        String original = ".test {animation-name:test}";
        String expected = ".test {-moz-animation-name:test; animation-name:test}";
        assertThat(process(original, animationSetup())).isEqualTo(expected);
    }

    @Test
    public void animationPlayState() {
        String original = ".test {animation-play-state:running}";
        String expected = ".test {-moz-animation-play-state:running; animation-play-state:running}";
        assertThat(process(original, animationSetup())).isEqualTo(expected);
    }

    @Test
    public void animationTimingFunction() {
        String original = ".test {animation-timing-function:ease}";
        String expected = ".test {-moz-animation-timing-function:ease; animation-timing-function:ease}";
        assertThat(process(original, animationSetup())).isEqualTo(expected);
    }

    @Test
    public void tabSize() {
        String original = "pre {tab-size: 4}";
        String expected = "pre {-moz-tab-size:4; tab-size:4}";
        Prefixer prefixer = Prefixer.customBrowserSupport(new SupportMatrix().browser(Browser.FIREFOX, 25));
        assertThat(process(original, prefixer)).isEqualTo(expected);
    }
    @Test
    public void hyphens() {
        String original = ".test {hyphens:auto}";
        String expected = ".test {-moz-hyphens:auto; hyphens:auto}";
        Prefixer prefixer = Prefixer.customBrowserSupport(new SupportMatrix().browser(Browser.FIREFOX, 25));
        assertThat(process(original, prefixer)).isEqualTo(expected);
    }
}
