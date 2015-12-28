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

package com.salesforce.omakase.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.salesforce.omakase.data.Browser;
import com.salesforce.omakase.data.Keyword;
import com.salesforce.omakase.data.PrefixTables;
import com.salesforce.omakase.data.Property;
import freemarker.template.TemplateException;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Handles updating the {@link PrefixTables} class.
 * <p/>
 * Run the main method or use 'script/omakase.sh'.
 * <p/>
 * Source of this data is from caniuse.com [https://github.com/Fyrd/caniuse]
 *
 * @author nmcwilliams
 */
@SuppressWarnings({"rawtypes", "JavaDoc", "unchecked"})
public class GeneratePrefixTablesClass {
    private static final String ENDPOINT = "https://raw.github.com/Fyrd/caniuse/master/features-json/";
    private static final Yaml yaml = new Yaml();

    public static void main(String[] args) throws IOException, TemplateException {
        new GeneratePrefixTablesClass().run();
    }

    public void run() throws IOException, TemplateException {
        // read the prefix input data
        System.out.println("reading prefixable.yaml...");
        Map types = (Map)yaml.load(Tools.readFile("/data/prefixable.yaml"));

        List<PropertyInfo> properties = loadProperties((Map)types.get("properties"));
        Map nonStandard = (Map)types.get("non-standard");
        properties.addAll(readNonStandardProperties((Map)nonStandard.get("properties")));

        List<KeywordInfo> keywords = loadKeywords((Map)types.get("keywords"));
        List<NameInfo> atRules = loadGeneric((Map)types.get("at-rules"));
        List<NameInfo> selectors = loadGeneric((Map)types.get("selectors"));
        List<NameInfo> functions = loadGeneric((Map)types.get("functions"));

        // write out the new class source
        SourceWriter writer = new SourceWriter();

        writer.generator(GeneratePrefixTablesClass.class);
        writer.classToWrite(PrefixTables.class);
        writer.template("prefix-tables-class.ftl");
        writer.data("properties", properties);
        writer.data("keywords", keywords);
        writer.data("atRules", atRules);
        writer.data("selectors", selectors);
        writer.data("functions", functions);

        writer.write();
    }

    /** load information on all the prefixable properties */
    private List<PropertyInfo> loadProperties(Map<String, List<String>> categories) throws IOException {
        List<PropertyInfo> info = Lists.newArrayList();

        for (Map.Entry<String, List<String>> category : categories.entrySet()) {
            for (Map.Entry<Browser, Double> entry : lastPrefixedBrowserVersions(category.getKey()).entrySet()) {
                // loop through each property name in the category
                for (String property : category.getValue()) {
                    Property prop = Property.lookup(property);
                    assert prop != null : String.format("property '%s' not found in the Property enum", property);
                    info.add(new PropertyInfo(prop, entry.getKey(), entry.getValue()));
                }
            }
        }

        return info;
    }

    /** read (not load) non standard properties */
    private List<PropertyInfo> readNonStandardProperties(Map<String, List<String>> properties) {
        List<PropertyInfo> info = Lists.newArrayList();

        for (Map.Entry<String, List<String>> entry : properties.entrySet()) {
            Property property = Property.lookup(entry.getKey());
            if (property == null) {
                String msg = "Property '%s' does not exist in the Property enum";
                throw new IllegalArgumentException(String.format(msg, entry.getKey()));
            }

            System.out.println(String.format("reading non-standard prefix data for '%s'...", entry.getKey()));
            for (String browser : entry.getValue()) {
                Browser b = Browser.valueOf(browser.toUpperCase());
                Double version = b.versions().get(0);
                info.add(new PropertyInfo(property, b, version));
                System.out.println(String.format("- last required with prefix in %s %s", b, version));
            }
            System.out.println();
        }
        return info;
    }

    /** load information on all the prefixable keywords */
    private List<KeywordInfo> loadKeywords(Map<String, List<String>> categories) throws IOException {
        List<KeywordInfo> info = Lists.newArrayList();

        for (Map.Entry<String, List<String>> category : categories.entrySet()) {
            for (Map.Entry<Browser, Double> entry : lastPrefixedBrowserVersions(category.getKey()).entrySet()) {
                // loop through each keyword name in the category
                for (String keyword : category.getValue()) {
                    Keyword kw = Keyword.lookup(keyword);
                    assert kw != null : String.format("keyword '%s' not found in the Keyword enum", keyword);
                    info.add(new KeywordInfo(kw, entry.getKey(), entry.getValue()));
                }
            }
        }

        return info;
    }

    /** load information on generic prefixable data */
    private List<NameInfo> loadGeneric(Map<String, List<String>> categories) throws IOException {
        List<NameInfo> info = Lists.newArrayList();

        for (Map.Entry<String, List<String>> category : categories.entrySet()) {
            for (Map.Entry<Browser, Double> entry : lastPrefixedBrowserVersions(category.getKey()).entrySet()) {
                // loop through each name in the category
                for (String function : category.getValue()) {
                    info.add(new NameInfo(function, entry.getKey(), entry.getValue()));
                }
            }
        }

        return info;
    }

    /** last prefixed version for each browser (each browser with at least one prefixed version) */
    private Map<Browser, Double> lastPrefixedBrowserVersions(String category) throws IOException {
        Map<Browser, Double> allVersions = Maps.newLinkedHashMap();

        // load data for the category
        Map<String, Object> stats = loadUrl(category);

        // for each known browser, check if it requires a prefix
        for (Browser browser : Browser.values()) {
            Map<String, String> browserSpecific = (Map)stats.get(browser.key());
            assert browserSpecific != null : String.format("browser %s not found in downloaded stats", browser);

            // find the last browser version that requires a prefix (presence of "x" indicates prefix is required)
            double lastPrefixed = 0d;
            for (Map.Entry<String, String> browserVersionPrefixInfo : browserSpecific.entrySet()) {
                if (browserVersionPrefixInfo.getValue().contains("x")) {
                    String last = Iterables.getLast(Splitter.on("-").split(browserVersionPrefixInfo.getKey()));
                    if (last.indexOf(".") != last.lastIndexOf(".")) {
                        // hacky deal with something like Android 4.4.3. Just treat it as 4.4 for now.
                        last = last.substring(0, last.lastIndexOf("."));
                    }
                    lastPrefixed = Math.max(lastPrefixed, Double.parseDouble(last));
                }
            }
            // caniuse contains data for future browser releases, however we don't want to keep track of anything
            // past the currently released browser version.
            double currentVersion = browser.versions().get(0);
            lastPrefixed = Math.min(lastPrefixed, currentVersion);

            // if we have a prefix requirement, mark it for each property in the category.
            if (lastPrefixed > 0) {
                System.out.println(String.format("- last required with prefix in %s %s", browser, lastPrefixed));
                allVersions.put(browser, lastPrefixed);
            }
        }

        System.out.println();
        return allVersions;
    }

    private Map<String, Object> loadUrl(String category) throws IOException {
        System.out.println(String.format("downloading prefix data for %s...", category));

        URLConnection connection = new URL(ENDPOINT + category + ".json").openConnection();
        connection.setUseCaches(false);
        connection.setConnectTimeout((int)TimeUnit.SECONDS.toMillis(2));
        connection.setReadTimeout((int)TimeUnit.SECONDS.toMillis(2));

        try {
            // parse the json and find the "stats" map entry which contains the browser prefix info
            Map map = new ObjectMapper().readValue(connection.getInputStream(), Map.class);
            return (Map)map.get("stats");
        } catch (IOException e) {
            System.out.println("retrying...");
            return loadUrl(category);
        }
    }

    private static class Info {
        private final Browser browser;
        private final Double version;

        public Info(Browser browser, Double version) {
            this.browser = browser;
            this.version = version;
        }

        public String getBrowser() {
            return browser.name();
        }

        public String getVersion() {
            return version.toString();
        }
    }

    public static final class PropertyInfo extends Info {
        private final Property property;

        public PropertyInfo(Property property, Browser browser, Double version) {
            super(browser, version);
            this.property = property;
        }

        public String getProperty() {
            return property.name();
        }
    }

    public static final class KeywordInfo extends Info {
        private final Keyword keyword;

        public KeywordInfo(Keyword keyword, Browser browser, Double version) {
            super(browser, version);
            this.keyword = keyword;
        }

        public String getKeyword() {
            return keyword.name();
        }
    }

    public static final class NameInfo extends Info {
        private final String name;

        public NameInfo(String name, Browser browser, Double version) {
            super(browser, version);
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
