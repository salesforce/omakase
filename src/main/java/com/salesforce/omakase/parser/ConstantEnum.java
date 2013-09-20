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

package com.salesforce.omakase.parser;

import com.salesforce.omakase.parser.token.TokenEnum;

/**
 * Very similar to a {@link TokenEnum}, except that this is used for enums that must match more than one character (a constant) as
 * opposed to a single token.
 * <p/>
 * By adding this interface to an Enum it allows a value to be easily parsed to the correct Enum member using {@link
 * Stream#optionalFromConstantEnum(Class)}.
 * <p/>
 * {@link TokenEnum} should be preferred over this if possible as matching a single token is more performant than matching a
 * constant.
 *
 * @author nmcwilliams
 */
public interface ConstantEnum {
    /**
     * Gets the constant representing the enum member.
     *
     * @return The constant value.
     */
    String constant();
}
