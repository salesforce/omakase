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
import com.salesforce.omakase.test.util.Util;
import com.salesforce.omakase.writer.StyleWriter;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.fest.assertions.api.Assertions.*;

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
        exp1.terms(Lists.<TermListMember>newArrayList(NumericalValue.of(800, "px")));
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
        q2.detach();
        assertThat(StyleWriter.verbose().writeSnippet(list)).isEqualTo("only screen and (min-width: 800px)");
    }

    @Test
    public void toStringTest() {
        assertThat(list.toString()).isNotEqualTo(Util.originalToString(list));
    }
}
