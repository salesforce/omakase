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

package com.salesforce.omakase.broadcast.emitter;

import com.salesforce.omakase.Message;
import com.salesforce.omakase.error.OmakaseException;

/**
 * An error that occurs while invoking a subscription method.
 *
 * @author nmcwilliams
 */
public final class SubscriptionException extends OmakaseException {
    private static final long serialVersionUID = 7730100425922298149L;

    /**
     * Constructs a new {@link SubscriptionException} with the given message.
     *
     * @param message
     *     The error message.
     */
    public SubscriptionException(String message) {
        super(message);
    }

    /**
     * Construct a new {@link SubscriptionException} with the given message and parameters.
     *
     * @param message
     *     The error message.
     * @param args
     *     The {@link String#format(String, Object...)} parameters.
     */
    public SubscriptionException(String message, Object... args) {
        super(Message.fmt(message, args));
    }

    /**
     * @param message
     *     The error message.
     * @param cause
     *     The underlying cause.
     */
    public SubscriptionException(String message, Throwable cause) {
        super(message + ":\n" + findMessage(cause), cause);
    }

    private static String findMessage(Throwable cause) {
        if (cause.getMessage() != null) return cause.toString();
        if (cause.getCause() != null) return findMessage(cause.getCause());
        return "(find the cause below)";
    }
}
