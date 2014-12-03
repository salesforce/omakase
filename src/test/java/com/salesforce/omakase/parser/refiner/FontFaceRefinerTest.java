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

package com.salesforce.omakase.parser.refiner;

import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.atrule.FontDescriptor;
import com.salesforce.omakase.ast.atrule.FontFaceBlock;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.declaration.RawFunction;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.parser.ParserException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link FontFaceRefiner}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings({"JavaDoc", "SpellCheckingInspection"})
public class FontFaceRefinerTest {
    @Rule public final ExpectedException exception = ExpectedException.none();

    private static final String SAMPLE = "font-family: MyFont;\n" +
        "  src: local(\"My Font\"), local(\"MyFont\"), url(MyFont.ttf);\n" +
        "  font-weight: bold;";

    FontFaceRefiner strategy;
    QueryableBroadcaster broadcaster;
    MasterRefiner refiner;

    @Before
    public void setup() {
        strategy = new FontFaceRefiner();
        broadcaster = new QueryableBroadcaster();
        refiner = new MasterRefiner(broadcaster).register(strategy);
    }

    @Test
    public void returnsFalseIfNotApplicable() {
        AtRule ar = new AtRule(1, 1, "blah", null, new RawSyntax(2, 2, SAMPLE), refiner);
        assertThat(strategy.refine(ar, broadcaster, refiner)).isSameAs(Refinement.NONE);
        assertThat(ar.isRefined()).isFalse();
    }

    @Test
    public void returnsTrueIfMatches() {
        AtRule ar = new AtRule(1, 1, "font-face", null, new RawSyntax(2, 2, SAMPLE), refiner);
        assertThat(strategy.refine(ar, broadcaster, refiner)).isSameAs(Refinement.FULL);
    }

    @Test
    public void errorsIfHasExpression() {
        AtRule ar = new AtRule(1, 1, "font-face", new RawSyntax(1, 1, ""), new RawSyntax(2, 2, SAMPLE), refiner);

        exception.expect(ParserException.class);
        exception.expectMessage(Message.UNEXPECTED_EXPRESSION_FONT_FACE.message());
        strategy.refine(ar, broadcaster, refiner);
    }

    @Test
    public void errorsIfMissingBlock() {
        AtRule ar = new AtRule(1, 1, "font-face", null, null, refiner);

        exception.expect(ParserException.class);
        exception.expectMessage(Message.FONT_FACE.message());
        strategy.refine(ar, broadcaster, refiner);
    }

    @Test
    public void errorsIfUnparsableContentAtEndOfBlock() {
        AtRule ar = new AtRule(1, 1, "font-face", null, new RawSyntax(2, 2, "font-family:MyFont; $"), refiner);

        exception.expect(ParserException.class);
        exception.expectMessage("Unable to parse the remaining content");
        strategy.refine(ar, broadcaster, refiner);
    }

    @Test
    public void setsTheBlock() {
        AtRule ar = new AtRule(1, 1, "font-face", null, new RawSyntax(2, 2, SAMPLE), refiner);
        strategy.refine(ar, broadcaster, refiner);
        assertThat(ar.block().get()).isInstanceOf(FontFaceBlock.class);
    }

    @Test
    public void fontFaceWithUnicodeRange() {
        // just test that it can handle it without stumbling
        String src = "font-family:MyFont; unicode-range: U+400-4ff";
        AtRule ar = new AtRule(1, 1, "font-face", null, new RawSyntax(2, 2, src), refiner);
        assertThat(strategy.refine(ar, broadcaster, refiner)).isSameAs(Refinement.FULL);
    }

    @Test
    public void fontFaceWithBase64() {
        // just test that it can handle it without stumbling
        String src = "font-family:MyFont;" +
            "src:url(data:application/x-font-ttf;charset=utf-8;base64," +
            "AEAAAALAIAAAwAwT1MvMggi/M4AAAC8AAAAYGNtYXAaVcxvAAAJHAAAAExnYXNwAAAAEAAAAWgAAAAIZ2x5Zqx71TYAAAFwAAATcGhlYWQJSKSJAAAU4AAAADZoaGVhJCMCPQAAFRgAAAAkaG10eDNAAJIAAJU8AAAAdGxvY2E6yD88AAAVsAAAADxtYXhwADIJQgAAFewAAAAgJmFtZcKc7PgAAJYMAAAJTnJvc3QAAwAAAAAXXAAAACAAAwIAAZAAJQAAAUwJZgAAAEcJTAFmAAAA9QA) " +
            "format('truetype');";
        AtRule ar = new AtRule(1, 1, "font-face", null, new RawSyntax(2, 2, src), refiner);
        assertThat(strategy.refine(ar, broadcaster, refiner)).isSameAs(Refinement.FULL);
    }

    @Test
    public void doesntLeakDeclarations() {
        AtRule ar = new AtRule(1, 1, "font-face", null, new RawSyntax(2, 2, SAMPLE), refiner);
        strategy.refine(ar, broadcaster, refiner);
        assertThat(broadcaster.filter(Declaration.class)).isEmpty();
    }

    @Test
    public void broadcastsFontDescriptors() {
        AtRule ar = new AtRule(1, 1, "font-face", null, new RawSyntax(2, 2, SAMPLE), refiner);
        strategy.refine(ar, broadcaster, refiner);
        assertThat(broadcaster.filter(FontDescriptor.class)).hasSize(3);
    }

    @Test
    public void broadcastsRawFunctionsInDescriptorPropertyValues() {
        String src = "font-family: t(siteFont);";
        AtRule ar = new AtRule(1, 1, "font-face", null, new RawSyntax(2, 2, src), refiner);
        strategy.refine(ar, broadcaster, refiner);
        assertThat(broadcaster.filter(RawFunction.class)).hasSize(1);
    }
}
