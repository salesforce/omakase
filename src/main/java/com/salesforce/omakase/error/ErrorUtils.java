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
import com.salesforce.omakase.ast.collection.Groupable;
import com.salesforce.omakase.ast.declaration.PropertyValue;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.parser.Source;

/**
 * Utils for working with errors.
 *
 * @author nmcwilliams
 */
public final class ErrorUtils {
    private ErrorUtils() {}

    public static String format(String sourceName, String message) {
        return sourceName != null ? String.format("%s:\n%s", sourceName, message) : message;
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
    public static String format(Source source, String message) {
        return format(null, source, message);
    }

    public static String format(String sourceName, Source source, String message) {
        String fmt = "%s:\nat line %s, column %s%s %s\n'%s'";
        return String.format(fmt,
            message,
            source.originalLine(),
            source.originalColumn(),
            sourceName != null ? " (" + sourceName + ") " : "",
            source.isSubSource() ? "near" : "in",
            source.toStringContextual()
        );
    }

    /**
     * Formats a validation error message.
     *
     * @param cause
     *     The {@link Syntax} that has the problem.
     * @param message
     *     The error message.
     *
     * @return The formatted message.
     */
    public static String format(Syntax cause, String message) {
        return format(null, cause, message);
    }

    /**
     * Formats a validation error message.
     *
     * @param sourceName
     *     Name of the resource (e.g., file name) that has the problem.
     * @param cause
     *     The {@link Syntax} that has the problem.
     * @param message
     *     The error message.
     *
     * @return The formatted message.
     */
    public static String format(String sourceName, Syntax cause, String message) {
        if (cause instanceof Groupable) {
            Object parent = ((Groupable<?, ?>)cause).parent();
            if (parent instanceof PropertyValue || parent instanceof Selector) {
                String fmt = "%s:\nat line %s, column %s%s, caused by\n%s\nin\n%s";
                return String.format(fmt,
                    message,
                    cause.line(),
                    cause.column(),
                    sourceName != null ? " (" + sourceName + ")" : "",
                    cause.toString(),
                    parent.toString()
                );
            }
        }

        String fmt = "%s:\nat line %s, column %s%s, caused by\n%s";
        return String.format(fmt,
            message,
            cause.line(),
            cause.column(),
            sourceName != null ? " (" + sourceName + ")" : "",
            cause.toString()
        );
    }

}
