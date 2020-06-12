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

package com.salesforce.omakase.ast.selector;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.Rule;
import com.salesforce.omakase.ast.Status;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.broadcast.emitter.SubscriptionPhase;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;

import static org.fest.assertions.api.Assertions.assertThat;

/** Unit tests for {@link Selector}. */
@SuppressWarnings("JavaDoc")
public class SelectorTest {
    @org.junit.Rule public final ExpectedException exception = ExpectedException.none();

    private Selector selector;

    @Test
    public void rawContent() {
        RawSyntax raw = new RawSyntax(5, 2, ".class > #id");
        selector = new Selector(raw);
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
        selector.appendAll(Lists.newArrayList(Combinator.descendant(), new IdSelector("test")));
        assertThat(selector.parts()).hasSize(3);
    }

    @Test
    public void propagatesBroadcasts() {
        ClassSelector cs = new ClassSelector("test");
        selector = new Selector(cs);
        QueryableBroadcaster qb = new QueryableBroadcaster();
        selector.propagateBroadcast(qb, Status.PARSED);
        assertThat(qb.find(ClassSelector.class).get()).isSameAs(cs);
        assertThat(qb.find(Selector.class).get()).isSameAs(selector);
    }

    @Test
    public void isRefinedTrue() {
        RawSyntax raw = new RawSyntax(5, 2, ".class > #id");
        selector = new Selector(raw);
        selector.parts().append(new ClassSelector("class"));
        assertThat(selector.isRefined()).isTrue();
    }

    @Test
    public void isRefinedFalse() {
        RawSyntax raw = new RawSyntax(5, 2, ".class > #id");
        selector = new Selector(raw);
        assertThat(selector.isRefined()).isFalse();
    }

    @Test
    public void isRefinedTrueForDynamicallyCreatedUnit() {
        selector = new Selector(new ClassSelector("test"));
        assertThat(selector.isRefined()).isTrue();
    }

    @Test
    public void callingDestroyAlsoCallsDestroyOnParts() {
        ClassSelector cs = new ClassSelector("test");
        IdSelector id = new IdSelector("id");
        Combinator combinator = Combinator.descendant();
        selector = new Selector(cs, combinator, id);

        selector.destroy();

        assertThat(selector.isDestroyed()).isTrue();
        assertThat(cs.isDestroyed()).isTrue();
        assertThat(id.isDestroyed()).isTrue();
        assertThat(combinator.isDestroyed()).isTrue();
    }

    @Test
    public void writeVerboseRefined() throws IOException {
        selector = new Selector(new ClassSelector("class"), Combinator.child(), new IdSelector("id"));
        assertThat(StyleWriter.verbose().writeSingle(selector)).isEqualTo(".class > #id");
    }

    @Test
    public void writeInlineRefined() throws IOException {
        selector = new Selector(new ClassSelector("class"), Combinator.child(), new IdSelector("id"));
        assertThat(StyleWriter.inline().writeSingle(selector)).isEqualTo(".class>#id");
    }

    @Test
    public void writeCompressedRefined() throws IOException {
        selector = new Selector(new ClassSelector("class"), Combinator.child(), new IdSelector("id"));
        assertThat(StyleWriter.compressed().writeSingle(selector)).isEqualTo(".class>#id");
    }

    @Test
    public void writeVerboseUnrefined() throws IOException {
        RawSyntax raw = new RawSyntax(5, 2, ".class > #id");
        selector = new Selector(raw);
        assertThat(StyleWriter.verbose().writeSingle(selector)).isEqualTo(".class > #id");
    }

    @Test
    public void writeInlineUnrefined() throws IOException {
        RawSyntax raw = new RawSyntax(5, 2, ".class > #id");
        selector = new Selector(raw);
        assertThat(StyleWriter.inline().writeSingle(selector)).isEqualTo(".class > #id");
    }

    @Test
    public void writeCompressedUnrefined() throws IOException {
        RawSyntax raw = new RawSyntax(5, 2, ".class > #id");
        selector = new Selector(raw);
        assertThat(StyleWriter.compressed().writeSingle(selector)).isEqualTo(".class > #id");
    }

    @Test
    public void writeSecondUnitVerbose() throws IOException {
        Selector s1 = new Selector(new ClassSelector("foo"));
        Selector s2 = new Selector(new ClassSelector("bar"));

        StyleWriter verbose = StyleWriter.verbose();
        StyleAppendable appendable = new StyleAppendable();

        verbose.incrementDepth();
        verbose.writeInner(s1, appendable);
        verbose.writeInner(s2, appendable);
        assertThat(appendable.toString()).isEqualTo(".foo, .bar");
    }

    @Test
    public void isWritableWhenAttached() {
        selector = new Selector(new ClassSelector("class"), Combinator.child(), new IdSelector("id"));
        Rule rule = new com.salesforce.omakase.ast.Rule();
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
        selector = new Selector(new RawSyntax(1, 1, ".test"));
        com.salesforce.omakase.ast.Rule rule = new com.salesforce.omakase.ast.Rule();
        rule.selectors().append(selector);
        assertThat(selector.isWritable()).isTrue();
    }

    @Test
    public void notWritableWhenNoParts() {
        selector = new Selector();
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

    @Test
    public void breakBroadcastIfNeverEmit() {
        selector = new Selector(new RawSyntax(5, 2, ".class > #id"));
        selector.status(Status.NEVER_EMIT);
        assertThat(selector.shouldBreakBroadcast(SubscriptionPhase.REFINE)).isTrue();
    }

    @Test
    public void breakBroadcastIfAlreadyRefined() {
        selector = new Selector(new ClassSelector("test"));
        assertThat(selector.shouldBreakBroadcast(SubscriptionPhase.REFINE)).isTrue();
    }

    @Test
    public void dontBreakBroadcastIfNotRefined() {
        selector = new Selector(new RawSyntax(5, 2, ".class > #id"));
        assertThat(selector.shouldBreakBroadcast(SubscriptionPhase.REFINE)).isFalse();
    }
}
