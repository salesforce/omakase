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

package com.salesforce.omakase.parser.raw;

import com.google.common.collect.Lists;
import com.salesforce.omakase.ast.Rule;
import com.salesforce.omakase.ast.Stylesheet;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.broadcast.Broadcastable;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.Source;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link StylesheetParser}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class StylesheetParserTest {
    @org.junit.Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void testEof() {
        exception.expect(ParserException.class);
        exception.expectMessage("Unparsable text found at the end of the source");
        new StylesheetParser().parse(new Source(".abc{color:red}   `"), new QueryableBroadcaster());
    }

    @Test
    public void expectedBroadcastContent() {
        QueryableBroadcaster qb = new QueryableBroadcaster();
        new StylesheetParser().parse(new Source(".abc{color:red}\n.xyz{color:blue;}"), qb);
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
        new StylesheetParser().parse(new Source(".abc{color:red} /*comment*/"), qb);
        assertThat(qb.find(Stylesheet.class).get().orphanedComments()).hasSize(1);
    }
}
