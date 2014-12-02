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

package com.salesforce.omakase.parser.token;

import com.salesforce.omakase.parser.Parser;

/**
 * A {@link TokenFactory} for retrieving standard {@link Token} objects. Mainly using by {@link Parser}s.
 *
 * @author nmcwilliams
 */
public final class StandardTokenFactory extends BaseTokenFactory {
    private static final TokenFactory INSTANCE = new StandardTokenFactory();

    private StandardTokenFactory() {}

    /**
     * Gets the cached factory instance.
     *
     * @return The cached instance.
     */
    public static TokenFactory instance() {
        return INSTANCE;
    }
}
