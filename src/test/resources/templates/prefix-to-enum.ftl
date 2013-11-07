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

/**
 * Enum of vendor prefixes.
 * <p/>
 * Example: {@code PropertyName.using(Property.BORDER_RADIUS).prefix(Prefix.WEBKIT)}.
 * <p/>
 * THIS FILE IS GENERATED. DO NOT EDIT DIRECTLY.
 * <p/>
 * See ${generator} for instructions on updating.
 */
@SuppressWarnings("UnusedDeclaration")
public enum Prefix {
    <#list prefixes as prefix>
    /** prefix '-${prefix}-' */
    ${prefix?upper_case}("-${prefix}-"),

    </#list>
    ;

    private final String prefix;

    Prefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public String toString() {
        return prefix;
    }
}
