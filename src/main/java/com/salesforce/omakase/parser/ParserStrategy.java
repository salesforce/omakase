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

import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.declaration.PropertyName;
import com.salesforce.omakase.parser.declaration.TermListParser;

/**
 * Helper for getting an appropriate {@link Parser} for a given {@link PropertyName}.
 *
 * @author nmcwilliams
 */
public final class ParserStrategy {
    /** do not construct */
    private ParserStrategy() {}

    /**
     * Gets the appropriate parser for the given property value. By default this will fallback to the {@link TermListParser}.
     *
     * @param propertyName
     *     The {@link Declaration}'s property name.
     *
     * @return The parser instance.
     */
    public static Parser getValueParser(PropertyName propertyName) {
        // more specific property value parsers to be added here based on the property name
        return ParserFactory.termListParser();
    }
}
