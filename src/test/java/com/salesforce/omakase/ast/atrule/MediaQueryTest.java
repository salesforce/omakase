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

package com.salesforce.omakase.ast.atrule;

import com.google.common.collect.Lists;
import com.salesforce.omakase.ast.declaration.NumericalValue;
import com.salesforce.omakase.ast.declaration.TermListMember;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.writer.StyleWriter;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;

import static org.fest.assertions.api.Assertions.*;

/**
 * Unit tests for {@link MediaQuery}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class MediaQueryTest {
    @Rule public final ExpectedException exception = ExpectedException.none();

    private MediaQuery mq;
    private MediaQueryExpression exp1;
    private MediaQueryExpression exp2;

    @Before
    public void setup() {
        mq = new MediaQuery();
        exp1 = new MediaQueryExpression("min-width");
        exp1.terms(Lists.<TermListMember>newArrayList(NumericalValue.of(800, "px")));
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
        mq.propagateBroadcast(broadcaster);
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
        assertThat(StyleWriter.compressed().writeSnippet(mq)).isEqualTo("not screen");
    }

    @Test
    public void writeWithTypeOnly() throws IOException {
        mq.type("screen");
        assertThat(StyleWriter.compressed().writeSnippet(mq)).isEqualTo("screen");
    }

    @Test
    public void writeWithTypeAndExpression() throws IOException {
        mq.type("screen").expressions().append(exp1);
        assertThat(StyleWriter.compressed().writeSnippet(mq)).isEqualTo("screen and (min-width:800px)");
    }

    @Test
    public void writeWithRestrictionTypeAndMultipleExpressions() throws IOException {
        mq.type("screen").restriction(MediaRestriction.ONLY).expressions().append(exp1).append(exp2);
        assertThat(StyleWriter.compressed().writeSnippet(mq)).isEqualTo("only screen and (min-width:800px) and (color)");
    }

    @Test
    public void writeWhenRestrictionAndTypeIsAll() throws IOException {
        mq.type("all").restriction(MediaRestriction.NOT);
        assertThat(StyleWriter.compressed().writeSnippet(mq)).isEqualTo("not all");
    }

    @Test
    public void writeWhenNoRestrictionAndTypeIsAll() throws IOException {
        mq.type("all").expressions().append(exp2);
        assertThat(StyleWriter.compressed().writeSnippet(mq)).isEqualTo("(color)");
    }

    @Test
    public void properHandlingWhenFirstExpressionIsDetached() throws IOException {
        mq.type("screen").expressions().append(exp1).append(exp2);
        exp1.detach();
        assertThat(StyleWriter.compressed().writeSnippet(mq)).isEqualTo("screen and (color)");
    }

    @Test
    public void properHandlingWhenLastExpressionIsDetached() throws IOException {
        mq.type("screen").expressions().append(exp1).append(exp2);
        exp2.detach();
        assertThat(StyleWriter.compressed().writeSnippet(mq)).isEqualTo("screen and (min-width:800px)");
    }
}
