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

package com.salesforce.omakase.parser;

import com.salesforce.omakase.Message;
import com.salesforce.omakase.error.OmakaseException;

/**
 * An error encountered while parsing.
 *
 * @author nmcwilliams
 */
public class ParserException extends OmakaseException {
    private static final long serialVersionUID = -8952238331167900360L;

    /**
     * Construct a new instance of a {@link ParserException} with the given {@link Message}.
     *
     * @param stream
     *     The stream containing the source of the error.
     * @param message
     *     The error message.
     */
    public ParserException(Stream stream, Message message) {
        this(stream, message.message());
    }

    /**
     * Construct a new instance of a {@link ParserException} with the given {@link Message} and message parameters.
     *
     * @param stream
     *     The stream containing the source of the error.
     * @param message
     *     The error message.
     * @param args
     *     The {@link String#format(String, Object...)} parameters to pass to {@link Message#message(Object...)}.
     */
    public ParserException(Stream stream, Message message, Object... args) {
        this(stream, message.message(args));
    }

    /**
     * Construct a new instance of a {@link ParserException}.
     *
     * @param stream
     *     The stream containing the source of the error.
     * @param message
     *     The error message.
     */
    public ParserException(Stream stream, String message) {
        super(format(message, stream));
    }

    /** formats the error message */
    private static String format(String message, Stream stream) {
        if (!stream.isSubStream()) {
            return String.format("Omakase CSS Parser - %s:\nat line %s, column %s in source\n'%s'",
                message,
                stream.line(),
                stream.column(),
                stream.toStringContextual()
            );
        } else {
            return String.format("Omakase CSS Parser - %s:\nat line %s, column %s (starting from line %s, " +
                "column %s in original source) in substring of original source\n'%s'",
                message,
                stream.line(),
                stream.column(),
                stream.anchorLine(),
                stream.anchorColumn(),
                stream.toStringContextual()
            );
        }
    }
}
