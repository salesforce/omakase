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

package ${package};

import com.google.common.base.Optional;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.declaration.KeywordValue;
import com.salesforce.omakase.ast.declaration.PropertyValue;
import com.salesforce.omakase.ast.declaration.Term;
import com.salesforce.omakase.ast.declaration.Values;

/**
 * Enum of all recognized CSS keywords.
 * <p/>
 * THIS FILE IS GENERATED. DO NOT EDIT DIRECTLY.
 * <p/>
 * See ${generator} for instructions on updating.
 */
@SuppressWarnings("UnusedDeclaration")
public enum Keyword {
    <#list keywords as keyword>
    /** CSS keyword named '${keyword}' */
    ${keyword?upper_case?replace("-","_")}("${keyword}"),

    </#list>
    ;

    private final String keyword;

    Keyword(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Gets whether this keyword is the only value of the given {@link Declaration}.
     * <p/>
     * Example:
     * <pre>
     * {@code Keyword.NONE.isOnlyValueIn(theDeclaration);}
     * </pre>
     *
     * @param declaration
     *     Check if this {@link Declaration}'s value only consists of this keyword.
     *
     * @return True if this keyword is the only value in the {@link Declaration}.
     */
    public boolean isOnlyValueIn(Declaration declaration) {
        return isOnlyValueIn(declaration.propertyValue());
    }

    /**
     * Gets whether the given {@link PropertyValue} only consists of one {@link Term} which is a {@link KeywordValue} with this
     * {@link Keyword}.
     * <p/>
     * Example:
     * <pre>
     * {@code Keyword.NONE.isOnlyValueIn(thePropertyValue);}
     * </pre>
     *
     * @param value
     *     The {@link PropertyValue} to check.
     *
     * @return True if this keyword is the only value in the {@link PropertyValue}.
     */
    public boolean isOnlyValueIn(PropertyValue value) {
        Optional<KeywordValue> keywordValue = Values.asKeyword(value);
        return keywordValue.isPresent() && keywordValue.get().keyword().equals(keyword);
    }

    @Override
    public String toString() {
        return keyword;
    }
}
