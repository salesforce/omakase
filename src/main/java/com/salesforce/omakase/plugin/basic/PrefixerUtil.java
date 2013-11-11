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
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.declaration.PropertyName;
import com.salesforce.omakase.data.Prefix;

/**
 * TESTME
 * <p/>
 * TODO description
 *
 * @author nmcwilliams
 */
public final class PrefixerUtil {
    private PrefixerUtil() {}

    public static Declaration createPrefixed(Declaration original, Prefix prefix, SupportMatrix supportMatrix) {
        assert !original.propertyName().isPrefixed() : "didn't expect the original declaration to be prefixed";

        PropertyName originalProperty = original.propertyName();

        // check for special use cases

        // default is just to use the same property value reference
        return new Declaration(originalProperty.cloneWithNewPrefix(prefix), original.propertyValue());
    }
}
