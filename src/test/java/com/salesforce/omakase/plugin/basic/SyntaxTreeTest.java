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

package com.salesforce.omakase.plugin.basic;

import com.salesforce.omakase.Omakase;
import com.salesforce.omakase.ast.Stylesheet;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.*;

/** Unit tests for {@link SyntaxTree}. */
@SuppressWarnings("JavaDoc")
public class SyntaxTreeTest {
    private static final String SRC = "@charset \"UTF8\";\n" +
        "@media all and (max-width:800px) { p{color:red}}\n" +
        "p{ color: yellow; font-size: 1em; margin: 10px;}\n" +
        "a:hover{color: green}\n" +
        "@media all and (max-width:800px) { a:hover{color:red}}\n" +
        ".class1 > .class2 {}" +
        "#div1, #div2 /*orphaned*/ { position:absolute; /*orphaned-r*/}\n" +
        "/*orphaned-s*/";

    private Stylesheet stylesheet;

    @Before
    public void setup() {
        SyntaxTree tree = new SyntaxTree();
        Omakase.source(SRC).request(tree).process();
        stylesheet = tree.stylesheet();
    }

    @Test
    public void statements() {
        assertThat(stylesheet.statements()).hasSize(7);
    }

    @Test
    public void atRules() {
        fail("unimplemented");
    }

    @Test
    public void rules() {
        fail("unimplemented");
    }

    @Test
    public void selectorsOrder() {
        fail("unimplemented");
    }

    @Test
    public void declarationsOrder() {
        fail("unimplemented");
    }

    @Test
    public void ruleOrphanedComments() {
        fail("unimplemented");
    }

    @Test
    public void sheetOrphanedComments() {
        fail("unimplemented");
    }
}
