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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.declaration.KeywordValue;
import com.salesforce.omakase.ast.declaration.PropertyValue;
import com.salesforce.omakase.broadcast.NoopBroadcaster;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.parser.Grammar;
import com.salesforce.omakase.parser.ParserException;

/**
 * Unit tests for {@link DeclarationPlugin}.
 *
 * @author nmcwilliams
 */
public class DeclarationPluginTest {
    @SuppressWarnings("deprecation")
    @Rule public final ExpectedException exception = ExpectedException.none();

    @Test
    public void refineDeclaration() {
        QueryableBroadcaster broadcaster = new QueryableBroadcaster();
        RawSyntax rawName = new RawSyntax(2, 3, "display");
        RawSyntax rawValue = new RawSyntax(2, 5, "none");
        Declaration declaration = new Declaration(rawName, rawValue);

        new DeclarationPlugin().refine(declaration, new Grammar(), broadcaster);

        Optional<PropertyValue> p = broadcaster.find(PropertyValue.class);
        assertThat(p.isPresent()).isTrue();
        assertThat(p.get().members()).hasSize(1);
        assertThat(p.get().members().first().get()).isInstanceOf(KeywordValue.class);
    }

    @Test
    public void refineDeclarationThrowsErrorIfUnparsableContent() {
        RawSyntax name = new RawSyntax(2, 3, "display");
        RawSyntax value = new RawSyntax(2, 5, "none ^^^^^^");
        Declaration declaration = new Declaration(name, value);

        exception.expect(ParserException.class);
        exception.expectMessage("Unable to parse remaining declaration value");
        new DeclarationPlugin().refine(declaration, new Grammar(), new NoopBroadcaster());
    }

    @Test
    public void refinedDeclarationBadUrange() {
        RawSyntax name = new RawSyntax(2, 3, "unicode-range");
        RawSyntax value = new RawSyntax(2, 5, "u+ffx");
        Declaration declaration = new Declaration(name, value);

        exception.expect(ParserException.class);
        exception.expectMessage("Unable to parse remaining declaration value");
        new DeclarationPlugin().refine(declaration, new Grammar(), new NoopBroadcaster());
    }

    @Test
    public void refineDeclarationAddsOrphanedComments() {
        RawSyntax name = new RawSyntax(2, 3, "display");
        RawSyntax value = new RawSyntax(2, 5, "none /*orphaned*/");
        Declaration declaration = new Declaration(name, value);

        new DeclarationPlugin().refine(declaration, new Grammar(), new NoopBroadcaster());

        assertThat(declaration.orphanedComments()).isNotEmpty();
    }
}