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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An {@link ErrorManager} that will throw an exception on the first reported {@link ErrorLevel#FATAL} error. Errors of level
 * {@link ErrorLevel#WARNING} will be logged.
 *
 * @author nmcwilliams
 */
public final class ThrowingErrorManager implements ErrorManager {
    private static final Logger logger = LoggerFactory.getLogger(ErrorManager.class);

    @Override
    public void report(ErrorLevel level, ParserException exception) {
        switch (level) {
        case FATAL:
            throw new FatalException(format(exception.getMessage()), exception);
        case WARNING:
            logger.warn(format(exception.getMessage()), exception);
        }
    }

    @Override
    public void report(ErrorLevel level, Syntax cause, String message) {
        switch (level) {
        case FATAL:
            throw new FatalException(format(message, cause));
        case WARNING:
            logger.warn(format(message, cause));
        }
    }

    /** formats the error message */
    private static String format(String message) {
        return String.format("Omakase CSS Parser - %s", message);
    }

    private static String format(String message, Syntax cause) {
        return String.format("Omakase CSS Parser - %s \n cause: %s \n at line %s, column %s.",
            message,
            cause.toString(),
            cause.line(),
            cause.column());
    }
}
