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

package com.salesforce.omakase.test.util.tool;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.salesforce.omakase.data.Browser;
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
@SuppressWarnings({"JavaDoc", "unchecked", "rawtypes"})
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
        List<NameInfo> functions = loadGeneric((Map)types.get("functions"));
        List<NameInfo> atRules = loadGeneric((Map)types.get("at-rules"));
        List<NameInfo> selectors = loadGeneric((Map)types.get("selectors"));

        Map nonStandard = (Map)types.get("non-standard");
        properties.addAll(readNonStandardProperties((Map)nonStandard.get("properties")));

        // write out the new class source
        SourceWriter writer = new SourceWriter();

        writer.generator(GeneratePrefixTablesClass.class);
        writer.classToWrite(PrefixTables.class);
        writer.template("prefix-tables-class.ftl");
        writer.data("properties", properties);
        writer.data("functions", functions);
        writer.data("atRules", atRules);
        writer.data("selectors", selectors);

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

    /** load information on all the prefixable functions */
    private List<NameInfo> loadGeneric(Map<String, List<String>> categories) throws IOException {
        List<NameInfo> info = Lists.newArrayList();

        for (Map.Entry<String, List<String>> category : categories.entrySet()) {
            for (Map.Entry<Browser, Double> entry : lastPrefixedBrowserVersions(category.getKey()).entrySet()) {
                // loop through each property name in the category
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
