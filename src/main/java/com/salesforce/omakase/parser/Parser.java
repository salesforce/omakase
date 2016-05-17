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

import com.salesforce.omakase.ast.Refinable;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.parser.refiner.MasterRefiner;

/**
 * Used to parse an aspect of CSS source code.
 * <p>
 * {@link Parser}s must <em>not</em> maintain any state or persistence from one parse operation to another. They should be
 * immutable objects.
 *
 * @author nmcwilliams
 */
public interface Parser {
    /**
     * Parse from the current position of the given source, notifying the given {@link Broadcaster} of any applicable events and
     * data.
     * <p>
     * <b>Important:</b> This method should only be used in limited circumstances. For example, doing partial content parsing.
     * <p>
     * Generally speaking, if you have a {@link MasterRefiner} instance given to you then you should use {@link #parse(Source,
     * Broadcaster, MasterRefiner)} almost always, as it will perform a lot better.
     *
     * @param source
     *     The source to parse.
     * @param broadcaster
     *     The {@link Broadcaster} to receive any events from the parser.
     *
     * @return True if we parsed <em>something</em> (excluding whitespace and comments), false otherwise. Note that a return value
     * of true does not indicate that the parsed content was actually valid syntax.
     */
    boolean parse(Source source, Broadcaster broadcaster);

    /**
     * Same as {@link #parse(Source, Broadcaster)}, except a {@link MasterRefiner} instance to pass along to any created {@link
     * Refinable} AST objects is given.
     *
     * @param source
     *     The source to parse.
     * @param broadcaster
     *     The {@link Broadcaster} to receive any events from the parser.
     * @param refiner
     *     The {@link MasterRefiner} to give to created AST objects.
     *
     * @return True if we parsed <em>something</em> (excluding whitespace and comments), false otherwise. Note that a return value
     * of true does not indicate that the parsed content was actually valid syntax.
     */
    boolean parse(Source source, Broadcaster broadcaster, MasterRefiner refiner);

    /**
     * Utility for creating a {@link CombinationParser}.
     *
     * @param other
     *     The other {@link Parser} in addition to this one to use for creating the {@link CombinationParser}.
     *
     * @return The {@link CombinationParser}.
     */
    Parser or(Parser other);
}
