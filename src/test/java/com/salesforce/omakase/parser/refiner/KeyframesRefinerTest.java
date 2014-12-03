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

import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.atrule.GenericAtRuleBlock;
import com.salesforce.omakase.ast.atrule.GenericAtRuleExpression;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.parser.ParserException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link KeyframesRefiner}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class KeyframesRefinerTest {
    @Rule public final ExpectedException exception = ExpectedException.none();

    KeyframesRefiner strategy;
    QueryableBroadcaster broadcaster;
    MasterRefiner refiner;

    @Before
    public void setup() {
        strategy = new KeyframesRefiner();
        broadcaster = new QueryableBroadcaster();
        refiner = new MasterRefiner(broadcaster).register(strategy);
    }

    @Test
    public void returnsFalseIfNotApplicable() {
        AtRule ar = new AtRule(1, 1, "blah",
            new RawSyntax(1, 1, "test"), new RawSyntax(2, 2, "50%{top:100px}"), refiner);
        assertThat(strategy.refine(ar, broadcaster, refiner)).isFalse();
        assertThat(ar.isRefined()).isFalse();
    }

    @Test
    public void returnsFalseIfNotApplicableAndPrefixed() {
        AtRule ar = new AtRule(1, 1, "-webkit-blah",
            new RawSyntax(1, 1, "test"), new RawSyntax(2, 2, "50%{top:100px}"), refiner);
        assertThat(strategy.refine(ar, broadcaster, refiner)).isFalse();
        assertThat(ar.isRefined()).isFalse();
    }

    @Test
    public void returnsTrueIfMatches() {
        AtRule ar = new AtRule(1, 1, "keyframes",
            new RawSyntax(1, 1, "test"), new RawSyntax(2, 2, "50%{top:100px}"), refiner);
        assertThat(strategy.refine(ar, broadcaster, refiner)).isTrue();
        assertThat(broadcaster.find(GenericAtRuleExpression.class).isPresent()).isTrue();
        assertThat(broadcaster.find(GenericAtRuleBlock.class).isPresent()).isTrue();
    }

    @Test
    public void returnsTrueIfMatchesPrefix() {
        AtRule ar = new AtRule(1, 1, "-webkit-keyframes",
            new RawSyntax(1, 1, "test"), new RawSyntax(2, 2, "50%{top:100px}"), refiner);
        assertThat(strategy.refine(ar, broadcaster, refiner)).isTrue();
        assertThat(broadcaster.find(GenericAtRuleExpression.class).isPresent()).isTrue();
        assertThat(broadcaster.find(GenericAtRuleBlock.class).isPresent()).isTrue();
    }

    @Test
    public void errorsIfMissingName() {
        AtRule ar = new AtRule(1, 1, "-webkit-keyframes",
            null, new RawSyntax(2, 2, "50%{top:100px}"), refiner);

        exception.expect(ParserException.class);
        exception.expectMessage(Message.KEYFRAME_NAME.message());
        strategy.refine(ar, broadcaster, refiner);
    }

    @Test
    public void errorsIfInvalidName() {
        AtRule ar = new AtRule(1, 1, "-webkit-keyframes",
            new RawSyntax(1, 1, "$1"), new RawSyntax(2, 2, "50%{top:100px}"), refiner);

        exception.expect(ParserException.class);
        exception.expectMessage(Message.KEYFRAME_NAME.message());
        strategy.refine(ar, broadcaster, refiner);
    }

    @Test
    public void errorsIfUnparsableContentInExpression() {
        AtRule ar = new AtRule(1, 1, "-webkit-keyframes",
            new RawSyntax(1, 1, "test test2"), new RawSyntax(2, 2, "50%{top:100px}"), refiner);

        exception.expect(ParserException.class);
        exception.expectMessage("Unexpected content");
        strategy.refine(ar, broadcaster, refiner);
    }

    @Test
    public void errorsIfMissingBlock() {
        AtRule ar = new AtRule(1, 1, "-webkit-keyframes",
            new RawSyntax(1, 1, "test"), null, refiner);

        exception.expect(ParserException.class);
        exception.expectMessage(Message.MISSING_KEYFRAMES_BLOCK.message());
        strategy.refine(ar, broadcaster, refiner);
    }

    @Test
    public void errorsIfUnparsableContentAtEndOfBlock() {
        AtRule ar = new AtRule(1, 1, "keyframes",
            new RawSyntax(1, 1, "test"), new RawSyntax(2, 2, "50%{top:100px} $"), refiner);

        exception.expect(ParserException.class);
        exception.expectMessage("Unable to parse the remaining content");
        strategy.refine(ar, broadcaster, refiner);
    }

    @Test
    public void setsTheExpression() {
        AtRule ar = new AtRule(1, 1, "keyframes",
            new RawSyntax(1, 1, "test"), new RawSyntax(2, 2, "50%{top:100px}"), refiner);
        strategy.refine(ar, broadcaster, refiner);
        assertThat(ar.expression().isPresent()).isTrue();
    }

    @Test
    public void setsTheBlock() {
        AtRule ar = new AtRule(1, 1, "keyframes",
            new RawSyntax(1, 1, "test"), new RawSyntax(2, 2, "50%{top:100px}"), refiner);
        strategy.refine(ar, broadcaster, refiner);
        assertThat(ar.block().isPresent()).isTrue();
    }
}
