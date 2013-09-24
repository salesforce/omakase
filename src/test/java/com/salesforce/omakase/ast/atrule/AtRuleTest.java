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

import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.Statement;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.parser.Refiner;
import com.salesforce.omakase.writer.StyleWriter;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.fest.assertions.api.Assertions.assertThat;

/** Unit tests for {@link AtRule}. */
@SuppressWarnings("JavaDoc")
public class AtRuleTest {
    private RawSyntax rawExpression;
    private RawSyntax rawBlock;
    private Refiner refiner;

    @Before
    public void setup() {
        rawExpression = new RawSyntax(1, 1, "all and (max-width: 800px)");
        rawBlock = new RawSyntax(1, 1, "p { color: red;}");
        refiner = new Refiner(new QueryableBroadcaster());
    }

    @Test
    public void getName() {
        AtRule ar = new AtRule(5, 5, "media", rawExpression, rawBlock, refiner);
        assertThat(ar.name()).isEqualTo("media");
    }

    @Test
    public void getRawExpression() {
        AtRule ar = new AtRule(5, 5, "media", rawExpression, rawBlock, refiner);
        assertThat(ar.rawExpression().get()).isSameAs(rawExpression);
    }

    @Test
    public void getRawBlock() {
        AtRule ar = new AtRule(5, 5, "media", rawExpression, rawBlock, refiner);
        assertThat(ar.rawBlock().get()).isSameAs(rawBlock);
    }

    @Test
    public void expressionAbsent() {
        AtRule ar = new AtRule(5, 5, "media", rawExpression, rawBlock, refiner);
        assertThat(ar.expression().isPresent()).isFalse();
    }

    @Test
    public void blockAbsent() {
        AtRule ar = new AtRule(5, 5, "media", rawExpression, rawBlock, refiner);
        assertThat(ar.block().isPresent()).isFalse();
    }

    @Test
    public void isRefinedFalse() {
        AtRule ar = new AtRule(5, 5, "media", rawExpression, rawBlock, refiner);
        assertThat(ar.isRefined()).isFalse();
    }

    @Test
    public void asRule() {
        Statement ar = new AtRule(5, 5, "media", rawExpression, rawBlock, refiner);
        assertThat(ar.asRule().isPresent()).isFalse();
    }

    @Test
    public void asAtRule() {
        Statement ar = new AtRule(5, 5, "media", rawExpression, rawBlock, refiner);
        assertThat(ar.asAtRule().isPresent()).isTrue();
    }

    @Test
    public void writeUnrefined() throws IOException {
        AtRule ar = new AtRule(5, 5, "media", rawExpression, rawBlock, refiner);
        StyleWriter writer = StyleWriter.verbose();
        assertThat(writer.writeSnippet(ar)).isEqualTo("@media all and (max-width: 800px) {\n  p { color: red;}\n}");
    }
}
