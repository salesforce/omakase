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

package com.salesforce.omakase.parser.selector;

import com.salesforce.omakase.ast.selector.AttributeSelector;
import com.salesforce.omakase.broadcaster.Broadcaster;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.Stream;

/**
 * TESTME
 * <p/>
 * Parses an {@link AttributeSelector}.
 *
 * @author nmcwilliams
 */
public class AttributeSelectorParser extends AbstractParser {

    @Override
    public boolean parse(Stream stream, Broadcaster broadcaster) {
        // note: important not to skip whitespace anywhere in here, as it could skip over a descendant combinator

        // TODO Auto-generated method stub
        return false;
    }
}
