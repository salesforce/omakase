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
import com.salesforce.omakase.broadcast.emitter.SubscriptionException;
import com.salesforce.omakase.parser.ParserException;

import java.util.ArrayList;
import java.util.List;

/**
 * The default Omakase {@link ErrorManager}.
 *
 * @author nmcwilliams
 */
public class DefaultErrorManager implements ErrorManager {
    private final String sourceName;

    private final List<String> errors = new ArrayList<>();
    private final List<String> warnings = new ArrayList<>();
    private final List<SubscriptionException> exceptions = new ArrayList<>();

    private boolean rethrow;
    private boolean showWarnings;

    /**
     * Creates a new {@link DefaultErrorManager} instance with no given name.
     */
    public DefaultErrorManager() {
        this(null);
    }

    /**
     * Creates a new {@link DefaultErrorManager} instance with the given name.
     * <p>
     * The name is used to provide more meaningful information on what CSS file or resource caused the problem.
     *
     * @param sourceName
     *     Name of the CSS file, to be used for error reporting.
     */
    public DefaultErrorManager(String sourceName) {
        this.sourceName = sourceName;
        this.rethrow = true;
        this.showWarnings = true;
    }

    /**
     * Specifies whether reported exceptions should be rethrown as encountered, instead of reported as another error
     * message (default true).
     *
     * @param rethrow
     *     Specify true to rethrow reported Exceptions.
     *
     * @return this, for chaining.
     */
    public DefaultErrorManager rethrow(boolean rethrow) {
        this.rethrow = rethrow;
        return this;
    }

    /**
     * Specifies whether to include warning messages in the summary (default true).
     *
     * @param showWarnings
     *     Specify true to include warning messages.
     *
     * @return this, for chaining.
     */
    public DefaultErrorManager warnings(boolean showWarnings) {
        this.showWarnings = showWarnings;
        return this;
    }

    @Override
    public String getSourceName() {
        return sourceName;
    }

    @Override
    public void report(ErrorLevel level, Syntax cause, String message) {
        switch (level) {
        case FATAL:
            errors.add(ErrorUtils.format(sourceName, cause, message));
            break;
        case WARNING:
            warnings.add(ErrorUtils.format(sourceName, cause, message));
            break;
        }
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void report(ParserException exception) {
        if (rethrow) {
            throw exception;
        }
        errors.add(ErrorUtils.format(sourceName, exception.getMessage()));
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void report(SubscriptionException exception) {
        if (rethrow) {
            throw exception;
        }
        this.exceptions.add(exception);
    }

    @Override
    public boolean hasErrors() {
        return !errors.isEmpty() || !exceptions.isEmpty() || (showWarnings && !warnings.isEmpty());
    }

    @Override
    public boolean autoSummarize() {
        return true;
    }

    @Override
    public String summarize() {
        StringBuilder builder = new StringBuilder(256);

        // handle subscription exceptions
        if (!exceptions.isEmpty()) {
            builder.append("Omakase CSS Parser - Plugin Errors\n");
            builder.append("-----------------------------------");

            for (SubscriptionException exception : exceptions) {
                builder.append("\n");
                builder.append(exception.getMessage());
                Throwable cause = exception.getCause();
                while (cause != null) {
                    builder.append("\n");
                    builder.append(cause);
                    cause = cause.getCause();
                }
                builder.append("\n");
            }
        }

        // handle errors
        if (!errors.isEmpty()) {
            if (!exceptions.isEmpty()) {
                builder.append("\n");
            }
            builder.append("Omakase CSS Parser - Errors\n");
            builder.append("----------------------------");

            for (String error : errors) {
                builder.append("\n");
                builder.append(error);
                builder.append("\n");
            }
        }

        // handle warnings
        if (showWarnings && !warnings.isEmpty()) {
            if (!exceptions.isEmpty() || !errors.isEmpty()) {
                builder.append("\n");
            }
            builder.append("Omakase CSS Parser - Warnings\n");
            builder.append("------------------------------");

            for (String warning : warnings) {
                builder.append("\n");
                builder.append(warning);
                builder.append("\n");
            }
        }

        return builder.toString();
    }
}
