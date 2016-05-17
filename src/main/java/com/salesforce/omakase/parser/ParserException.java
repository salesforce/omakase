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
     * <p>
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
     * <p>
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
