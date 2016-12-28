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
import com.salesforce.omakase.broadcast.annotation.Validate;
import com.salesforce.omakase.broadcast.emitter.SubscriptionException;
import com.salesforce.omakase.parser.ParserException;

import java.lang.reflect.InvocationTargetException;

/**
 * Responsible for handling errors.
 * <p>
 * Errors will come from 1) syntax validator plugins, 2) {@link ParserException}s, which are usually thrown by core parsers or
 * custom refiners, or 3) {@link SubscriptionException}s, which are usually caused by programming errors in plugin subscription
 * methods.
 *
 * @author nmcwilliams
 */
public interface ErrorManager {
    /**
     * Gets the name of the source currently being parsed. If not set this may return null.
     *
     * @return The name of the source currently being parsed.
     */
    String getSourceName();

    /**
     * Reports an error message. This is usually called from {@link Validate} subscription methods.
     * <p>
     * Implementations should <b>not</b> throw or rethrow an exception from this method.
     * <p>
     * You can use {@link ErrorUtils#format(Syntax, String)} as a helper.
     *
     * @param level
     *     The {@link ErrorLevel}.
     * @param cause
     *     The {@link Syntax} unit that is the cause of the error (e.g., the unit that has failed validation).
     * @param message
     *     The error message.
     */
    void report(ErrorLevel level, Syntax cause, String message);

    /**
     * Reports an error based on a {@link ParserException}.
     * <p>
     * These are usually thrown by core parsers or custom refiner plugins, which is usually caused by bad CSS source code input.
     *
     * @param exception
     *     The exception that describes the error.
     */
    void report(ParserException exception);

    /**
     * Reports an uncaught exception from a subscription method.
     * <p>
     * This will occur when a subscription plugin method throws an exception. This usually means there is a programming error in
     * the plugin, e.g., an NPE. It also might mean the plugin throws an unrelated (to the parser) exception which will results in
     * an {@link InvocationTargetException} (this should be avoided).
     * <p>
     * Check the cause to find the underlying {@link InvocationTargetException}.
     *
     * @param exception
     *     The exception.
     */
    void report(SubscriptionException exception);

    /**
     * Returns true if there were any errors.
     *
     * @return True of there were errors.
     */
    boolean hasErrors();

    /**
     * Should return true if {@link #summarize()} should automatically be called at the end of parsing if there are errors and
     * the result wrapped and thrown by a new {@link ProblemSummaryException}.
     *
     * @return True if #{@link #summarize()} should be called automatically.
     */
    boolean autoSummarize();

    /**
     * Returns a summary of errors.
     * <p>
     * If there are no errors then this should return an empty string.
     *
     * @return True if there were else, otherwise an empty string.
     */
    String summarize();
}
