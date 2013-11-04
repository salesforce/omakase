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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

import java.util.Map;

/**
 * Enum of all recognized CSS properties. Use {@link #toString()} to get the CSS-output representation.
 * <p/>
 * THIS FILE IS GENERATED. DO NOT EDIT DIRECTLY.
 * <p/>
 * See ${generatorName} for instructions on updating.
 */
public enum Property {
    <#list properties as property>
    /** CSS property named '${property}' */
    ${property?upper_case?replace("-","_")}("${property}"),

    </#list>
    ;

    /** reverse lookup map */
    private static final Map<String, Property> map;
    static {
        Builder<String, Property> builder = ImmutableMap.builder();
        for (Property pn : Property.values()) {
            builder.put(pn.toString(), pn);
        }
        map = builder.build();
    }

    private final String name;

    Property(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Gets the  property associated with the given name
     *
     * @param name
     *     Name of the property.
     *
     * @return The matching {@link Property}.
     */
    public static Property lookup(String name) {
        return map.get(name);
    }
}
