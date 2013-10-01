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

package com.salesforce.omakase.parser.refiner;

import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.extended.UnquotedIEFilter;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.plugin.other.UnquotedIEFilterPlugin;

/**
 * Checks for unquoted ie filter property values. For more information (and to enable it) see {@link UnquotedIEFilterPlugin}.
 *
 * @author nmcwilliams
 */
public class UnquotedIEFilterStrategy implements RefinableStrategy {
    private static final String PROGID = "progid:";

    @Override
    public boolean refineAtRule(AtRule atRule, Broadcaster broadcaster, Refiner refiner) {
        return false;
    }

    @Override
    public boolean refineSelector(Selector selector, Broadcaster broadcaster, Refiner refiner) {
        return false;
    }

    @Override
    public boolean refineDeclaration(Declaration declaration, Broadcaster broadcaster, Refiner refiner) {
        RawSyntax raw = declaration.rawPropertyValue();
        if (raw.content().startsWith(PROGID)) {
            declaration.propertyValue(new UnquotedIEFilter(raw.line(), raw.column(), raw.content()));
            return true;
        }
        return false;
    }
}
