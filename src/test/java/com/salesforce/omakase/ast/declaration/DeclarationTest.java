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

package com.salesforce.omakase.ast.declaration;

import com.google.common.collect.Sets;
import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.OrphanedComment;
import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.Status;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.declaration.value.Keyword;
import com.salesforce.omakase.ast.declaration.value.KeywordValue;
import com.salesforce.omakase.ast.declaration.value.NumericalValue;
import com.salesforce.omakase.ast.declaration.value.PropertyValue;
import com.salesforce.omakase.ast.declaration.value.TermList;
import com.salesforce.omakase.ast.declaration.value.TermOperator;
import com.salesforce.omakase.ast.declaration.value.Value;
import com.salesforce.omakase.broadcaster.AbstractBroadcaster;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.writer.StyleWriter;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.Set;

import static org.fest.assertions.api.Assertions.*;

/** Unit tests for {@link Declaration}. */
@SuppressWarnings("JavaDoc")
public class DeclarationTest {
    @Rule public final ExpectedException exception = ExpectedException.none();

    private RawSyntax rawName;
    private RawSyntax rawValue;
    private Declaration fromRaw;

    @Before
    public void setup() {
        rawName = new RawSyntax(2, 3, "display");
        rawValue = new RawSyntax(2, 5, "none");
        fromRaw = new Declaration(rawName, rawValue, new StatusChangingBroadcaster());
    }

    @Test
    public void rawValues() {
        assertThat(fromRaw.rawPropertyName()).isSameAs(rawName);
        assertThat(fromRaw.rawPropertyValue()).isSameAs(rawValue);
        assertThat(fromRaw.line()).isEqualTo(rawName.line());
        assertThat(fromRaw.column()).isEqualTo(rawName.column());
    }

    @Test
    public void setPropertyName() {
        Declaration d = new Declaration(Property.MARGIN, NumericalValue.of(5, "px"));
        d.propertyName(PropertyName.using(Property.PADDING));
        assertThat(d.propertyName().name()).isEqualTo("padding");
    }

    @Test
    public void setPropertyNameUsingShorthand() {
        Declaration d = new Declaration(Property.MARGIN, NumericalValue.of(5, "px"));
        d.propertyName(Property.PADDING);
        assertThat(d.propertyName().name()).isEqualTo("padding");
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
    public void setPropertyValue() {
        Declaration d = new Declaration(Property.DISPLAY, KeywordValue.of(Keyword.NONE));
        TermList newValue = TermList.singleValue(KeywordValue.of(Keyword.BLOCK));
        d.propertyValue(newValue);
        assertThat(d.propertyValue()).isSameAs(newValue);
    }

    @Test
    public void setPropertyValueShorthand() {
        Declaration d = new Declaration(Property.DISPLAY, KeywordValue.of(Keyword.NONE));
        d.propertyValue(KeywordValue.of(Keyword.BLOCK));
        assertThat(Value.asKeyword(d.propertyValue()).isPresent()).isTrue();
    }

    @Test
    public void setPropertyValueBroadcastsUnbroadcasted() {
        Declaration d = new Declaration(Property.DISPLAY, KeywordValue.of(Keyword.NONE));
        d.broadcaster(new StatusChangingBroadcaster());
        TermList newValue = TermList.singleValue(KeywordValue.of(Keyword.BLOCK));

        assertThat(newValue.status()).isSameAs(Status.UNBROADCASTED);
        d.propertyValue(newValue);
        assertThat(newValue.status()).isNotSameAs(Status.UNBROADCASTED);
    }

    @Test
    public void setPropertyValueDoesntBroadcastAlreadyBroadcasted() {
        Declaration d = new Declaration(Property.DISPLAY, KeywordValue.of(Keyword.NONE));
        StatusChangingBroadcaster broadcaster = new StatusChangingBroadcaster();
        d.broadcaster(broadcaster);
        TermList newValue = TermList.singleValue(KeywordValue.of(Keyword.BLOCK));
        newValue.status(Status.BROADCASTED_PREPROCESS);

        d.propertyValue(newValue);
        assertThat(broadcaster.all).isEmpty();
    }

    @Test
    public void propagatebroadcastBroadcastsPropertyValue() {
        PropertyValue pv = TermList.singleValue(KeywordValue.of(Keyword.NONE));
        Declaration d = new Declaration(Property.DISPLAY, pv);

        assertThat(pv.status()).isSameAs(Status.UNBROADCASTED);
        d.propagateBroadcast(new StatusChangingBroadcaster());
        assertThat(pv.status()).isNotSameAs(Status.UNBROADCASTED);
    }

    @Test
    public void getPropetyValueWhenUnrefined() {
        assertThat(fromRaw.propertyValue()).isNotNull();
    }

    @Test
    public void getPropertyValueWhenRefined() {
        PropertyValue propertyValue = fromRaw.propertyValue();
        assertThat(fromRaw.propertyValue()).isSameAs(propertyValue);
    }

    @Test
    public void isPropertyWithAnotherPropertyNameTrue() {
        Declaration d = new Declaration(Property.DISPLAY, KeywordValue.of(Keyword.NONE));
        assertThat(d.isProperty(PropertyName.using(Property.DISPLAY))).isTrue();
    }

    @Test
    public void isPropertyWithAnotherPropertyNameFalse() {
        Declaration d = new Declaration(Property.DISPLAY, KeywordValue.of(Keyword.NONE));
        assertThat(d.isProperty(PropertyName.using(Property.COLOR))).isFalse();
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
    public void isPropertyTrue() {
        Declaration d = new Declaration(Property.DISPLAY, TermList.singleValue(KeywordValue.of(Keyword.NONE)));
        assertThat(d.isProperty(PropertyName.using(Property.DISPLAY))).isTrue();
    }

    @Test
    public void isPropertyFalse() {
        Declaration d = new Declaration(Property.DISPLAY, KeywordValue.of(Keyword.NONE));
        assertThat(d.isProperty(Property.COLOR)).isFalse();
    }

    @Test
    public void isRefinedTrue() {
        fromRaw.refine();
        assertThat(fromRaw.isRefined()).isTrue();
    }

    @Test
    public void isRefinedFalse() {
        assertThat(fromRaw.isRefined()).isFalse();
    }

    @Test
    public void isRefinedTrueForDynamicallyCreatedUnit() {
        Declaration d = new Declaration(Property.DISPLAY, KeywordValue.of(Keyword.NONE));
        assertThat(d.isRefined()).isTrue();
    }

    @Test
    public void refine() {
        assertThat(fromRaw.refine().propertyName()).isNotNull();
        assertThat(fromRaw.refine().propertyValue()).isNotNull();
    }

    @Test
    public void refineThrowsErrorIfUnparsableContent() {
        RawSyntax name = new RawSyntax(2, 3, "display");
        RawSyntax value = new RawSyntax(2, 5, "none ^^^^^^");

        exception.expect(ParserException.class);
        exception.expectMessage(Message.UNPARSABLE_VALUE.message());
        new Declaration(name, value, new StatusChangingBroadcaster()).refine();
    }

    @Test
    public void refineAddsOrphanedComments() {
        RawSyntax name = new RawSyntax(2, 3, "display");
        RawSyntax value = new RawSyntax(2, 5, "none /*orphaned*/");

        Declaration d = new Declaration(name, value, new StatusChangingBroadcaster()).refine();
        assertThat(d.orphanedComments()).isNotEmpty();
    }

    @Test
    public void setOrphanedComments() {
        OrphanedComment c = new OrphanedComment("c", OrphanedComment.Location.DECLARATION);
        fromRaw.orphanedComment(c);
        assertThat(fromRaw.orphanedComments()).contains(c);
    }

    @Test
    public void getOrphanedCommentsWhenAbsent() {
        assertThat(fromRaw.orphanedComments()).isEmpty();
    }

    @Test
    public void writeVerboseRefined() throws IOException {
        TermList terms = TermList.ofValues(TermOperator.SPACE, NumericalValue.of(1, "px"), NumericalValue.of(2, "px"));
        Declaration d = new Declaration(Property.MARGIN, terms);
        StyleWriter writer = StyleWriter.verbose();
        assertThat(writer.writeSnippet(d)).isEqualTo("margin: 1px 2px");
    }

    @Test
    public void writeInlineRefined() throws IOException {
        TermList terms = TermList.ofValues(TermOperator.SPACE, NumericalValue.of(1, "px"), NumericalValue.of(2, "px"));
        Declaration d = new Declaration(Property.MARGIN, terms);
        StyleWriter writer = StyleWriter.inline();
        assertThat(writer.writeSnippet(d)).isEqualTo("margin:1px 2px");
    }

    @Test
    public void writeCompressedRefined() throws IOException {
        TermList terms = TermList.ofValues(TermOperator.SPACE, NumericalValue.of(1, "px"), NumericalValue.of(2, "px"));
        Declaration d = new Declaration(Property.MARGIN, terms);
        StyleWriter writer = StyleWriter.compressed();
        assertThat(writer.writeSnippet(d)).isEqualTo("margin:1px 2px");
    }

    @Test
    public void writeVerboseUnrefined() throws IOException {
        RawSyntax name = new RawSyntax(2, 3, "border");
        RawSyntax value = new RawSyntax(2, 5, "1px solid red");
        Declaration d = new Declaration(name, value, new StatusChangingBroadcaster());

        StyleWriter writer = StyleWriter.verbose();
        assertThat(writer.writeSnippet(d)).isEqualTo("border: 1px solid red");
    }

    @Test
    public void writeInlineUnrefined() throws IOException {
        RawSyntax name = new RawSyntax(2, 3, "border");
        RawSyntax value = new RawSyntax(2, 5, "1px solid red");
        Declaration d = new Declaration(name, value, new StatusChangingBroadcaster());

        StyleWriter writer = StyleWriter.inline();
        assertThat(writer.writeSnippet(d)).isEqualTo("border:1px solid red");
    }

    @Test
    public void writeCompressedUnrefined() throws IOException {
        RawSyntax name = new RawSyntax(2, 3, "border");
        RawSyntax value = new RawSyntax(2, 5, "1px solid red");
        Declaration d = new Declaration(name, value, new StatusChangingBroadcaster());

        StyleWriter writer = StyleWriter.compressed();
        assertThat(writer.writeSnippet(d)).isEqualTo("border:1px solid red");
    }

    public void writeCompressedUnrefinedDoesMinification() {
        // TODO add test when functionality is added
    }

    @Test
    public void isWritableWhenAttached() {
        Declaration d = new Declaration(Property.DISPLAY, KeywordValue.of(Keyword.NONE));
        com.salesforce.omakase.ast.Rule rule = new com.salesforce.omakase.ast.Rule();
        rule.declarations().append(d);
        assertThat(d.isWritable()).isTrue();
    }

    @Test
    public void isNotWritableWhenDetached() {
        Declaration d = new Declaration(Property.DISPLAY, KeywordValue.of(Keyword.NONE));
        assertThat(d.isWritable()).isFalse();
    }

    private static final class StatusChangingBroadcaster extends AbstractBroadcaster {
        private final Set<Syntax> all = Sets.newHashSet();

        @Override
        public <T extends Syntax> void broadcast(T syntax) {
            if (all.contains(syntax)) {
                fail("unit shouldn't be broadcasted twice!");
            }
            all.add(syntax);
            syntax.status(Status.BROADCASTED_PREPROCESS);
        }
    }
}
