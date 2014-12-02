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

package com.salesforce.omakase.parser.refiner;

import com.google.common.collect.Iterables;
import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.declaration.LinearGradientFunctionValue;
import com.salesforce.omakase.ast.declaration.RawFunction;
import com.salesforce.omakase.ast.declaration.UrlFunctionValue;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.test.functional.StatusChangingBroadcaster;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link StandardRefiner}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class StandardRefinerTest {
    @Rule public final ExpectedException exception = ExpectedException.none();

    @Test
    public void refineSelector() {
        RawSyntax raw = new RawSyntax(5, 2, ".class > #id");
        Selector selector = new Selector(raw, new MasterRefiner(new StatusChangingBroadcaster()));
        selector.refine();
        assertThat(selector.parts()).isNotEmpty();
    }

    @Test
    public void refineSelectorThrowsErrorIfHasUnparsableContent() {
        RawSyntax raw = new RawSyntax(5, 2, ".class > #id !!!!");
        Selector selector = new Selector(raw, new MasterRefiner(new StatusChangingBroadcaster()));

        exception.expect(ParserException.class);
        exception.expectMessage(Message.UNPARSABLE_SELECTOR.message());
        selector.refine();
    }

    @Test
    public void refinedSelectorAddsOrphanedComments() {
        RawSyntax raw = new RawSyntax(5, 2, ".class > #id /*orphaned*/");
        Selector selector = new Selector(raw, new MasterRefiner(new StatusChangingBroadcaster()));
        selector.refine();
        assertThat(selector.orphanedComments()).isNotEmpty();
    }

    @Test
    public void refineDeclaration() {
        RawSyntax rawName = new RawSyntax(2, 3, "display");
        RawSyntax rawValue = new RawSyntax(2, 5, "none");
        Declaration declaration = new Declaration(rawName, rawValue, new MasterRefiner(new StatusChangingBroadcaster()));
        declaration.refine();
        assertThat(declaration.propertyName()).isNotNull();
        assertThat(declaration.propertyValue()).isNotNull();
    }

    @Test
    public void refineDeclarationThrowsErrorIfUnparsableContent() {
        RawSyntax name = new RawSyntax(2, 3, "display");
        RawSyntax value = new RawSyntax(2, 5, "none ^^^^^^");

        exception.expect(ParserException.class);
        exception.expectMessage("Unable to parse remaining declaration value");
        new Declaration(name, value, new MasterRefiner(new StatusChangingBroadcaster())).refine();
    }

    @Test
    public void refinedDeclarationBadUrange() {
        RawSyntax name = new RawSyntax(2, 3, "unicode-range");
        RawSyntax value = new RawSyntax(2, 5, "u+ffx");

        exception.expect(ParserException.class);
        exception.expectMessage("Unable to parse remaining declaration value");
        new Declaration(name, value, new MasterRefiner(new StatusChangingBroadcaster())).refine();
    }

    @Test
    public void refineDeclarationAddsOrphanedComments() {
        RawSyntax name = new RawSyntax(2, 3, "display");
        RawSyntax value = new RawSyntax(2, 5, "none /*orphaned*/");
        Declaration d = new Declaration(name, value, new MasterRefiner(new StatusChangingBroadcaster()));
        d.refine();
        assertThat(d.orphanedComments()).isNotEmpty();
    }

    @Test
    public void refinedUrlFunctionValue() {
        RawFunction raw = new RawFunction(5, 2, "url", "one.png");
        QueryableBroadcaster qb = new QueryableBroadcaster();
        new StandardRefiner().refine(raw, qb, new MasterRefiner(qb));

        assertThat(qb.all()).hasSize(1);
        assertThat(Iterables.get(qb.all(), 0)).isInstanceOf(UrlFunctionValue.class);
    }

    @Test
    public void refinedLinearGradientFunctionValue() {
        RawFunction raw = new RawFunction(5, 2, "linear-gradient", "red, yellow");
        QueryableBroadcaster qb = new QueryableBroadcaster();
        new StandardRefiner().refine(raw, qb, new MasterRefiner(qb));

        assertThat(qb.all()).hasSize(1);
        assertThat(Iterables.get(qb.all(), 0)).isInstanceOf(LinearGradientFunctionValue.class);
    }

    @Test
    public void refinedMediaQuery() {
        MasterRefiner refiner = new MasterRefiner(new StatusChangingBroadcaster());
        AtRule ar = new AtRule(1, 1, "media", new RawSyntax(1, 1, "all"), new RawSyntax(2, 2, ".class{color:red}"), refiner);
        new StandardRefiner().refine(ar, refiner.broadcaster(), refiner);

        assertThat(ar.isRefined()).isTrue();
    }

    @Test
    public void refinesKeyframes() {
        MasterRefiner refiner = new MasterRefiner(new StatusChangingBroadcaster());
        AtRule ar = new AtRule(1, 1, "keyframes", new RawSyntax(1, 1, "test"), new RawSyntax(2, 2, "from{top:0%} to{top:100%}"), refiner);
        new StandardRefiner().refine(ar, refiner.broadcaster(), refiner);

        assertThat(ar.isRefined()).isTrue();
    }

    @Test
    public void refinesFontFace() {
        MasterRefiner refiner = new MasterRefiner(new StatusChangingBroadcaster());
        AtRule ar = new AtRule(1, 1, "font-face", null, new RawSyntax(2, 2, "font-family:MyFont; src:url(MyFont.ttf);"), refiner);
        new StandardRefiner().refine(ar, refiner.broadcaster(), refiner);
        assertThat(ar.isRefined()).isTrue();
    }
}
