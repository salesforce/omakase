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

package com.salesforce.omakase.plugin.basic;

import com.google.common.collect.Sets;
import com.salesforce.omakase.SupportMatrix;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.declaration.FunctionValue;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.data.Property;

import java.util.Set;

/**
 * Information for {@link PrefixerStep}s.
 *
 * @author nmcwilliams
 */
final class PrefixerCtx {
    final boolean prune;
    final boolean rearrange;
    final Property property;
    final Set<Prefix> handled;
    final SupportMatrix support;
    final FunctionValue function;
    final Declaration declaration;

    public PrefixerCtx(SupportMatrix sup, boolean prune, boolean rearrange, Declaration decl, Property prop, FunctionValue func) {
        this.support = sup;
        this.rearrange = rearrange;
        this.prune = prune;
        this.declaration = decl;
        this.property = prop;
        this.function = func;
        this.handled = Sets.newHashSet();
    }
}
