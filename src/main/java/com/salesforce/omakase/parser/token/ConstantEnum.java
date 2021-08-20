/*
 * Copyright (c) 2015, salesforce.com, inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 *
 *    Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *    following disclaimer.
 *
 *    Redistributions in binary form must reproduce the above copyright notice, this list of conditions and
 *    the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 *    Neither the name of salesforce.com, inc. nor the names of its contributors may be used to endorse or
 *    promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.salesforce.omakase.parser.token;

/**
 * Very similar to a {@link TokenEnum}, except that this is used for enums that must match more than one character (a constant) as
 * opposed to a single token.
 * <p>
 * By adding this interface to an Enum it allows a value to be easily parsed to the correct Enum member using {@link
 * Source#optionalFromConstantEnum(Class)}.
 * <p>
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

    /**
     * Whether the constant is case-sensitive. Returning true is more performant.
     * <p>
     * <b>Important:</b> if returning false (not case-sensitive), the value from {@link #constant()} must be lower-cased.
     *
     * @return True if the constant is case-sensitive, e.g., must match exactly.
     */
    boolean caseSensitive();
}
