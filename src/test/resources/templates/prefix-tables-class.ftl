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

package ${package};

import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;

/**
 * Contains the last version of a browser that requires a prefix for various CSS properties.
 * <p/>
 * The *CSS Prefix data* in this file is retrieved from caniuse.com and
 * licensed under CC-BY-4.0 (http://creativecommons.org/licenses/by/4.0).
 * <p/>
 * THIS FILE IS GENERATED. DO NOT EDIT DIRECTLY.
 * <p/>
 * See ${generator} for instructions on updating.
 */
@SuppressWarnings("AutoBoxing")
public final class PrefixTables {
    static final Table<Property, Browser, Double> PROPERTIES;
    static final Table<Keyword, Browser, Double> KEYWORDS;
    static final Table<String, Browser, Double> AT_RULES;
    static final Table<String, Browser, Double> SELECTORS;
    static final Table<String, Browser, Double> FUNCTIONS;

    static {
        ImmutableTable.Builder<Property, Browser, Double> builder = ImmutableTable.builder();

        <#list properties as p>
        builder.put(Property.${p.property}, Browser.${p.browser}, ${p.version});
        </#list>

        PROPERTIES = builder.build();
    }

    static {
        ImmutableTable.Builder<Keyword, Browser, Double> builder = ImmutableTable.builder();

        <#list keywords as k>
        builder.put(Keyword.${k.keyword}, Browser.${k.browser}, ${k.version});
        </#list>

        KEYWORDS = builder.build();
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

    static {
        ImmutableTable.Builder<String, Browser, Double> builder = ImmutableTable.builder();

        <#list functions as f>
        builder.put("${f.name}", Browser.${f.browser}, ${f.version});
        </#list>

        FUNCTIONS = builder.build();
    }

    private PrefixTables() {}
}
