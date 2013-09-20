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

package com.salesforce.omakase.ast.selector;

import com.salesforce.omakase.parser.ConstantEnum;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;
import com.salesforce.omakase.writer.Writable;

import java.io.IOException;

/**
 * Represents the match type in an {@link AttributeSelector}.
 *
 * @author nmcwilliams
 */
public enum AttributeMatchType implements Writable, ConstantEnum {
    /** exact attribute match */
    EQUALS("="),
    /** attributes with whitespace-separated words, one of which is a specific value */
    INCLUDES("~="),
    /** attribute has exact value, or value immediately followed by '-' */
    DASHMATCH("|="),
    /** attribute value starts with */
    PREFIXMATCH("^="),
    /** attribute value ends with */
    SUFFIXMATCH("$="),
    /** attribute value contains */
    SUBSTRINGMATCH("*=");

    private final String matcher;

    private AttributeMatchType(String matcher) {
        this.matcher = matcher;
    }

    @Override
    public String constant() {
        return matcher;
    }

    @Override
    public boolean isWritable() {
        return true;
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        appendable.append(matcher);
    }
}
