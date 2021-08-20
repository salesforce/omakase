/*
 * Copyright (c) 2017, salesforce.com, inc.
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

package com.salesforce.omakase.test.functional;

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.salesforce.omakase.Omakase;
import com.salesforce.omakase.ast.RawFunction;
import com.salesforce.omakase.ast.Rule;
import com.salesforce.omakase.ast.Stylesheet;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.atrule.GenericAtRuleExpression;
import com.salesforce.omakase.ast.atrule.MediaQueryList;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.declaration.GenericFunctionValue;
import com.salesforce.omakase.ast.declaration.HexColorValue;
import com.salesforce.omakase.ast.declaration.KeywordValue;
import com.salesforce.omakase.ast.declaration.NumericalValue;
import com.salesforce.omakase.ast.declaration.PropertyValue;
import com.salesforce.omakase.ast.declaration.UrlFunctionValue;
import com.salesforce.omakase.ast.selector.ClassSelector;
import com.salesforce.omakase.ast.selector.IdSelector;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.ast.selector.TypeSelector;
import com.salesforce.omakase.broadcast.Broadcastable;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.annotation.Observe;
import com.salesforce.omakase.broadcast.annotation.Refine;
import com.salesforce.omakase.broadcast.annotation.Rework;
import com.salesforce.omakase.broadcast.annotation.Validate;
import com.salesforce.omakase.data.Keyword;
import com.salesforce.omakase.data.Property;
import com.salesforce.omakase.error.ErrorManager;
import com.salesforce.omakase.parser.Grammar;
import com.salesforce.omakase.plugin.Plugin;
import com.salesforce.omakase.plugin.core.AutoRefine;

/**
 * Verifies expected delivery order for subscription methods.
 *
 * @author nmcwilliams
 */
@SuppressWarnings({"unused"})
public class SubscriptionDeliveryOrderTest {
    private static final String SRC = "#test .test, .test2 > p {\n" +
        "  margin: 5px 3px;\n" +
        "  background: custom(1+1);\n" +
        "}\n" +
        "\n" +
        "@media all and (min-width: 800px) {\n" +
        "  .test {\n" +
        "    color: #3a3;\n" +
        "    background: url(foo.png);\n" +
        "  }\n" +
        "}";

    @Test
    public void testStaticDeliveryOrderRefine() throws Exception {
        List<Broadcastable> refined = new ArrayList<>();

        Omakase.source(SRC)
            .use(new Plugin() {
                @Refine
                public void observe(Selector unit, Grammar grammar, Broadcaster broadcaster) {
                    refined.add(unit);
                }

                @Refine
                public void observe(Declaration unit, Grammar grammar, Broadcaster broadcaster) {
                    refined.add(unit);
                }

                @Refine
                public void observe(AtRule unit, Grammar grammar, Broadcaster broadcaster) {
                    refined.add(unit);
                }

                @Refine
                public void observe(RawFunction unit, Grammar grammar, Broadcaster broadcaster) {
                    refined.add(unit);
                }
            })
            .use(AutoRefine.everything())
            .process();

        assertThat(refined).hasSize(10);
        assertThat(refined.get(0)).isInstanceOf(Selector.class);
        assertThat(refined.get(1)).isInstanceOf(Selector.class);
        assertThat(refined.get(2)).isInstanceOf(Declaration.class);
        assertThat(refined.get(3)).isInstanceOf(Declaration.class);
        assertThat(refined.get(4)).isInstanceOf(RawFunction.class);
        assertThat(refined.get(5)).isInstanceOf(AtRule.class);
        assertThat(refined.get(6)).isInstanceOf(Selector.class);
        assertThat(refined.get(7)).isInstanceOf(Declaration.class);
        assertThat(refined.get(8)).isInstanceOf(Declaration.class);
        assertThat(refined.get(9)).isInstanceOf(RawFunction.class);
    }

    @Test
    public void testStaticDeliveryOrderProcess() throws Exception {
        List<Broadcastable> processed = new ArrayList<>();

        Omakase.source(SRC)
            .use(new Plugin() {
                @Rework
                public void rework(Syntax unit) {
                    processed.add(unit);
                }
            })
            .use(AutoRefine.everything())
            .process();

        assertThat(processed).hasSize(26);
        assertThat(processed.get(0)).isInstanceOf(IdSelector.class);
        assertThat(processed.get(1)).isInstanceOf(ClassSelector.class);
        assertThat(processed.get(2)).isInstanceOf(Selector.class);
        assertThat(processed.get(3)).isInstanceOf(ClassSelector.class);
        assertThat(processed.get(4)).isInstanceOf(TypeSelector.class);
        assertThat(processed.get(5)).isInstanceOf(Selector.class);
        assertThat(processed.get(6)).isInstanceOf(NumericalValue.class);
        assertThat(processed.get(7)).isInstanceOf(NumericalValue.class);
        assertThat(processed.get(8)).isInstanceOf(PropertyValue.class);
        assertThat(processed.get(9)).isInstanceOf(Declaration.class);
        assertThat(processed.get(10)).isInstanceOf(GenericFunctionValue.class);
        assertThat(processed.get(11)).isInstanceOf(PropertyValue.class);
        assertThat(processed.get(12)).isInstanceOf(Declaration.class);
        assertThat(processed.get(13)).isInstanceOf(Rule.class);
        assertThat(processed.get(14)).isInstanceOf(MediaQueryList.class);
        assertThat(processed.get(15)).isInstanceOf(ClassSelector.class);
        assertThat(processed.get(16)).isInstanceOf(Selector.class);
        assertThat(processed.get(17)).isInstanceOf(HexColorValue.class);
        assertThat(processed.get(18)).isInstanceOf(PropertyValue.class);
        assertThat(processed.get(19)).isInstanceOf(Declaration.class);
        assertThat(processed.get(20)).isInstanceOf(UrlFunctionValue.class);
        assertThat(processed.get(11)).isInstanceOf(PropertyValue.class);
        assertThat(processed.get(22)).isInstanceOf(Declaration.class);
        assertThat(processed.get(23)).isInstanceOf(Rule.class);
        assertThat(processed.get(24)).isInstanceOf(AtRule.class);
        assertThat(processed.get(25)).isInstanceOf(Stylesheet.class);
    }

    @Test
    public void testStaticDeliveryOrderValidate() throws Exception {
        List<Broadcastable> validated = new ArrayList<>();

        Omakase.source(SRC)
            .use(new Plugin() {
                @Validate
                public void validate(Syntax unit, ErrorManager em) {
                    validated.add(unit);
                }
            })
            .use(AutoRefine.everything())
            .process();

        assertThat(validated).hasSize(26);
        assertThat(validated.get(0)).isInstanceOf(IdSelector.class);
        assertThat(validated.get(1)).isInstanceOf(ClassSelector.class);
        assertThat(validated.get(2)).isInstanceOf(Selector.class);
        assertThat(validated.get(3)).isInstanceOf(ClassSelector.class);
        assertThat(validated.get(4)).isInstanceOf(TypeSelector.class);
        assertThat(validated.get(5)).isInstanceOf(Selector.class);
        assertThat(validated.get(6)).isInstanceOf(NumericalValue.class);
        assertThat(validated.get(7)).isInstanceOf(NumericalValue.class);
        assertThat(validated.get(8)).isInstanceOf(PropertyValue.class);
        assertThat(validated.get(9)).isInstanceOf(Declaration.class);
        assertThat(validated.get(10)).isInstanceOf(GenericFunctionValue.class);
        assertThat(validated.get(11)).isInstanceOf(PropertyValue.class);
        assertThat(validated.get(12)).isInstanceOf(Declaration.class);
        assertThat(validated.get(13)).isInstanceOf(Rule.class);
        assertThat(validated.get(14)).isInstanceOf(MediaQueryList.class);
        assertThat(validated.get(15)).isInstanceOf(ClassSelector.class);
        assertThat(validated.get(16)).isInstanceOf(Selector.class);
        assertThat(validated.get(17)).isInstanceOf(HexColorValue.class);
        assertThat(validated.get(18)).isInstanceOf(PropertyValue.class);
        assertThat(validated.get(19)).isInstanceOf(Declaration.class);
        assertThat(validated.get(20)).isInstanceOf(UrlFunctionValue.class);
        assertThat(validated.get(11)).isInstanceOf(PropertyValue.class);
        assertThat(validated.get(22)).isInstanceOf(Declaration.class);
        assertThat(validated.get(23)).isInstanceOf(Rule.class);
        assertThat(validated.get(24)).isInstanceOf(AtRule.class);
        assertThat(validated.get(25)).isInstanceOf(Stylesheet.class);
    }

    @Test
    public void testPrependAndAppendNormalUnits() {
        List<Broadcastable> broadcasted = new ArrayList<>();

        String src = ".test {\n" +
            "  color: red;\n" +
            "}";

        Omakase.source(src)
            .use(new Plugin() {
                @Rework
                public void rework(ClassSelector unit) {
                    broadcasted.add(unit);

                    if (unit.name().equals("test")) {
                        unit.prepend(new ClassSelector("prepend"));
                    } else if (unit.name().equals("prepend")) {
                        unit.append(new ClassSelector("append"));
                    } else if (unit.name().equals("append")) {
                        unit.parent().append(new Selector());
                    }

                }

                @Rework
                public void rework(KeywordValue unit) {
                    broadcasted.add(unit);

                    if (unit.name().equals("red")) {
                        unit.prepend(new KeywordValue("prepend"));
                    } else if (unit.name().equals("prepend")) {
                        unit.append(new KeywordValue("append"));
                    } else if (unit.name().equals("append")) {
                        unit.declaration().append(new Declaration(Property.DISPLAY, KeywordValue.of(Keyword.NONE)));
                    }

                }

                @Observe
                public void observe(Selector unit) {
                    broadcasted.add(unit);
                }

                @Observe
                public void observe(Declaration unit) {
                    broadcasted.add(unit);
                }
            })
            .use(AutoRefine.everything())
            .process();

        assertThat(broadcasted).hasSize(11);
        assertThat(broadcasted.get(0)).isInstanceOf(ClassSelector.class);
        assertThat(broadcasted.get(1)).isInstanceOf(ClassSelector.class);
        assertThat(broadcasted.get(2)).isInstanceOf(ClassSelector.class);
        assertThat(broadcasted.get(3)).isInstanceOf(Selector.class);
        assertThat(broadcasted.get(4)).isInstanceOf(Selector.class);
        assertThat(broadcasted.get(5)).isInstanceOf(KeywordValue.class);
        assertThat(broadcasted.get(6)).isInstanceOf(KeywordValue.class);
        assertThat(broadcasted.get(7)).isInstanceOf(KeywordValue.class);
        assertThat(broadcasted.get(8)).isInstanceOf(KeywordValue.class);
        assertThat(broadcasted.get(9)).isInstanceOf(Declaration.class);
        assertThat(broadcasted.get(10)).isInstanceOf(Declaration.class);
    }

    @Test
    public void testAddStatements() {
        List<Broadcastable> broadcasted = new ArrayList<>();

        String src = "@media all and (min-width: 800px) {\n" +
            "  .test { color: red }\n" +
            "}";

        Omakase.source(src)
            .use(new Plugin() {
                boolean appendedRule = false;
                boolean appendedAtRule = false;

                @Rework
                public void rework(Rule unit) {
                    broadcasted.add(unit);

                    if (!appendedRule) {
                        appendedRule = true;
                        Rule r = new Rule();
                        r.selectors().append(new Selector(new ClassSelector(".appended")));
                        r.declarations().append(new Declaration(Property.DISPLAY, KeywordValue.of(Keyword.NONE)));
                        unit.append(r);
                    }
                }

                @Rework
                public void rework(AtRule unit) {
                    broadcasted.add(unit);

                    if (!appendedAtRule) {
                        appendedAtRule = true;
                        unit.append(new AtRule("appended", new GenericAtRuleExpression("foo"), null));
                    }
                }

                @Observe
                public void observe(Selector unit) {
                    broadcasted.add(unit);
                }

                @Observe
                public void observe(ClassSelector unit) {
                    broadcasted.add(unit);
                }

                @Observe
                public void observe(Declaration unit) {
                    broadcasted.add(unit);
                }

                @Observe
                public void observe(KeywordValue unit) {
                    broadcasted.add(unit);
                }
            })
            .use(AutoRefine.everything())
            .process();

        assertThat(broadcasted).hasSize(12);
        assertThat(broadcasted.get(0)).isInstanceOf(ClassSelector.class);
        assertThat(broadcasted.get(1)).isInstanceOf(Selector.class);
        assertThat(broadcasted.get(2)).isInstanceOf(KeywordValue.class);
        assertThat(broadcasted.get(3)).isInstanceOf(Declaration.class);
        assertThat(broadcasted.get(4)).isInstanceOf(Rule.class);
        assertThat(broadcasted.get(5)).isInstanceOf(ClassSelector.class);
        assertThat(broadcasted.get(6)).isInstanceOf(Selector.class);
        assertThat(broadcasted.get(7)).isInstanceOf(KeywordValue.class);
        assertThat(broadcasted.get(8)).isInstanceOf(Declaration.class);
        assertThat(broadcasted.get(9)).isInstanceOf(Rule.class);
        assertThat(broadcasted.get(10)).isInstanceOf(AtRule.class);
        assertThat(broadcasted.get(10)).isInstanceOf(AtRule.class);
    }

    @Test
    public void testChangePropertyValue() {
        List<Broadcastable> broadcasted = new ArrayList<>();

        String src = ".test {\n" +
            "  color: #111;\n" +
            "}";

        Omakase.source(src)
            .use(new Plugin() {

                @Rework
                public void rework(Declaration unit) {
                    broadcasted.add(unit);

                    PropertyValue pv = new PropertyValue();
                    pv.append(new HexColorValue("#333"));
                    unit.propertyValue(pv);
                }

                @Observe
                public void observe(PropertyValue unit) {
                    broadcasted.add(unit);
                }

                @Observe
                public void observe(HexColorValue unit) {
                    broadcasted.add(unit);
                }
            })
            .use(AutoRefine.everything())
            .process();

        assertThat(broadcasted).hasSize(5);
        assertThat(broadcasted.get(0)).isInstanceOf(HexColorValue.class);
        assertThat(broadcasted.get(1)).isInstanceOf(PropertyValue.class);
        assertThat(broadcasted.get(2)).isInstanceOf(Declaration.class);
        assertThat(broadcasted.get(3)).isInstanceOf(HexColorValue.class);
        assertThat(broadcasted.get(4)).isInstanceOf(PropertyValue.class);
    }

    @Test
    public void testWhenCopied() {
        List<Broadcastable> broadcasted = new ArrayList<>();

        String src = ".test {\n" +
            "  color: #111;\n" +
            "}";

        Omakase.source(src)
            .use(new Plugin() {
                boolean copied = false;

                @Rework
                public void rework(Declaration unit) {
                    broadcasted.add(unit);

                    if (!copied) {
                        copied = true;
                        Declaration copy = unit.copy();
                        unit.prepend(copy);
                    }
                }

                @Observe
                public void observe(PropertyValue unit) {
                    broadcasted.add(unit);
                }

                @Observe
                public void observe(HexColorValue unit) {
                    broadcasted.add(unit);
                }
            })
            .use(AutoRefine.everything())
            .process();

        assertThat(broadcasted).hasSize(6);
        assertThat(broadcasted.get(0)).isInstanceOf(HexColorValue.class);
        assertThat(broadcasted.get(1)).isInstanceOf(PropertyValue.class);
        assertThat(broadcasted.get(2)).isInstanceOf(Declaration.class);
        assertThat(broadcasted.get(3)).isInstanceOf(HexColorValue.class);
        assertThat(broadcasted.get(4)).isInstanceOf(PropertyValue.class);
        assertThat(broadcasted.get(5)).isInstanceOf(Declaration.class);
    }
}
