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

package com.salesforce.omakase.parser;

import com.salesforce.omakase.Omakase;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.selector.ClassSelector;
import com.salesforce.omakase.broadcaster.QueryableBroadcaster;
import com.salesforce.omakase.plugin.basic.AutoRefiner;
import com.salesforce.omakase.plugin.basic.SyntaxTree;
import org.junit.Test;

import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit test that ensures that regular comments are correctly associated with units and that orphaned comments are created as
 * appropriate.
 */
@SuppressWarnings("JavaDoc")
public class CommentPositionTest {
    private static final String SRC1 = "/*x*/\n" +
        "\n" +
        "/*x*/.class /*x*/.class/*x*/.class /*x*/,/*x*/ p#id /*x*/ { /*x*/\n" +
        "    /*x*/ border: /*x*/ 1px /*x*/ solid red /*x*/;/*x*/ /*x*/\n" +
        "    /*x*/ margin: 1px; /*x*/\n" +
        "    /*x*/\n" +
        "}\n" +
        "\n" +
        "/*x*/\n";

    private static final String SRC2 = "/*x*/\n" +
        "/*x*/.class /*x*/.class/*x*/.class /*x*/,/*x*/ p#id /*x*/ { /*x*/\n" +
        "    /*x*/ margin: 1px /*x*/\n" +
        "    /*x*/\n" +
        "} /*x*/\n";

    @Test
    public void parsesUnrefined() {
        parseNoRefine(SRC1);
        parseNoRefine(SRC2);
        // no errors
    }

    @Test
    public void parsesRefined() {
        parseRefined(SRC1);
        parseRefined(SRC2);
        // no errors
    }

    @Test
    public void testBroadcasts() {
        List<Syntax> broadcasted = parseRefined(SRC1).all();

        assertThat(broadcasted).hasSize(10);

        assertThat(broadcasted.get(0)).isInstanceOf(ClassSelector.class);
        assertThat(broadcasted.get(1)).isInstanceOf(ClassSelector.class);
        assertThat(broadcasted.get(2)).isInstanceOf(ClassSelector.class);
        assertThat(broadcasted.get(3)).isInstanceOf(ClassSelector.class);
        assertThat(broadcasted.get(4)).isInstanceOf(ClassSelector.class);
        assertThat(broadcasted.get(5)).isInstanceOf(ClassSelector.class);
        assertThat(broadcasted.get(6)).isInstanceOf(ClassSelector.class);
        assertThat(broadcasted.get(7)).isInstanceOf(ClassSelector.class);
        assertThat(broadcasted.get(8)).isInstanceOf(ClassSelector.class);
        assertThat(broadcasted.get(9)).isInstanceOf(ClassSelector.class);
    }

    private QueryableBroadcaster parseRefined(String input) {
        QueryableBroadcaster broadcaster = new QueryableBroadcaster();

        Omakase.source(input)
            .request(new AutoRefiner().all())
            .request(new SyntaxTree())
            .broadcaster(broadcaster)
            .process();

        return broadcaster;
    }

    private QueryableBroadcaster parseNoRefine(String input) {
        QueryableBroadcaster broadcaster = new QueryableBroadcaster();

        Omakase.source(input)
            .broadcaster(broadcaster)
            .process();

        return broadcaster;
    }

}
