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

package com.salesforce.omakase.test.sample.custom.selector;

import com.salesforce.omakase.parser.token.BaseTokenFactory;
import com.salesforce.omakase.parser.token.Token;
import com.salesforce.omakase.parser.token.TokenFactory;

/**
 * Since we are redefining what is recognized as a valid selector, we have to provide a custom {@link TokenFactory}.
 * <p/>
 * If our custom syntax did not affect any of the delimiter points (beginning and end of standard AST objects) then we would not
 * need this class.
 *
 * @author nmcwilliams
 */
public final class PlaceholderTokenFactory extends BaseTokenFactory {
    @Override
    public Token selectorBegin() {
        // allow selectors to begin with the percentage as well
        return super.selectorBegin().or(PlaceholderTokens.PERCENTAGE);
    }
}
