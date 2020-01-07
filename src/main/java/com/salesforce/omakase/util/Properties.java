/*
 * Copyright (c) 2016, salesforce.com, inc.
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

package com.salesforce.omakase.util;

import com.salesforce.omakase.data.Property;

import java.util.EnumSet;
import java.util.regex.Pattern;

/**
 * Utilities for working with {@link Property}.
 *
 * @author nmcwilliams
 */
public final class Properties {
    private Properties() {}

    /**
     * Expands a string with wildcards ('*') to all known properties.
     * <p>
     * For example, 'margin*' expands to 'margin-top', 'margin-right', and so on.
     *
     * @param pattern
     *     The property pattern. Must be lower-case.
     *
     * @return The set of all matched properties.
     */
    public static EnumSet<Property> expand(String pattern) {
        int starIndex = pattern.indexOf('*');

        if (starIndex == -1) {
            return EnumSet.of(Property.lookup(pattern));
        }

        EnumSet<Property> ret = EnumSet.noneOf(Property.class);

        if (starIndex == pattern.length() - 1) {
            // if one star at the end do a faster startsWith match
            String startPattern = pattern.substring(0, pattern.length() - 1);
            for (Property prop : Property.values()) {
                if (prop.toString().startsWith(startPattern)) {
                    ret.add(prop);
                }
            }
        } else {
            // do a full regex match
            Pattern regexPattern = Pattern.compile(pattern.replace("*", "[a-z\\-]*"));
            for (Property property : Property.values()) {
                if (regexPattern.matcher(property.toString()).matches()) {
                    ret.add(property);
                }
            }
        }

        return ret;
    }
}
