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

import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.parser.refiner.Refiner;

/**
 * Combines two {@link Parser}s together. If the first parser does not succeed (i.e., returns false) then the second parser will
 * be executed.
 *
 * @author nmcwilliams
 */
public class CombinationParser extends AbstractParser {
    private final Parser first;
    private final Parser second;

    /**
     * Construct a new {@link CombinationParser} instance with the given two {@link Parser}s.
     *
     * @param first
     *     The first {@link Parser} to try.
     * @param second
     *     The second {@link Parser} to try,
     */
    public CombinationParser(Parser first, Parser second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean parse(Source source, Broadcaster broadcaster) {
        return first.parse(source, broadcaster) || second.parse(source, broadcaster);
    }

    @Override
    public boolean parse(Source source, Broadcaster broadcaster, Refiner refiner) {
        return first.parse(source, broadcaster, refiner) || second.parse(source, broadcaster, refiner);
    }
}
