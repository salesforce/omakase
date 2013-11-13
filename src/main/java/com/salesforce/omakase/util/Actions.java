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

package com.salesforce.omakase.util;

import com.salesforce.omakase.ast.collection.Groupable;

/**
 * Collection of common {@link Action}s.
 *
 * @author nmcwilliams
 */
public final class Actions {
    private Actions() {}

    /**
     * TESTME
     * <p/>
     * Calls {@link Groupable#detach()} on each instance.
     */
    public static final Action<Groupable<?, ?>> DETACH = new Action<Groupable<?, ?>>() {
        @Override
        public void apply(Groupable<?, ?> instance) {
            instance.detach();
        }
    };
}
