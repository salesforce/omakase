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

package com.salesforce.omakase.parser;

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.collect.Lists;
import com.salesforce.omakase.ast.Rule;
import com.salesforce.omakase.ast.Stylesheet;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.broadcast.Broadcastable;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;

/**
 * Unit tests for {@link StylesheetParser}.
 *
 * @author nmcwilliams
 */
public class StylesheetParserTest {
    @SuppressWarnings("deprecation")
    @org.junit.Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void testEof() {
        exception.expect(ParserException.class);
        exception.expectMessage("Unparsable text found at the end of the source");
        new StylesheetParser().parse(new Source(".abc{color:red}   `"), new Grammar(), new QueryableBroadcaster());
    }

    @Test
    public void expectedBroadcastContent() {
        QueryableBroadcaster qb = new QueryableBroadcaster();
        new StylesheetParser().parse(new Source(".abc{color:red}\n.xyz{color:blue;}"), new Grammar(), qb);
        List<Broadcastable> all = Lists.newArrayList(qb.all());

        assertThat(all).hasSize(7);
        assertThat(all.get(0)).isInstanceOf(Selector.class);
        assertThat(all.get(1)).isInstanceOf(Declaration.class);
        assertThat(all.get(2)).isInstanceOf(Rule.class);
        assertThat(all.get(3)).isInstanceOf(Selector.class);
        assertThat(all.get(4)).isInstanceOf(Declaration.class);
        assertThat(all.get(5)).isInstanceOf(Rule.class);
        assertThat(all.get(6)).isInstanceOf(Stylesheet.class);
    }

    @Test
    public void testOrphanedComment() {
        QueryableBroadcaster qb = new QueryableBroadcaster();
        new StylesheetParser().parse(new Source(".abc{color:red} /*comment*/"), new Grammar(), qb);
        assertThat(qb.find(Stylesheet.class).get().orphanedComments()).hasSize(1);
    }
    
    @Test
    public void testComplexStyleSheet() {
        
    }
}
