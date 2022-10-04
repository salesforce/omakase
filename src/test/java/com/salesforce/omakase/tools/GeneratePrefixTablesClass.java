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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.yaml.snakeyaml.Yaml;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.primitives.Doubles;
import com.salesforce.omakase.data.Browser;
import com.salesforce.omakase.data.Keyword;
import com.salesforce.omakase.data.PrefixTables;
import com.salesforce.omakase.data.Property;

import freemarker.template.TemplateException;

/**
 * Handles updating the {@link PrefixTables} class.
 * <p>
 * Run the main method or use 'script/omakase.sh'.
 * <p>
 * Source of this data is from caniuse.com [https://github.com/Fyrd/caniuse]
 *
 * @author nmcwilliams
 */
@SuppressWarnings("unchecked")
public class GeneratePrefixTablesClass {
    private static final String ENDPOINT = "https://raw.github.com/Fyrd/caniuse/master/features-json/";
    private static final Yaml yaml = new Yaml();
    private final ObjectMapper jsonMapper = new ObjectMapper()
            // This adds support for using byte code to perform
            // deserialization:
            // https://github.com/FasterXML/jackson-modules-base/tree/master/afterburner
            // Need to set setUseValueClassLoader to false to fix a class
            // loader issue in Java 9
            // https://github.com/FasterXML/jackson-modules-base/issues/37
            // This means it will only look at public properties.
            .registerModule(new AfterburnerModule().setUseValueClassLoader(false));

    public static void main(String[] args) throws IOException, TemplateException {
        new GeneratePrefixTablesClass().run();
    }

    public void run() throws IOException, TemplateException {
        // read the prefix input data
        System.out.println("reading prefixable.yaml");
        @SuppressWarnings("rawtypes")
        Map<String, Map> types = (Map<String, Map>)yaml.load(Tools.readFile("/data/prefixable.yaml"));

        List<PropertyInfo> properties = loadProperties(types.get("properties"));
        Map<String, Map<String, List<String>>> nonStandard = types.get("non-standard");
        properties.addAll(readNonStandardProperties(nonStandard.get("properties")));

        List<KeywordInfo> keywords = loadKeywords(types.get("keywords"));
        List<NameInfo> atRules = loadGeneric(types.get("at-rules"));
        List<NameInfo> selectors = loadGeneric(types.get("selectors"));
        List<NameInfo> functions = loadGeneric(types.get("functions"));

        // write out the new class source
        System.out.println();
        SourceWriter writer = new SourceWriter();

        writer.generator(GeneratePrefixTablesClass.class)
              .classToWrite(PrefixTables.class)
              .template("prefix-tables-class.ftl")
              .data("properties", properties)
              .data("keywords", keywords)
              .data("atRules", atRules)
              .data("selectors", selectors)
              .data("functions", functions);
        writer.write();
    }

    /** load information on all the prefixable properties */
    private List<PropertyInfo> loadProperties(Map<String, List<String>> categories) throws IOException {
        List<PropertyInfo> info = new ArrayList<>();

        for (Map.Entry<String, List<String>> category : categories.entrySet()) {
            for (Map.Entry<Browser, Double> entry : lastPrefixedBrowserVersions(category.getKey()).entrySet()) {
                Browser browser = entry.getKey();
                Double version = entry.getValue();

                // loop through each property name in the category
                for (String property : category.getValue()) {
                    Property prop = Property.lookup(property);
                    assert prop != null : ("property '" + property + "' not found in the Property enum.");

                    // check for override
                    Optional<Double> override = PrefixTableOverrides.getOverride(prop, browser, version);
                    if (override.isPresent()) {
                        String msg = "- overriding property '" + prop + "' for " + browser + " from " + version + " to " + override.get();
                        System.out.println(msg);
                        version = override.get();
                    }

                    info.add(new PropertyInfo(prop, browser, version));
                }
            }
        }

        return info;
    }

    /** read (not load) non standard properties */
    private static List<PropertyInfo> readNonStandardProperties(Map<String, List<String>> properties) {
        List<PropertyInfo> info = new ArrayList<>();

        for (Map.Entry<String, List<String>> entry : properties.entrySet()) {
            Property property = Property.lookup(entry.getKey());
            if (property == null) {
                String msg = "Property '" + entry.getKey() + "' does not exist in the Property enum.";
                throw new IllegalArgumentException(msg);
            }

            System.out.println();
            System.out.println("reading non-standard prefix data for '" + entry.getKey() + "'...");
            for (String browser : entry.getValue()) {
                Browser b = Browser.valueOf(browser.toUpperCase());
                Double version = b.versions().get(0);
                info.add(new PropertyInfo(property, b, version));
                System.out.println("- last required with prefix in " + b + version);
            }
        }
        return info;
    }

    /** load information on all the prefixable keywords */
    private List<KeywordInfo> loadKeywords(Map<String, List<String>> categories) throws IOException {
        List<KeywordInfo> info = new ArrayList<>();

        for (Map.Entry<String, List<String>> category : categories.entrySet()) {
            for (Map.Entry<Browser, Double> entry : lastPrefixedBrowserVersions(category.getKey()).entrySet()) {
                // loop through each keyword name in the category
                for (String keyword : category.getValue()) {
                    Keyword kw = Keyword.lookup(keyword);
                    assert kw != null : ("keyword '" + keyword + "' not found in the Keyword enum.");
                    info.add(new KeywordInfo(kw, entry.getKey(), entry.getValue()));
                }
            }
        }

        return info;
    }

    /** load information on generic prefixable data */
    private List<NameInfo> loadGeneric(Map<String, List<String>> categories) throws IOException {
        List<NameInfo> info = new ArrayList<>();

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
        Map<Browser, Double> allVersions = new LinkedHashMap<>();

        // load data for the category
        Map<String, Object> stats = loadUrl(category);

        // for each known browser, check if it requires a prefix
        for (Browser browser : Browser.values()) {
            Map<String, String> browserSpecific = (Map<String, String>)stats.get(browser.key());
            assert browserSpecific != null : ("browser " + browser + " not found in downloaded stats");

            // find the last browser version that requires a prefix (presence of "x" indicates prefix is required)
            double lastPrefixedVersion = 0d;
            for (Map.Entry<String, String> browserVersionPrefixInfo : browserSpecific.entrySet()) {
                if (browserVersionPrefixInfo.getValue().contains("x")) {
                    String versionString = Iterables.getLast(Splitter.on('-').split(browserVersionPrefixInfo.getKey()));

                    if (versionString.indexOf('.') != versionString.lastIndexOf('.')) {
                        // hacky deal with something like Android 4.4.3. Just treat it as 4.4 for now.
                        versionString = versionString.substring(0, versionString.lastIndexOf('.'));
                    }

                    Double version = Doubles.tryParse(versionString);
                    if (version != null) { // may be null for non-numeric browser versions such as Safari TP (technical preview).
                        lastPrefixedVersion = Math.max(lastPrefixedVersion, version);
                    }
                }
            }
            // caniuse contains data for future browser releases, however we don't want to keep track of anything
            // past the currently released browser version.
            double currentVersion = browser.versions().get(0);
            lastPrefixedVersion = Math.min(lastPrefixedVersion, currentVersion);

            // if we have a prefix requirement, mark it for each property in the category.
            if (lastPrefixedVersion > 0) {
                System.out.println("- last required with prefix in " + browser + ' ' + lastPrefixedVersion);
                allVersions.put(browser, lastPrefixedVersion);
            }
        }

        return allVersions;
    }

    private Map<String, Object> loadUrl(String category) throws IOException {
        System.out.println();
        System.out.println(String.format("downloading prefix data for %s...", category));

        URLConnection connection = new URL(ENDPOINT + category + ".json").openConnection();
        connection.setUseCaches(false);
        connection.setConnectTimeout((int)TimeUnit.SECONDS.toMillis(2));
        connection.setReadTimeout((int)TimeUnit.SECONDS.toMillis(2));

        try (final InputStream is = connection.getInputStream()) {
            // parse the json and find the "stats" map entry which contains the browser prefix info
            Map<String, Map<String, Object>> map = jsonMapper.readValue(is, Map.class);
            return map.get("stats");
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
