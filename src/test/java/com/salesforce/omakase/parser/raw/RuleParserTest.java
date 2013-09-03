/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser.raw;

import static com.salesforce.omakase.util.Templates.withExpectedResult;
import static org.fest.assertions.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.parser.AbstractParserTest;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.util.Templates.SourceWithExpectedResult;

/**
 * Unit tests for {@link RuleParser}.
 * 
 * @author nmcwilliams
 */
@SuppressWarnings("javadoc")
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
            ".class {\n  color:red;\n\n  margin:  1px }",
            ".class1, .class2 {color:red;}",
            ".class, \n .class2, #id1.class2 + p {color:red;}",
            ".class \n{color:red;}",
            ".class{color:red;}",
            "/*com{}ment*/.class{/*comme{}nt*/color:red;}",
            ".class \n { color: red; /*comment*/ }",
            ".class \n /* comment */ { color: red; }");
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
    public void matchesExpectedBroadcastContent() {
        GenericParseResult result = parse("   .class{color:red}").get(0);
        assertThat(result.broadcasted).hasSize(2);
        assertThat(result.broadcasted.get(0)).isInstanceOf(Selector.class);
        assertThat(result.broadcasted.get(1)).isInstanceOf(Declaration.class);
    }

    @Test
    @Override
    public void matchesExpectedBroadcastCount() {
        List<ParseResult<Integer>> results = parseWithExpected(
            withExpectedResult(".class{ color: red }", 2),
            withExpectedResult(".class{ color: red } .class{ color: red }", 2),
            withExpectedResult("     .class{ color: red }", 2),
            withExpectedResult("\n\n\n   .class{ color: red }", 2),
            withExpectedResult("/*com{}ment*/.class{/*comme{}nt*/color:red;}", 2));

        for (ParseResult<Integer> result : results) {
            assertThat(result.broadcasted).describedAs(result.stream.toString()).hasSize(result.expected);
        }
    }

    public void correctLineAndColumnNumber() {
        // n/a
    }

    @Test
    public void errorsOnMissingOpeningBracket() {
        exception.expect(ParserException.class);
        exception.expectMessage("Expected to find opening bracket");
        parse(".class \n ");
    }

    @Test
    public void errorsOnMissingClosingBracket() {
        exception.expect(ParserException.class);
        exception.expectMessage("Expected to find closing bracket");
        parse(".class \n { color: red");
    }
}
