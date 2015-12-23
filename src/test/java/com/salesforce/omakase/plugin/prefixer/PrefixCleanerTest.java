/*
 * Copyright (c) 2015, salesforce.com, inc.
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

package com.salesforce.omakase.plugin.prefixer;

import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.parser.refiner.MasterRefiner;
import com.salesforce.omakase.util.Declarations;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link PrefixCleaner}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class PrefixCleanerTest {

    @Test
    public void testRemoveDeclarations() {
        PrefixCleaner pruner = PrefixCleaner.mismatchedPrefixedUnits();

        RawSyntax exp = new RawSyntax(-1, -1, "test");

        RawSyntax block = new RawSyntax(-1, -1, "from {-webkit-transform:rotate(0deg); -ms-transform:rotate(0deg); -moz-transform:rotate(0deg); " +
            "transform:rotate(0deg)}\n to {-webkit-transform:rotate(360deg); -moz-transform:rotate(0deg); -ms-transform:rotate(0deg); transform:rotate" +
            "(360deg)}\n");

        AtRule ar = new AtRule(-1, -1, "-webkit-keyframes", exp, block, new MasterRefiner());
        ar.refine();

        pruner.atRule(ar);

        for (Declaration declaration : Declarations.within(ar.block().get())) {
            if (declaration.isPrefixed()) {
                assertThat(declaration.propertyName().prefix().get()).isSameAs(Prefix.WEBKIT);
            }
        }
    }
}
