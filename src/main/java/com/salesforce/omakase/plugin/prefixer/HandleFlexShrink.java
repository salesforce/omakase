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

import com.salesforce.omakase.SupportMatrix;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.declaration.PropertyName;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.data.Property;

/**
 * Flexbox support.
 * <p/>
 * Handles flex-shrink properties.
 *
 * @author nmcwilliams
 */
public class HandleFlexShrink extends HandleProperty {
    @Override
    protected boolean applicable(Declaration instance, SupportMatrix support) {
        return instance.isProperty(Property.FLEX_SHRINK);
    }

    @Override
    protected void copy(Declaration original, Prefix prefix, SupportMatrix support) {
        if (PrefixBehaviors.FLEX_WRAPPING.matches(support, prefix)) {
            super.copy(original, prefix, support);
        }
    }

    @Override
    protected void prefix(Declaration copied, Prefix prefix, SupportMatrix support) {
        if (prefix == Prefix.MS) {
            copied.propertyName(PropertyName.of("flex-negative").prefix(prefix));
        } else {
            super.prefix(copied, prefix, support);
        }
    }
}
