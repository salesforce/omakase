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

import com.salesforce.omakase.ast.RawFunction;
import com.salesforce.omakase.ast.declaration.QuotationMode;
import com.salesforce.omakase.ast.declaration.UrlFunctionValue;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.parser.Grammar;
import com.salesforce.omakase.parser.ParserException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link UrlPlugin}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class UrlPluginTest {
    @Rule public final ExpectedException exception = ExpectedException.none();

    private Grammar grammar;
    private UrlPlugin urlPlugin;
    private QueryableBroadcaster broadcaster;

    @Before
    public void setup() {
        grammar = new Grammar();
        urlPlugin = new UrlPlugin();
        broadcaster = new QueryableBroadcaster();
    }

    @Test
    public void refinesToUrlFunctionValueInstance() {
        RawFunction raw = new RawFunction(1, 1, "url", "/img/poof.png");
        urlPlugin.refine(raw, grammar, broadcaster);
        assertThat(broadcaster.find(UrlFunctionValue.class).isPresent()).isTrue();
        assertThat(broadcaster.count()).isEqualTo(1);
    }

    @Test
    public void assignsCorrectLineAndColumn() {
        RawFunction raw = new RawFunction(5, 2, "url", "/img/poof.png");
        urlPlugin.refine(raw, grammar, broadcaster);
        UrlFunctionValue value = broadcaster.find(UrlFunctionValue.class).get();

        assertThat(value.line()).isEqualTo(5);
        assertThat(value.column()).isEqualTo(2);
    }

    @Test
    public void errorIfMissingClosingQuote() {
        RawFunction raw = new RawFunction(1, 1, "url", "\"/img/poof.png");
        exception.expect(ParserException.class);
        urlPlugin.refine(raw, grammar, broadcaster);
    }

    @Test
    public void correctlyFindsDoubleQuotes() {
        RawFunction raw = new RawFunction(1, 1, "url", "\"/img/poof.png\"");
        urlPlugin.refine(raw, grammar, broadcaster);
        UrlFunctionValue value = broadcaster.find(UrlFunctionValue.class).get();

        assertThat(value.url()).isEqualTo("/img/poof.png");
        assertThat(value.quotationMode().get()).isEqualTo(QuotationMode.DOUBLE);
    }

    @Test
    public void correctFindsSingleQuotes() {
        RawFunction raw = new RawFunction(1, 1, "url", "'/img/poof.png'");
        urlPlugin.refine(raw, grammar, broadcaster);
        UrlFunctionValue value = broadcaster.find(UrlFunctionValue.class).get();

        assertThat(value.url()).isEqualTo("/img/poof.png");
        assertThat(value.quotationMode().get()).isEqualTo(QuotationMode.SINGLE);
    }

    @Test
    public void correctlyFindsNoQuotes() {
        RawFunction raw = new RawFunction(1, 1, "url", "/img/poof.png");
        urlPlugin.refine(raw, grammar, broadcaster);
        UrlFunctionValue value = broadcaster.find(UrlFunctionValue.class).get();

        assertThat(value.url()).isEqualTo("/img/poof.png");
        assertThat(value.quotationMode().isPresent()).isFalse();
    }

    @Test
    public void errorsIfContentAfterClosingQuote() {
        RawFunction raw = new RawFunction(1, 1, "url", "\"/img/poof.png\"aaa");
        exception.expect(ParserException.class);
        exception.expectMessage("Unexpected content in url after closing quote");
        urlPlugin.refine(raw, grammar, broadcaster);
    }

    @Test
    public void handlesSpaceAroundQuotes() {
        RawFunction raw = new RawFunction(5, 2, "url", " '/img/poof.png' ");
        urlPlugin.refine(raw, grammar, broadcaster);
        UrlFunctionValue value = broadcaster.find(UrlFunctionValue.class).get();

        assertThat(value.url()).isEqualTo("/img/poof.png");
        assertThat(value.line()).isEqualTo(5);
        assertThat(value.column()).isEqualTo(2);
    }
}
