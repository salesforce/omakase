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
import com.salesforce.omakase.BrowserVersion;
import com.salesforce.omakase.data.Browser;
import com.salesforce.omakase.data.PrefixInfo;
import com.salesforce.omakase.data.Property;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

/**
 * Handles updating the {@link PrefixInfo} class.
 * <p/>
 * Run the main method or use 'bin/run.sh'.
 * <p/>
 * Source of this data is from caniuse.com [https://github.com/Fyrd/caniuse]
 *
 * @author nmcwilliams
 */
@SuppressWarnings({"JavaDoc", "unchecked", "rawtypes"})
public final class PrefixInfoClassGenerator {
    private static final String ENDPOINT = "https://raw.github.com/Fyrd/caniuse/master/features-json/";
    private static final Logger logger = LoggerFactory.getLogger(PrefixInfoClassGenerator.class);
    private static final Yaml yaml = new Yaml();

    private PrefixInfoClassGenerator() {}

    public static void main(String[] args) throws IOException, TemplateException {
        // read the prefix input data
        logger.info("reading prefix-info.yaml...");
        Map types = (Map)yaml.load(Tools.readFile("/data/prefix-info.yaml"));

        List<PropertyInfo> properties = loadProperties((Map)types.get("properties"));
        List<FunctionInfo> functions = loadFunctions((Map)types.get("functions"));

        // write out the new class source
        SourceWriter writer = new SourceWriter();

        writer.generator(PrefixInfoClassGenerator.class);
        writer.classToWrite(PrefixInfo.class);
        writer.template("prefix-info-class.ftl");
        writer.data("properties", properties);
        writer.data("functions", functions);

        writer.write();
    }

    /** load information on all the prefixable properties */
    private static List<PropertyInfo> loadProperties(Map<String, List<String>> categories) throws IOException {
        List<PropertyInfo> info = Lists.newArrayList();

        for (Map.Entry<String, List<String>> category : categories.entrySet()) {
            for (BrowserVersion browserVersion : lastPrefixedBrowserVersions(category.getKey())) {
                // loop through each property name in the category
                for (String property : category.getValue()) {
                    Property prop = Property.lookup(property);
                    assert prop != null : String.format("property '%s' not found in the Property enum", property);
                    info.add(new PropertyInfo(prop, browserVersion.browser(), browserVersion.version()));
                }
            }
        }

        return info;
    }

    /** load information on all the prefixable functions */
    private static List<FunctionInfo> loadFunctions(Map<String, List<String>> categories) throws IOException {
        List<FunctionInfo> info = Lists.newArrayList();

        for (Map.Entry<String, List<String>> category : categories.entrySet()) {
            for (BrowserVersion browserVersion : lastPrefixedBrowserVersions(category.getKey())) {
                // loop through each property name in the category
                for (String function : category.getValue()) {
                    info.add(new FunctionInfo(function, browserVersion.browser(), browserVersion.version()));
                }
            }
        }

        return info;
    }

    private static List<BrowserVersion> lastPrefixedBrowserVersions(String category) throws IOException {
        List<BrowserVersion> versions = Lists.newArrayList();

        // load data for the category
        logger.info("downloading prefix data for {}...", category);
        URLConnection connection = new URL(ENDPOINT + category + ".json").openConnection();
        connection.setUseCaches(false);

        // parse the json and find the "stats" map entry which contains the browser prefix info
        Map map = new ObjectMapper().readValue(connection.getInputStream(), Map.class);
        Map<String, Object> stats = (Map)map.get("stats");

        // for each known browser, check if it requires a prefix
        for (Browser browser : Browser.values()) {
            Map<String, String> browserSpecific = (Map)stats.get(browser.key());
            assert browserSpecific != null : String.format("browser %s not found in downloaded stats", browser);

            // find the last browser version that requires a prefix (presence of "x" indicates prefix is required)
            Double latest = 0d;
            for (Map.Entry<String, String> browserVersionPrefixInfo : browserSpecific.entrySet()) {
                if (browserVersionPrefixInfo.getValue().contains("x")) {
                    String last = Iterables.getLast(Splitter.on("-").split(browserVersionPrefixInfo.getKey()));
                    latest = Math.max(latest, Double.valueOf(last));
                }
            }
            latest = Math.min(latest, browser.versions().get(0)); // don't go past the current browser version

            // if we have a prefix requirement, mark it for each property in the category.
            if (latest > 0) {
                logger.info("- last required with prefix in {} {}", browser, latest);
                versions.add(new BrowserVersion(browser, latest));
            }
        }

        return versions;
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

    public static final class FunctionInfo extends Info {
        private final String function;

        public FunctionInfo(String function, Browser browser, Double version) {
            super(browser, version);
            this.function = function;
        }

        public String getFunction() {
            return function;
        }
    }
}
