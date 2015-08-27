/*
 * Copyright (C) 2015 salesforce.com, inc.
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

package com.salesforce.omakase.plugin.prefixer;

import com.google.common.collect.Multimap;
import com.salesforce.omakase.SupportMatrix;
import com.salesforce.omakase.ast.Statement;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.data.PrefixTablesUtil;
import com.salesforce.omakase.util.Equivalents;

import java.util.Set;

/**
 * handles prefixing at rules.
 *
 * @author nmcwilliams
 */
final class HandleAtRule extends AbstractHandlerSimple<AtRule, Statement> {
    @Override
    protected boolean applicable(AtRule instance, SupportMatrix support) {
        return PrefixTablesUtil.isPrefixableAtRule(instance.name());
    }

    @Override
    protected AtRule subject(AtRule instance) {
        return instance;
    }

    @Override
    protected Set<Prefix> required(AtRule instance, SupportMatrix support) {
        return support.prefixesForAtRule(instance.name());
    }

    @Override
    protected Multimap<Prefix, AtRule> equivalents(AtRule instance) {
        return Equivalents.prefixes(subject(instance), instance, Equivalents.AT_RULES);
    }

    @Override
    protected void prefix(Statement copied, Prefix prefix, SupportMatrix support) {
        AtRule atRule = copied.asAtRule().get();
        atRule.name(prefix + atRule.name());
    }
}
