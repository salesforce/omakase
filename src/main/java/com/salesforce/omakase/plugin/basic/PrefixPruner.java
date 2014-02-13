/*
 * Copyright (C) 2014 salesforce.com, inc.
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

package com.salesforce.omakase.plugin.basic;

import com.google.common.base.Optional;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.broadcast.annotation.Rework;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.plugin.Plugin;
import com.salesforce.omakase.util.Declarations;
import com.salesforce.omakase.util.Prefixes;

/**
 * TODO description
 *
 * @author nmcwilliams
 */
public final class PrefixPruner implements Plugin {
    private boolean prefixedAtRules;

    public PrefixPruner() {
    }

    public PrefixPruner(Prefix prefix) {
        removeAllExcept(prefix);
    }

    @SuppressWarnings("UnusedParameters")
    public PrefixPruner removeAllExcept(Prefix prefix) {
        // this.keeper = checkNotNull(prefix, "prefix cannot be null");
        //return this;
        throw new UnsupportedOperationException("not yet supported");
    }

    public PrefixPruner prunePrefixedAtRules() {
        prefixedAtRules = true;
        return this;
    }

    @Rework
    public void atRule(AtRule atRule) {
        if (prefixedAtRules && atRule.name() != null && atRule.block().isPresent()) {
            Optional<Prefix> prefix = Prefixes.parsePrefix(atRule.name());
            if (prefix.isPresent()) {
                for (Declaration declaration : Declarations.within(atRule.block().get())) {
                    Optional<Prefix> declarationPrefix = declaration.propertyName().prefix();
                    if (declarationPrefix.isPresent() && declarationPrefix.get() != prefix.get()) {
                        declaration.destroy();
                    }
                }
            }
        }

    }

    public static PrefixPruner onlyKeep(Prefix active) {
        return new PrefixPruner(active);
    }

    public static PrefixPruner prefixedAtRules() {
        return new PrefixPruner().prunePrefixedAtRules();
    }
}
