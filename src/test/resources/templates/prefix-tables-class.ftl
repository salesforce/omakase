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

import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;

/**
 * Contains the last version of a browser that requires a prefix for various CSS properties.
 * <p/>
 * THIS FILE IS GENERATED. DO NOT EDIT DIRECTLY.
 * <p/>
 * See ${generator} for instructions on updating.
 */
public final class PrefixTables {
    static final Table<Property, Browser, Double> PROPERTIES;
    static final Table<String, Browser, Double> FUNCTIONS;
    static final Table<String, Browser, Double> AT_RULES;
    static final Table<String, Browser, Double> SELECTORS;

    static {
        ImmutableTable.Builder<Property, Browser, Double> builder = ImmutableTable.builder();

        <#list properties as p>
        builder.put(Property.${p.property}, Browser.${p.browser}, ${p.version});
        </#list>

        PROPERTIES = builder.build();
    }

    static {
        ImmutableTable.Builder<String, Browser, Double> builder = ImmutableTable.builder();

        <#list functions as f>
        builder.put("${f.name}", Browser.${f.browser}, ${f.version});
        </#list>

        FUNCTIONS = builder.build();
    }

    static {
        ImmutableTable.Builder<String, Browser, Double> builder = ImmutableTable.builder();

        <#list atRules as a>
        builder.put("${a.name}", Browser.${a.browser}, ${a.version});
        </#list>

        AT_RULES = builder.build();
    }

    static {
        ImmutableTable.Builder<String, Browser, Double> builder = ImmutableTable.builder();

        <#list selectors as s>
        builder.put("${s.name}", Browser.${s.browser}, ${s.version});
        </#list>

        SELECTORS = builder.build();
    }

    private PrefixTables() {}
}
