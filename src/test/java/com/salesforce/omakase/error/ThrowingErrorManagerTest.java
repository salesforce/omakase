/**
 * ADD LICENSE
 */
package com.salesforce.omakase.error;

import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.Stream;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Unit tests for {@link ThrowingErrorManager}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class ThrowingErrorManagerTest {
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void fatalThrowsException() {
        exception.expect(FatalException.class);
        exception.expectMessage("e");
        new ThrowingErrorManager().report(ErrorLevel.FATAL, new ParserException(new Stream(""), "e"));
    }

    @Test
    public void warningNoException() {
        new ThrowingErrorManager().report(ErrorLevel.WARNING, new ParserException(new Stream(""), "e"));
        // no exception
    }
}
