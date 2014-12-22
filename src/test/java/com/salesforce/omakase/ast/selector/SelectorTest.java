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

package com.salesforce.omakase.ast.selector;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.Status;
import com.salesforce.omakase.parser.refiner.MasterRefiner;
import com.salesforce.omakase.test.functional.StatusChangingBroadcaster;
import com.salesforce.omakase.writer.StyleWriter;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;

import static org.fest.assertions.api.Assertions.assertThat;

/** Unit tests for {@link Selector}. */
@SuppressWarnings("JavaDoc")
public class SelectorTest {
    @Rule public final ExpectedException exception = ExpectedException.none();

    private Selector selector;

    @Test
    public void rawContent() {
        RawSyntax raw = new RawSyntax(5, 2, ".class > #id");
        selector = new Selector(raw, new MasterRefiner(new StatusChangingBroadcaster()));
        assertThat(selector.raw().get()).isSameAs(raw);
    }

    @Test
    public void getParts() {
        ClassSelector cs = new ClassSelector("test");
        IdSelector id = new IdSelector("id");
        Combinator combinator = Combinator.descendant();
        selector = new Selector(cs, combinator, id);
        assertThat(selector.parts()).containsExactly(cs, combinator, id);
    }

    @Test
    public void appendPart() {
        selector = new Selector(new ClassSelector("test"));
        selector.append(new IdSelector("test"));
        assertThat(selector.parts()).hasSize(2);
    }

    @Test
    public void appendAllParts() {
        selector = new Selector(new ClassSelector("test"));
        selector.appendAll(Lists.<SelectorPart>newArrayList(Combinator.descendant(), new IdSelector("test")));
        assertThat(selector.parts()).hasSize(3);
    }

    @Test
    public void propagatesBroadcasts() {
        ClassSelector cs = new ClassSelector("test");
        selector = new Selector(cs);
        selector.propagateBroadcast(new StatusChangingBroadcaster());
        assertThat(cs.status()).isNotSameAs(Status.UNBROADCASTED);
        assertThat(selector.status()).isNotSameAs(Status.UNBROADCASTED);
    }

    @Test
    public void isRefinedTrue() {
        RawSyntax raw = new RawSyntax(5, 2, ".class > #id");
        selector = new Selector(raw, new MasterRefiner(new StatusChangingBroadcaster()));
        selector.refine();
        assertThat(selector.isRefined()).isTrue();
    }

    @Test
    public void isRefinedFalse() {
        RawSyntax raw = new RawSyntax(5, 2, ".class > #id");
        selector = new Selector(raw, new MasterRefiner(new StatusChangingBroadcaster()));
        assertThat(selector.isRefined()).isFalse();
    }

    @Test
    public void isRefinedTrueForDynamicallyCreatedUnit() {
        selector = new Selector(new ClassSelector("test"));
        assertThat(selector.isRefined()).isTrue();
    }

    @Test
    public void refine() {
        RawSyntax raw = new RawSyntax(5, 2, ".class > #id");
        selector = new Selector(raw, new MasterRefiner(new StatusChangingBroadcaster()));
        selector.refine();
        assertThat(selector.parts()).isNotEmpty();
    }

    @Test
    public void writeVerboseRefined() throws IOException {
        selector = new Selector(new ClassSelector("class"), Combinator.child(), new IdSelector("id"));
        selector.refine();
        assertThat(StyleWriter.verbose().writeSnippet(selector)).isEqualTo(".class > #id");
    }

    @Test
    public void writeInlineRefined() throws IOException {
        selector = new Selector(new ClassSelector("class"), Combinator.child(), new IdSelector("id"));
        selector.refine();
        assertThat(StyleWriter.inline().writeSnippet(selector)).isEqualTo(".class>#id");
    }

    @Test
    public void writeCompressedRefined() throws IOException {
        selector = new Selector(new ClassSelector("class"), Combinator.child(), new IdSelector("id"));
        selector.refine();
        assertThat(StyleWriter.compressed().writeSnippet(selector)).isEqualTo(".class>#id");
    }

    @Test
    public void writeVerboseUnrefined() throws IOException {
        RawSyntax raw = new RawSyntax(5, 2, ".class > #id");
        selector = new Selector(raw, new MasterRefiner(new StatusChangingBroadcaster()));
        assertThat(StyleWriter.verbose().writeSnippet(selector)).isEqualTo(".class > #id");
    }

    @Test
    public void writeInlineUnrefined() throws IOException {
        RawSyntax raw = new RawSyntax(5, 2, ".class > #id");
        selector = new Selector(raw, new MasterRefiner(new StatusChangingBroadcaster()));
        assertThat(StyleWriter.inline().writeSnippet(selector)).isEqualTo(".class > #id");
    }

    @Test
    public void writeCompressedUnrefined() throws IOException {
        RawSyntax raw = new RawSyntax(5, 2, ".class > #id");
        selector = new Selector(raw, new MasterRefiner(new StatusChangingBroadcaster()));
        assertThat(StyleWriter.compressed().writeSnippet(selector)).isEqualTo(".class > #id");
    }

    @Test
    public void isWritableWhenAttached() {
        selector = new Selector(new ClassSelector("class"), Combinator.child(), new IdSelector("id"));
        com.salesforce.omakase.ast.Rule rule = new com.salesforce.omakase.ast.Rule();
        rule.selectors().append(selector);
        assertThat(selector.isWritable()).isTrue();
    }

    @Test
    public void notWritableWhenDetached() {
        selector = new Selector(new ClassSelector("class"), Combinator.child(), new IdSelector("id"));
        selector.destroy();
        assertThat(selector.isWritable()).isFalse();
    }

    @Test
    public void alwaysWritableWhenAttachedAndUnrefined() {
        selector = new Selector(new RawSyntax(1, 1, ".test"), new MasterRefiner());
        com.salesforce.omakase.ast.Rule rule = new com.salesforce.omakase.ast.Rule();
        rule.selectors().append(selector);
        assertThat(selector.isWritable()).isTrue();
    }

    @Test
    public void notWritableWhenNoParts() {
        selector = new Selector();
        selector.refine();
        assertThat(selector.isWritable()).isFalse();
    }

    @Test
    public void notWritableWhenPartsDestroyed() {
        ClassSelector cs = new ClassSelector("test");
        selector = new Selector(cs);
        cs.destroy();
        assertThat(selector.isWritable()).isFalse();
    }

    @Test
    public void copy() {
        ClassSelector cs = new ClassSelector("test");
        Combinator combinator = Combinator.descendant();
        IdSelector id = new IdSelector("id");
        selector = new Selector(cs, combinator, id);

        Selector copy = selector.copy();
        assertThat(copy.parts()).hasSize(3);
        assertThat(Iterables.get(copy.parts(), 0)).isInstanceOf(ClassSelector.class);
        assertThat(Iterables.get(copy.parts(), 1)).isInstanceOf(Combinator.class);
        assertThat(Iterables.get(copy.parts(), 2)).isInstanceOf(IdSelector.class);
    }

    @Test
    public void keyframeSelectorTrue() {
        selector = new Selector(new KeyframeSelector("from"));
        assertThat(selector.isKeyframe()).isTrue();
    }

    @Test
    public void keyframeSelectorFalse() {
        selector = new Selector(new ClassSelector("test"));
        assertThat(selector.isKeyframe()).isFalse();
    }
}
