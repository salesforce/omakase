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

package com.salesforce.omakase.ast.declaration;

import com.google.common.collect.Lists;
import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.Rule;
import com.salesforce.omakase.ast.Status;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.atrule.GenericAtRuleBlock;
import com.salesforce.omakase.ast.atrule.GenericAtRuleExpression;
import com.salesforce.omakase.ast.selector.ClassSelector;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.broadcast.emitter.SubscriptionPhase;
import com.salesforce.omakase.data.Keyword;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.data.Property;
import com.salesforce.omakase.util.Values;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;

import static org.fest.assertions.api.Assertions.assertThat;

/** Unit tests for {@link Declaration}. */
@SuppressWarnings("JavaDoc")
public class DeclarationTest {
    @org.junit.Rule public final ExpectedException exception = ExpectedException.none();

    private RawSyntax rawName;
    private RawSyntax rawValue;
    private Declaration fromRaw;

    @Before
    public void setup() {
        rawName = new RawSyntax(2, 3, "display");
        rawValue = new RawSyntax(2, 5, "none");
        fromRaw = new Declaration(rawName, rawValue);
    }

    @Test
    public void rawValues() {
        assertThat(fromRaw.rawPropertyName().get()).isSameAs(rawName);
        assertThat(fromRaw.rawPropertyValue().get()).isSameAs(rawValue);
        assertThat(fromRaw.line()).isEqualTo(rawName.line());
        assertThat(fromRaw.column()).isEqualTo(rawName.column());
    }

    @Test
    public void setPropertyName() {
        Declaration d = new Declaration(Property.MARGIN, NumericalValue.of(5, "px"));
        d.propertyName(PropertyName.of(Property.PADDING));
        assertThat(d.propertyName().name()).isEqualTo("padding");
    }

    @Test
    public void setPropertyNameUsingShorthand() {
        Declaration d = new Declaration(Property.MARGIN, NumericalValue.of(5, "px"));
        d.propertyName(Property.PADDING);
        assertThat(d.propertyName().name()).isEqualTo("padding");
    }

    @Test
    public void setPropertyNameUsingString() {
        Declaration d = new Declaration(Property.ORDER, NumericalValue.of(5));
        d.propertyName("-webkit-order");
        assertThat(d.propertyName().name()).isEqualTo("-webkit-order");
        assertThat(d.propertyName().prefix().get()).isEqualTo(Prefix.WEBKIT);
    }

    @Test
    public void getPropertyNameWhenUnrefined() {
        assertThat(fromRaw.propertyName().name()).isEqualTo("display");
    }

    @Test
    public void getPropertyNameWhenRefined() {
        assertThat(fromRaw.propertyName().name()).isEqualTo("display");
        assertThat(fromRaw.propertyName().name()).isEqualTo("display");
    }

    @Test
    public void getName() {
        assertThat(fromRaw.propertyName().name()).isEqualTo(fromRaw.name());
    }

    @Test
    public void setPropertyValue() {
        Declaration d = new Declaration(Property.DISPLAY, KeywordValue.of(Keyword.NONE));
        PropertyValue newValue = PropertyValue.of(KeywordValue.of(Keyword.BLOCK));
        d.propertyValue(newValue);
        assertThat(d.propertyValue()).isSameAs(newValue);
    }

    @Test
    public void setPropertyValueShorthand() {
        Declaration d = new Declaration(Property.DISPLAY, KeywordValue.of(Keyword.NONE));
        d.propertyValue(KeywordValue.of(Keyword.BLOCK));
        assertThat(Values.asKeyword(d.propertyValue()).isPresent()).isTrue();
    }

    @Test
    public void changedPropertyValueIsBroadcasted() {
        Declaration d = new Declaration(Property.DISPLAY, KeywordValue.of(Keyword.NONE));

        QueryableBroadcaster qb = new QueryableBroadcaster();
        d.propagateBroadcast(qb, Status.PARSED);

        PropertyValue newValue = PropertyValue.of(KeywordValue.of(Keyword.BLOCK));
        d.propertyValue(newValue);

        assertThat(qb.filter(PropertyValue.class)).hasSize(2);
    }

    @Test
    public void setPropertyValueDoesntBroadcastAlreadyBroadcasted() {
        Declaration d = new Declaration(Property.DISPLAY, KeywordValue.of(Keyword.NONE));

        QueryableBroadcaster qb = new QueryableBroadcaster();
        d.propagateBroadcast(qb, Status.PARSED);
        assertThat(qb.filter(PropertyValue.class)).hasSize(1);

        PropertyValue newValue = PropertyValue.of(NumericalValue.of(5));
        newValue.status(Status.PROCESSED);
        d.propertyValue(newValue);
        assertThat(qb.filter(PropertyValue.class)).hasSize(1);
    }

    @Test
    public void setPropertyValueAssignsParent() {
        Declaration d = new Declaration(Property.DISPLAY, KeywordValue.of(Keyword.NONE));
        PropertyValue newValue = PropertyValue.of(KeywordValue.of(Keyword.BLOCK));
        d.propertyValue(newValue);
        assertThat(d.propertyValue().declaration()).isSameAs(d);
    }

    @Test
    public void setPropertyValueRemovesParentFromOldPropertyValue() {
        Declaration d = new Declaration(Property.DISPLAY, KeywordValue.of(Keyword.NONE));
        PropertyValue oldValue = d.propertyValue();

        PropertyValue newValue = PropertyValue.of(KeywordValue.of(Keyword.BLOCK));
        d.propertyValue(newValue);

        assertThat(oldValue.declaration()).isNull();
        assertThat(oldValue.status() == Status.NEVER_EMIT);
    }

    @Test
    public void propagatebroadcastBroadcastsPropertyValue() {
        PropertyValue pv = PropertyValue.of(KeywordValue.of(Keyword.NONE));
        Declaration d = new Declaration(Property.DISPLAY, pv);

        QueryableBroadcaster qb = new QueryableBroadcaster();
        d.propagateBroadcast(qb, Status.PARSED);
        assertThat(qb.find(PropertyValue.class).get()).isSameAs(pv);
    }

    @Test
    public void getPropertyValueWhenUnrefined() {
        // should be empty!
        assertThat(fromRaw.propertyValue()).isNotNull();
        assertThat(fromRaw.propertyValue().members()).isEmpty();
    }

    @Test
    public void getPropertyValueWhenRefined() {
        fromRaw.propertyValue(PropertyValue.of(KeywordValue.of(Keyword.NONE)));
        assertThat(fromRaw.propertyValue()).isNotNull();
        assertThat(fromRaw.propertyValue().members()).hasSize(1);
    }

    @Test
    public void getPropertyValueWhenDynamicallyCreated() {
        Declaration d = new Declaration(Property.DISPLAY, KeywordValue.of(Keyword.NONE));
        assertThat(d.propertyValue()).isNotNull();
        assertThat(d.propertyValue().members()).hasSize(1);
    }

    @Test
    public void isPropertyWithAnotherPropertyNameTrue() {
        Declaration d = new Declaration(Property.DISPLAY, KeywordValue.of(Keyword.NONE));
        assertThat(d.isProperty(PropertyName.of(Property.DISPLAY))).isTrue();
    }

    @Test
    public void isPropertyWithAnotherPropertyNameFalse() {
        Declaration d = new Declaration(Property.DISPLAY, KeywordValue.of(Keyword.NONE));
        assertThat(d.isProperty(PropertyName.of(Property.COLOR))).isFalse();
    }

    @Test
    public void isPropertyStringTrue() {
        Declaration d = new Declaration(Property.DISPLAY, KeywordValue.of(Keyword.NONE));
        assertThat(d.isProperty("display")).isTrue();
    }

    @Test
    public void isPropertyStringFalse() {
        Declaration d = new Declaration(Property.DISPLAY, KeywordValue.of(Keyword.NONE));
        assertThat(d.isProperty("color")).isFalse();
    }

    @Test
    public void isPropertyReturnsTrueForMatchingCustomProperty() {
        Declaration d = new Declaration(PropertyName.of("--custom-color"), KeywordValue.of(Keyword.RED));
        assertThat(d.isProperty(PropertyName.of("--custom-color"))).isTrue();
    }

    @Test
    public void isPropertyReturnsFalseForDifferentCasedCustomProperty() {
        Declaration d = new Declaration(PropertyName.of("--custom-color"), KeywordValue.of(Keyword.RED));
        assertThat(d.isProperty(PropertyName.of("--CUSTOM-color"))).isFalse();
    }

    @Test
    public void isPropertyTrue() {
        Declaration d = new Declaration(Property.DISPLAY, PropertyValue.of(KeywordValue.of(Keyword.NONE)));
        assertThat(d.isProperty(PropertyName.of(Property.DISPLAY))).isTrue();
    }

    @Test
    public void isPropertyFalse() {
        Declaration d = new Declaration(Property.DISPLAY, KeywordValue.of(Keyword.NONE));
        assertThat(d.isProperty(Property.COLOR)).isFalse();
    }

    @Test
    public void isPropertyIgnorePrefixTrue() {
        Declaration d = new Declaration(Property.DISPLAY, KeywordValue.of(Keyword.NONE));
        d.propertyName().prefix(Prefix.MOZ);
        assertThat(d.isPropertyIgnorePrefix(Property.DISPLAY)).isTrue();
    }

    @Test
    public void isPropertyIgnorePrefixFalse() {
        Declaration d = new Declaration(Property.DISPLAY, KeywordValue.of(Keyword.NONE));
        d.propertyName().prefix(Prefix.MOZ);
        assertThat(d.isPropertyIgnorePrefix(Property.MARGIN)).isFalse();
    }

    @Test
    public void isPropertyIgnorePrefixForPNTrue() {
        Declaration d = new Declaration(Property.DISPLAY, KeywordValue.of(Keyword.NONE));
        d.propertyName().prefix(Prefix.MOZ);
        assertThat(d.isPropertyIgnorePrefix(PropertyName.of("display"))).isTrue();
    }

    @Test
    public void isPropertyIgnorePrefixForPNFalse() {
        Declaration d = new Declaration(Property.DISPLAY, KeywordValue.of(Keyword.NONE));
        d.propertyName().prefix(Prefix.MOZ);
        assertThat(d.isPropertyIgnorePrefix(PropertyName.of("margin"))).isFalse();
    }

    @Test
    public void isPropertyIgnorePrefixForStringTrue() {
        Declaration d = new Declaration(Property.ORDER, KeywordValue.of(Keyword.NONE));
        d.propertyName().prefix(Prefix.WEBKIT);
        assertThat(d.isPropertyIgnorePrefix("order")).isTrue();
    }

    @Test
    public void isPropertyIgnorePrefixForStringFalse() {
        Declaration d = new Declaration(Property.ORDER, KeywordValue.of(Keyword.NONE));
        d.propertyName().prefix(Prefix.WEBKIT);
        assertThat(d.isPropertyIgnorePrefix("ordinal")).isFalse();
    }

    @Test
    public void isPrefixedTrue() {
        Declaration d = new Declaration(Property.DISPLAY, KeywordValue.of(Keyword.NONE));
        d.propertyName().prefix(Prefix.MOZ);
        assertThat(d.isPrefixed()).isTrue();
    }

    @Test
    public void isPrefixedFalse() {
        Declaration d = new Declaration(Property.DISPLAY, KeywordValue.of(Keyword.NONE));
        assertThat(d.isPrefixed()).isFalse();
    }

    @Test
    public void isRefinedTrue() {
        fromRaw.propertyValue(PropertyValue.of(KeywordValue.of(Keyword.NONE)));
        assertThat(fromRaw.isRefined()).isTrue();
    }

    @Test
    public void isRefinedTrueDynamicallyCreated() {
        Declaration d = new Declaration(Property.DISPLAY, KeywordValue.of(Keyword.NONE));
        assertThat(d.isRefined()).isTrue();
    }

    @Test
    public void isRefinedFalse() {
        assertThat(fromRaw.isRefined()).isFalse();
    }

    @Test
    public void isRefinedFalseButPropertyNameRefined() {
        fromRaw.propertyName();
        assertThat(fromRaw.isRefined()).isFalse();
    }

    @Test
    public void isRefinedTrueForDynamicallyCreatedUnit() {
        Declaration d = new Declaration(Property.DISPLAY, KeywordValue.of(Keyword.NONE));
        assertThat(d.isRefined()).isTrue();
    }

    @Test
    public void writeVerboseRefined() throws IOException {
        PropertyValue terms = PropertyValue.of(NumericalValue.of(1, "px"), NumericalValue.of(2, "px"));
        Declaration d = new Declaration(Property.MARGIN, terms);
        assertThat(StyleWriter.verbose().writeSingle(d)).isEqualTo("margin: 1px 2px");
    }

    @Test
    public void writeInlineRefined() throws IOException {
        PropertyValue terms = PropertyValue.of(NumericalValue.of(1, "px"), NumericalValue.of(2, "px"));
        Declaration d = new Declaration(Property.MARGIN, terms);
        assertThat(StyleWriter.inline().writeSingle(d)).isEqualTo("margin:1px 2px");
    }

    @Test
    public void writeCompressedRefined() throws IOException {
        PropertyValue terms = PropertyValue.of(NumericalValue.of(1, "px"), NumericalValue.of(2, "px"));
        Declaration d = new Declaration(Property.MARGIN, terms);
        assertThat(StyleWriter.compressed().writeSingle(d)).isEqualTo("margin:1px 2px");
    }

    @Test
    public void writeVerboseUnrefined() throws IOException {
        RawSyntax name = new RawSyntax(2, 3, "border");
        RawSyntax value = new RawSyntax(2, 5, "1px solid red");
        Declaration d = new Declaration(name, value);

        assertThat(StyleWriter.verbose().writeSingle(d)).isEqualTo("border: 1px solid red");
    }

    @Test
    public void writeInlineUnrefined() throws IOException {
        RawSyntax name = new RawSyntax(2, 3, "border");
        RawSyntax value = new RawSyntax(2, 5, "1px solid red");
        Declaration d = new Declaration(name, value);

        assertThat(StyleWriter.inline().writeSingle(d)).isEqualTo("border:1px solid red");
    }

    @Test
    public void writeCompressedUnrefined() throws IOException {
        RawSyntax name = new RawSyntax(2, 3, "border");
        RawSyntax value = new RawSyntax(2, 5, "1px solid red");
        Declaration d = new Declaration(name, value);

        assertThat(StyleWriter.compressed().writeSingle(d)).isEqualTo("border:1px solid red");
    }

    @Test
    public void writeSecondUnitVerbose() throws IOException {
        Declaration d1 = new Declaration(Property.DISPLAY, KeywordValue.of(Keyword.NONE));
        Declaration d2 = new Declaration(Property.MARGIN, NumericalValue.of("5", "px"));

        StyleWriter verbose = StyleWriter.verbose();
        StyleAppendable appendable = new StyleAppendable();

        verbose.incrementDepth();
        verbose.writeInner(d1, appendable);
        verbose.writeInner(d2, appendable);
        assertThat(appendable.toString()).isEqualTo("display: none;\nmargin: 5px");
    }

    @Test
    public void writeSecondUnitInline() throws IOException {
        Declaration d1 = new Declaration(Property.DISPLAY, KeywordValue.of(Keyword.NONE));
        Declaration d2 = new Declaration(Property.MARGIN, NumericalValue.of("5", "px"));

        StyleWriter inline = StyleWriter.inline();
        StyleAppendable appendable = new StyleAppendable();

        inline.incrementDepth();
        inline.writeInner(d1, appendable);
        inline.writeInner(d2, appendable);
        assertThat(appendable.toString()).isEqualTo("display:none; margin:5px");
    }

    @Test
    public void isWritableWhenAttached() {
        Declaration d = new Declaration(Property.DISPLAY, KeywordValue.of(Keyword.NONE));
        Rule rule = new Rule();
        rule.declarations().append(d);
        assertThat(d.isWritable()).isTrue();
    }

    @Test
    public void isWritableWhenUnrefinedAndAttached() {
        RawSyntax name = new RawSyntax(2, 3, "border");
        RawSyntax value = new RawSyntax(2, 5, "1px solid red");
        Declaration d = new Declaration(name, value);
        Rule rule = new Rule();
        rule.declarations().append(d);
        assertThat(d.isWritable()).isTrue();
    }

    @Test
    public void isNotWritableWhenPropertyValueNotWritable() {
        Declaration d = new Declaration(Property.DISPLAY, new PropertyValue());
        Rule rule = new Rule();
        rule.declarations().append(d);
        assertThat(d.isWritable()).isFalse();
    }

    @Test
    public void copy() {
        Declaration d = new Declaration(Property.MARGIN, NumericalValue.of(5, "px"));
        d.comments(Lists.newArrayList("test"));

        Declaration copy = d.copy();
        assertThat(copy.isProperty(Property.MARGIN));
        assertThat(copy.propertyValue().members()).hasSameSizeAs(d.propertyValue().members());
        assertThat(copy.comments()).hasSameSizeAs(d.comments());
    }

    @Test
    public void copyUnrefined() {
        fromRaw.comment("test");
        Declaration copy = fromRaw.copy();
        assertThat(copy.isProperty(Property.DISPLAY));
        assertThat(copy.rawPropertyName()).isNotNull();
        assertThat(copy.rawPropertyValue()).isNotNull();
        assertThat(copy.propertyValue().members()).isEmpty();
        assertThat(copy.comments()).hasSameSizeAs(fromRaw.comments());
    }

    @Test
    public void copyUnrefinedPropertyNameRefined() {
        fromRaw.comment("test");
        fromRaw.propertyName();
        Declaration copy = fromRaw.copy();
        assertThat(copy.isProperty(Property.DISPLAY));
        assertThat(copy.rawPropertyName()).isNotNull();
        assertThat(copy.rawPropertyValue()).isNotNull();
        assertThat(copy.propertyValue().members()).isEmpty();
        assertThat(copy.comments()).hasSameSizeAs(fromRaw.comments());
    }

    @Test
    public void testParentAtRulePresent() {
        Declaration d = new Declaration(Property.COLOR, KeywordValue.of(Keyword.RED));
        Rule rule = new Rule(1, 1);
        rule.selectors().append(new Selector(new ClassSelector("test")));
        rule.declarations().append(d);

        GenericAtRuleBlock block = new GenericAtRuleBlock();
        block.statements().append(rule);
        AtRule ar = new AtRule("media", new GenericAtRuleExpression(1, 1, "all"), block);

        assertThat(d.parentAtRule().get()).isSameAs(ar);
    }

    @Test
    public void testParentAtRuleAbsent() {
        Declaration d = new Declaration(Property.COLOR, KeywordValue.of(Keyword.RED));
        Rule rule = new Rule(1, 1);
        rule.selectors().append(new Selector(new ClassSelector("test")));
        rule.declarations().append(d);

        assertThat(d.parentAtRule().isPresent()).isFalse();
    }

    @Test
    public void testDestroyWithNoMembers() {
        Declaration d = new Declaration(PropertyName.of(Property.MARGIN), new PropertyValue());
        d.destroy();
        assertThat(d.isDestroyed()).isTrue();

    }

    @Test
    public void testDestroyAlsoDestroysInnerTerms() {
        PropertyValue terms = PropertyValue.of(NumericalValue.of(1, "px"), NumericalValue.of(2, "px"));
        Declaration d = new Declaration(Property.MARGIN, terms);

        d.destroy();
        assertThat(d.isDestroyed()).isTrue();
        assertThat(d.propertyValue().members().isEmpty());
    }

    @Test
    public void breakBroadcastIfNeverEmit() {
        fromRaw.status(Status.NEVER_EMIT);
        assertThat(fromRaw.shouldBreakBroadcast(SubscriptionPhase.REFINE)).isTrue();
    }

    @Test
    public void breakBroadcastIfAlreadyRefined() {
        Declaration d = new Declaration(Property.DISPLAY, KeywordValue.of(Keyword.NONE));
        assertThat(d.shouldBreakBroadcast(SubscriptionPhase.REFINE)).isTrue();
    }

    @Test
    public void dontBreakBroadcastIfNotRefined() {
        assertThat(fromRaw.shouldBreakBroadcast(SubscriptionPhase.REFINE)).isFalse();
    }
}
