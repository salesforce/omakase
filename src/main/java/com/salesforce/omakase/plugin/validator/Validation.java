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

package com.salesforce.omakase.plugin.validator;

import com.google.common.collect.Lists;
import com.salesforce.omakase.plugin.Plugin;

import java.util.List;

/**
 * Standard library-provided validation plugins.
 *
 * @author nmcwilliams
 */
public final class Validation {
    /** do not construct */
    private Validation() {}

    /**
     * Gets the list of normal validations. This should be used in almost all cases, unless you don't want any validation at all.
     *
     * @return The list of normal validations.
     */
    public static List<Plugin> normal() {
        return Lists.<Plugin>newArrayList(new PseudoElementValidator());
    }
}
