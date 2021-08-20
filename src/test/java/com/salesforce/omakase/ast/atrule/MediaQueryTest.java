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

package com.salesforce.omakase.ast.atrule;

import static org.fest.assertions.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.collect.Lists;
import com.salesforce.omakase.ast.Status;
import com.salesforce.omakase.ast.declaration.NumericalValue;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.writer.StyleWriter;

/**
 * Unit tests for {@link MediaQuery}.
 *
 * @author nmcwilliams
 */
public class MediaQueryTest {
    @SuppressWarnings("deprecation")
    @Rule public final ExpectedException exception = ExpectedException.none();

    private MediaQuery mq;
    private MediaQueryExpression exp1;
    private MediaQueryExpression exp2;

    @Before
    public void setup() {
        mq = new MediaQuery();
        exp1 = new MediaQueryExpression("min-width");
        exp1.terms(Lists.newArrayList(NumericalValue.of(800, "px")));
        exp2 = new MediaQueryExpression("color");
    }

    @Test
    public void defaultRestrictionIsAbsent() {
        assertThat(mq.restriction().isPresent()).isFalse();
    }

    @Test
    public void setRestriction() {
        mq.type("test").restriction(MediaRestriction.NOT);
        assertThat(mq.restriction().get()).isSameAs(MediaRestriction.NOT);
    }

    @Test
    public void removeRestriction() {
        mq.type("test").restriction(MediaRestriction.NOT);
        mq.type(null).restriction(null);
        assertThat(mq.restriction().isPresent()).isFalse();
    }

    @Test
    public void errorsIfAddingRestrictionWithoutMediaType() {
        exception.expect(IllegalStateException.class);
        mq.restriction(MediaRestriction.NOT);
    }

    @Test
    public void defaultTypeAbsent() {
        assertThat(mq.type().isPresent()).isFalse();
    }

    @Test
    public void setType() {
        mq.type("test");
        assertThat(mq.type().get()).isEqualTo("test");
    }

    @Test
    public void typeLowerCased() {
        mq.type("TEST");
        assertThat(mq.type().get()).isEqualTo("test");
    }

    @Test
    public void removeType() {
        mq.type("test");
        mq.type(null);
        assertThat(mq.type().isPresent()).isFalse();
    }

    @Test
    public void getExpressions() {
        mq.expressions().append(exp1);
        assertThat(mq.expressions()).containsExactly(exp1);
    }

    @Test
    public void propagatesBroadcastToExpressions() {
        mq.expressions().append(exp1);
        QueryableBroadcaster broadcaster = new QueryableBroadcaster();
        mq.propagateBroadcast(broadcaster, Status.PARSED);
        assertThat(broadcaster.find(MediaQueryExpression.class).get()).isSameAs(exp1);
    }

    @Test
    public void isWritableWhenHasTypeOnly() {
        mq.type("test");
        assertThat(mq.isWritable()).isTrue();
    }

    @Test
    public void isWritableWhenHasExpressionsOnly() {
        mq.expressions().append(exp1);
        assertThat(mq.isWritable()).isTrue();
    }

    @Test
    public void notWritableWhenNoTypeOrExpression() {
        assertThat(mq.isWritable()).isFalse();
    }

    @Test
    public void writeWithRestriction() throws IOException {
        mq.type("screen").restriction(MediaRestriction.NOT);
        assertThat(StyleWriter.compressed().writeSingle(mq)).isEqualTo("not screen");
    }

    @Test
    public void writeWithTypeOnly() throws IOException {
        mq.type("screen");
        assertThat(StyleWriter.compressed().writeSingle(mq)).isEqualTo("screen");
    }

    @Test
    public void writeWithTypeAndExpression() throws IOException {
        mq.type("screen").expressions().append(exp1);
        assertThat(StyleWriter.compressed().writeSingle(mq)).isEqualTo("screen and (min-width:800px)");
    }

    @Test
    public void writeWithRestrictionTypeAndMultipleExpressions() throws IOException {
        mq.type("screen").restriction(MediaRestriction.ONLY).expressions().append(exp1).append(exp2);
        assertThat(StyleWriter.compressed().writeSingle(mq)).isEqualTo("only screen and (min-width:800px) and (color)");
    }

    @Test
    public void writeWhenRestrictionAndTypeIsAll() throws IOException {
        mq.type("all").restriction(MediaRestriction.NOT);
        assertThat(StyleWriter.compressed().writeSingle(mq)).isEqualTo("not all");
    }

    @Test
    public void writeWhenNoRestrictionAndTypeIsAll() throws IOException {
        mq.type("all").expressions().append(exp2);
        assertThat(StyleWriter.compressed().writeSingle(mq)).isEqualTo("(color)");
    }

    @Test
    public void properHandlingWhenFirstExpressionIsDetached() throws IOException {
        mq.type("screen").expressions().append(exp1).append(exp2);
        exp1.destroy();
        assertThat(StyleWriter.compressed().writeSingle(mq)).isEqualTo("screen and (color)");
    }

    @Test
    public void properHandlingWhenLastExpressionIsDetached() throws IOException {
        mq.type("screen").expressions().append(exp1).append(exp2);
        exp2.destroy();
        assertThat(StyleWriter.compressed().writeSingle(mq)).isEqualTo("screen and (min-width:800px)");
    }

    @Test
    public void makeCopyNoTypeOrResetriction() {
        mq.expressions().append(exp1);
        MediaQuery copy = mq.copy();
        assertThat(copy.expressions()).hasSize(1);
        assertThat(copy.expressions().first().get().feature()).isEqualTo("min-width");
    }

    @Test
    public void makeCopyTypeOnly() {
        mq.type("tv");
        mq.expressions().append(exp1);
        MediaQuery copy = mq.copy();

        assertThat(copy.type().get()).isEqualTo("tv");
        assertThat(copy.expressions()).hasSize(1);
    }

    @Test
    public void makeCopyTypeAndRestriction() {
        mq.type("tv").restriction(MediaRestriction.NOT);
        MediaQuery copy = mq.copy();

        assertThat(copy.type().get()).isEqualTo("tv");
        assertThat(copy.restriction().get()).isEqualTo(MediaRestriction.NOT);
    }
}
