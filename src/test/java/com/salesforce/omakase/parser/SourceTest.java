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

package com.salesforce.omakase.parser;

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.parser.token.ConstantEnum;
import com.salesforce.omakase.parser.token.Token;
import com.salesforce.omakase.parser.token.TokenEnum;
import com.salesforce.omakase.parser.token.Tokens;
import com.salesforce.omakase.test.util.TemplatesHelper;

/**
 * Unit tests for {@link Source}.
 *
 * @author nmcwilliams
 */
public class SourceTest {
    static final String INLINE = ".class, #id { color: red }";

    @SuppressWarnings("deprecation")
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void line() {
        Source source = new Source(INLINE);
        assertThat(source.line()).isEqualTo(1);
    }

    @Test
    public void column() {
        Source source = new Source(INLINE);
        assertThat(source.column()).isEqualTo(1);
    }

    @Test
    public void index() {
        Source source = new Source(INLINE);
        assertThat(source.index()).isEqualTo(0);
        source.next();
        assertThat(source.index()).isEqualTo(1);
    }

    @Test
    public void streamFromRaw() {
        RawSyntax raw = new RawSyntax(5, 6, "test");
        Source source = new Source(raw);

        assertThat(source.anchorLine()).isEqualTo(5);
        assertThat(source.anchorColumn()).isEqualTo(6);
        assertThat(source.fullSource()).isEqualTo("test");
    }

    @Test
    public void anchorLine() {
        Source source = new Source(INLINE, 10, 5);
        assertThat(source.anchorLine()).isEqualTo(10);
    }

    @Test
    public void anchorColumn() {
        Source source = new Source(INLINE, 10, 5);
        assertThat(source.anchorColumn()).isEqualTo(5);
    }

    @Test
    public void isSubstream() {
        Source source = new Source(INLINE, 10, 5);
        assertThat(source.isSubSource()).isTrue();
    }

    @Test
    public void originalLineAnchorLineIs1LineIs1() {
        Source source = new Source("abcdef", 1, 1);
        assertThat(source.originalLine()).isEqualTo(1);
    }

    @Test
    public void originalLineAnchorLineIs1LineMoreThan1() {
        Source source = new Source("abc\ndef", 1, 1);
        source.forward(4);
        assertThat(source.line()).isEqualTo(2);
        assertThat(source.originalLine()).isEqualTo(2);
    }

    @Test
    public void originalLineAnchorLineMoreThan1LineIs1() {
        Source source = new Source("abcdef", 2, 1);
        assertThat(source.originalLine()).isEqualTo(2);
    }

    @Test
    public void originalLineAnchorLineMoreThan1LineMoreThan1() {
        Source source = new Source("abc\nd\nef", 6, 1);
        source.forward(6);
        assertThat(source.line()).isEqualTo(3);
        assertThat(source.originalLine()).isEqualTo(8);
    }

    @Test
    public void originalColumnAnchorColumn1ColumnIs1() {
        Source source = new Source("abcdef", 1, 1);
        assertThat(source.originalColumn()).isEqualTo(1);
    }

    @Test
    public void originalColumnAnchorColumnIs1ColumnMoreThan1() {
        Source source = new Source("abcdef", 1, 1);
        source.forward(4);
        assertThat(source.column()).isEqualTo(5);
        assertThat(source.originalColumn()).isEqualTo(5);
    }

    @Test
    public void originalColumnAnchorColumnMoreThan1ColumnIs1() {
        Source source = new Source("abcdef", 1, 5);
        assertThat(source.originalColumn()).isEqualTo(5);
    }

    @Test
    public void originalColumnAnchorColumnMoreThan1ColumnMoreThan1() {
        Source source = new Source("abcdef", 1, 3);
        source.forward(4);
        assertThat(source.column()).isEqualTo(5);
        assertThat(source.originalColumn()).isEqualTo(7);
    }

    @Test
    public void originalColumnWhenLineGreaterThan1() {
        Source source = new Source("ab\ncdef", 3, 3);
        source.forward(4);
        assertThat(source.column()).isEqualTo(2);
        assertThat(source.originalColumn()).isEqualTo(2);
    }

    @Test
    public void source() {
        Source source = new Source(INLINE);
        assertThat(source.fullSource()).isEqualTo(INLINE);
    }

    @Test
    public void remaining() {
        Source source = new Source(INLINE);
        source.forward(8);
        assertThat(source.remaining()).isEqualTo("#id { color: red }");
    }

    @Test
    public void length() {
        Source source = new Source(INLINE);
        assertThat(source.length()).isEqualTo(INLINE.length());
    }

    @Test
    public void isEscaped() {
        Source source = new Source("abc\\\"abc");
        assertThat(source.isEscaped()).isFalse();
        source.forward(4);
        assertThat(source.isEscaped()).isTrue();
        source.next();
        assertThat(source.isEscaped()).isFalse();
    }

    @Test
    public void eof() {
        Source source = new Source("abc\n");
        source.next();
        source.next();
        source.next();
        source.next();
        source.next();
        assertThat(source.eof()).isTrue();
    }

    @Test
    public void current() {
        Source source = new Source("abc");
        assertThat(source.current()).isEqualTo('a');
        source.next();
        assertThat(source.current()).isEqualTo('b');
    }

    @Test
    public void nextAdvancesColumnAndLine() {
        Source source = new Source("a\nab");
        assertThat(source.line()).isEqualTo(1);
        assertThat(source.column()).isEqualTo(1);

        source.next();
        assertThat(source.line()).isEqualTo(1);
        assertThat(source.column()).isEqualTo(2);

        source.next();
        assertThat(source.line()).isEqualTo(2);
        assertThat(source.column()).isEqualTo(1);
    }

    @Test
    public void forward() {
        Source source = new Source("abc");
        source.forward(2);
        assertThat(source.index()).isEqualTo(2);
    }

    @Test
    public void forwardOutOfRange() {
        Source source = new Source("abc");
        exception.expect(IndexOutOfBoundsException.class);
        source.forward(10);
    }

    @Test
    public void skip() {
        Source source = new Source("abc");
        source.skip();
        assertThat(source.index()).isEqualTo(1);
    }

    @Test
    public void skipAtEnd() {
        Source source = new Source("abc");
        source.skip().skip().skip().skip();
        assertThat(source.eof()).isTrue();
    }

    @Test
    public void peek() {
        Source source = new Source("abc");
        assertThat(source.peek()).isEqualTo('b');
        assertThat(source.index()).isEqualTo(0);
    }

    @Test
    public void peekFarther() {
        Source source = new Source("abc");
        assertThat(source.peek(2)).isEqualTo('c');
        assertThat(source.index()).isEqualTo(0);
    }

    @Test
    public void peekOutOfRange() {
        Source source = new Source("abc");
        assertThat(source.peek(3)).isEqualTo(Source.NULL_CHAR);
    }

    @Test
    public void peekPrevious() {
        Source source = new Source("abc");
        source.next();
        assertThat(source.peekPrevious()).isEqualTo('a');
        assertThat(source.index()).isEqualTo(1);
    }

    @Test
    public void peekPreviousWhenAtBeginning() {
        Source source = new Source("abc");
        assertThat(source.peekPrevious()).isEqualTo(Source.NULL_CHAR);
    }

    @Test
    public void skipWhitespaceSpaces() {
        Source source = new Source("   abc");
        source.skipWhitepace();
        assertThat(source.index()).isEqualTo(3);
    }

    @Test
    public void skipWhitespaceNewlines() {
        Source source = new Source("\n\n\na\nabc");
        source.skipWhitepace();
        assertThat(source.index()).isEqualTo(3);
    }

    @Test
    public void skipWhitespaceMixed() {
        Source source = new Source(" \n\t \r\n abc");
        source.skipWhitepace();
        assertThat(source.index()).isEqualTo(7);
    }

    @Test
    public void optionalMatches() {
        Source source = new Source("abc123");
        assertThat(source.optional(Tokens.ALPHA).get()).isEqualTo('a');
        assertThat(source.index()).isEqualTo(1);
    }

    @Test
    public void optionalDoesntMatch() {
        Source source = new Source("abc123");
        assertThat(source.optional(Tokens.DIGIT).isPresent()).isFalse();
        assertThat(source.index()).isEqualTo(0);
    }

    @Test
    public void optionallyMatchesPresent() {
        Source source = new Source("abc123");
        assertThat(source.optionallyPresent(Tokens.ALPHA)).isTrue();
        assertThat(source.index()).isEqualTo(1);
    }

    @Test
    public void optionallyMatchesDoesntMatch() {
        Source source = new Source("abc123");
        assertThat(source.optionallyPresent(Tokens.DIGIT)).isFalse();
        assertThat(source.index()).isEqualTo(0);
    }

    @Test
    public void optionalFromEnumMatches() {
        Source source = new Source("abc123");
        assertThat(source.optionalFromEnum(SourceEnum.class).get()).isSameAs(SourceEnum.ONE);
        assertThat(source.index()).isEqualTo(1);
    }

    @Test
    public void optionalFromEnumDoesntMatch() {
        Source source = new Source("___abc");
        assertThat(source.optionalFromEnum(SourceEnum.class).isPresent()).isFalse();
        assertThat(source.index()).isEqualTo(0);
    }

    @Test
    public void constantEnumCaseInsensitiveMatchesCase() {
        Source source = new Source("abc  1");
        Optional<ConstantsCaseInsensitive> matched = source.optionalFromConstantEnum(ConstantsCaseInsensitive.class);
        assertThat(matched.get()).isSameAs(ConstantsCaseInsensitive.ONE);
        assertThat(source.index()).isEqualTo(3);
    }

    @Test
    public void constantEnumCaseInsensitiveMatchesUpperCase() {
        Source source = new Source("ABC  1");
        Optional<ConstantsCaseInsensitive> matched = source.optionalFromConstantEnum(ConstantsCaseInsensitive.class);
        assertThat(matched.get()).isSameAs(ConstantsCaseInsensitive.ONE);
        assertThat(source.index()).isEqualTo(3);
    }

    @Test
    public void optionalFromConstantEnumCaseInsensitiveDoesntMatch() {
        Source source = new Source("foobar abc");
        Optional<ConstantsCaseInsensitive> matched = source.optionalFromConstantEnum(ConstantsCaseInsensitive.class);
        assertThat(matched.isPresent()).isFalse();
        assertThat(source.index()).isEqualTo(0);
    }

    @Test
    public void constantEnumCaseSensitiveMatchesCase() {
        Source source = new Source("abc  1");
        Optional<ConstantsCaseSensitive> matched = source.optionalFromConstantEnum(ConstantsCaseSensitive.class);
        assertThat(matched.get()).isSameAs(ConstantsCaseSensitive.ONE);
        assertThat(source.index()).isEqualTo(3);
    }

    @Test
    public void constantEnumCaseSensitiveMatchesUpperCase() {
        Source source = new Source("ABC  1");
        Optional<ConstantsCaseSensitive> matched = source.optionalFromConstantEnum(ConstantsCaseSensitive.class);
        assertThat(matched.isPresent()).isFalse();
        assertThat(source.index()).isEqualTo(0);
    }

    @Test
    public void optionalFromConstantEnumCaseSensitiveDoesntMatch() {
        Source source = new Source("foobar abc");
        Optional<ConstantsCaseSensitive> matched = source.optionalFromConstantEnum(ConstantsCaseSensitive.class);
        assertThat(matched.isPresent()).isFalse();
        assertThat(source.index()).isEqualTo(0);
    }

    @Test
    public void expectMatches() {
        Source source = new Source("abc");
        source.expect(Tokens.ALPHA);
        // no exception
        assertThat(source.index()).isEqualTo(1);
    }

    @Test
    public void expectDoesntMatch() {
        Source source = new Source("abc");
        exception.expect(ParserException.class);
        source.expect(Tokens.DIGIT);
    }

    @Test
    public void until() {
        Source source = new Source("123___*\n\n123  abc} \n 123");
        String content = source.until(Tokens.CLOSE_BRACE);
        assertThat(content).isEqualTo("123___*\n\n123  abc");
        assertThat(source.index()).isEqualTo(17);
    }

    @Test
    public void untilWhenAtEof() {
        Source source = new Source("a}");
        source.next();
        source.next();
        String content = source.until(Tokens.CLOSE_BRACE);
        assertThat(source.eof()).isTrue();
        assertThat(content).isEmpty();
    }

    @Test
    public void untilSkipString() {
        Source source = new Source("abc\"111\"abc1");
        String contents = source.until(Tokens.DIGIT);
        assertThat(contents).isEqualTo("abc\"111\"abc");
        assertThat(source.index()).isEqualTo(11);
    }

    @Test
    public void untilNotPresent() {
        Source source = new Source("abc\n");
        String content = source.until(Tokens.DIGIT);
        assertThat(content).isEqualTo("abc\n");
        assertThat(source.eof());
    }

    @Test
    public void untilSkipEscaped() {
        Source source = new Source("abc\\}123}");
        String content = source.until(Tokens.CLOSE_BRACE);
        assertThat(content).isEqualTo("abc\\}123");
        assertThat(source.index()).isEqualTo(8);
    }

    @Test
    public void untilSkipParens() {
        Source source = new Source("abc(abcd12349;ad\"adada\") ; 123");
        String content = source.until(Tokens.SEMICOLON);
        assertThat(content).isEqualTo("abc(abcd12349;ad\"adada\") ");
        assertThat(source.index()).isEqualTo(25);
    }

    @Test
    public void chompMatches() {
        Source source = new Source("abcdefgABCDEFG1abc");
        String chomped = source.chomp(Tokens.ALPHA);
        assertThat(chomped).isEqualTo("abcdefgABCDEFG");
        assertThat(source.index()).isEqualTo(14);
    }

    @Test
    public void chompDoesntMatch() {
        Source source = new Source("abcdefgABCDEFG");
        String chomped = source.chomp(Tokens.ALPHA);
        assertThat(chomped).isEqualTo("abcdefgABCDEFG");
        assertThat(source.eof()).isTrue();
    }

    @Test
    public void chompEof() {
        Source source = new Source("a");
        source.next();
        String content = source.chomp(Tokens.ALPHA);
        assertThat(content).isEmpty();
        assertThat(source.eof()).isTrue();
    }

    @Test
    public void chompEnclosedDifferentDelimiters() {
        Source source = new Source("(abcdefg) 1");
        String chomped = source.chompEnclosedValue(Tokens.OPEN_PAREN, Tokens.CLOSE_PAREN);
        assertThat(chomped).isEqualTo("abcdefg");
        assertThat(source.index()).isEqualTo(9);
    }

    @Test
    public void chompEnclosedSameDelimiters() {
        Source source = new Source("1abcd_efg1");
        String chomped = source.chompEnclosedValue(Tokens.DIGIT, Tokens.DIGIT);
        assertThat(chomped).isEqualTo("abcd_efg");
        assertThat(source.index()).isEqualTo(10);
    }

    @Test
    public void chompEnclosedInDoubleQuotes() {
        Source source = new Source("\"abcd\\\"efg\" 1");
        String chomped = source.chompEnclosedValue(Tokens.DOUBLE_QUOTE, Tokens.DOUBLE_QUOTE);
        assertThat(chomped).isEqualTo("abcd\\\"efg");
        assertThat(source.index()).isEqualTo(11);
    }

    @Test
    public void chompEnclosedInSingleQuotes() {
        Source source = new Source("'abc\\'defg' 1");
        String chomped = source.chompEnclosedValue(Tokens.SINGLE_QUOTE, Tokens.SINGLE_QUOTE);
        assertThat(chomped).isEqualTo("abc\\'defg");
        assertThat(source.index()).isEqualTo(11);
    }

    @Test
    public void chompEnclosedWithNesting() {
        Source source = new Source("(abc(abc)ab\nc)");
        String chomped = source.chompEnclosedValue(Tokens.OPEN_PAREN, Tokens.CLOSE_PAREN);
        assertThat(chomped).isEqualTo("abc(abc)ab\nc");
        assertThat(source.index()).isEqualTo(14);
    }

    @Test
    public void chompEnclosedWithEscaped() {
        Source source = new Source("(abc(abc)ab\nc)");
        String chomped = source.chompEnclosedValue(Tokens.OPEN_PAREN, Tokens.CLOSE_PAREN);
        assertThat(chomped).isEqualTo("abc(abc)ab\nc");
        assertThat(source.index()).isEqualTo(14);
    }

    @Test
    public void chompEnclosedDoesntMatch() {
        Source source = new Source("(abc");

        exception.expect(ParserException.class);
        exception.expectMessage("Expected to find closing");
        source.chompEnclosedValue(Tokens.OPEN_PAREN, Tokens.CLOSE_PAREN);
    }

    @Test
    public void chompUnclosedUnmatchedNested() {
        Source source = new Source("(abc(abc)ab\nc");
        exception.expect(ParserException.class);
        exception.expectMessage("Expected to find closing");
        source.chompEnclosedValue(Tokens.OPEN_PAREN, Tokens.CLOSE_PAREN);
    }

    @Test
    public void collectComments() {
        Source source = new Source("/*abc*/ /____ abc");
        List<String> comments = source.collectComments().flushComments();
        assertThat(comments).hasSize(1);
        assertThat(comments.get(0)).isEqualTo("abc");
        assertThat(source.flushComments()).hasSize(0);
    }

    @Test
    public void collectCommentsMultiple() {
        Source source = new Source("/*abc*//*123*/....");
        List<String> comments = source.collectComments().flushComments();
        assertThat(comments).hasSize(2);
        assertThat(comments.get(0)).isEqualTo("abc");
        assertThat(comments.get(1)).isEqualTo("123");
    }

    @Test
    public void collectCommentsPrecedingWhitespace() {
        Source source = new Source("   \n /*abc*/");
        List<String> comments = source.collectComments().flushComments();
        assertThat(comments).hasSize(1);
        assertThat(comments.get(0)).isEqualTo("abc");
        assertThat(source.flushComments()).hasSize(0);
    }

    @Test
    public void collectCommentsWhitespaceInBetween() {
        Source source = new Source("/*abc*/\n\n  /**123\n* 123*/....");
        List<String> comments = source.collectComments().flushComments();
        assertThat(comments).hasSize(2);
        assertThat(comments.get(0)).isEqualTo("abc");
        assertThat(comments.get(1)).isEqualTo("*123\n* 123");
    }

    @Test
    public void collectCommentsNoSkipWhitespace() {
        Source source = new Source("/*abc*/ \n\n  /**123\n* 123*/....");
        List<String> comments = source.collectComments(false).flushComments();
        assertThat(comments).hasSize(1);
        assertThat(comments.get(0)).isEqualTo("abc");
    }

    @Test
    public void findCommentsTrueSkipWhitespaceTrue() {
        Source source = new Source("/*abc d*/ /____ abc");
        assertThat(source.findComments(true)).isTrue();
        assertThat(source.index()).isEqualTo(10);
    }

    @Test
    public void findCommentsFalseSkipWhitespaceTrue() {
        Source source = new Source(".a /*abc*/ /____ abc");
        assertThat(source.findComments(true)).isFalse();
        assertThat(source.index()).isEqualTo(0);
    }

    @Test
    public void findCommentsTrueSkipWhitespaceTrueBecauseOfWhitespace() {
        Source source = new Source("  .a /*abc*/ /____ abc");
        assertThat(source.findComments(true)).isTrue();
        assertThat(source.index()).isEqualTo(2);
    }

    @Test
    public void findCommentsTrueSkipWhitespaceOnlyWhitespace() {
        Source source = new Source("  ");
        assertThat(source.findComments(true)).isTrue();
        assertThat(source.index()).isEqualTo(2);
    }

    @Test
    public void findCommentsTrueSkipWhitespaceFalse() {
        Source source = new Source("/*abc d*/ /____ abc");
        assertThat(source.findComments(false)).isTrue();
        assertThat(source.index()).isEqualTo(9);
    }

    @Test
    public void findCommentsFalseSkipWhitespaceFalse() {
        Source source = new Source(" /*abc d*/ /____ abc");
        assertThat(source.findComments(false)).isFalse();
        assertThat(source.index()).isEqualTo(0);
    }

    @Test
    public void correctIndexPositionWhenCommentFound() {
        Source source = new Source("/*abc*/a");
        source.collectComments();
        assertThat(source.index()).isEqualTo(7);
    }

    @Test
    public void unclosedComment() {
        exception.expect(ParserException.class);
        exception.expectMessage(Message.MISSING_COMMENT_CLOSE);
        Source source = new Source("/*abc/a");
        source.collectComments();
    }

    @Test
    public void multilineComment() {
        Source source = new Source("/*abc\nabc\nanc    */abc");
        source.collectComments();
        assertThat(source.index()).isEqualTo(19);
    }

    @Test
    public void commentsWithEscapes() {
        Source source = new Source("/*ab*\\/c*/a");
        source.collectComments();
        assertThat(source.index()).isEqualTo(10);
    }

    @Test
    public void snapshot() {
        Source source = new Source("abc\n123");
        source.forward(6);
        Source.Snapshot snapshot = source.snapshot();

        assertThat(snapshot.line).isEqualTo(2);
        assertThat(snapshot.column).isEqualTo(3);
        assertThat(snapshot.index).isEqualTo(6);
        assertThat(snapshot.inString).isFalse();
    }

    @Test
    public void rollback() {
        Source source = new Source("ab\nc123");
        source.next();
        Source.Snapshot snapshot = source.snapshot();
        source.next();
        source.next();

        assertThat(source.line()).isEqualTo(2);
        assertThat(source.column()).isEqualTo(1);

        snapshot.rollback();
        assertThat(source.index()).isEqualTo(1);
        assertThat(source.line()).isEqualTo(1);
        assertThat(source.column()).isEqualTo(2);
    }

    @Test
    public void rollbackWithMessage() {
        Source source = new Source("abc");
        Source.Snapshot snapshot = source.snapshot();
        source.next();

        exception.expect(ParserException.class);
        snapshot.rollback(Message.EXPECTED_DECIMAL);
    }

    @Test
    public void readConstantMatches() {
        Source source = new Source("abc def ghi");
        boolean result = source.readConstant("abc");

        assertThat(result).isTrue();
        assertThat(source.index()).isEqualTo(3);
    }

    @Test
    public void readConstantMatchesMiddle() {
        Source source = new Source("abc def ghi");
        source.forward(4);
        boolean result = source.readConstant("def");

        assertThat(result).isTrue();
        assertThat(source.index()).isEqualTo(7);
    }

    @Test
    public void readConstantMatchesEnd() {
        Source source = new Source("abc def ghi");
        source.forward(8);
        boolean result = source.readConstant("ghi");

        assertThat(result).isTrue();
        assertThat(source.eof()).isTrue();
    }

    @Test
    public void readConstantDoesntMatchOutOfBounds() {
        Source source = new Source("abc");
        boolean result = source.readConstant("abcd");

        assertThat(result).isFalse();
        assertThat(source.index()).isEqualTo(0);
    }

    @Test
    public void readConstantDoesntMatchInBounds() {
        Source source = new Source("abc def abc");
        boolean result = source.readConstant("abcd");

        assertThat(result).isFalse();
        assertThat(source.index()).isEqualTo(0);
    }

    @Test
    public void readConstantCaseInsensitiveMatches() {
        Source source = new Source("ABC DEF GHI");
        boolean result = source.readConstantCaseInsensitive("abc");

        assertThat(result).isTrue();
        assertThat(source.index()).isEqualTo(3);
    }

    @Test
    public void readConstantCaseInsensitiveDoesntMatch() {
        Source source = new Source("ABC DEF GHI");
        boolean result = source.readConstantCaseInsensitive("DEF");

        assertThat(result).isFalse();
        assertThat(source.index()).isEqualTo(0);
    }

    @Test
    public void readConstantEof() {
        Source source = new Source("abc");
        source.forward(3);
        boolean result = source.readConstant("a");

        assertThat(result).isFalse();
    }

    @Test
    public void readIdentMatches() {
        Source source = new Source("keyword-one");
        assertThat(source.readIdent().get()).isEqualTo("keyword-one");
        assertThat(source.eof());
    }

    @Test
    public void readIdentMatchesUC() {
        Source source = new Source("KEYWORD-one");
        assertThat(source.readIdent().get()).isEqualTo("KEYWORD-one");
        assertThat(source.eof());
    }

    @Test
    public void readIdentMatchesStartsWithHyphen() {
        Source source = new Source("-keyword-one");
        assertThat(source.readIdent().get()).isEqualTo("-keyword-one");
        assertThat(source.eof());
    }

    @Test
    public void readIdentMatchesStartsWithUnderscore() {
        Source source = new Source("_keyword-one");
        assertThat(source.readIdent().get()).isEqualTo("_keyword-one");
        assertThat(source.eof());
    }

    @Test
    public void readIdentDoesntMatch() {
        Source source = new Source("111a");
        assertThat(source.readIdent().isPresent()).isFalse();
        assertThat(source.index()).isEqualTo(0);
    }

    @Test
    public void readIdentDoubleHyphen() {
        Source source = new Source("--abc");
        assertThat(source.readIdent().isPresent()).isFalse();
        assertThat(source.index()).isEqualTo(0);
    }

    @Test
    public void readIdentHypenDigit() {
        Source source = new Source("-1abc");
        assertThat(source.readIdent().isPresent()).isFalse();
        assertThat(source.index()).isEqualTo(0);
    }

    @Test
    public void readIdentHyphenOnly() {
        Source source = new Source("-");
        assertThat(source.readIdent().isPresent()).isFalse();
        assertThat(source.index()).isEqualTo(0);
    }

    @Test
    public void readIdentDoesntMatchOther() {
        Source source = new Source("$abc");
        assertThat(source.readIdent().isPresent()).isFalse();
        assertThat(source.index()).isEqualTo(0);
    }

    @Test
    public void readIdentLevel3Matches() {
        Source source = new Source("keyword-one");
        assertThat(source.readIdentLevel3().get()).isEqualTo("keyword-one");
        assertThat(source.eof());
    }

    @Test
    public void readIdentLevel3MatchesUC() {
        Source source = new Source("KEYWORD-one");
        assertThat(source.readIdentLevel3().get()).isEqualTo("KEYWORD-one");
        assertThat(source.eof());
    }

    @Test
    public void readIdentLevel3MatchesStartsWithHyphen() {
        Source source = new Source("-keyword-one");
        assertThat(source.readIdentLevel3().get()).isEqualTo("-keyword-one");
        assertThat(source.eof());
    }

    @Test
    public void readIdentLevel3MatchesStartsWithUnderscore() {
        Source source = new Source("_keyword-one");
        assertThat(source.readIdentLevel3().get()).isEqualTo("_keyword-one");
        assertThat(source.eof());
    }

    @Test
    public void readIdentLevel3DoubleHyphenMatches() {
        Source source = new Source("--custom-color");
        assertThat(source.readIdentLevel3().get()).isEqualTo("--custom-color");
        assertThat(source.eof());
    }

    @Test
    public void readIdentLevel3DoubleHyphenOnlyMatches() {
        Source source = new Source("--");
        assertThat(source.readIdentLevel3().get()).isEqualTo("--");
        assertThat(source.eof());
    }

    @Test
    public void readIdentLevel3DoesntMatch() {
        Source source = new Source("111a");
        assertThat(source.readIdentLevel3().isPresent()).isFalse();
        assertThat(source.index()).isEqualTo(0);
    }

    @Test
    public void readIdentLevel3HypenDigit() {
        Source source = new Source("-1abc");
        assertThat(source.readIdentLevel3().isPresent()).isFalse();
        assertThat(source.index()).isEqualTo(0);
    }

    @Test
    public void readIdentLevel3HyphenOnly() {
        Source source = new Source("-");
        assertThat(source.readIdentLevel3().isPresent()).isFalse();
        assertThat(source.index()).isEqualTo(0);
    }

    @Test
    public void readIdentLevel3DoesntMatchOther() {
        Source source = new Source("$abc");
        assertThat(source.readIdentLevel3().isPresent()).isFalse();
        assertThat(source.index()).isEqualTo(0);
    }

    @Test
    public void readStringAbsent() {
        Source source = new Source("abc");
        assertThat(source.readString().isPresent()).isFalse();

        source = new Source("123");
        assertThat(source.readString().isPresent()).isFalse();

        source = new Source("abc\"123\"");
        assertThat(source.readString().isPresent()).isFalse();

        source = new Source("abc'123'");
        assertThat(source.readString().isPresent()).isFalse();

        source = new Source(" \"abc");
        assertThat(source.readString().isPresent()).isFalse();

        source = new Source(" '123");
        assertThat(source.readString().isPresent()).isFalse();
    }

    @Test
    public void readStringSingleQuote() {
        Source source = new Source("'abc'123");
        Optional<String> matched = source.readString();

        assertThat(matched.get()).isEqualTo("abc");
        assertThat(source.index()).isEqualTo(5);
    }

    @Test
    public void readStringDoubleQuote() {
        Source source = new Source("\"abc\"123");
        Optional<String> matched = source.readString();

        assertThat(matched.get()).isEqualTo("abc");
        assertThat(source.index()).isEqualTo(5);
    }

    @Test
    public void readStringSingleQuoteWithInnerEscapes() {
        Source source = new Source("'ab\\'c'123");
        Optional<String> matched = source.readString();

        assertThat(matched.get()).isEqualTo("ab\\'c");
        assertThat(source.index()).isEqualTo(7);
    }

    @Test
    public void readStringDoubleQuoteWithInnerEscapes() {
        Source source = new Source("'ab\\\"c'123");
        Optional<String> matched = source.readString();

        assertThat(matched.get()).isEqualTo("ab\\\"c");
        assertThat(source.index()).isEqualTo(7);
    }

    @Test
    public void readStringMissingClosingSingleQuote() {
        Source source = new Source("'abc");

        exception.expect(ParserException.class);
        source.readString();
    }

    @Test
    public void readStringMissingClosingDoubleQuote() {
        Source source = new Source("'\"abc");

        exception.expect(ParserException.class);
        source.readString();
    }

    @Test
    public void readStringStartingSingleQuoteIsEcaped() {
        Source source = new Source("\\'abc");
        source.next();
        assertThat(source.readString().isPresent()).isFalse();
    }

    @Test
    public void readStringStartingDoubleQuoteIsEscaped() {
        Source source = new Source("\\\"abc");
        source.next();
        assertThat(source.readString().isPresent()).isFalse();
    }

    @Test
    public void toStringPositioning() {
        Source source = new Source("a\nbcd");
        source.next();
        source.next();
        source.next();
        assertThat(source.toString()).isEqualTo("a\nb\u00BBcd");
    }

    @Test
    public void toStringContextualShort() {
        Source source = new Source("abcabcabcabc");
        source.forward(4);
        assertThat(source.toStringContextual()).isEqualTo(source.toString());
    }

    @Test
    public void toStringContextualLong() {
        Source source = new Source(TemplatesHelper.longSource());
        source.forward(2586);
        assertThat(source.toStringContextual()).isEqualTo("(...snipped...)k {\n" +
            "  color: blue;\n" +
            "  text-decoration: none;\n" +
            "}\n" +
            "\n" +
            "a:hover, a:focus {\n" +
            "  color: \u00BBred;\n" +
            "  text-decoration: red;\n" +
            "}\n" +
            "\n" +
            ".test {\n" +
            "  color: #16ff2b;\n" +
            "}\n" +
            "\n" +
            "#test2 {\n" +
            "  ma(...snipped...)");
    }

    @Test
    public void toStringContextualLongAtBeginning() {
        Source source = new Source(TemplatesHelper.longSource());
        source.forward(50);
        assertThat(source.toStringContextual()).isEqualTo(".test {\n" +
            "  color: #16ff2b;\n" +
            "}\n" +
            "\n" +
            "#test2 {\n" +
            "  margin: 5p\u00BBx 10px;\n" +
            "  padding: 10px;\n" +
            "  border: 1px solid red;\n" +
            "  border-radius: 10px;\n" +
            "}(...snipped...)");
    }

    @Test
    public void toStringContextualLongAtEnd() {
        Source source = new Source(TemplatesHelper.longSource());
        source.forward(source.length() - 50);
        assertThat(source.toStringContextual()).isEqualTo("(...snipped...)us: 10px;\n" +
            "}\n" +
            "\n" +
            "a:link {\n" +
            "  color: blue;\n" +
            "  text-decoration: none;\n" +
            "}\n" +
            "\n" +
            "a:hover, a\u00BB:focus {\n" +
            "  color: red;\n" +
            "  text-decoration: red;\n" +
            "}\n");
    }

    @Test
    public void testInString() {
        Source source = new Source("a\"a\"a");
        source.forward(2);
        assertThat(source.inString()).isTrue();
    }

    @Test
    public void singleQuoteDoesntOpenStringInsideComments() {
        Source source = new Source("/*abc'*/abc");
        source.collectComments();
        assertThat(source.index()).isEqualTo(8);
        assertThat(source.inString()).isFalse();
    }

    @Test
    public void doubleQuoteDoesntOpenStringInsideComments() {
        Source source = new Source("/*abc\"\"\n\"*/abc");
        source.collectComments();
        assertThat(source.index()).isEqualTo(11);
        assertThat(source.inString()).isFalse();
    }

    @Test
    public void nestedStringsDoubleQuotes() {
        Source source = new Source("a\"b'c'd\"e");

        assertThat(source.inString()).isFalse(); //a

        source.next(); //"
        assertThat(source.inString()).isTrue();

        source.next();//b
        assertThat(source.inString()).isTrue();

        source.next();//'
        assertThat(source.inString()).isTrue();

        source.next();//c
        assertThat(source.inString()).isTrue();

        source.next();//'
        assertThat(source.inString()).isTrue();

        source.next();//d
        assertThat(source.inString()).isTrue();

        source.next();//"
        assertThat(source.inString()).isFalse();

        source.next();//e
        assertThat(source.inString()).isFalse();

        source.next();//(eof)
        assertThat(source.inString()).isFalse();
    }

    @Test
    public void nestedStringsSingleQuotes() {
        Source source = new Source("a'b\"c\"d'e");

        assertThat(source.inString()).isFalse(); //a

        source.next(); //"
        assertThat(source.inString()).isTrue();

        source.next();//b
        assertThat(source.inString()).isTrue();

        source.next();//'
        assertThat(source.inString()).isTrue();

        source.next();//c
        assertThat(source.inString()).isTrue();

        source.next();//'
        assertThat(source.inString()).isTrue();

        source.next();//d
        assertThat(source.inString()).isTrue();

        source.next();//"
        assertThat(source.inString()).isFalse();

        source.next();//e
        assertThat(source.inString()).isFalse();

        source.next();//(eof)
        assertThat(source.inString()).isFalse();
    }

    public enum SourceEnum implements TokenEnum {
        ONE(Tokens.ALPHA),
        TWO(Tokens.DIGIT);

        private final Token token;

        SourceEnum(Token token) {
            this.token = token;
        }

        @Override
        public Token token() {
            return token;
        }
    }

    public enum ConstantsCaseInsensitive implements ConstantEnum {
        ONE("abc"),
        TWO("123");

        private final String constant;

        ConstantsCaseInsensitive(String constant) {
            this.constant = constant;
        }

        @Override
        public String constant() {
            return constant;
        }

        @Override
        public boolean caseSensitive() {
            return false;
        }
    }

    public enum ConstantsCaseSensitive implements ConstantEnum {
        ONE("abc"),
        TWO("123");

        private final String constant;

        ConstantsCaseSensitive(String constant) {
            this.constant = constant;
        }

        @Override
        public String constant() {
            return constant;
        }

        @Override
        public boolean caseSensitive() {
            return true;
        }

    }
}
