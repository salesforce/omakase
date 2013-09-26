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
 * A {@link Parser} that parses {@link Refinable} AST objects. These objects must be given a {@link Refiner} instance.
 *
 * @author nmcwilliams
 */
public interface RefinableParser extends Parser {
    /**
     * Same as {@link #parse(Stream, Broadcaster)}, except a {@link Refiner} instance to pass along to any created {@link
     * Refinable} AST objects is given.
     *
     * @param stream
     *     The stream to parse.
     * @param broadcaster
     *     The {@link Broadcaster} to receive any events from the parser.
     * @param refiner
     *     The {@link Refiner} to give to created AST objects.
     *
     * @return True if we parsed <em>something</em> (excluding whitespace and comments), false otherwise. Note that a return value
     *         of true does not indicate that the parsed content was actually valid syntax.
     */
    boolean parse(Stream stream, Broadcaster broadcaster, Refiner refiner);
}
