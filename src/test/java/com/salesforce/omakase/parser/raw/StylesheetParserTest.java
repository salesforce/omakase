/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser.raw;

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.salesforce.omakase.ast.Commentable;
import com.salesforce.omakase.broadcaster.QueryableBroadcaster;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.Stream;

/**
 * Unit tests for {@link StylesheetParser}.
 * 
 * @author nmcwilliams
 */
@SuppressWarnings("javadoc")
public class StylesheetParserTest {
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void testEof() {
        exception.expect(ParserException.class);
        exception.expectMessage("Extraneous text found at the end of the source");
        new StylesheetParser().parse(new Stream(".abc{color:red}   `"), new QueryableBroadcaster());
    }

    @Test
    public void commentsAfterLastSelectorShouldBeIgnored() {
        final QueryableBroadcaster broadcaster = new QueryableBroadcaster();
        new StylesheetParser().parse(new Stream(".class, .class /*comment*/ { color: red }"), broadcaster);
        for (Commentable c : broadcaster.filter(Commentable.class)) {
            assertThat(c.comments()).describedAs(c.toString()).isEmpty();
        }
    }

    @Test
    public void commentsAfterLastSemicolonShouldBeIgnored() {
        final QueryableBroadcaster broadcaster = new QueryableBroadcaster();
        new StylesheetParser().parse(new Stream(".class { color: red; /*comment*/ }"), broadcaster);
        for (Commentable c : broadcaster.filter(Commentable.class)) {
            assertThat(c.comments()).describedAs(c.toString()).isEmpty();
        }
    }
}
