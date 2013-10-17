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

import com.salesforce.omakase.ast.Refinable;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.parser.refiner.Refiner;

/**
 * Used to parse an aspect of CSS source code.
 * <p/>
 * {@link Parser}s must <em>not</em> maintain any state or persistence from one parse operation to another. They should be
 * immutable objects.
 *
 * @author nmcwilliams
 */
public interface Parser {
    /**
     * Parse from the current position of the given source, notifying the given {@link Broadcaster} of any applicable events and
     * data.
     * <p/>
     * <b>Important:</b> This method should only be used in limited circumstances. For example, doing partial content parsing.
     * <p/>
     * Generally speaking, if you have a {@link Refiner} instance given to you then you should use {@link #parse(Source,
     * Broadcaster, Refiner)} almost always, as it will perform a lot better.
     *
     * @param source
     *     The source to parse.
     * @param broadcaster
     *     The {@link Broadcaster} to receive any events from the parser.
     *
     * @return True if we parsed <em>something</em> (excluding whitespace and comments), false otherwise. Note that a return value
     *         of true does not indicate that the parsed content was actually valid syntax.
     */
    boolean parse(Source source, Broadcaster broadcaster);

    /**
     * Same as {@link #parse(Source, Broadcaster)}, except a {@link Refiner} instance to pass along to any created {@link
     * Refinable} AST objects is given.
     *
     * @param source
     *     The source to parse.
     * @param broadcaster
     *     The {@link Broadcaster} to receive any events from the parser.
     * @param refiner
     *     The {@link Refiner} to give to created AST objects.
     *
     * @return True if we parsed <em>something</em> (excluding whitespace and comments), false otherwise. Note that a return value
     *         of true does not indicate that the parsed content was actually valid syntax.
     */
    boolean parse(Source source, Broadcaster broadcaster, Refiner refiner);

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
