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

package com.salesforce.omakase.plugin.other;

import com.google.common.collect.Lists;
import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.extended.UnquotedIEFilter;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.parser.refiner.Refiner;
import com.salesforce.omakase.parser.refiner.RefinerStrategy;
import org.junit.Before;
import org.junit.Test;

import static com.salesforce.omakase.plugin.other.UnquotedIEFilterPlugin.REFINER;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link UnquotedIEFilterPlugin}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class UnquotedIEFilterPluginTest {
    private Refiner refiner;
    private QueryableBroadcaster broadcaster;

    @Before
    public void setup() {
        broadcaster = new QueryableBroadcaster();
        refiner = new Refiner(broadcaster, Lists.<RefinerStrategy>newArrayList(REFINER));
    }

    @Test
    public void refineDeclarationNoMatchReturnsFalse() {
        Declaration d = new Declaration(new RawSyntax(2, 3, "display"), new RawSyntax(2, 5, "none"), refiner);
        assertThat(REFINER.refine(d, broadcaster, refiner)).isFalse();
        assertThat(d.isRefined()).isFalse();
    }

    @Test
    public void refineDeclarationMatches() {
        Declaration d = new Declaration(new RawSyntax(2, 3, "filter"), new RawSyntax(2, 5,
            "progid:DXImageTransform.Microsoft.Shadow(color='#969696', Direction=145, Strength=3)"), refiner);

        assertThat(REFINER.refine(d, broadcaster, refiner)).isTrue();
        assertThat(d.propertyValue()).isInstanceOf(UnquotedIEFilter.class);

        UnquotedIEFilter ief = (UnquotedIEFilter)d.propertyValue();
        assertThat(ief.line()).isEqualTo(2);
        assertThat(ief.column()).isEqualTo(5);
        assertThat(ief.content()).isEqualTo("progid:DXImageTransform.Microsoft.Shadow(color='#969696', Direction=145, " +
            "Strength=3)");
    }
}
