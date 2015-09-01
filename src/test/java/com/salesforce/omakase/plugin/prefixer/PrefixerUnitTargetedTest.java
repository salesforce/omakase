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

package com.salesforce.omakase.plugin.prefixer;

import com.salesforce.omakase.Omakase;
import com.salesforce.omakase.SupportMatrix;
import com.salesforce.omakase.data.Browser;
import com.salesforce.omakase.plugin.basic.AutoRefiner;
import com.salesforce.omakase.writer.StyleWriter;
import com.salesforce.omakase.writer.WriterMode;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Targeted functional tests for {@link Prefixer}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class PrefixerUnitTargetedTest {
    private String process(String original, Prefixer prefixer) {
        return process(original, prefixer, WriterMode.INLINE);
    }

    private String process(String original, Prefixer prefixer, WriterMode mode) {
        StyleWriter writer = new StyleWriter(mode);
        Omakase.source(original)
            .use(AutoRefiner.refineEverything())
            .use(prefixer)
            .use(PrefixCleaner.mismatchedPrefixedUnits())
            .use(writer)
            .process();
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
        String expected = ".test {-moz-transition:width 1s,-moz-border-radius 2px,-moz-transform 1s; transition:width 1s," +
            "border-radius 2px,transform 1s}";
        assertThat(process(original, transitionSetup())).isEqualTo(expected);
    }

    @Test
    public void transitionWithPrefixablePropsLeavesExisting() {
        String original = ".test {-moz-transition:-moz-transform 1s; transition:transform 1s}";
        String expected = ".test {-moz-transition:-moz-transform 1s; transition:transform 1s}";
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

    //@Test known issue, to fix start with PrefixerHandlers#TRANSITION#equivalents
    public void transitionNotPrefixedButValueIsLeavesExisting() {
        String original = ".test {transition:width 1s,-webkit-transform 1s; transition:width 1s,transform 1s}";
        String expected = ".test {transition:width 1s,-webkit-transform 1s; transition:width 1s,transform 1s}";
        Prefixer prefixer = Prefixer.customBrowserSupport();
        prefixer.support().browser(Browser.CHROME, 30); // chrome 30 has transform prefixed but not transition
        assertThat(process(original, prefixer)).isEqualTo(expected);
    }

    //@Test known issue (maybe not an issue?), see HandleTransition for more info
    public void transitionPrefixedInOneBrowserNotInOtherButValueIs() {
        String original = ".test {transition:transform 1s}";
        String expected = ".test {" +
            "-webkit-transition:-webkit-transform 1s; " +
            "-moz-transition:-moz-transform 1s; " +
            "transition:-webkit-transform 1s; " +
            "transition:transform 1s}";
        Prefixer prefixer = Prefixer.customBrowserSupport();
        prefixer.support().browser(Browser.CHROME, 30); // chrome 30 has transform prefixed but not transition
        prefixer.support().browser(Browser.IOS_SAFARI, 6.1); // ios safari 6 has transition and transform prefixed
        prefixer.support().browser(Browser.FIREFOX, 15);// for transition and transform
        assertThat(process(original, prefixer)).isEqualTo(expected);
    }

    @Test
    public void transitionProperty() {
        String original = ".test {transition-property:width,border-radius,transform}";
        String expected = ".test {-moz-transition-property:width,-moz-border-radius,-moz-transform; transition-property:width," +
            "border-radius,transform}";
        assertThat(process(original, transitionSetup())).isEqualTo(expected);
    }

    @Test
    public void transitionPropertyWithPrefixableProps() {
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
        String expected = ".test {-moz-animation:anim 3s linear 1s infinite alternate; animation:anim 3s linear 1s infinite " +
            "alternate}";
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
    public void keyframes() {
        String original = "@keyframes test {\n" +
            "  from {top:30px}\n" +
            "  to {top:100px}\n" +
            "}\n";

        String expected = "@-moz-keyframes test {\n" +
            "  from {top:30px}\n" +
            "  to {top:100px}\n" +
            "}\n" +
            "@keyframes test {\n" +
            "  from {top:30px}\n" +
            "  to {top:100px}\n" +
            "}";

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

    private Prefixer borderImageSetup() {
        Prefixer prefixer = Prefixer.customBrowserSupport();
        prefixer.support().browser(Browser.FIREFOX, 14);
        return prefixer;
    }

    @Test
    public void borderImage() {
        String original = ".test {border-image:url(i.png) 30% repeat}";
        String expected = ".test {-moz-border-image:url(i.png) 30% repeat; border-image:url(i.png) 30% repeat}";
        assertThat(process(original, borderImageSetup())).isEqualTo(expected);
    }

    @Test
    public void borderImageSource() {
        String original = ".test {border-image-source:url(i.png)}";
        String expected = ".test {-moz-border-image-source:url(i.png); border-image-source:url(i.png)}";
        assertThat(process(original, borderImageSetup())).isEqualTo(expected);
    }

    @Test
    public void borderImageWidth() {
        String original = ".test {border-image-width: 3em 2em}";
        String expected = ".test {-moz-border-image-width:3em 2em; border-image-width:3em 2em}";
        assertThat(process(original, borderImageSetup())).isEqualTo(expected);
    }

    @Test
    public void borderImageSlice() {
        String original = ".test {border-image-slice: 20%}";
        String expected = ".test {-moz-border-image-slice:20%; border-image-slice:20%}";
        assertThat(process(original, borderImageSetup())).isEqualTo(expected);
    }

    @Test
    public void borderImageRepeat() {
        String original = ".test {border-image-repeat: stretch}";
        String expected = ".test {-moz-border-image-repeat:stretch; border-image-repeat:stretch}";
        assertThat(process(original, borderImageSetup())).isEqualTo(expected);
    }

    @Test
    public void borderImageOutset() {
        String original = ".test {border-image-outset:30%}";
        String expected = ".test {-moz-border-image-outset:30%; border-image-outset:30%}";
        assertThat(process(original, borderImageSetup())).isEqualTo(expected);
    }

    private Prefixer bgdImageSetup() {
        Prefixer prefixer = Prefixer.customBrowserSupport();
        prefixer.support().browser(Browser.FIREFOX, 3.6);
        return prefixer;
    }

    @Test
    public void backgroundClip() {
        String original = ".test {background-clip:border-box}";
        String expected = ".test {-moz-background-clip:border-box; background-clip:border-box}";
        assertThat(process(original, bgdImageSetup())).isEqualTo(expected);
    }

    @Test
    public void backgroundOrigin() {
        String original = ".test {background-origin:border-box}";
        String expected = ".test {-moz-background-origin:border-box; background-origin:border-box}";
        assertThat(process(original, bgdImageSetup())).isEqualTo(expected);
    }

    @Test
    public void backgroundSize() {
        String original = ".test {background-size:2em}";
        String expected = ".test {-moz-background-size:2em; background-size:2em}";
        assertThat(process(original, bgdImageSetup())).isEqualTo(expected);
    }

    @Test
    public void userSelectNone() {
        String original = ".test {user-select:none}";
        String expected = ".test {-moz-user-select:none; user-select:none}";
        Prefixer prefixer = Prefixer.customBrowserSupport(new SupportMatrix().browser(Browser.FIREFOX, 25));
        assertThat(process(original, prefixer)).isEqualTo(expected);
    }

    public Prefixer linearGradientSetup() {
        Prefixer prefixer = Prefixer.customBrowserSupport();
        prefixer.support().browser(Browser.FIREFOX, 15);
        prefixer.support().browser(Browser.CHROME, 25);
        prefixer.support().browser(Browser.SAFARI, 6);
        prefixer.support().browser(Browser.OPERA, 12);
        return prefixer;
    }

    @Test
    public void linearGradient() {
        String original = ".test {" +
            "background: red;" +
            "background: linear-gradient(yellow, red);" +
            "}";

        String expected = ".test {\n" +
            "  background: red;\n" +
            "  background: -webkit-linear-gradient(yellow, red);\n" +
            "  background: -moz-linear-gradient(yellow, red);\n" +
            "  background: -o-linear-gradient(yellow, red);\n" +
            "  background: linear-gradient(yellow, red);\n" +
            "}";

        assertThat(process(original, linearGradientSetup(), WriterMode.VERBOSE)).isEqualTo(expected);
    }

    @Test
    public void linearGradientMultiple() {
        String original = ".test {" +
            "background: linear-gradient(red, green) no-repeat, linear-gradient(blue, yellow) no-repeat;" +
            "}";

        String expected = ".test {\n" +
            "  background: -webkit-linear-gradient(red, green) no-repeat, -webkit-linear-gradient(blue, yellow) no-repeat;\n" +
            "  background: -moz-linear-gradient(red, green) no-repeat, -moz-linear-gradient(blue, yellow) no-repeat;\n" +
            "  background: -o-linear-gradient(red, green) no-repeat, -o-linear-gradient(blue, yellow) no-repeat;\n" +
            "  background: linear-gradient(red, green) no-repeat, linear-gradient(blue, yellow) no-repeat;\n" +
            "}";

        assertThat(process(original, linearGradientSetup(), WriterMode.VERBOSE)).isEqualTo(expected);
    }

    @Test
    public void linearGradientToTop() {
        String original = ".test {" +
            "background: linear-gradient(to top, yellow, red);" +
            "}";

        String expected = ".test {\n" +
            "  background: -webkit-linear-gradient(bottom, yellow, red);\n" +
            "  background: -moz-linear-gradient(bottom, yellow, red);\n" +
            "  background: -o-linear-gradient(bottom, yellow, red);\n" +
            "  background: linear-gradient(to top, yellow, red);\n" +
            "}";

        assertThat(process(original, linearGradientSetup(), WriterMode.VERBOSE)).isEqualTo(expected);
    }

    @Test
    public void linearGradientToBottom() {
        String original = ".test {" +
            "background: linear-gradient(to bottom, yellow, red);" +
            "}";

        String expected = ".test {\n" +
            "  background: -webkit-linear-gradient(top, yellow, red);\n" +
            "  background: -moz-linear-gradient(top, yellow, red);\n" +
            "  background: -o-linear-gradient(top, yellow, red);\n" +
            "  background: linear-gradient(to bottom, yellow, red);\n" +
            "}";

        assertThat(process(original, linearGradientSetup(), WriterMode.VERBOSE)).isEqualTo(expected);
    }

    @Test
    public void linearGradientToRight() {
        String original = ".test {" +
            "background: linear-gradient(to right, yellow, red);" +
            "}";

        String expected = ".test {\n" +
            "  background: -webkit-linear-gradient(left, yellow, red);\n" +
            "  background: -moz-linear-gradient(left, yellow, red);\n" +
            "  background: -o-linear-gradient(left, yellow, red);\n" +
            "  background: linear-gradient(to right, yellow, red);\n" +
            "}";

        assertThat(process(original, linearGradientSetup(), WriterMode.VERBOSE)).isEqualTo(expected);
    }

    @Test
    public void linearGradientToLeft() {
        String original = ".test {" +
            "background: linear-gradient(to left, yellow, red);" +
            "}";

        String expected = ".test {\n" +
            "  background: -webkit-linear-gradient(right, yellow, red);\n" +
            "  background: -moz-linear-gradient(right, yellow, red);\n" +
            "  background: -o-linear-gradient(right, yellow, red);\n" +
            "  background: linear-gradient(to left, yellow, red);\n" +
            "}";

        assertThat(process(original, linearGradientSetup(), WriterMode.VERBOSE)).isEqualTo(expected);
    }

    @Test
    public void linearGradientToTopRight() {
        String original = ".test {" +
            "background: linear-gradient(to top right, yellow, red);" +
            "}";

        String expected = ".test {\n" +
            "  background: -webkit-linear-gradient(bottom left, yellow, red);\n" +
            "  background: -moz-linear-gradient(bottom left, yellow, red);\n" +
            "  background: -o-linear-gradient(bottom left, yellow, red);\n" +
            "  background: linear-gradient(to top right, yellow, red);\n" +
            "}";

        assertThat(process(original, linearGradientSetup(), WriterMode.VERBOSE)).isEqualTo(expected);
    }

    @Test
    public void linearGradientToTopLeft() {
        String original = ".test {" +
            "background: linear-gradient(to top left, yellow, red);" +
            "}";

        String expected = ".test {\n" +
            "  background: -webkit-linear-gradient(bottom right, yellow, red);\n" +
            "  background: -moz-linear-gradient(bottom right, yellow, red);\n" +
            "  background: -o-linear-gradient(bottom right, yellow, red);\n" +
            "  background: linear-gradient(to top left, yellow, red);\n" +
            "}";

        assertThat(process(original, linearGradientSetup(), WriterMode.VERBOSE)).isEqualTo(expected);
    }

    @Test
    public void linearGradientToBottomRight() {
        String original = ".test {" +
            "background: linear-gradient(to bottom right, yellow, red);" +
            "}";

        String expected = ".test {\n" +
            "  background: -webkit-linear-gradient(top left, yellow, red);\n" +
            "  background: -moz-linear-gradient(top left, yellow, red);\n" +
            "  background: -o-linear-gradient(top left, yellow, red);\n" +
            "  background: linear-gradient(to bottom right, yellow, red);\n" +
            "}";

        assertThat(process(original, linearGradientSetup(), WriterMode.VERBOSE)).isEqualTo(expected);
    }

    @Test
    public void linearGradientToBottomLeft() {
        String original = ".test {" +
            "background: linear-gradient(to bottom left, yellow, red);" +
            "}";

        String expected = ".test {\n" +
            "  background: -webkit-linear-gradient(top right, yellow, red);\n" +
            "  background: -moz-linear-gradient(top right, yellow, red);\n" +
            "  background: -o-linear-gradient(top right, yellow, red);\n" +
            "  background: linear-gradient(to bottom left, yellow, red);\n" +
            "}";

        assertThat(process(original, linearGradientSetup(), WriterMode.VERBOSE)).isEqualTo(expected);
    }

    @Test
    public void linearGradientWithAngle() {
        String original = ".test {" +
            "background: linear-gradient(60deg, yellow, red);" +
            "}";

        String expected = ".test {\n" +
            "  background: -webkit-linear-gradient(30deg, yellow, red);\n" +
            "  background: -moz-linear-gradient(30deg, yellow, red);\n" +
            "  background: -o-linear-gradient(30deg, yellow, red);\n" +
            "  background: linear-gradient(60deg, yellow, red);\n" +
            "}";

        assertThat(process(original, linearGradientSetup(), WriterMode.VERBOSE)).isEqualTo(expected);
    }

    @Test
    public void linearGradientWithNegativeAngle() {
        String original = ".test {" +
            "background: linear-gradient(-90deg, yellow, red);" +
            "}";

        String expected = ".test {\n" +
            "  background: -webkit-linear-gradient(180deg, yellow, red);\n" +
            "  background: -moz-linear-gradient(180deg, yellow, red);\n" +
            "  background: -o-linear-gradient(180deg, yellow, red);\n" +
            "  background: linear-gradient(-90deg, yellow, red);\n" +
            "}";

        assertThat(process(original, linearGradientSetup(), WriterMode.VERBOSE)).isEqualTo(expected);
    }

    @Test
    public void repeatingLinearGradient() {
        String original = ".test {" +
            "background: repeating-linear-gradient(green, yellow 5%, red 15%);" +
            "}";

        String expected = ".test {\n" +
            "  background: -webkit-repeating-linear-gradient(green, yellow 5%, red 15%);\n" +
            "  background: -moz-repeating-linear-gradient(green, yellow 5%, red 15%);\n" +
            "  background: -o-repeating-linear-gradient(green, yellow 5%, red 15%);\n" +
            "  background: repeating-linear-gradient(green, yellow 5%, red 15%);\n" +
            "}";

        assertThat(process(original, linearGradientSetup(), WriterMode.VERBOSE)).isEqualTo(expected);
    }

    @Test
    public void repeatingLinearGradientWithDirection() {
        String original = ".test {" +
            "background: repeating-linear-gradient(to right, green, yellow 5%, red 15%);" +
            "}";

        String expected = ".test {\n" +
            "  background: -webkit-repeating-linear-gradient(left, green, yellow 5%, red 15%);\n" +
            "  background: -moz-repeating-linear-gradient(left, green, yellow 5%, red 15%);\n" +
            "  background: -o-repeating-linear-gradient(left, green, yellow 5%, red 15%);\n" +
            "  background: repeating-linear-gradient(to right, green, yellow 5%, red 15%);\n" +
            "}";

        assertThat(process(original, linearGradientSetup(), WriterMode.VERBOSE)).isEqualTo(expected);
    }

    @Test
    public void repeatingLinearGradientWithAngle() {
        String original = ".test {" +
            "background: repeating-linear-gradient(75deg, green, yellow 5%, red 15%);" +
            "}";

        String expected = ".test {\n" +
            "  background: -webkit-repeating-linear-gradient(15deg, green, yellow 5%, red 15%);\n" +
            "  background: -moz-repeating-linear-gradient(15deg, green, yellow 5%, red 15%);\n" +
            "  background: -o-repeating-linear-gradient(15deg, green, yellow 5%, red 15%);\n" +
            "  background: repeating-linear-gradient(75deg, green, yellow 5%, red 15%);\n" +
            "}";

        assertThat(process(original, linearGradientSetup(), WriterMode.VERBOSE)).isEqualTo(expected);
    }

    @Test
    public void selection() {
        String original = "::selection {color:red}";
        String expected = "::-moz-selection {color:red}\n::selection {color:red}";
        Prefixer prefixer = Prefixer.customBrowserSupport(new SupportMatrix().browser(Browser.FIREFOX, 25));
        assertThat(process(original, prefixer)).isEqualTo(expected);
    }

    @Test
    public void selectionWithOthers() {
        String original = "p::selection {color:red}";
        String expected = "p::-moz-selection {color:red}\np::selection {color:red}";
        Prefixer prefixer = Prefixer.customBrowserSupport(new SupportMatrix().browser(Browser.FIREFOX, 25));
        assertThat(process(original, prefixer)).isEqualTo(expected);
    }

    private Prefixer columnsSetup() {
        return Prefixer.customBrowserSupport(new SupportMatrix().browser(Browser.FIREFOX, 25));
    }

    @Test
    public void columns() {
        String original = ".test {columns: 3}";
        String expected = ".test {-moz-columns:3; columns:3}";
        assertThat(process(original, columnsSetup())).isEqualTo(expected);
    }

    @Test
    public void columnWidth() {
        String original = ".test {column-width:6px}";
        String expected = ".test {-moz-column-width:6px; column-width:6px}";
        assertThat(process(original, columnsSetup())).isEqualTo(expected);
    }

    @Test
    public void columnCount() {
        String original = ".test {column-count:2}";
        String expected = ".test {-moz-column-count:2; column-count:2}";
        assertThat(process(original, columnsSetup())).isEqualTo(expected);
    }

    @Test
    public void columnGap() {
        String original = ".test {column-gap:15px}";
        String expected = ".test {-moz-column-gap:15px; column-gap:15px}";
        assertThat(process(original, columnsSetup())).isEqualTo(expected);
    }

    @Test
    public void columnRule() {
        String original = ".test {column-rule:solid}";
        String expected = ".test {-moz-column-rule:solid; column-rule:solid}";
        assertThat(process(original, columnsSetup())).isEqualTo(expected);
    }

    @Test
    public void columnRuleWidth() {
        String original = ".test {column-rule-width:5px}";
        String expected = ".test {-moz-column-rule-width:5px; column-rule-width:5px}";
        assertThat(process(original, columnsSetup())).isEqualTo(expected);
    }

    @Test
    public void columnRuleStyle() {
        String original = ".test {column-rule-style:groove}";
        String expected = ".test {-moz-column-rule-style:groove; column-rule-style:groove}";
        assertThat(process(original, columnsSetup())).isEqualTo(expected);
    }

    @Test
    public void columnRuleColor() {
        String original = ".test {column-rule-color:red}";
        String expected = ".test {-moz-column-rule-color:red; column-rule-color:red}";
        assertThat(process(original, columnsSetup())).isEqualTo(expected);
    }

    @Test
    public void columnSpan() {
        String original = ".test {column-span:all}";
        String expected = ".test {-moz-column-span:all; column-span:all}";
        assertThat(process(original, columnsSetup())).isEqualTo(expected);
    }

    @Test
    public void columnFill() {
        String original = ".test {column-fill:balance}";
        String expected = ".test {-moz-column-fill:balance; column-fill:balance}";
        assertThat(process(original, columnsSetup())).isEqualTo(expected);
    }

    private Prefixer placeholderSetup() {
        Prefixer prefixer = Prefixer.customBrowserSupport();
        prefixer.support().all(Browser.FIREFOX);
        prefixer.support().all(Browser.CHROME);
        prefixer.support().all(Browser.IE);
        return prefixer;
    }

    @Test
    public void placeholder() {
        String original = "input::placeholder {color:red}";

        String expected = "input::-webkit-input-placeholder {color:red}\n" +
            "input:-moz-placeholder {color:red}\n" +
            "input::-moz-placeholder {color:red}\n" +
            "input:-ms-input-placeholder {color:red}\n" +
            "input::placeholder {color:red}";

        assertThat(process(original, placeholderSetup())).isEqualTo(expected);
    }

    @Test
    public void placeholderExistingPresentDontRemove() {
        String original = "input::-webkit-input-placeholder {color:red}\n" +
            "input:-moz-placeholder {color:red}\n" +
            "input::-moz-placeholder {color:red}\n" +
            "input::placeholder {color:red}\n" +
            "input:-ms-input-placeholder {color:red}";

        String expected = "input::-webkit-input-placeholder {color:red}\n" +
            "input:-moz-placeholder {color:red}\n" +
            "input::-moz-placeholder {color:red}\n" +
            "input::placeholder {color:red}\n" +
            "input:-ms-input-placeholder {color:red}";

        Prefixer prefixer = placeholderSetup();
        prefixer.prune(false);
        prefixer.rearrange(false);

        assertThat(process(original, prefixer)).isEqualTo(expected);
    }

    @Test
    public void placeholderExistingPresentRearrangeAndAdd() {
        String original = "input::-moz-placeholder {color:red}\n" +
            "input::placeholder {color:red}\n" +
            "input:-ms-input-placeholder {color:red}";

        String expected = "input::-webkit-input-placeholder {color:red}\n" +
            "input::-moz-placeholder {color:red}\n" +
            "input:-ms-input-placeholder {color:red}\n" +
            "input::placeholder {color:red}";

        Prefixer prefixer = placeholderSetup();
        prefixer.prune(false);
        prefixer.rearrange(true);

        assertThat(process(original, prefixer)).isEqualTo(expected);
    }

    @Test
    public void appearance() {
        String original = ".test {appearance:none}";
        String expected = ".test {-webkit-appearance:none; -moz-appearance:none; appearance:none}";

        Prefixer prefixer = Prefixer.customBrowserSupport();
        prefixer.support().latest(Browser.FIREFOX);
        prefixer.support().latest(Browser.CHROME);
        assertThat(process(original, prefixer)).isEqualTo(expected);
    }

    public Prefixer flexSetup() {
        Prefixer prefixer = Prefixer.customBrowserSupport();
        prefixer.support().all(Browser.FIREFOX);
        prefixer.support().all(Browser.CHROME);
        prefixer.support().all(Browser.SAFARI);
        prefixer.support().all(Browser.IOS_SAFARI);
        prefixer.support().all(Browser.IE);
        return prefixer;
    }

    public Prefixer flexSetupAlt() {
        Prefixer prefixer = Prefixer.customBrowserSupport();
        prefixer.support().browser(Browser.FIREFOX, 32); // no
        prefixer.support().browser(Browser.CHROME, 43); // no
        prefixer.support().browser(Browser.SAFARI, 7.1); // yes, final
        prefixer.support().browser(Browser.IOS_SAFARI, 6.1); // yes, old
        prefixer.support().browser(Browser.IE, 10); // yes
        return prefixer;
    }

    public Prefixer flexSetupNone() {
        Prefixer prefixer = Prefixer.customBrowserSupport();
        prefixer.support().browser(Browser.FIREFOX, 32);
        prefixer.support().browser(Browser.CHROME, 43);
        return prefixer;
    }

    public Prefixer flex2009MozSetup() {
        Prefixer prefixer = Prefixer.customBrowserSupport();
        prefixer.support().browser(Browser.FIREFOX, 21);
        prefixer.support().browser(Browser.CHROME, 29);
        prefixer.support().browser(Browser.IE, 11);
        return prefixer;
    }

    public Prefixer flexFinalWebkitSetup() {
        Prefixer prefixer = Prefixer.customBrowserSupport();
        prefixer.support().browser(Browser.FIREFOX, 32);
        prefixer.support().browser(Browser.CHROME, 30);
        prefixer.support().browser(Browser.SAFARI, 7.1); // needs -webkit
        prefixer.support().browser(Browser.IE, 11);
        return prefixer;
    }

    public Prefixer flexFinalMsSetup() {
        Prefixer prefixer = Prefixer.customBrowserSupport();
        prefixer.support().browser(Browser.FIREFOX, 32);
        prefixer.support().browser(Browser.CHROME, 30);
        prefixer.support().browser(Browser.IE, 10);
        return prefixer;
    }

    @Test
    public void displayFlex() {
        String original = ".test {display:flex}";
        String expected = ".test {display:-webkit-box; display:-webkit-flex; display:-moz-box; display:-ms-flexbox; display:flex}";

        assertThat(process(original, flexSetup())).isEqualTo(expected);
    }

    @Test
    public void displayFlexAlt() {
        String original = ".test {display:flex}";
        String expected = ".test {display:-webkit-box; display:-webkit-flex; display:-ms-flexbox; display:flex}";

        assertThat(process(original, flexSetupAlt())).isEqualTo(expected);
    }

    @Test
    public void displayFlex2009Moz() {
        String original = ".test {display:flex}";
        String expected = ".test {display:-moz-box; display:flex}";

        assertThat(process(original, flex2009MozSetup())).isEqualTo(expected);
    }

    @Test
    public void displayFlexFinalWebkit() {
        String original = ".test {display:flex}";
        String expected = ".test {display:-webkit-flex; display:flex}";

        assertThat(process(original, flexFinalWebkitSetup())).isEqualTo(expected);
    }

    @Test
    public void displayFlexFinalMs() {
        String original = ".test {display:flex}";
        String expected = ".test {display:-ms-flexbox; display:flex}";

        assertThat(process(original, flexFinalMsSetup())).isEqualTo(expected);
    }

    @Test
    public void displayFlexPresentLeaveAsIs() {
        String original = ".test {display:-webkit-box; display:-webkit-flex; display:-moz-box; display:-ms-flexbox; display:flex}";
        String expected = ".test {display:-webkit-box; display:-webkit-flex; display:-moz-box; display:-ms-flexbox; display:flex}";

        Prefixer prefixer = flexSetup();
        prefixer.prune(false);
        prefixer.rearrange(false);
        assertThat(process(original, prefixer)).isEqualTo(expected);
    }

    @Test
    public void displayFlexPresentRemove() {
        String original = ".test {display:-webkit-box; display:-moz-box; display:-ms-flexbox; display:flex; display:-webkit-flex}";
        String expected = ".test {display:flex}";

        Prefixer prefixer = flexSetupNone();
        prefixer.prune(true);
        prefixer.rearrange(false);
        assertThat(process(original, prefixer)).isEqualTo(expected);
    }

    @Test
    public void displayFlexPresentRearrange() {
        String original = ".test {display:-webkit-flex; display:-webkit-box; display:flex; display:-ms-flexbox; display:-moz-box}";
        String expected = ".test {display:-webkit-box; display:-webkit-flex; display:-moz-box; display:-ms-flexbox; display:flex}";

        Prefixer prefixer = flexSetup();
        prefixer.prune(false);
        prefixer.rearrange(true);
        assertThat(process(original, prefixer)).isEqualTo(expected);
    }

    // known issue: see notes in HandleFlexValue
    //@Test
    public void displayFlexPresentRearrangeAddRemove() {
        // add -webkit-box, remove -moz-box, rearrange rest
        String original = ".test {display:-webkit-flex; display:flex; display:-ms-flexbox; display:-moz-box}";
        String expected = ".test {display:-webkit-flex; display:-webkit-box; display:-ms-flexbox; display:flex}";

        Prefixer prefixer = flexSetupAlt();
        prefixer.prune(true);
        prefixer.rearrange(true);
        assertThat(process(original, prefixer)).isEqualTo(expected);
    }

    @Test
    public void displayFlexWithInlineFlex() {
        // not really valid use case, but it should still handle situations where unprefixed versions of inline-flex exist in
        // the same as display:flex
        String original = ".test {display:-webkit-inline-flex; display:flex; display:moz-inline-box}";
        String expected = ".test {display:-webkit-inline-flex; display:-webkit-box; display:-webkit-flex; display:-ms-flexbox; " +
            "display:flex; display:moz-inline-box}";

        assertThat(process(original, flexSetupAlt())).isEqualTo(expected);
    }

    @Test
    public void displayInlineFlex() {
        String original = ".test {display:inline-flex}";
        String expected = ".test {display:-webkit-inline-box; display:-webkit-inline-flex; display:-moz-inline-box; " +
            "display:-ms-inline-flexbox; display:inline-flex}";

        assertThat(process(original, flexSetup())).isEqualTo(expected);
    }

    @Test
    public void displayInlineFlexFinalWebkit() {
        String original = ".test {display:inline-flex}";
        String expected = ".test {display:-webkit-inline-flex; display:inline-flex}";

        assertThat(process(original, flexFinalWebkitSetup())).isEqualTo(expected);
    }

    @Test
    public void displayInlineFlexPresentRemove() {
        String original = ".test {display:-webkit-inline-box; display:-moz-inline-box; display:-ms-inline-flexbox; " +
            "display:inline-flex; display:-webkit-inline-flex}";
        String expected = ".test {display:inline-flex}";

        Prefixer prefixer = flexSetupNone();
        prefixer.prune(true);
        prefixer.rearrange(false);
        assertThat(process(original, prefixer)).isEqualTo(expected);
    }

    @Test
    public void displayInlineFlexPresentRearrange() {
        String original = ".test {display:-webkit-inline-flex; display:-webkit-inline-box; display:inline-flex; " +
            "display:-ms-inline-flexbox; display:-moz-inline-box}";
        String expected = ".test {display:-webkit-inline-box; display:-webkit-inline-flex; display:-moz-inline-box; " +
            "display:-ms-inline-flexbox; display:inline-flex}";

        Prefixer prefixer = flexSetup();
        prefixer.prune(false);
        prefixer.rearrange(true);
        assertThat(process(original, prefixer)).isEqualTo(expected);
    }

    @Test
    public void displayFlexOrder() {
        String original = ".test {order:0}";
        String expected = ".test {-webkit-box-ordinal-group:1; -webkit-order:0; -moz-box-ordinal-group:1; -ms-flex-order:0; order:0}";

        assertThat(process(original, flexSetup())).isEqualTo(expected);
    }

    @Test
    public void displayFlexOrderAlt() {
        String original = ".test {order:0}";
        String expected = ".test {-webkit-box-ordinal-group:1; -webkit-order:0; -ms-flex-order:0; order:0}";

        assertThat(process(original, flexSetupAlt())).isEqualTo(expected);
    }

    @Test
    public void displayFlexOrder2009Moz() {
        String original = ".test {order:-2}";
        String expected = ".test {-moz-box-ordinal-group:-1; order:-2}";

        assertThat(process(original, flex2009MozSetup())).isEqualTo(expected);
    }

    @Test
    public void testNegativeToPositiveFlexOrder() {
        String original = ".test {order:-1}";
        String expected = ".test {-moz-box-ordinal-group:0; order:-1}";

        assertThat(process(original, flex2009MozSetup())).isEqualTo(expected);
    }

    @Test
    public void displayFlexOrderFinalWebkit() {
        String original = ".test {order:2}";
        String expected = ".test {-webkit-order:2; order:2}";

        assertThat(process(original, flexFinalWebkitSetup())).isEqualTo(expected);
    }

    @Test
    public void displayFlexOrderFinalMs() {
        String original = ".test {order:-1}";
        String expected = ".test {-ms-flex-order:-1; order:-1}";

        assertThat(process(original, flexFinalMsSetup())).isEqualTo(expected);
    }

    @Test
    public void displayFlexOrderPresentLeaveAsIs() {
        String original = ".test {-webkit-box-ordinal-group:1; -webkit-order:0; -moz-box-ordinal-group:1; -ms-flex-order:0; order:0}";
        String expected = ".test {-webkit-box-ordinal-group:1; -webkit-order:0; -moz-box-ordinal-group:1; -ms-flex-order:0; order:0}";

        Prefixer prefixer = flexSetup();
        prefixer.prune(false);
        prefixer.rearrange(false);
        assertThat(process(original, prefixer)).isEqualTo(expected);
    }

    @Test
    public void displayFlexOrderPresentRemove() {
        String original = ".test {-webkit-box-ordinal-group:1; -webkit-order:0; -moz-box-ordinal-group:1; -ms-flex-order:0; order:0}";
        String expected = ".test {order:0}";

        Prefixer prefixer = flexSetupNone();
        prefixer.prune(true);
        prefixer.rearrange(false);
        assertThat(process(original, prefixer)).isEqualTo(expected);
    }

    @Test
    public void displayFlexOrderPresentRearrange() {
        String original = ".test {-webkit-order:0; -webkit-box-ordinal-group:1; order:0; -ms-flex-order:0; " +
            "-moz-box-ordinal-group:1; margin:0}";
        String expected = ".test {-webkit-box-ordinal-group:1; -webkit-order:0; -moz-box-ordinal-group:1; " +
            "-ms-flex-order:0; order:0; margin:0}";

        Prefixer prefixer = flexSetup();
        prefixer.prune(false);
        prefixer.rearrange(true);
        assertThat(process(original, prefixer)).isEqualTo(expected);
    }

    @Test
    public void flexWrap() {
        String original = ".test {flex-wrap:wrap}";
        String expected = ".test {-webkit-flex-wrap:wrap; -ms-flex-wrap:wrap; flex-wrap:wrap}";

        assertThat(process(original, flexSetup())).isEqualTo(expected);
    }

    @Test
    public void flexWrapRemoval() {
        String original = ".test {-webkit-flex-wrap:wrap; -ms-flex-wrap:wrap; flex-wrap:wrap; -moz-flex-wrap: wrap}";
        String expected = ".test {flex-wrap:wrap}";

        Prefixer prefixer = flexSetupNone();
        prefixer.prune(true);
        prefixer.rearrange(false);
        assertThat(process(original, prefixer)).isEqualTo(expected);
    }

    @Test
    public void flexShrink() {
        String original = ".test {flex-shrink:0}";
        String expected = ".test {-webkit-flex-shrink:0; -ms-flex-negative:0; flex-shrink:0}";

        assertThat(process(original, flexSetup())).isEqualTo(expected);
    }

    @Test
    public void flexShrinkRemoval() {
        String original = ".test {-webkit-flex-shrink:-1; -ms-flex-shrink:-1; flex-shrink:-1; -moz-flex-shrink: -1}";
        String expected = ".test {flex-shrink:-1}";

        Prefixer prefixer = flexSetupNone();
        prefixer.prune(true);
        prefixer.rearrange(false);
        assertThat(process(original, prefixer)).isEqualTo(expected);
    }
}
