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

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.salesforce.omakase.ast.OrphanedComment;
import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.Status;
import com.salesforce.omakase.broadcast.AbstractBroadcaster;
import com.salesforce.omakase.broadcast.Broadcastable;
import com.salesforce.omakase.parser.Refiner;
import com.salesforce.omakase.test.util.Util;
import com.salesforce.omakase.writer.StyleWriter;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.Set;

import static org.fest.assertions.api.Assertions.*;

/** Unit tests for {@link Selector}. */
@SuppressWarnings("JavaDoc")
public class SelectorTest {
    @Rule public final ExpectedException exception = ExpectedException.none();

    private Selector selector;

    @Test
    public void rawContent() {
        RawSyntax raw = new RawSyntax(5, 2, ".class > #id");
        selector = new Selector(raw, new Refiner(new StatusChangingBroadcaster()));
        assertThat(selector.rawContent()).isSameAs(raw);
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
        selector.parts().append(new IdSelector("test"));
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
        selector = new Selector(raw, new Refiner(new StatusChangingBroadcaster()));
        selector.refine();
        assertThat(selector.isRefined()).isTrue();
    }

    @Test
    public void isRefinedFalse() {
        RawSyntax raw = new RawSyntax(5, 2, ".class > #id");
        selector = new Selector(raw, new Refiner(new StatusChangingBroadcaster()));
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
        selector = new Selector(raw, new Refiner(new StatusChangingBroadcaster()));
        assertThat(selector.refine().parts()).isNotEmpty();
    }

    @Test
    public void addOrphanedComment() {
        selector = new Selector(new ClassSelector("test"));
        OrphanedComment c = new OrphanedComment("test", OrphanedComment.Location.SELECTOR);
        selector.orphanedComment(c);
        assertThat(selector.orphanedComments()).containsExactly(c);
    }

    @Test
    public void getOrphanedCommentsWhenAbsent() {
        selector = new Selector(new ClassSelector("test"));
        assertThat(selector.orphanedComments()).isEmpty();
    }

    @Test
    public void writeVerboseRefined() throws IOException {
        selector = new Selector(new ClassSelector("class"), Combinator.child(), new IdSelector("id"));
        assertThat(StyleWriter.verbose().writeSnippet(selector.refine())).isEqualTo(".class > #id");
    }

    @Test
    public void writeInlineRefined() throws IOException {
        selector = new Selector(new ClassSelector("class"), Combinator.child(), new IdSelector("id"));
        assertThat(StyleWriter.inline().writeSnippet(selector.refine())).isEqualTo(".class>#id");
    }

    @Test
    public void writeCompressedRefined() throws IOException {
        selector = new Selector(new ClassSelector("class"), Combinator.child(), new IdSelector("id"));
        assertThat(StyleWriter.compressed().writeSnippet(selector.refine())).isEqualTo(".class>#id");
    }

    @Test
    public void writeVerboseUnrefined() throws IOException {
        RawSyntax raw = new RawSyntax(5, 2, ".class > #id");
        selector = new Selector(raw, new Refiner(new StatusChangingBroadcaster()));
        assertThat(StyleWriter.verbose().writeSnippet(selector)).isEqualTo(".class > #id");
    }

    @Test
    public void writeInlineUnrefined() throws IOException {
        RawSyntax raw = new RawSyntax(5, 2, ".class > #id");
        selector = new Selector(raw, new Refiner(new StatusChangingBroadcaster()));
        assertThat(StyleWriter.inline().writeSnippet(selector)).isEqualTo(".class > #id");
    }

    @Test
    public void writeCompressedUnrefined() throws IOException {
        RawSyntax raw = new RawSyntax(5, 2, ".class > #id");
        selector = new Selector(raw, new Refiner(new StatusChangingBroadcaster()));
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
        selector.detach();
        assertThat(selector.isWritable()).isFalse();
    }

    @Test
    public void toStringTest() {
        selector = new Selector(new ClassSelector("class"), Combinator.child(), new IdSelector("id"));
        assertThat(selector.toString()).isNotEqualTo(Util.originalToString(selector));
    }

    private static final class StatusChangingBroadcaster extends AbstractBroadcaster {
        private final Set<Broadcastable> all = Sets.newHashSet();

        @Override
        public void broadcast(Broadcastable broadcastable) {
            if (all.contains(broadcastable)) {
                fail("unit shouldn't be broadcasted twice!");
            }
            all.add(broadcastable);
            broadcastable.status(Status.BROADCASTED_PREPROCESS);
        }
    }
}
