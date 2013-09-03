/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.fail;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.collect.Iterables;
import com.salesforce.omakase.Message;
import com.salesforce.omakase.parser.token.Token;
import com.salesforce.omakase.parser.token.TokenEnum;
import com.salesforce.omakase.parser.token.Tokens;

/**
 * Unit tests for {@link Stream}.
 * 
 * @author nmcwilliams
 */
@SuppressWarnings("javadoc")
public class StreamTest {
    final String INLINE = ".class, #id { color: red }";

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
        fail("unimplemented");
    }

    @Test
    public void untilSkipString() {
        fail("unimplemented");
    }

    @Test
    public void untilNotPresent() {
        fail("unimplemented");
    }

    @Test
    public void untilSkipEscaped() {
        fail("unimplemented");
    }

    @Test
    public void chompEof() {
        fail("unimplemented");
    }

    @Test
    public void chompMatches() {
        fail("unimplemented");
    }

    @Test
    public void chompDoesntMatch() {
        fail("unimplemented");
    }

    @Test
    public void chompEnclosedDifferentDelimiters() {
        fail("unimplemented");
    }

    @Test
    public void chompEnclosedSameDelimiters() {
        fail("unimplemented");
    }

    @Test
    public void chompEnclosedInDoubleQuotes() {
        fail("unimplemented");
    }

    @Test
    public void chompEnclosedInSingleQuotes() {
        fail("unimplemented");
    }

    @Test
    public void chompEnclosedWithNesting() {
        fail("unimplemented");
    }

    @Test
    public void chompEnclosedWithEscaped() {
        fail("unimplemented");
    }

    @Test
    public void chompEnclosedDoesntMatch() {
        fail("unimplemented");
    }

    @Test
    public void collectComments() {
        Stream stream = new Stream("/*abc*/ ___ /* 123 */ ___");
        stream.forward(25);
        Iterable<String> comments = stream.flushComments();
        assertThat(comments).hasSize(2);
        assertThat(Iterables.get(comments, 0)).isEqualTo("abc");
        assertThat(Iterables.get(comments, 1)).isEqualTo(" 123 ");
        assertThat(stream.flushComments()).hasSize(0);
    }

    @Test
    public void correctIndexPositionWhenCommentFound() {
        Stream stream = new Stream("a/*abc*/a");
        stream.next();
        assertThat(stream.index()).isEqualTo(8);
    }

    @Test
    public void unclosedComment() {
        exception.expect(ParserException.class);
        exception.expectMessage(Message.MISSING_COMMENT_CLOSE.message());
        Stream stream = new Stream("a/*abc/a");
        stream.next();
    }

    @Test
    public void multilineComment() {
        Stream stream = new Stream("1/*abc\nabc\nanc    */abc");
        stream.next();
        assertThat(stream.index()).isEqualTo(20);
    }

    @Test
    public void commentsNotAllowed() {
        Stream stream = new Stream("a/*abc*/a");
        stream.rejectComments();

        exception.expect(ParserException.class);
        exception.expectMessage("Comments not allowed in this location");
        stream.next();
    }

    @Test
    public void commentsWithEscapes() {
        Stream stream = new Stream("a/*ab*\\/c*/a");
        stream.next();
        assertThat(stream.index()).isEqualTo(11);
    }

    @Test
    public void rollback() {
        Stream stream = new Stream("ab\nc123");
        stream.next();
        stream.snapshot();
        stream.next();
        stream.next();

        assertThat(stream.line()).isEqualTo(2);
        assertThat(stream.column()).isEqualTo(1);

        stream.rollback();
        assertThat(stream.index()).isEqualTo(1);
        assertThat(stream.line()).isEqualTo(1);
        assertThat(stream.column()).isEqualTo(2);
    }

    @Test
    public void rollbackNotAllowed() {
        Stream stream = new Stream("ab\nc123");
        exception.expect(IllegalStateException.class);
        stream.rollback();
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
        assertThat(stream.toString()).isEqualTo("a\nb»cd");
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
