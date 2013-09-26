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

package com.salesforce.omakase.parser.raw;

import com.google.common.collect.ImmutableList;
import com.salesforce.omakase.ast.OrphanedComment;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.notification.NotifyDeclarationBlockEnd;
import com.salesforce.omakase.notification.NotifyDeclarationBlockStart;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.parser.AbstractParserTest;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.test.util.TemplatesHelper.SourceWithExpectedResult;
import org.junit.Test;

import java.util.List;

import static com.salesforce.omakase.test.util.TemplatesHelper.withExpectedResult;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link RawRuleParser}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class RawRuleParserTest extends AbstractParserTest<RawRuleParser> {

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
    public boolean allowedToTrimLeadingWhitespace() {
        return true;
    }

    @Test
    @Override
    public void matchesExpectedBroadcastCount() {

        List<ParseResult<Integer>> results = parseWithExpected(
            // +1 for selector, +1 for declaration, +2 for notify declaration block start/end
            withExpectedResult(".class{ color: red }", 4),
            withExpectedResult(".class{ color: red } .class{ color: red }", 4),
            withExpectedResult("     .class{ color: red }", 4),
            withExpectedResult("\n\n\n   .class{ color: red }", 4),
            withExpectedResult("/*com{}ment*/.class{/*comme{}nt*/color:red;}", 4));

        for (ParseResult<Integer> result : results) {
            assertThat(result.broadcasted).describedAs(result.stream.toString()).hasSize(result.expected);
        }
    }

    @Test
    @Override
    public void matchesExpectedBroadcastContent() {
        GenericParseResult result = parse("   .class{color:red}").get(0);
        assertThat(result.broadcasted).hasSize(4);
        assertThat(result.broadcasted.get(0)).isInstanceOf(Selector.class);
        assertThat(result.broadcasted.get(1)).isInstanceOf(NotifyDeclarationBlockStart.class);
        assertThat(result.broadcasted.get(2)).isInstanceOf(Declaration.class);
        assertThat(result.broadcasted.get(3)).isInstanceOf(NotifyDeclarationBlockEnd.class);
    }

    @Test
    public void matchesExpectedBroadcastContentWithOrphaned() {
        GenericParseResult result = parse(".class{color:red; /*orphaned*/}").get(0);
        assertThat(result.broadcasted).hasSize(5);
        assertThat(result.broadcasted.get(0)).isInstanceOf(Selector.class);
        assertThat(result.broadcasted.get(1)).isInstanceOf(NotifyDeclarationBlockStart.class);
        assertThat(result.broadcasted.get(2)).isInstanceOf(Declaration.class);
        assertThat(result.broadcasted.get(3)).isInstanceOf(OrphanedComment.class);
        assertThat(result.broadcasted.get(4)).isInstanceOf(NotifyDeclarationBlockEnd.class);
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

    @Test
    public void sendsDeclarationBlockStart() {
        GenericParseResult result = parse(".class{color:red}").get(0);
        assertThat(result.broadcaster.filter(NotifyDeclarationBlockStart.class)).hasSize(1);
    }

    @Test
    public void sendsDeclarationBlockEnd() {
        GenericParseResult result = parse(".class{color:red}").get(0);
        assertThat(result.broadcaster.filter(NotifyDeclarationBlockEnd.class)).hasSize(1);
    }
}
