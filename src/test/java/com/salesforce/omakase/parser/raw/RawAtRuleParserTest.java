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

package com.salesforce.omakase.parser.raw;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.parser.AbstractParserTest;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.test.util.TemplatesHelper.SourceWithExpectedResult;
import org.junit.Test;

import java.util.List;

import static com.salesforce.omakase.test.util.TemplatesHelper.withExpectedResult;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link RawAtRuleParser}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class RawAtRuleParserTest extends AbstractParserTest<RawAtRuleParser> {
    @Override
    public List<String> invalidSources() {
        return ImmutableList.of(
            "",
            "  ",
            "\n",
            "media",
            "keyframes",
            "!@media",
            "\n 1234",
            "\"@media\"");
    }

    @Override
    public List<String> validSources() {
        return ImmutableList
            .of(
                "@charset \"UTF-8\";",
                "  \n @charset \"utf-8;\";",
                "/* comment */ @charset 'iso-8859-15';",
                "@import url(\"fineprint.css\") print;",
                "@import url(\"bluish.css\") projection, tv;",
                "@import url(\"chrome://communicator/skin/\");",
                "@import url('landscape.css') screen and (orientation:landscape);",
                "@import \"common.css\" screen, projection;",
                "@page :first {\n  margin: 2in 3in;\n}",
                "@page :right {\nmargin-left: 4cm;\nmargin-right: 3cm;\n}",
                "@page { margin: 2cm }",
                "@page :blank {\n  @top-center { content: none }\n}",
                "@page :right {\n  @top-center { content: \"Preliminary edition\" }\n  @bottom-center { content: counter(page) " +
                    "}\n}",
                "@page :first {\n  color: green;\n\n  @top-left {\n    content: \"foo\";\n    color: blue;\n  }\n  @top-right " +
                    "{\n    content: \"bar\";\n  }\n}",
                "@keyframes diagonal-slide {\n\n  from {\n    left: 0;\n    top: 0;\n  }\n\n  to {\n    left: 100px;\n    top: " +
                    "100px;\n  }\n\n}",
                "@keyframes wobble {\n  0% {\n    left: 100px;\n  }\n\n  40% {\n    left: 150px;\n  }\n\n  60% {\n    left: " +
                    "75px;\n  }\n\n  100% {\n    left: 100px;\n  }\n}",
                "@keyframes bounce {\n\n  from {\n    top: 100px;\n    animation-timing-function: ease-out;\n  }\n\n  25% {\n  " +
                    "  top: 50px;\n    animation-timing-function: ease-in;\n  }\n\n  50% {\n    top: 100px;\n    " +
                    "animation-timing-function: ease-out;\n  }\n\n  75% {\n    top: 75px;\n    animation-timing-function: " +
                    "ease-in;\n  }\n\n  to {\n    top: 100px;\n  }\n\n}",
                "@-webkit-keyframes myfirst /* Safari and Chrome */\n{\nfrom {background: red;}\nto {background: yellow;}\n}",
                " /* Safari and Chrome */ @-webkit-keyframes myfirst\n{\nfrom {background: red;}\nto {background: yellow;}\n}",
                "@supports (display: flex) {\n div { display: flex; }\n}",
                "@supports ( display: flexbox ) {\n  body, #navigation, #content { display: flexbox; }\n  #navigation { " +
                    "background: blue; color: white; }\n  #article { background: white; color: black; }\n}",
                "@supports not ( display: flexbox ) {\n  body { width: 100%; height: 100%; background: white; color: black; }\n" +
                    "  #navigation { width: 25%; }\n  #article { width: 75%; }\n}",
                "@supports ( box-shadow: 2px 2px 2px black ) or\n          ( -moz-box-shadow: 2px 2px 2px black ) or\n         " +
                    " ( -webkit-box-shadow: 2px 2px 2px black ) or\n          ( -o-box-shadow: 2px 2px 2px black ) {\n  " +
                    ".outline {\n    color: white;\n    -moz-box-shadow: 2px 2px 2px black;\n    -webkit-box-shadow: 2px 2px " +
                    "2px black;\n    -o-box-shadow: 2px 2px 2px black;\n    box-shadow: 2px 2px 2px black; /* unprefixed last " +
                    "*/\n  }\n}",
                "@supports (transition-property: color) or\n          ((animation-name: foo) and\n           (transform: rotate" +
                    "(10deg))) {\n  body { color: red}\n}",
                "@namespace \"http://www.w3.org/1999/xhtml\";",
                "@namespace svg \"http://www.w3.org/2000/svg\";",
                "@font-face {\n  font-family: Headline;\n  src: local(Futura-Medium),\n       url(fonts.svg#MyGeometricModern) " +
                    "format(\"svg\");\n}",
                "@font-face {\n  font-family: jpgothic;\n  src: local(HiraKakuPro-W3), local(Meiryo), local(IPAPGothic);\n}",
                "@font-face {\n  font-family: BBCBengali;\n  src: url(fonts/BBCBengali.ttf) format(\"opentype\");\n  " +
                    "unicode-range: U+00-FF, U+980-9FF;\n}",
                "@font-face {\n  font-family: 'MyFontFamily';\n  src: url('myfont-webfont.eot?#iefix') format" +
                    "('embedded-opentype'), \n        url('myfont-webfont.woff') format('woff'), " +
                    "\n       url('myfont-webfont.ttf')  format('truetype'),\n        url('myfont-webfont.svg#svgFontName') " +
                    "format('svg');\n }",
                "@font-face {\n  font-family: 'Graublau Web';\n  src: url('GraublauWeb.eot?') format('eot'), " +
                    "url('GraublauWeb.woff') format('woff'), url('GraublauWeb.ttf') format('truetype');\n}",
                "@media (min-width: 700px) { ... }",
                "@media (min-width: 700px) and (orientation: landscape) { ... }",
                "@media tv and (min-width: 700px) and (orientation: landscape) { ... }",
                "@media (min-width: 700px), handheld and (orientation: landscape) { ... }",
                "@media not all and (monochrome) { ... }",
                "@media screen and (max-width: 600px) {\n  .class {\n    background: #ccc;\n  }\n}",
                "@media only screen and (max-width:632px) { ... }",
                "@if(IE7) { .class { color: red;}}"
            );
    }

    @Override
    public List<SourceWithExpectedResult<Integer>> validSourcesWithExpectedEndIndex() {
        return ImmutableList
            .of(
                withExpectedResult("@charset \"UTF-8\"; \n .class {...}", 17),
                withExpectedResult("@import url('landscape.css') screen and (orientation:landscape); \n .class", 64),
                withExpectedResult("@page :first {\n  margin: 2in 3in;\n} \n .class", 35),
                withExpectedResult(
                    "@page :first {\n  color: green;\n\n  @top-left {\n    content: \"foo\";\n    color: blue;\n  }\n  " +
                        "@top-right {\n    content: \"bar\";\n  }\n} \n .class",
                    127),
                withExpectedResult(
                    "@keyframes diagonal-slide {\n\n  from {\n    left: 0;\n    top: 0;\n  }\n\n  to {\n    left: 100px;\n    " +
                        "top: 100px;\n  }\n\n} \n .class",
                    114),
                withExpectedResult(
                    "@keyframes bounce {\n\n  from {\n    top: 100px;\n    animation-timing-function: ease-out;\n  }\n\n  25% " +
                        "{\n    top: 50px;\n    animation-timing-function: ease-in;\n  }\n\n  50% {\n    top: 100px;\n    " +
                        "animation-timing-function: ease-out;\n  }\n\n  75% {\n    top: 75px;\n    animation-timing-function: " +
                        "ease-in;\n  }\n\n  to {\n    top: 100px;\n  }\n\n} \n .class",
                    327),
                withExpectedResult(
                    "@-webkit-keyframes myfirst /* Safari and Chrome */\n{\nfrom {background: red;}\nto {background: yellow;" +
                        "}\n}.class",
                    103),
                withExpectedResult(
                    "@supports (transition-property: color) or\n          ((animation-name: foo) and\n           (transform: " +
                        "rotate(10deg))) {\n  body { color: red}\n}#id",
                    142),
                withExpectedResult(
                    "@namespace svg \"http://www.w3.org/2000/svg\";@import",
                    44),
                withExpectedResult(
                    "@font-face {\n  font-family: Headline;\n  src: local(Futura-Medium)," +
                        "\n       url(fonts.svg#MyGeometricModern) format(\"svg\");\n}@font-face",
                    123),
                withExpectedResult("@media tv and (min-width: 700px) and (orientation: landscape) { ... }\n@media", 69)
            );
    }

    @Override
    public String validSourceForPositionTesting() {
        return Iterables.get(validSources(), 0);
    }

    @Override
    public boolean allowedToTrimLeadingWhitespace() {
        return true;
    }

    @Test
    @Override
    public void matchesExpectedBroadcastContent() {
        // expression value
        List<ParseResult<String>> results = parseWithExpected(
            withExpectedResult(
                "@charset \"UTF-8\"; \n .class {...}",
                "\"UTF-8\""),
            withExpectedResult(
                "@import url('landscape.css') screen and (orientation:landscape); \n .class",
                "url('landscape.css') screen and (orientation:landscape)"),
            withExpectedResult(
                "@page :first {\n  margin: 2in 3in;\n} \n .class",
                ":first"),
            withExpectedResult(
                "@page :first {\n  color: green;\n\n  @top-left {\n    content: \"foo\";\n    color: blue;\n  }\n  @top-right " +
                    "{\n    content: \"bar\";\n  }\n} \n .class",
                ":first"),
            withExpectedResult(
                "@keyframes diagonal-slide {\n\n  from {\n    left: 0;\n    top: 0;\n  }\n\n  to {\n    left: 100px;\n    top: " +
                    "100px;\n  }\n\n} \n .class",
                "diagonal-slide"),
            withExpectedResult(
                "@keyframes bounce {\n\n  from {\n    top: 100px;\n    animation-timing-function: ease-out;\n  }\n\n  25% {\n  " +
                    "  top: 50px;\n    animation-timing-function: ease-in;\n  }\n\n  50% {\n    top: 100px;\n    " +
                    "animation-timing-function: ease-out;\n  }\n\n  75% {\n    top: 75px;\n    animation-timing-function: " +
                    "ease-in;\n  }\n\n  to {\n    top: 100px;\n  }\n\n} \n .class",
                "bounce"),
            withExpectedResult(
                "@-webkit-keyframes myfirst /* Safari and Chrome */\n{\nfrom {background: red;}\nto {background: yellow;}\n}" +
                    ".class",
                "myfirst /* Safari and Chrome */"),
            withExpectedResult(
                "@supports (transition-property: color) or\n          ((animation-name: foo) and\n           (transform: rotate" +
                    "(10deg))) {\n  body { color: red}\n}#id",
                "(transition-property: color) or\n          ((animation-name: foo) and\n           (transform: rotate(10deg)))"),
            withExpectedResult(
                "@namespace svg \"http://www.w3.org/2000/svg\";@import",
                "svg \"http://www.w3.org/2000/svg\""),
            withExpectedResult(
                "@font-face {\n  font-family: Headline;\n  src: local(Futura-Medium),\n       url(fonts.svg#MyGeometricModern) " +
                    "format(\"svg\");\n}@font-face",
                ""),
            withExpectedResult(
                "@font-face {\n    font-family: \"My Font\";\n    src: url(\"data:font/opentype;base64," +
                    "[base-encoded font here];{}\\\"\");\n}",
                ""),
            withExpectedResult(
                "@media tv and (min-width: 700px) and (orientation: landscape) { ... }\n@media",
                "tv and (min-width: 700px) and (orientation: landscape)"));

        for (ParseResult<String> result : results) {
            AtRule r = result.broadcaster.findOnly(AtRule.class).get();
            if (result.expected == null || result.expected.isEmpty()) {
                assertThat(r.rawExpression().isPresent()).describedAs(result.source.toString()).isFalse();
            } else {
                assertThat(r.rawExpression().get().content()).describedAs(result.source.toString()).isEqualTo(result.expected);
            }
        }
    }

    @Test
    public void expectedName() {
        List<ParseResult<String>> results = parseWithExpected(
            withExpectedResult(
                "@charset \"UTF-8\"; \n .class {...}",
                "charset"),
            withExpectedResult(
                "@import url('landscape.css') screen and (orientation:landscape); \n .class",
                "import"),
            withExpectedResult(
                "@page :first {\n  margin: 2in 3in;\n} \n .class",
                "page"),
            withExpectedResult(
                "@keyframes diagonal-slide {\n\n  from {\n    left: 0;\n    top: 0;\n  }\n\n  to {\n    left: 100px;\n    top: " +
                    "100px;\n  }\n\n} \n .class",
                "keyframes"),
            withExpectedResult(
                "@-webkit-keyframes myfirst /* Safari and Chrome */\n{\nfrom {background: red;}\nto {background: yellow;}\n}" +
                    ".class",
                "-webkit-keyframes"),
            withExpectedResult(
                "@supports (transition-property: color) or\n          ((animation-name: foo) and\n           (transform: rotate" +
                    "(10deg))) {\n  body { color: red}\n}#id",
                "supports"),
            withExpectedResult(
                "@namespace svg \"http://www.w3.org/2000/svg\";@import",
                "namespace"),
            withExpectedResult(
                "@font-face {\n  font-family: Headline;\n  src: local(Futura-Medium),\n       url(fonts.svg#MyGeometricModern) " +
                    "format(\"svg\");\n}@font-face",
                "font-face"),
            withExpectedResult(
                "@media tv and (min-width: 700px) and (orientation: landscape) { ... }\n@media",
                "media"),
            withExpectedResult(
                "@if(IE7) { ... }",
                "if"));

        for (ParseResult<String> result : results) {
            AtRule r = result.broadcaster.findOnly(AtRule.class).get();
            assertThat(r.name()).describedAs(result.source.toString()).isEqualTo(result.expected);
        }
    }

    @Test
    public void expectedBlockValue() {
        List<ParseResult<String>> results = parseWithExpected(
            withExpectedResult(
                "@charset \"UTF-8\"; \n .class {...}",
                ""),
            withExpectedResult(
                "@import url('landscape.css') screen and (orientation:landscape); \n .class",
                ""),
            withExpectedResult(
                "@page :first {\n  margin: 2in 3in;\n} \n .class",
                "margin: 2in 3in;"),
            withExpectedResult(
                "@page :first {\n  color: green;\n\n  @top-left {\n    content: \"foo\";\n    color: blue;\n  }\n  @top-right " +
                    "{\n    content: \"bar\";\n  }\n} \n .class",
                "color: green;\n\n  @top-left {\n    content: \"foo\";\n    color: blue;\n  }\n  @top-right {\n    content: " +
                    "\"bar\";\n  }"),
            withExpectedResult(
                "@keyframes diagonal-slide {\n\n  from {\n    left: 0;\n    top: 0;\n  }\n\n  to {\n    left: 100px;\n    top: " +
                    "100px;\n  }\n\n} \n .class",
                "from {\n    left: 0;\n    top: 0;\n  }\n\n  to {\n    left: 100px;\n    top: 100px;\n  }"),
            withExpectedResult(
                "@keyframes bounce {\n\n  from {\n    top: 100px;\n    animation-timing-function: ease-out;\n  }\n\n  25% {\n  " +
                    "  top: 50px;\n    animation-timing-function: ease-in;\n  }\n\n  50% {\n    top: 100px;\n    " +
                    "animation-timing-function: ease-out;\n  }\n\n  75% {\n    top: 75px;\n    animation-timing-function: " +
                    "ease-in;\n  }\n\n  to {\n    top: 100px;\n  }\n\n} \n .class",
                "from {\n    top: 100px;\n    animation-timing-function: ease-out;\n  }\n\n  25% {\n    top: 50px;\n    " +
                    "animation-timing-function: ease-in;\n  }\n\n  50% {\n    top: 100px;\n    animation-timing-function: " +
                    "ease-out;\n  }\n\n  75% {\n    top: 75px;\n    animation-timing-function: ease-in;\n  }\n\n  to {\n    " +
                    "top: 100px;\n  }"),
            withExpectedResult(
                "@-webkit-keyframes myfirst /* Safari and Chrome */\n{\nfrom {background: red;}\nto {background: yellow;}\n}" +
                    ".class",
                "from {background: red;}\nto {background: yellow;}"),
            withExpectedResult(
                "@supports (transition-property: color) or\n          ((animation-name: foo) and\n           (transform: rotate" +
                    "(10deg))) {\n  body { color: red}\n}#id",
                "body { color: red}"),
            withExpectedResult(
                "@namespace svg \"http://www.w3.org/2000/svg\";@import",
                ""),
            withExpectedResult(
                "@font-face {\n  font-family: Headline;\n  src: local(Futura-Medium),\n       url(fonts.svg#MyGeometricModern) " +
                    "format(\"svg\");\n}@font-face",
                "font-family: Headline;\n  src: local(Futura-Medium),\n       url(fonts.svg#MyGeometricModern) format(\"svg\");"),
            withExpectedResult(
                "@font-face {\n    font-family: \"My Font\";\n    src: url(\"data:font/opentype;base64," +
                    "[base-encoded font here];{}\\\"\");}",
                "font-family: \"My Font\";\n    src: url(\"data:font/opentype;base64,[base-encoded font here];{}\\\"\");"),
            withExpectedResult(
                "@media tv and (min-width: 700px) and (orientation: landscape) { ... }\n@media",
                "..."));

        for (ParseResult<String> result : results) {
            AtRule r = result.broadcaster.findOnly(AtRule.class).get();
            if (result.expected == null || result.expected.isEmpty()) {
                assertThat(r.rawBlock().isPresent()).describedAs(result.source.toString()).isFalse();
            } else {
                assertThat(r.rawBlock().get().content()).describedAs(result.source.toString()).isEqualTo(result.expected);
            }
        }
    }

    @Test
    public void noNameAfterAtSymbol() {
        exception.expect(ParserException.class);
        exception.expectMessage(Message.MISSING_AT_RULE_NAME.message());
        parse("@");
    }

    @Test
    public void spaceAfterSymbol() {
        exception.expect(ParserException.class);
        exception.expectMessage(Message.MISSING_AT_RULE_NAME.message());
        parse("@ media");
    }

    @Test
    public void missingExpressionAndBlock() {
        exception.expect(ParserException.class);
        exception.expectMessage(Message.MISSING_AT_RULE_VALUE.message());
        parse("@media ");
    }

    @Test
    public void attachesCommentsBeforeAtRule() {
        GenericParseResult result = parse("/*comment*/@charset \"utf-8\";").get(0);
        AtRule r = result.broadcaster.findOnly(AtRule.class).get();
        assertThat(r.comments()).hasSize(1);
    }
}
