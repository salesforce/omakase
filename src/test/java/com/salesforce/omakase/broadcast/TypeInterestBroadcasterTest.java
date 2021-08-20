/*
 * Copyright (c) 2017, salesforce.com, inc.
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

package com.salesforce.omakase.broadcast;

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.salesforce.omakase.ast.declaration.KeywordValue;
import com.salesforce.omakase.ast.declaration.NumericalValue;
import com.salesforce.omakase.ast.declaration.PropertyValue;
import com.salesforce.omakase.ast.declaration.Term;
import com.salesforce.omakase.ast.selector.ClassSelector;
import com.salesforce.omakase.ast.selector.SelectorPart;
import com.salesforce.omakase.data.Keyword;

/**
 * Unit tests for {@link TypeInterestBroadcaster}.
 *
 * @author nmcwilliams
 */
public class TypeInterestBroadcasterTest {
    @SuppressWarnings("deprecation")
    @Rule public final ExpectedException exception = ExpectedException.none();

    @Test
    public void testRetrieval() {
        TypeInterestBroadcaster<Term> b = new TypeInterestBroadcaster<>(Term.class);
        NumericalValue nv = NumericalValue.of(12, "px");
        KeywordValue kv = KeywordValue.of(Keyword.NONE);

        b.broadcast(nv);
        b.broadcast(kv);

        assertThat(b.one().get()).isSameAs(nv);
        assertThat(b.gather()).containsExactly(nv, kv);
    }

    @Test
    public void ignoresBroadcastsOfWrongType() {
        TypeInterestBroadcaster<Term> b = new TypeInterestBroadcaster<>(Term.class);
        PropertyValue pv = new PropertyValue();
        NumericalValue nv = NumericalValue.of(12, "px");
        KeywordValue kv = KeywordValue.of(Keyword.NONE);
        ClassSelector cs = new ClassSelector("test");

        b.broadcast(pv);
        b.broadcast(nv);
        b.broadcast(kv);
        b.broadcast(cs);

        assertThat(b.one().get()).isSameAs(nv);
        assertThat(b.gather()).containsExactly(nv, kv);
    }

    @Test
    public void relaysWhenMatched() {
        TypeInterestBroadcaster<Term> b = new TypeInterestBroadcaster<>(Term.class);
        QueryableBroadcaster qb = b.chain(new QueryableBroadcaster());

        NumericalValue nv = NumericalValue.of(12, "px");
        b.broadcast(nv);

        assertThat(qb.find(NumericalValue.class).get()).isSameAs(nv);
    }

    @Test
    public void relaysWhenNotMatched() {
        TypeInterestBroadcaster<SelectorPart> b = new TypeInterestBroadcaster<>(SelectorPart.class);
        QueryableBroadcaster qb = b.chain(new QueryableBroadcaster());

        NumericalValue nv = NumericalValue.of(12, "px");
        b.broadcast(nv);

        assertThat(qb.find(NumericalValue.class).get()).isSameAs(nv);
    }

    @Test
    public void resets() {
        TypeInterestBroadcaster<Term> b = new TypeInterestBroadcaster<>(Term.class);
        NumericalValue nv = NumericalValue.of(12, "px");
        b.broadcast(nv);
        assertThat(b.one().get()).isSameAs(nv);

        b.reset();

        KeywordValue kv = KeywordValue.of(Keyword.NONE);
        b.broadcast(kv);
        assertThat(b.one().get()).isSameAs(kv);
    }
}