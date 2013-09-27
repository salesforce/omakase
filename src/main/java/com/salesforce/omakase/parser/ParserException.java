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
     * @param msg
     *     The error message.
     */
    public ParserException(Stream stream, Message msg) {
        this(stream, msg.message());
    }

    /**
     * Construct a new instance of a {@link ParserException} with the given {@link Message} and message parameters.
     *
     * @param stream
     *     The stream containing the source of the error.
     * @param msg
     *     The error message.
     * @param args
     *     The {@link String#format(String, Object...)} parameters to pass to {@link Message#message(Object...)}.
     */
    public ParserException(Stream stream, Message msg, Object... args) {
        this(stream, msg.message(args));
    }

    /**
     * Construct a new instance of a {@link ParserException}.
     *
     * @param stream
     *     The stream containing the source of the error.
     * @param msg
     *     The error message.
     */
    public ParserException(Stream stream, String msg) {
        super(msg + indicator(stream));
    }

    /** formats the error message */
    private static String indicator(Stream stream) {
        StringBuilder builder = new StringBuilder(256);
        builder.append("\n ")
            .append("at line ").append(stream.line()).append(", ")
            .append("column ").append(stream.column()).append(" ")
            .append("in '").append(stream).append("'");

        if (stream.isSubStream()) {
            builder.append(" ").append(stream.anchorPositionMessage());
        }

        return builder.toString();
    }
}
