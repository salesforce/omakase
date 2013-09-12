/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

import com.salesforce.omakase.Message;
import com.salesforce.omakase.parser.token.Token;
import com.salesforce.omakase.parser.token.TokenEnum;
import com.salesforce.omakase.parser.token.Tokens;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link Stream}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings({"JavaDoc", "SpellCheckingInspection"})
public class StreamTest {
    static final String INLINE = ".class, #id { color: red }";

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void line() {
        Stream stream = new Stream(INLINE);
        assertThat(stream.line()).isEqualTo(1);
    }

    @Test
    public void column() {
        Stream stream = new Stream(INLINE);
        assertThat(stream.column()).isEqualTo(1);
    }

    @Test
    public void index() {
        Stream stream = new Stream(INLINE);
        assertThat(stream.index()).isEqualTo(0);
        stream.next();
        assertThat(stream.index()).isEqualTo(1);
    }

    @Test
    public void anchorLine() {
        Stream stream = new Stream(INLINE, 10, 5);
        assertThat(stream.anchorLine()).isEqualTo(10);
    }

    @Test
    public void anchorColumn() {
        Stream stream = new Stream(INLINE, 10, 5);
        assertThat(stream.anchorColumn()).isEqualTo(5);
    }

    @Test
    public void isSubstream() {
        Stream stream = new Stream(INLINE, 10, 5);
        assertThat(stream.isSubStream()).isTrue();
    }

    @Test
    public void source() {
        Stream stream = new Stream(INLINE);
        assertThat(stream.source()).isEqualTo(INLINE);
    }

    @Test
    public void remaining() {
        Stream stream = new Stream(INLINE);
        stream.forward(8);
        assertThat(stream.remaining()).isEqualTo("#id { color: red }");
    }

    @Test
    public void length() {
        Stream stream = new Stream(INLINE);
        assertThat(stream.length()).isEqualTo(INLINE.length());
    }

    @Test
    public void isEscaped() {
        Stream stream = new Stream("abc\\\"abc");
        assertThat(stream.isEscaped()).isFalse();
        stream.forward(4);
        assertThat(stream.isEscaped()).isTrue();
        stream.next();
        assertThat(stream.isEscaped()).isFalse();
    }

    @Test
    public void eof() {
        Stream stream = new Stream("abc\n");
        stream.next();
        stream.next();
        stream.next();
        stream.next();
        stream.next();
        assertThat(stream.eof()).isTrue();
    }

    @Test
    public void current() {
        Stream stream = new Stream("abc");
        assertThat(stream.current()).isEqualTo('a');
        stream.next();
        assertThat(stream.current()).isEqualTo('b');
    }

    @Test
    public void nextAdvancesColumnAndLine() {
        Stream stream = new Stream("a\nab");
        assertThat(stream.line()).isEqualTo(1);
        assertThat(stream.column()).isEqualTo(1);

        stream.next();
        assertThat(stream.line()).isEqualTo(1);
        assertThat(stream.column()).isEqualTo(2);

        stream.next();
        assertThat(stream.line()).isEqualTo(2);
        assertThat(stream.column()).isEqualTo(1);
    }

    @Test
    public void forward() {
        Stream stream = new Stream("abc");
        stream.forward(2);
        assertThat(stream.index()).isEqualTo(2);
    }

    @Test
    public void forwardOutOfRange() {
        Stream stream = new Stream("abc");
        exception.expect(IllegalArgumentException.class);
        stream.forward(10);
    }

    @Test
    public void peek() {
        Stream stream = new Stream("abc");
        assertThat(stream.peek()).isEqualTo('b');
        assertThat(stream.index()).isEqualTo(0);
    }

    @Test
    public void peekFarther() {
        Stream stream = new Stream("abc");
        assertThat(stream.peek(2)).isEqualTo('c');
        assertThat(stream.index()).isEqualTo(0);
    }

    @Test
    public void peekOutOfRange() {
        Stream stream = new Stream("abc");
        assertThat(stream.peek(3)).isNull();
    }

    @Test
    public void peekPrevious() {
        Stream stream = new Stream("abc");
        stream.next();
        assertThat(stream.peekPrevious()).isEqualTo('a');
        assertThat(stream.index()).isEqualTo(1);
    }

    @Test
    public void peekPreviousWhenAtBeginning() {
        Stream stream = new Stream("abc");
        assertThat(stream.peekPrevious()).isNull();
    }

    @Test
    public void skipWhitespaceSpaces() {
        Stream stream = new Stream("   abc");
        stream.skipWhitepace();
        assertThat(stream.index()).isEqualTo(3);
    }

    @Test
    public void skipWhitespaceNewlines() {
        Stream stream = new Stream("\n\n\na\nabc");
        stream.skipWhitepace();
        assertThat(stream.index()).isEqualTo(3);
    }

    @Test
    public void skipWhitespaceMixed() {
        Stream stream = new Stream(" \n\t \r\n abc");
        stream.skipWhitepace();
        assertThat(stream.index()).isEqualTo(7);
    }

    @Test
    public void optionalMatches() {
        Stream stream = new Stream("abc123");
        assertThat(stream.optional(Tokens.ALPHA).get()).isEqualTo('a');
        assertThat(stream.index()).isEqualTo(1);
    }

    @Test
    public void optionalDoesntMatch() {
        Stream stream = new Stream("abc123");
        assertThat(stream.optional(Tokens.DIGIT).isPresent()).isFalse();
        assertThat(stream.index()).isEqualTo(0);
    }

    @Test
    public void optionallyMatchesPresent() {
        Stream stream = new Stream("abc123");
        assertThat(stream.optionallyPresent(Tokens.ALPHA)).isTrue();
        assertThat(stream.index()).isEqualTo(1);
    }

    @Test
    public void optionallyMatchesDoesntMatch() {
        Stream stream = new Stream("abc123");
        assertThat(stream.optionallyPresent(Tokens.DIGIT)).isFalse();
        assertThat(stream.index()).isEqualTo(0);
    }

    @Test
    public void optionalFromEnumMatches() {
        Stream stream = new Stream("abc123");
        assertThat(stream.optionalFromEnum(StreamEnum.class).get()).isSameAs(StreamEnum.ONE);
        assertThat(stream.index()).isEqualTo(1);
    }

    @Test
    public void optionalFromEnumDoesntMatch() {
        Stream stream = new Stream("___abc");
        assertThat(stream.optionalFromEnum(StreamEnum.class).isPresent()).isFalse();
        assertThat(stream.index()).isEqualTo(0);
    }

    @Test
    public void expectMatches() {
        Stream stream = new Stream("abc");
        stream.expect(Tokens.ALPHA);
        // no exception
        assertThat(stream.index()).isEqualTo(1);
    }

    @Test
    public void expectDoesntMatch() {
        Stream stream = new Stream("abc");
        exception.expect(ParserException.class);
        stream.expect(Tokens.DIGIT);
    }

    @Test
    public void until() {
        Stream stream = new Stream("123___*\n\n123  abc} \n 123");
        String content = stream.until(Tokens.CLOSE_BRACKET);
        assertThat(content).isEqualTo("123___*\n\n123  abc");
        assertThat(stream.index()).isEqualTo(17);
    }

    @Test
    public void untilWhenAtEof() {
        Stream stream = new Stream("a}");
        stream.next();
        stream.next();
        String content = stream.until(Tokens.CLOSE_BRACKET);
        assertThat(stream.eof()).isTrue();
        assertThat(content).isEmpty();
    }

    @Test
    public void untilSkipString() {
        Stream stream = new Stream("abc\"111\"abc1");
        String contents = stream.until(Tokens.DIGIT);
        assertThat(contents).isEqualTo("abc\"111\"abc");
        assertThat(stream.index()).isEqualTo(11);
    }

    @Test
    public void untilNotPresent() {
        Stream stream = new Stream("abc\n");
        String content = stream.until(Tokens.DIGIT);
        assertThat(content).isEqualTo("abc\n");
        assertThat(stream.eof());
    }

    @Test
    public void untilSkipEscaped() {
        Stream stream = new Stream("abc\\}123}");
        String content = stream.until(Tokens.CLOSE_BRACKET);
        assertThat(content).isEqualTo("abc\\}123");
        assertThat(stream.index()).isEqualTo(8);
    }

    @Test
    public void untilSkipParens() {
        Stream stream = new Stream("abc(abcd12349;ad\"adada\") ; 123");
        String content = stream.until(Tokens.SEMICOLON);
        assertThat(content).isEqualTo("abc(abcd12349;ad\"adada\") ");
        assertThat(stream.index()).isEqualTo(25);
    }

    @Test
    public void chompMatches() {
        Stream stream = new Stream("abcdefgABCDEFG1abc");
        String chomped = stream.chomp(Tokens.ALPHA);
        assertThat(chomped).isEqualTo("abcdefgABCDEFG");
        assertThat(stream.index()).isEqualTo(14);
    }

    @Test
    public void chompDoesntMatch() {
        Stream stream = new Stream("abcdefgABCDEFG");
        String chomped = stream.chomp(Tokens.ALPHA);
        assertThat(chomped).isEqualTo("abcdefgABCDEFG");
        assertThat(stream.eof()).isTrue();
    }

    @Test
    public void chompEof() {
        Stream stream = new Stream("a");
        stream.next();
        String content = stream.chomp(Tokens.ALPHA);
        assertThat(content).isEmpty();
        assertThat(stream.eof()).isTrue();
    }

    @Test
    public void chompEnclosedDifferentDelimiters() {
        Stream stream = new Stream("(abcdefg) 1");
        String chomped = stream.chompEnclosedValue(Tokens.OPEN_PAREN, Tokens.CLOSE_PAREN);
        assertThat(chomped).isEqualTo("abcdefg");
        assertThat(stream.index()).isEqualTo(9);
    }

    @Test
    public void chompEnclosedSameDelimiters() {
        Stream stream = new Stream("1abcd_efg1");
        String chomped = stream.chompEnclosedValue(Tokens.DIGIT, Tokens.DIGIT);
        assertThat(chomped).isEqualTo("abcd_efg");
        assertThat(stream.index()).isEqualTo(10);
    }

    @Test
    public void chompEnclosedInDoubleQuotes() {
        Stream stream = new Stream("\"abcd\\\"efg\" 1");
        String chomped = stream.chompEnclosedValue(Tokens.DOUBLE_QUOTE, Tokens.DOUBLE_QUOTE);
        assertThat(chomped).isEqualTo("abcd\\\"efg");
        assertThat(stream.index()).isEqualTo(11);
    }

    @Test
    public void chompEnclosedInSingleQuotes() {
        Stream stream = new Stream("'abc\\'defg' 1");
        String chomped = stream.chompEnclosedValue(Tokens.SINGLE_QUOTE, Tokens.SINGLE_QUOTE);
        assertThat(chomped).isEqualTo("abc\\'defg");
        assertThat(stream.index()).isEqualTo(11);
    }

    @Test
    public void chompEnclosedWithNesting() {
        Stream stream = new Stream("(abc(abc)ab\nc)");
        String chomped = stream.chompEnclosedValue(Tokens.OPEN_PAREN, Tokens.CLOSE_PAREN);
        assertThat(chomped).isEqualTo("abc(abc)ab\nc");
        assertThat(stream.index()).isEqualTo(14);
    }

    @Test
    public void chompEnclosedWithEscaped() {
        Stream stream = new Stream("(abc(abc)ab\nc)");
        String chomped = stream.chompEnclosedValue(Tokens.OPEN_PAREN, Tokens.CLOSE_PAREN);
        assertThat(chomped).isEqualTo("abc(abc)ab\nc");
        assertThat(stream.index()).isEqualTo(14);
    }

    @Test
    public void chompEnclosedDoesntMatch() {
        Stream stream = new Stream("(abc");

        exception.expect(ParserException.class);
        exception.expectMessage("Expected to find closing");
        stream.chompEnclosedValue(Tokens.OPEN_PAREN, Tokens.CLOSE_PAREN);
    }

    @Test
    public void chompUnclosedUnmatchedNested() {
        Stream stream = new Stream("(abc(abc)ab\nc");
        exception.expect(ParserException.class);
        exception.expectMessage("Expected to find closing");
        stream.chompEnclosedValue(Tokens.OPEN_PAREN, Tokens.CLOSE_PAREN);
    }

    @Test
    public void collectComments() {
        Stream stream = new Stream("/*abc*/ /____ abc");
        List<String> comments = stream.collectComments().flushComments();
        assertThat(comments).hasSize(1);
        assertThat(comments.get(0)).isEqualTo("abc");
        assertThat(stream.flushComments()).hasSize(0);
    }

    @Test
    public void collectCommentsMultiple() {
        Stream stream = new Stream("/*abc*//*123*/....");
        List<String> comments = stream.collectComments().flushComments();
        assertThat(comments).hasSize(2);
        assertThat(comments.get(0)).isEqualTo("abc");
        assertThat(comments.get(1)).isEqualTo("123");
    }

    @Test
    public void collectCommentsPrecedingWhitespace() {
        Stream stream = new Stream("   \n /*abc*/");
        List<String> comments = stream.collectComments().flushComments();
        assertThat(comments).hasSize(1);
        assertThat(comments.get(0)).isEqualTo("abc");
        assertThat(stream.flushComments()).hasSize(0);
    }

    @Test
    public void collectCommentsWhitespaceInBetween() {
        Stream stream = new Stream("/*abc*/\n\n  /**123\n* 123*/....");
        List<String> comments = stream.collectComments().flushComments();
        assertThat(comments).hasSize(2);
        assertThat(comments.get(0)).isEqualTo("abc");
        assertThat(comments.get(1)).isEqualTo("*123\n* 123");
    }

    @Test
    public void collectCommentsNoSkipWhitespace() {
        Stream stream = new Stream("/*abc*/ \n\n  /**123\n* 123*/....");
        List<String> comments = stream.collectComments(false).flushComments();
        assertThat(comments).hasSize(1);
        assertThat(comments.get(0)).isEqualTo("abc");
    }

    @Test
    public void correctIndexPositionWhenCommentFound() {
        Stream stream = new Stream("/*abc*/a");
        stream.collectComments();
        assertThat(stream.index()).isEqualTo(7);
    }

    @Test
    public void unclosedComment() {
        exception.expect(ParserException.class);
        exception.expectMessage(Message.MISSING_COMMENT_CLOSE.message());
        Stream stream = new Stream("/*abc/a");
        stream.collectComments();
    }

    @Test
    public void multilineComment() {
        Stream stream = new Stream("/*abc\nabc\nanc    */abc");
        stream.collectComments();
        assertThat(stream.index()).isEqualTo(19);
    }

    @Test
    public void commentsWithEscapes() {
        Stream stream = new Stream("/*ab*\\/c*/a");
        stream.collectComments();
        assertThat(stream.index()).isEqualTo(10);
    }

    @Test
    public void snapshot() {
        Stream stream = new Stream("abc\n123");
        stream.forward(6);
        Stream.Snapshot snapshot = stream.snapshot();

        assertThat(snapshot.line).isEqualTo(2);
        assertThat(snapshot.column).isEqualTo(3);
        assertThat(snapshot.index).isEqualTo(6);
        assertThat(snapshot.inString).isFalse();
    }

    @Test
    public void rollback() {
        Stream stream = new Stream("ab\nc123");
        stream.next();
        Stream.Snapshot snapshot = stream.snapshot();
        stream.next();
        stream.next();

        assertThat(stream.line()).isEqualTo(2);
        assertThat(stream.column()).isEqualTo(1);

        snapshot.rollback();
        assertThat(stream.index()).isEqualTo(1);
        assertThat(stream.line()).isEqualTo(1);
        assertThat(stream.column()).isEqualTo(2);
    }

    @Test
    public void rollbackWithMessage() {
        Stream stream = new Stream("abc");
        Stream.Snapshot snapshot = stream.snapshot();
        stream.next();

        exception.expect(ParserException.class);
        snapshot.rollback(Message.EXPECTED_DECIMAL);
    }

    @Test
    public void readIdentMatches() {
        Stream stream = new Stream("keyword-one");
        assertThat(stream.readIdent().get()).isEqualTo("keyword-one");
    }

    @Test
    public void readIdentDoesntMatch() {
        Stream stream = new Stream("111a");
        assertThat(stream.readIdent().isPresent()).isFalse();
    }

    @Test
    public void readIdentDoubleHyphen() {
        Stream stream = new Stream("--abc");
        assertThat(stream.readIdent().isPresent()).isFalse();
    }

    @Test
    public void readIdentHypenDigit() {
        Stream stream = new Stream("-1abc");
        assertThat(stream.readIdent().isPresent()).isFalse();
    }

    @Test
    public void toStringPositioning() {
        Stream stream = new Stream("a\nbcd");
        stream.next();
        stream.next();
        stream.next();
        assertThat(stream.toString()).isEqualTo("a\nb\u00BBcd");
    }

    public enum StreamEnum implements TokenEnum<StreamEnum> {
        ONE(Tokens.ALPHA),
        TWO(Tokens.DIGIT);

        private final Token token;

        StreamEnum(Token token) {
            this.token = token;
        }

        @Override
        public Token token() {
            return token;
        }
    }
}
