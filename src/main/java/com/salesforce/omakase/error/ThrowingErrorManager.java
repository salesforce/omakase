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
    private Logger logger;

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
            if (logger == null) logger = LoggerFactory.getLogger(ErrorManager.class);
            logger.warn(ErrorUtils.format(exception.getMessage()), exception);
        }
    }

    @Override
    public void report(ErrorLevel level, Syntax cause, String message) {
        switch (level) {
        case FATAL:
            throw new FatalException(ErrorUtils.format(cause, sourceName, message));
        case WARNING:
            if (logger == null) logger = LoggerFactory.getLogger(ErrorManager.class);
            logger.warn(ErrorUtils.format(cause, sourceName, message));
        }
    }

    @Override
    public String getSourceName() {
        return sourceName;
    }
}
