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

package com.salesforce.omakase.test.util.tool;

import com.google.common.collect.Sets;
import com.salesforce.omakase.SupportMatrix;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.data.Property;
import com.salesforce.omakase.plugin.prefixer.Prefixer;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Prints stuff that's prefixed by {@link Prefixer#defaultBrowserSupport()}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings({"JavaDoc", "rawtypes"})
public class PrintDefaultPrefixed {
    private static final Yaml yaml = new Yaml();

    public static void main(String[] args) throws IOException {
        new PrintDefaultPrefixed().run();
    }

    @SuppressWarnings("unchecked")
    public void run() throws IOException {
        System.out.println("Items automatically prefixed with Prefixer.defaultBrowserSupport:\n");

        SupportMatrix support = Prefixer.defaultBrowserSupport().support();

        Map types = (Map)yaml.load(Tools.readFile("/data/prefixable.yaml"));
        String header = "%-28s   %s%n";

        // props
        System.out.printf(header, "Property", "Prefix");
        System.out.printf(header, dash(28), dash(15));
        Map<String, List<String>> properties = (Map)types.get("properties");
        Set<String> props = Sets.newTreeSet();
        for (Map.Entry<String, List<String>> entry : properties.entrySet()) {
            props.addAll(entry.getValue());
        }
        Map<String, List<String>> nsProperties = (Map)((Map)types.get("non-standard")).get("properties");
        for (Map.Entry<String, List<String>> entry : nsProperties.entrySet()) {
            props.add(entry.getKey());
        }
        for (String p : props) {
            Property prop = Property.lookup(p);
            for (Prefix prefix : Prefix.values()) {
                if (support.requiresPrefixForProperty(prefix, prop)) {
                    System.out.println(String.format("%-28s   %s", prop, prefix.name().toLowerCase()));
                }
            }
        }
        System.out.println();

        // functions
        System.out.printf(header, "Function", "Prefix");
        System.out.printf(header, dash(28), dash(15));
        Map<String, List<String>> functions = (Map)types.get("functions");
        Set<String> funcs = Sets.newTreeSet();
        for (Map.Entry<String, List<String>> entry : functions.entrySet()) {
            funcs.addAll(entry.getValue());
        }
        for (String f : funcs) {
            for (Prefix prefix : Prefix.values()) {
                if (support.requiresPrefixForFunction(prefix, f)) {
                    System.out.println(String.format("%-28s   %s", f, prefix.name().toLowerCase()));
                }
            }
        }
        System.out.println();

        // at-rules
        System.out.printf(header, "At Rule", "Prefix");
        System.out.printf(header, dash(28), dash(15));
        Map<String, List<String>> atRules = (Map)types.get("at-rules");
        Set<String> ars = Sets.newTreeSet();
        for (Map.Entry<String, List<String>> entry : atRules.entrySet()) {
            ars.addAll(entry.getValue());
        }
        for (String ar : ars) {
            for (Prefix prefix : Prefix.values()) {
                if (support.requiresPrefixForAtRule(prefix, ar)) {
                    System.out.println(String.format("%-28s   %s", ar, prefix.name().toLowerCase()));
                }
            }
        }

        System.out.println();

        // selectors
        System.out.printf(header, "Selector", "Prefix");
        System.out.printf(header, dash(28), dash(15));
        Map<String, List<String>> selectors = (Map)types.get("selectors");
        Set<String> sels = Sets.newTreeSet();
        for (Map.Entry<String, List<String>> entry : selectors.entrySet()) {
            sels.addAll(entry.getValue());
        }
        for (String s : sels) {
            for (Prefix prefix : Prefix.values()) {
                if (support.requiresPrefixForSelector(prefix, s)) {
                    System.out.println(String.format("%-28s   %s", s, prefix.name().toLowerCase()));
                }
            }
        }
        System.out.println();
    }

    private String dash(int number) {
        return repeat("-", number);
    }

    private String repeat(String string, int number) {
        return new String(new char[number]).replace("\0", string);
    }
}
