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

package com.salesforce.omakase.test.sample.custom.declaration;

import com.google.common.base.Optional;
import com.salesforce.omakase.parser.token.BaseTokenFactory;
import com.salesforce.omakase.parser.token.CompoundToken;
import com.salesforce.omakase.parser.token.Token;
import com.salesforce.omakase.parser.token.TokenFactory;
import com.salesforce.omakase.parser.token.Tokens;

/**
 * Since we are redefining what is recognized as a valid declaration, we have to provide a custom {@link TokenFactory}.
 * <p/>
 * If our custom declaration did not begin with a non-standard symbol (the +) then we wouldn't need this class necessarily.
 *
 * @author nmcwilliams
 */
public class MixinTokenFactory extends BaseTokenFactory {
    @Override
    public Optional<Token> specialDeclarationBegin() {
        // allow declarations to start with a + symbol
        Optional<Token> other = super.specialDeclarationBegin();
        Token token = other.isPresent() ? new CompoundToken(other.get(), Tokens.PLUS) : Tokens.PLUS;
        return Optional.of(token);
    }
}
