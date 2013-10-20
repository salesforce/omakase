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
import com.salesforce.omakase.parser.Source;

/**
 * Utils for working with errors.
 *
 * @author nmcwilliams
 */
public final class ErrorUtils {
    private ErrorUtils() {}

    /**
     * Formats an error message.
     *
     * @param message
     *     The error message.
     *
     * @return The formatted message.
     */
    public static String format(String message) {
        return String.format("Omakase CSS Parser - %s", message);
    }

    /**
     * formats an error message.
     *
     * @param message
     *     The error message.
     * @param cause
     *     The {@link Syntax} that has the problem.
     *
     * @return The formatted message.
     */
    public static String format(String message, Syntax cause) {
        return format(null, message, cause);
    }

    /**
     * Formats an error message.
     *
     * @param sourceName
     *     Name of the resource (e.g., file name) that has the problem.
     * @param message
     *     The error message.
     * @param cause
     *     The {@link Syntax} that has the problem.
     *
     * @return The formatted message.
     */
    public static String format(String sourceName, String message, Syntax cause) {
        if (sourceName != null) {
            return String.format("Omakase CSS Parser Validation Problem - %s:\nat line %s, column %s in source %s, " +
                "caused by\n%s",
                message,
                cause.line(),
                cause.column(),
                sourceName,
                cause.toString()
            );
        } else {
            return String.format("Omakase CSS Parser Validation Problem - %s:\nat line %s, column %s, caused by\n%s",
                message,
                cause.line(),
                cause.column(),
                cause.toString()
            );
        }
    }

    /**
     * Formats an parsing error message.
     *
     * @param source
     *     The source where the error occurred.
     * @param message
     *     The error message.
     *
     * @return The formatted message.
     */
    public static String format(Source source, String message) {
        if (!source.isSubSource()) {
            return String.format("%s:\nat line %s, column %s in\n'%s'",
                message,
                source.originalLine(),
                source.originalColumn(),
                source.toStringContextual()
            );
        } else {
            return String.format("%s:\nat line %s, column %s near\n'%s'",
                message,
                source.originalLine(),
                source.originalColumn(),
                source.toStringContextual()
            );
        }
    }

    /**
     * Formats a parsing error message. Prefer {@link #format(Source, String)} over this one.
     *
     * @param line
     *     The line where the error occurred.
     * @param column
     *     The column where the error occurred.
     * @param message
     *     The error message.
     *
     * @return The formatted message.
     */
    public static String format(int line, int column, String message) {
        return String.format("%s:\nat line %s, column %s.",
            message,
            line,
            column
        );
    }
}
