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

import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.parser.ParserException;

/**
 * Responsible for handling errors, either from {@link ParserException}s or from syntax validator plugins.
 *
 * @author nmcwilliams
 */
public interface ErrorManager {
    /**
     * Reports an error based on a {@link ParserException}.
     *
     * @param level
     *     The {@link ErrorLevel}.
     * @param exception
     *     The exception that describes the error.
     */
    void report(ErrorLevel level, ParserException exception);

    /**
     * Reports an error message.
     *
     * @param level
     *     The {@link ErrorLevel}.
     * @param cause
     *     The {@link Syntax} unit that is the cause of the error (e.g., the unit that has failed validation).
     * @param message
     *     The error message.
     */
    void report(ErrorLevel level, Syntax cause, String message);

    /**
     * Gets the name of the source currently being parsed. If not available this may return null.
     *
     * @return The name of the source currently being parsed.
     */
    String getSourceName();
}
