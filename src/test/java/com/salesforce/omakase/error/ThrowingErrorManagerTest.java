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

package com.salesforce.omakase.error;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.salesforce.omakase.ast.selector.ClassSelector;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.Source;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.LoggerFactory;

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
        new ThrowingErrorManager().report(ErrorLevel.FATAL, new ParserException(new Source(""), "e"));
    }

    @Test
    public void fatalWithStringMessage() {
        ClassSelector s = new ClassSelector(5, 2, "cs");
        exception.expect(FatalException.class);
        exception.expectMessage("Omakase CSS Parser - message:\n" +
            "at line 5, column 2, caused by\n" +
            ".cs (class-selector)");
        new ThrowingErrorManager().report(ErrorLevel.FATAL, s, "message");
    }

    @Test
    public void fatalWithStringAndSourceName() {
        ClassSelector s = new ClassSelector(5, 2, "cs");
        exception.expect(FatalException.class);
        exception.expectMessage("Omakase CSS Parser - message:\n" +
            "at line 5, column 2 in source /css/source.css, caused by\n" +
            ".cs (class-selector)");
        new ThrowingErrorManager("/css/source.css").report(ErrorLevel.FATAL, s, "message");
    }

    @Test
    public void warningNoException() {
        Logger root = (Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        Level original = root.getLevel();
        root.setLevel(Level.ERROR);
        new ThrowingErrorManager().report(ErrorLevel.WARNING, new ParserException(new Source(""), "e"));
        root.setLevel(original);
        // no exception
    }
}
