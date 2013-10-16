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

    private final String sourceName;

    /** Creates a new {@link ThrowingErrorManager} instance with no given name. */
    public ThrowingErrorManager() {
        this(null);
    }

    /**
     * Creates a new {@link ThrowingErrorManager} instance with the given name.
     * <p/>
     * The name is used to provide more meaningful information on what CSS file or resource caused the problem.
     *
     * @param sourceName
     *     Name of the CSS file, to be used for error reporting.
     */
    public ThrowingErrorManager(String sourceName) {
        this.sourceName = sourceName;
    }

    @Override
    public void report(ErrorLevel level, ParserException exception) {
        switch (level) {
        case FATAL:
            throw new FatalException(ErrorUtils.format(exception.getMessage()), exception);
        case WARNING:
            logger.warn(ErrorUtils.format(exception.getMessage()), exception);
        }
    }

    @Override
    public void report(ErrorLevel level, Syntax cause, String message) {
        switch (level) {
        case FATAL:
            throw new FatalException(ErrorUtils.format(sourceName, message, cause));
        case WARNING:
            logger.warn(ErrorUtils.format(sourceName, message, cause));
        }
    }
}
