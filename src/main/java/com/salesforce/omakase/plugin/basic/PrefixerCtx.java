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

import com.salesforce.omakase.SupportMatrix;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.declaration.FunctionValue;
import com.salesforce.omakase.data.Property;

/**
 * TODO delete
 * Information for {@link PrefixerStep}s.
 *
 * @author nmcwilliams
 */
final class PrefixerCtx {
    final boolean prune;
    final boolean rearrange;
    final SupportMatrix support;

    boolean handled;

    Property property;
    FunctionValue function;
    Declaration declaration;
    AtRule atRule;

    public PrefixerCtx(SupportMatrix sup, boolean prune, boolean rearrange) {
        this.support = sup;
        this.rearrange = rearrange;
        this.prune = prune;
    }
}
