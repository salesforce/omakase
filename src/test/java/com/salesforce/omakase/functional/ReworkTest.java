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

package com.salesforce.omakase.functional;

import com.salesforce.omakase.Omakase;
import com.salesforce.omakase.PluginRegistry;
import com.salesforce.omakase.ast.Rule;
import com.salesforce.omakase.ast.Status;
import com.salesforce.omakase.ast.Stylesheet;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.declaration.KeywordValue;
import com.salesforce.omakase.ast.declaration.NumericalValue;
import com.salesforce.omakase.ast.declaration.OperatorType;
import com.salesforce.omakase.ast.declaration.PropertyName;
import com.salesforce.omakase.ast.declaration.PropertyValue;
import com.salesforce.omakase.ast.selector.ClassSelector;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.broadcast.Broadcastable;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.broadcast.annotation.Rework;
import com.salesforce.omakase.data.Keyword;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.data.Property;
import com.salesforce.omakase.plugin.DependentPlugin;
import com.salesforce.omakase.plugin.Plugin;
import com.salesforce.omakase.plugin.basic.AutoRefiner;
import com.salesforce.omakase.plugin.basic.SyntaxTree;
import com.salesforce.omakase.plugin.validator.StandardValidation;
import com.salesforce.omakase.writer.StyleWriter;
import org.junit.Test;

import java.io.IOException;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests a basic rework workflow.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class ReworkTest {
    private static final String INPUT = ".left-alone {margin: 0;}\n" +
        ".THIS .inner, .xyz .THIS {display: inline-block; padding: 10px;}\n" +
        ".curvy {border: 3px solid red; border-radius: 40px 10px;}";

    private static final String EXPECTED = ".left-alone {margin:0}\n" +
        ".replaced .inner, .xyz .THIS {display:inline-block; zoom:1; padding:10px}\n" +
        ".curvy {border:3px solid red; -webkit-border-radius:40px 10px; border-radius:40px 10px}\n" +
        ".rounded {-webkit-border-radius:10px 5em; border-radius:10px 5em}\n" +
        ".replaced {display:none}";

    @Test
    public void testRework() throws IOException {
        // plugins
        PrefixBorderRadius prefixBorderRadius = new PrefixBorderRadius();
        ReplaceClassName replaceClassName = new ReplaceClassName("replaced");
        ReworkInlineBlock addZoomToInlineBlock = new ReworkInlineBlock("IE7");
        AddNewRules addNewRules = new AddNewRules();

        // for test verification / debugging
        QueryableBroadcaster broadcaster = new QueryableBroadcaster();
        StyleWriter inlineWriter = StyleWriter.inline();

        // parsing
        Omakase
            .source(INPUT) // specify INPUT css source code
            .broadcaster(broadcaster) // wrap the default broadcaster within our own
            .use(prefixBorderRadius) // add plugin instance
            .use(replaceClassName) // add plugin instance
            .use(addZoomToInlineBlock) // add plugin instance
            .use(addNewRules) // add plugin instance
            .use(new StandardValidation()) // add all default validation plugins
            .use(inlineWriter) // add the inline mode writer
            .process(); // process the css source + run plugins

        // uncomment to view output
        // inline.write(System.out);

        // ensure everything is broadcasted
        for (Broadcastable broadcastable : broadcaster.all()) {
            assertThat(broadcastable.status() != Status.UNBROADCASTED);
        }

        // declaration subscriptions called exactly 10 times (5 + 5 added)
        assertThat(prefixBorderRadius.count).isEqualTo(10);
        assertThat(addZoomToInlineBlock.count).isEqualTo(10);

        // class selector subscriptions called times (6 + 2 added)
        assertThat(replaceClassName.count).isEqualTo(8);

        // stylesheet subscriptions called once
        assertThat(addNewRules.count).isEqualTo(1);

        assertThat(inlineWriter.write()).isEqualTo(EXPECTED);
    }

    /** this class serves as an example for creating custom rework */
    public static final class PrefixBorderRadius implements Plugin {
        int count;

        /**
         * Sample rework task.
         * <p/>
         * In this task we want to prepend a prefixed version of border radius before the unprefixed version, e.g.,
         * -webkit-border-radius before declarations with border-radius. Note that we use the same property value so that if one
         * changes the other will be in sync.
         * <p/>
         * We could take this further by 1) only adding the prefixed version if the prefixed version doesn't already exist in the
         * rule (by iterating over declaration.group and checking isProperty(prop)) or 2) adding validation that checks for
         * prefixed declarations and throws an error, stating that the framework will handle it and all vendor prefixes should be
         * deleted).
         */
        @Rework
        public void addVendorPrefix(Declaration declaration) {
            // filter declarations for ones with the border-radius property
            if (declaration.isProperty(Property.BORDER_RADIUS)) {

                // create a prefixed version of the property
                PropertyName prop = PropertyName.of(Property.BORDER_RADIUS).prefix(Prefix.WEBKIT);

                // create a new declaration with the prefixed property name and same property value
                Declaration newDeclaration = new Declaration(prop, declaration.propertyValue());

                // prepend the new declaration so that the unprefixed one is last
                declaration.prepend(newDeclaration);
            }

            count++; // for the test
        }
    }

    /** this class serves as an example for creating custom rework */
    public static final class ReplaceClassName implements DependentPlugin {
        private final String replacement;
        int count;

        public ReplaceClassName(String replacement) {
            this.replacement = replacement;
        }

        @Override
        public void dependencies(PluginRegistry registry) {
            // auto refinement required of selectors because ClassSelector is only surfaced during refinement.
            registry.require(AutoRefiner.class).selectors();

            // SyntaxTree is required since we care about the order of units within the tree (isFirst())
            registry.require(SyntaxTree.class);
        }

        /**
         * Sample rework task.
         * <p/>
         * This will update classes at the beginning of the selector list named "THIS" with a replacement value.
         */
        @Rework
        public void updateClassName(ClassSelector selector) {
            if (selector.isFirst() && selector.name().equals("THIS")) {
                selector.name(replacement);
            }
            count++; // for the test
        }
    }

    /** this class serves as an example for creating custom rework */
    public static final class ReworkInlineBlock implements Plugin {
        private final String browser;
        int count;

        public ReworkInlineBlock(String browser) {
            this.browser = browser;
        }

        /**
         * Sample rework task.
         * <p/>
         * This adds "zoom: 1" after declarations with "display: inline-block", if the browser is IE7.
         * <p/>
         * We could take this further by 1) allow prevention of the addition by adding a css comment annotation before the
         * inline-block declaration such as "{@literal @}nozoom", and 2) only adding if a declaration with "zoom: 1" doesn't exist
         * in the declaration block (by checking declaration.group()), or better yet throwing a validation error if it does exist
         * so that we can ensure it's only added to IE7.
         */
        @Rework
        public void addZoomToDisplayInlineBlock(Declaration declaration) {
            boolean isDisplay = declaration.isProperty(Property.DISPLAY);
            boolean isInlineBlock = Keyword.INLINE_BLOCK.isOnlyValueIn(declaration);

            if (browser.equals("IE7") && isDisplay && isInlineBlock) {
                Declaration newDeclaration = new Declaration(Property.ZOOM, NumericalValue.of(1));
                declaration.append(newDeclaration);
            }
            count++; // for the test
        }
    }

    public static final class AddNewRules implements DependentPlugin {
        int count;

        @Override
        public void dependencies(PluginRegistry registry) {
            // SyntaxTree is required for non-direct content units like Stylesheet
            registry.require(SyntaxTree.class);
        }

        /**
         * Sample rework.
         * <p/>
         * When you need to perform multiple reworks on the same syntax unit type and the order is important then you should use
         * inner child methods. If order is not important then multiple methods annotated with {@link Rework} is fine as well.
         *
         * @param stylesheet
         */
        @Rework
        public void onStylesheet(Stylesheet stylesheet) {
            addRuleBorderRadius(stylesheet);
            addRuleDisplay(stylesheet);
            count++; // for the test
        }

        /**
         * Sample rework delegate method.
         * <p/>
         * This will add a new rule to the end of the stylesheet. The rule contains a declaration with the border-radius property.
         * This showcases how dynamically created units will be broadcasted to other rework subscription methods (like the one in
         * this class that should prepend a declaration with the webkit prefix).
         */
        private void addRuleBorderRadius(Stylesheet stylesheet) {
            // new rule
            Rule rule = new Rule();

            // selector
            Selector selector = new Selector(new ClassSelector("rounded"));
            rule.selectors().append(selector);

            // declaration
            NumericalValue px10 = NumericalValue.of(10, "px");
            NumericalValue em5 = NumericalValue.of(5, "em");
            PropertyValue value = PropertyValue.ofTerms(OperatorType.SPACE, px10, em5);
            Declaration declaration = new Declaration(Property.BORDER_RADIUS, value);
            rule.declarations().append(declaration);

            // add rule to stylesheet
            stylesheet.append(rule);
        }

        /**
         * Sample rework delegate method.
         * <p/>
         * This will add a new rule to the end of the stylesheet. The main purpose is to test that .THIS replacement from above
         * works on dynamically added class selectors.
         */
        private void addRuleDisplay(Stylesheet stylesheet) {
            // new rule
            Rule rule = new Rule();

            // selector
            Selector selector = new Selector(new ClassSelector("THIS"));
            rule.selectors().append(selector);

            // declaration
            Declaration declaration = new Declaration(Property.DISPLAY, KeywordValue.of(Keyword.NONE));
            rule.declarations().append(declaration);

            // add rule to stylesheet
            stylesheet.append(rule);
        }
    }
}
