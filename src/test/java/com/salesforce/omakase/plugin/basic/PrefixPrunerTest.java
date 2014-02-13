/*
 * Copyright (C) 2014 salesforce.com, inc.
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

import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.parser.refiner.GenericRefiner;
import com.salesforce.omakase.util.Declarations;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link PrefixPruner}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class PrefixPrunerTest {

    @Test
    public void testRemoveDeclarations() {
        PrefixPruner pruner = PrefixPruner.prunePrefixedAtRules();

        RawSyntax exp = new RawSyntax(-1, -1, "test");

        RawSyntax block = new RawSyntax(-1, -1, "from {-webkit-transform:rotate(0deg); -ms-transform:rotate(0deg); -moz-transform:rotate(0deg); " +
            "transform:rotate(0deg)}\n to {-webkit-transform:rotate(360deg); -moz-transform:rotate(0deg); -ms-transform:rotate(0deg); transform:rotate" +
            "(360deg)}\n");

        AtRule ar = new AtRule(-1, -1, "-webkit-keyframes", exp, block, new GenericRefiner());
        ar.refine();

        pruner.atRule(ar);

        for (Declaration declaration : Declarations.within(ar.block().get())) {
            if (declaration.isPrefixed()) {
                assertThat(declaration.propertyName().prefix().get()).isSameAs(Prefix.WEBKIT);
            }
        }
    }
}
