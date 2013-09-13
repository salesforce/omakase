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

/**
 * An exception indicating that an unrecoverable, fatal error has occurred, upon which processing should immediately be halted.
 *
 * @author nmcwilliams
 */
public final class FatalException extends OmakaseException {
    private static final long serialVersionUID = 2723924358238169904L;

    /**
     * Creates a new exception with the given message.
     *
     * @param message
     *     The description of the exception.
     */
    protected FatalException(String message) {
        super(message);
    }

    /**
     * Creates a new exception with the given message and cause.
     *
     * @param message
     *     The description of the exception.
     * @param cause
     *     The cause of the exception.
     */
    public FatalException(String message, Throwable cause) {
        super(message, cause);
    }
}
