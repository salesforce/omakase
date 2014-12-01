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
import com.salesforce.omakase.broadcast.annotation.Validate;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.parser.refiner.Refiner;

/**
 * Utils for working with errors.
 *
 * @author nmcwilliams
 */
public final class ErrorUtils {
    private ErrorUtils() {}

    /**
     * Formats a high-level error message. Usually the message is from a {@link ParserException}, formatted using {@link
     * #format(Source, String)}
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
     * Formats a parsing error message.
     *
     * @param source
     *     The source where the error occurred.
     * @param message
     *     The error message.
     *
     * @return The formatted message.
     */
    @SuppressWarnings("AutoBoxing")
    public static String format(Source source, String message) {
        String fmt = source.isSubSource() ? "%s:\nat line %s, column %s near\n'%s'" : "%s:\nat line %s, column %s in\n'%s'";
        return String.format(fmt,
            message,
            source.originalLine(),
            source.originalColumn(),
            source.toStringContextual()
        );
    }

    /**
     * Formats a validation error message, usually called by a {@link Refiner} or a {@link Validate} method.
     *
     * @param cause
     *     The {@link Syntax} that has the problem.
     * @param message
     *     The error message.
     *
     * @return The formatted message.
     */
    public static String format(Syntax cause, String message) {
        return format(cause, null, message);
    }

    /**
     * Formats a validation error message, usually called by a {@link Refiner} or a {@link Validate} method.
     *
     * @param cause
     *     The {@link Syntax} that has the problem.
     * @param resourceName
     *     Name of the resource (e.g., file name) that has the problem.
     * @param message
     *     The error message.
     *
     * @return The formatted message.
     */
    @SuppressWarnings("AutoBoxing")
    public static String format(Syntax cause, String resourceName, String message) {
        if (resourceName != null) {
            return format(String.format("%s:\nat line %s, column %s in source %s, " +
                "caused by\n%s",
                message,
                cause.line(),
                cause.column(),
                resourceName,
                cause.toString()
            ));
        } else {
            return format(String.format("%s:\nat line %s, column %s, caused by\n%s",
                message,
                cause.line(),
                cause.column(),
                cause.toString()
            ));
        }
    }
}
