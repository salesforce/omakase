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

package com.salesforce.omakase.parser;

import static com.salesforce.omakase.test.util.TemplatesHelper.withExpectedResult;
import static org.fest.assertions.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.salesforce.omakase.ast.Rule;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.test.util.TemplatesHelper.SourceWithExpectedResult;

/**
 * Unit tests for {@link RuleParser}.
 *
 * @author nmcwilliams
 */
public class RuleParserTest extends AbstractParserTest<RuleParser> {
    @Override
    public List<String> invalidSources() {
        return ImmutableList.of("", "\n", "   ", "1234", "$abc {}", "{color:red}");
    }

    @Override
    public List<String> validSources() {
        return ImmutableList.of(
            ".class{ color: red }",
            ".class {color:red;}",
            ".class {color:red;margin: 1px}",
            ".class {color:red;font-family:\"Times new roman\";}",
            ".class {\n  color:red;\n\n  margin:  1px }",
            ".class1, .class2 {color:red;}",
            ".class, \n .class2, #id1.class2 + p {color:red;}",
            ".class \n{color:red;}",
            ".class{color:red;}",
            ".class{color :red}",
            ".class{color : red}",
            ".class{color: red}",
            ".class{\tcolor: red}",
            ".class{\n\n\tcolor:\tred}",
            "/*com{}ment*/.class{/*comme{}nt*/color:red;}",
            ".class \n { color: red; /*comment*/ }",
            ".class \n { color: red /*comment*/ }",
            ".class \n /* comment */ { color: red; }"
        );
    }

    @Override
    public List<SourceWithExpectedResult<Integer>> validSourcesWithExpectedEndIndex() {
        return ImmutableList.of(
            withExpectedResult(".class{ color: red }", 20),
            withExpectedResult(".class{ color: red } .class{ color: red }", 20),
            withExpectedResult(".class{ color: red }.class{ color: red }", 20),
            withExpectedResult(".class{ color: red }\n\n.class{ color: red }", 20),
            withExpectedResult(".class{color:red;margin:10px}", 29),
            withExpectedResult("     .class{ color: red }", 25),
            withExpectedResult("\n\n\n   .class{ color: red }", 26),
            withExpectedResult("/*com{}ment*/.class{/*comme{}nt*/color:red;}", 44));
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
            withExpectedResult(".class{ color: red }", 3),
            withExpectedResult(".class{ color: red } .class{ color: red }", 3),
            withExpectedResult("     .class{ color: red }", 3),
            withExpectedResult("\n\n\n   .class{ color: red }", 3),
            withExpectedResult("/*com{}ment*/.class{/*comme{}nt*/color:red;}", 3));

        for (ParseResult<Integer> result : results) {
            assertThat(result.broadcasted).describedAs(result.source.toString()).hasSize(result.expected);
        }
    }

    @Test
    @Override
    public void matchesExpectedBroadcastContent() {
        GenericParseResult result = parse("   .class{color:red}").get(0);
        assertThat(result.broadcasted).hasSize(3);
        assertThat(Iterables.get(result.broadcasted, 0)).isInstanceOf(Selector.class);
        assertThat(Iterables.get(result.broadcasted, 1)).isInstanceOf(Declaration.class);
        assertThat(Iterables.get(result.broadcasted, 2)).isInstanceOf(Rule.class);
    }

    @Test
    public void addsOrphanedComments() {
        GenericParseResult result = parse(".class{color:red; /*orphaned*//*orphaned*/}").get(0);
        Rule rule = result.broadcaster.find(Rule.class).get();
        assertThat(rule.orphanedComments()).hasSize(2);
    }

    @Test
    public void errorsOnMissingOpeningBracket() {
        exception.expect(ParserException.class);
        exception.expectMessage("Expected to find opening brace");
        parse(".class \n ");
    }

    @Test
    public void errorsOnMissingClosingBracket() {
        exception.expect(ParserException.class);
        exception.expectMessage("Expected to find closing brace");
        parse(".class \n { color: red");
    }
}
