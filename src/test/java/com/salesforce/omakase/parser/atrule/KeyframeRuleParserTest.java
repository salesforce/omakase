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

package com.salesforce.omakase.parser.atrule;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.salesforce.omakase.ast.Rule;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.selector.KeyframeSelector;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.parser.AbstractParserTest;
import com.salesforce.omakase.parser.ParserException;
import org.junit.Test;

import java.util.List;

import static com.salesforce.omakase.test.util.TemplatesHelper.*;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link KeyframeRuleParser}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class KeyframeRuleParserTest extends AbstractParserTest<KeyframeRuleParser> {

    @Override
    public List<String> invalidSources() {
        return ImmutableList.of("", "\n", "   ", "blah");
    }

    @Override
    public List<String> validSources() {
        return ImmutableList.of(
            "from { top: 100% }",
            "to { top: 100% }",
            "50%, 50% { top: 100% }",
            "from { top: 100%; left: 50px }",
            "from { top: 100%; visibility: hidden; right: 10em }",
            "from { /*comment*/top: 100%; visibility: hidden; right: 10em }",
            "/*comment*/from { top: 100%; visibility: hidden; right: 10em /*comment*/ }",
            "from /*comment*/{ /*comment*/top: 100%; visibility: /*comment*/hidden; right: 10em }"
        );
    }

    @Override
    public List<SourceWithExpectedResult<Integer>> validSourcesWithExpectedEndIndex() {
        return ImmutableList.of(
            withExpectedResult("from { top: 100% } from { top: 100% }", 18),
            withExpectedResult("50%, 50% { top: 100% } another", 22),
            withExpectedResult("from /*comment*/{  /*com{}ment*/top: 100%; visibility: /*comment*/hidden; right: 10em } \n blah",
                87));
    }

    @Override
    public String validSourceForPositionTesting() {
        return Iterables.get(validSources(), 0);
    }

    @Override
    public boolean allowedToTrimLeadingWhitespace() {
        return true;
    }

    @Test
    @Override
    public void matchesExpectedBroadcastCount() {
        List<ParseResult<Integer>> results = parseWithExpected(
            // +1 for selector, +1 for declaration, +2 for notify declaration block start/end
            withExpectedResult("from { top: 100% }", 4),
            withExpectedResult("50%, 50% { top: 100% }", 6),
            withExpectedResult("from { /*comment*/top: 100%; visibility: hidden; right: 10em }", 6));

        for (ParseResult<Integer> result : results) {
            assertThat(result.broadcasted).describedAs(result.source.toString()).hasSize(result.expected);
        }
    }

    @Test
    @Override
    public void matchesExpectedBroadcastContent() {
        GenericParseResult result = parse("from { top: 100% } from { top: 100% }").get(0);
        assertThat(result.broadcasted).hasSize(4);
        assertThat(Iterables.get(result.broadcasted, 0)).isInstanceOf(KeyframeSelector.class);
        assertThat(Iterables.get(result.broadcasted, 1)).isInstanceOf(Selector.class);
        assertThat(Iterables.get(result.broadcasted, 2)).isInstanceOf(Declaration.class);
        assertThat(Iterables.get(result.broadcasted, 3)).isInstanceOf(Rule.class);
    }

    @Test
    public void addsOrphanedComments() {
        GenericParseResult result = parse("50%{top:0; /*orphaned*//*orphaned*/}").get(0);
        Rule rule = result.broadcaster.find(Rule.class).get();
        assertThat(rule.orphanedComments()).hasSize(2);
    }

    @Test
    public void errorsOnMissingOpeningBracket() {
        exception.expect(ParserException.class);
        exception.expectMessage("Expected to find opening brace");
        parse("50% \n ");
    }

    @Test
    public void errorsOnMissingClosingBracket() {
        exception.expect(ParserException.class);
        exception.expectMessage("Expected to find closing brace");
        parse("50% { top: 0");
    }
}
