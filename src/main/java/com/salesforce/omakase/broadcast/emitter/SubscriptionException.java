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

package com.salesforce.omakase.broadcast.emitter;

import com.salesforce.omakase.Message;
import com.salesforce.omakase.error.OmakaseException;
import com.salesforce.omakase.parser.ParserException;

/**
 * An error that occurs while invoking a subscription method.
 *
 * @author nmcwilliams
 */
public class SubscriptionException extends OmakaseException {
    private static final long serialVersionUID = 7730100425922298149L;

    /**
     * Construct a new instance of a {@link ParserException} with the given {@link Message} and message parameters.
     *
     * @param message
     *     The error message.
     * @param args
     *     The {@link String#format(String, Object...)} parameters to pass to {@link Message#message(Object...)}.
     */
    public SubscriptionException(Message message, Object... args) {
        this(message.message(args));
    }

    /**
     * @param message
     *     The error message.
     */
    public SubscriptionException(String message) {
        super(message);
    }

    /**
     * @param message
     *     The error message.
     * @param cause
     *     The underlying cause.
     */
    public SubscriptionException(String message, Throwable cause) {
        super(message, cause);
    }
}
