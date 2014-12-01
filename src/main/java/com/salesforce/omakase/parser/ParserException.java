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
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.error.ErrorUtils;
import com.salesforce.omakase.error.OmakaseException;
import com.salesforce.omakase.parser.refiner.Refiner;

/**
 * An error encountered while parsing.
 *
 * @author nmcwilliams
 */
public final class ParserException extends OmakaseException {
    private static final long serialVersionUID = -8952238331167900360L;

    /**
     * Constructs a new instance of a {@link ParserException} with the given {@link Message} and message parameters.
     *
     * @param source
     *     The source containing the source of the error.
     * @param message
     *     The error message.
     * @param args
     *     The {@link String#format(String, Object...)} parameters to pass to {@link Message#message(Object...)}.
     */
    public ParserException(Source source, Message message, Object... args) {
        this(source, message.message(args));
    }

    /**
     * Constructs a new instance of a {@link ParserException}.
     *
     * @param source
     *     The source containing the source of the error.
     * @param message
     *     The error message.
     */
    public ParserException(Source source, String message) {
        super(ErrorUtils.format(source, message));
    }

    /**
     * Constructs a new instance of a {@link ParserException} for an error caused by the given {@link Syntax} unit.
     * <p/>
     * This is normally used by {@link Refiner}s.
     *
     * @param cause
     *     The syntax unit that caused the problem.
     * @param message
     *     The error message.
     * @param args
     *     The {@link String#format(String, Object...)} parameters to pass to {@link Message#message(Object...)}.
     */
    public ParserException(Syntax cause, Message message, Object... args) {
        this(cause, message.message(args));
    }

    /**
     * Constructs a new instance of a {@link ParserException} for an error caused by the given {@link Syntax} unit.
     * <p/>
     * This is normally used by {@link Refiner}s.
     *
     * @param cause
     *     The syntax unit that caused the problem.
     * @param message
     *     The error message.
     */
    public ParserException(Syntax cause, String message) {
        super(ErrorUtils.format(cause, message));
    }

    /**
     * Constructs a new instance of a {@link ParserException} from the given cause. This is usually used to wrap around external
     * (to Omakase) checked exceptions from custom {@link Refiner} objects.
     *
     * @param cause
     *     The cause of the exception.
     */
    public ParserException(Throwable cause) {
        super(cause);
    }
}
