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

package com.salesforce.omakase.plugin.syntax;

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.RawFunction;
import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.atrule.FontDescriptor;
import com.salesforce.omakase.ast.atrule.FontFaceBlock;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.parser.Grammar;
import com.salesforce.omakase.parser.ParserException;

/**
 * Unit tests for {@link FontFacePlugin}.
 *
 * @author nmcwilliams
 */
public class FontFacePluginTest {
    @SuppressWarnings("deprecation")
    @Rule public final ExpectedException exception = ExpectedException.none();

    private static final String SAMPLE = "font-family: MyFont;\n" +
        "  src: local(\"My Font\"), local(\"MyFont\"), url(MyFont.ttf);\n" +
        "  font-weight: bold;";

    FontFacePlugin plugin;
    QueryableBroadcaster broadcaster;

    @Before
    public void setup() {
        plugin = new FontFacePlugin();
        broadcaster = new QueryableBroadcaster();
    }

    @Test
    public void errorsIfHasExpression() {
        AtRule ar = new AtRule(1, 1, "font-face", new RawSyntax(1, 1, ""), new RawSyntax(2, 2, SAMPLE));

        exception.expect(ParserException.class);
        exception.expectMessage(Message.UNEXPECTED_EXPRESSION_FONT_FACE);
        plugin.refine(ar, new Grammar(), broadcaster);
    }

    @Test
    public void errorsIfMissingBlock() {
        AtRule ar = new AtRule(1, 1, "font-face", null, null);

        exception.expect(ParserException.class);
        exception.expectMessage(Message.FONT_FACE);
        plugin.refine(ar, new Grammar(), broadcaster);
    }

    @Test
    public void errorsIfUnparsableContentAtEndOfBlock() {
        AtRule ar = new AtRule(1, 1, "font-face", null, new RawSyntax(2, 2, "font-family:MyFont; $"));

        exception.expect(ParserException.class);
        exception.expectMessage("Unable to parse the remaining content");
        plugin.refine(ar, new Grammar(), broadcaster);
    }

    @Test
    public void broadcastsTheBlock() {
        AtRule ar = new AtRule(1, 1, "font-face", null, new RawSyntax(2, 2, SAMPLE));

        plugin.refine(ar, new Grammar(), broadcaster);
        Optional<FontFaceBlock> block = broadcaster.find(FontFaceBlock.class);
        assertThat(block.isPresent()).isTrue();
    }

    @Test
    public void refinesFontDescriptors() {
        AtRule ar = new AtRule(1, 1, "font-face", null, new RawSyntax(2, 2, SAMPLE));

        plugin.refine(ar, new Grammar(), broadcaster);
        Optional<FontFaceBlock> block = broadcaster.find(FontFaceBlock.class);
        assertThat(block.isPresent()).isTrue();
        assertThat(block.get().fontDescriptors()).hasSize(3);
        assertThat(block.get().fontDescriptors().first().get().name()).isEqualTo("font-family");
        assertThat(block.get().fontDescriptors().first().get().propertyValue().members()).hasSize(1);
    }

    @Test
    public void fontFaceWithUnicodeRange() {
        // just test that it can handle it without stumbling
        String src = "font-family:MyFont; unicode-range: U+400-4ff";
        AtRule ar = new AtRule(1, 1, "font-face", null, new RawSyntax(2, 2, src));

        plugin.refine(ar, new Grammar(), broadcaster);
        Optional<FontFaceBlock> block = broadcaster.find(FontFaceBlock.class);
        assertThat(block.isPresent()).isTrue();
    }

    @Test
    public void fontFaceWithBase64() {
        // just test that it can handle it without stumbling
        String src = "font-family:MyFont;" +
            "src:url(data:application/x-font-ttf;charset=utf-8;base64," +
            "AEAAAALAIAAAwAwT1MvMggi/M4AAAC8AAAAYGNtYXAaVcxvAAAJHAAAAExnYXNwAAAAEAAAAWgAAAAIZ2x5Zqx71TYAAAFwAAATcGhlYWQJSKSJAAAU4AAAADZoaGVhJCMCPQAAFRgAAAAkaG10eDNAAJIAAJU8AAAAdGxvY2E6yD88AAAVsAAAADxtYXhwADIJQgAAFewAAAAgJmFtZcKc7PgAAJYMAAAJTnJvc3QAAwAAAAAXXAAAACAAAwIAAZAAJQAAAUwJZgAAAEcJTAFmAAAA9QA) " +
            "format('truetype');";
        AtRule ar = new AtRule(1, 1, "font-face", null, new RawSyntax(2, 2, src));

        plugin.refine(ar, new Grammar(), broadcaster);
        Optional<FontFaceBlock> block = broadcaster.find(FontFaceBlock.class);
        assertThat(block.isPresent()).isTrue();
    }

    @Test
    public void doesntLeakDeclarations() {
        AtRule ar = new AtRule(1, 1, "font-face", null, new RawSyntax(2, 2, SAMPLE));

        plugin.refine(ar, new Grammar(), broadcaster);
        assertThat(broadcaster.filter(Declaration.class)).isEmpty();
    }

    @Test
    public void broadcastsFontDescriptors() {
        AtRule ar = new AtRule(1, 1, "font-face", null, new RawSyntax(2, 2, SAMPLE));

        plugin.refine(ar, new Grammar(), broadcaster);
        assertThat(broadcaster.filter(FontDescriptor.class)).hasSize(3);
    }

    @Test
    public void broadcastsRawFunctionsInDescriptorPropertyValues() {
        String src = "font-family: t(siteFont);";
        AtRule ar = new AtRule(1, 1, "font-face", null, new RawSyntax(2, 2, src));

        plugin.refine(ar, new Grammar(), broadcaster);
        assertThat(broadcaster.filter(RawFunction.class)).hasSize(1);
    }
}
