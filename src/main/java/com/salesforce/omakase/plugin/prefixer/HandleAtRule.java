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
