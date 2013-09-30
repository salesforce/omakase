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

package com.salesforce.omakase.error;

import com.salesforce.omakase.ast.selector.ClassSelector;
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
    public void fatalWithStringMessage() {
        ClassSelector s = new ClassSelector(5, 2, "cs");
        exception.expect(FatalException.class);
        exception.expectMessage("Omakase CSS Parser Validation Problem - message:\n" +
            "at line 5, column 2, caused by\n" +
            "ClassSelector {\n" +
            "  abstract: {line=5, column=2}\n" +
            "  name: cs\n" +
            "}");
        new ThrowingErrorManager().report(ErrorLevel.FATAL, s, "message");
    }

    @Test
    public void fatalWithStringAndSourceName() {
        ClassSelector s = new ClassSelector(5, 2, "cs");
        exception.expect(FatalException.class);
        exception.expectMessage("Omakase CSS Parser Validation Problem - message:\n" +
            "at line 5, column 2 in source /css/source.css, caused by\n" +
            "ClassSelector {\n" +
            "  abstract: {line=5, column=2}\n" +
            "  name: cs\n" +
            "}");
        new ThrowingErrorManager("/css/source.css").report(ErrorLevel.FATAL, s, "message");
    }

    @Test
    public void warningNoException() {
        new ThrowingErrorManager().report(ErrorLevel.WARNING, new ParserException(new Stream(""), "e"));
        // no exception
    }
}
