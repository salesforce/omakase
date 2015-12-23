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

import com.google.common.collect.Lists;
import com.salesforce.omakase.ast.declaration.NumericalValue;
import com.salesforce.omakase.ast.declaration.PropertyValueMember;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.writer.StyleWriter;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link MediaQueryList}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class MediaQueryListTest {
    private MediaQueryList list;
    private MediaQuery q1;
    private MediaQuery q2;

    @Before
    public void setup() {
        list = new MediaQueryList();

        MediaQueryExpression exp1 = new MediaQueryExpression("min-width");
        exp1.terms(Lists.<PropertyValueMember>newArrayList(NumericalValue.of(800, "px")));
        q1 = new MediaQuery().type("screen").restriction(MediaRestriction.ONLY);
        q1.expressions().append(exp1);

        q2 = new MediaQuery();
        q2.type("screen").expressions().append(new MediaQueryExpression("color"));
    }

    @Test
    public void addMediaQuery() {
        list.queries().append(q1).append(q2);
        assertThat(list.queries()).containsExactly(q1, q2);
    }

    @Test
    public void propagatesBroadcast() {
        list.queries().append(q1);

        QueryableBroadcaster qb = new QueryableBroadcaster();
        list.propagateBroadcast(qb);

        assertThat(qb.find(MediaQuery.class).get()).isSameAs(q1);
    }

    @Test
    public void isWritableIfHasQueries() {
        list.queries().append(q1);
        assertThat(list.isWritable()).isTrue();
    }

    @Test
    public void isNotWritableIfNoQueries() {
        assertThat(list.isWritable()).isFalse();
    }

    @Test
    public void writeWithOneQuery() throws IOException {
        list.queries().append(q1);
        assertThat(StyleWriter.verbose().writeSnippet(list)).isEqualTo("only screen and (min-width: 800px)");
    }

    @Test
    public void writeWithMultipleQueries() throws IOException {
        list.queries().append(q1).append(q2);
        assertThat(StyleWriter.verbose().writeSnippet(list)).isEqualTo("only screen and (min-width: 800px), screen and (color)");
    }

    @Test
    public void writeCompressedMultipleQueries() throws IOException {
        list.queries().append(q1).append(q2);
        assertThat(StyleWriter.compressed().writeSnippet(list)).isEqualTo("only screen and (min-width:800px),screen and (color)");
    }

    @Test
    public void handlesDetachedQueriesCorrectly() throws IOException {
        list.queries().append(q1).append(q2);
        q2.destroy();
        assertThat(StyleWriter.verbose().writeSnippet(list)).isEqualTo("only screen and (min-width: 800px)");
    }

    @Test
    public void testCopy() {
        list.queries().append(q1).append(q2);
        MediaQueryList copy = (MediaQueryList)list.copy();
        assertThat(copy.queries()).hasSize(2);
    }
}
