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

package com.salesforce.omakase.util.tool;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.salesforce.omakase.data.Browser;
import com.salesforce.omakase.data.Prefix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Handles updating the {@link Browser} enum.
 * <p/>
 * Run the main method or use 'bin/run.sh'.
 * <p/>
 * Source of this data is from caniuse.com [https://github.com/Fyrd/caniuse]
 *
 * @author nmcwilliams
 */
@SuppressWarnings({"JavaDoc", "rawtypes", "unchecked"})
public final class BrowserEnum {
    private static final String BROWSERS_ENDPOINT = "https://raw.github.com/Fyrd/caniuse/master/data.json";
    private static final Logger logger = LoggerFactory.getLogger(BrowserEnum.class);

    private BrowserEnum() {}

    public static void main(String[] args) throws Exception {
        logger.info("downloading browser data...");
        URLConnection connection = new URL(BROWSERS_ENDPOINT).openConnection();
        connection.setUseCaches(false);
        Map map = new ObjectMapper().readValue(connection.getInputStream(), Map.class);
        Map agents = (Map)map.get("agents");

        List<BrowserInfo> browsers = Lists.newArrayList();

        Map ie = (Map)agents.get("ie");
        Map opera = (Map)agents.get("opera");
        Map chrome = (Map)agents.get("chrome");
        Map safari = (Map)agents.get("safari");
        Map firefox = (Map)agents.get("firefox");
        Map android = (Map)agents.get("android");
        Map ieMobile = (Map)agents.get("ie_mob");
        Map iosSafari = (Map)agents.get("ios_saf");
        Map operaMini = (Map)agents.get("op_mini");

        browsers.add(new BrowserInfo("IE", "Internet Explorer", Prefix.MS, versions(ie)));
        browsers.add(new BrowserInfo("OPERA", "Opera", Prefix.O, versions(opera)));
        browsers.add(new BrowserInfo("CHROME", "Chrome", Prefix.WEBKIT, versions(chrome)));
        browsers.add(new BrowserInfo("SAFARI", "Safari", Prefix.WEBKIT, versions(safari)));
        browsers.add(new BrowserInfo("FIREFOX", "Firefox", Prefix.MOZ, versions(firefox)));
        browsers.add(new BrowserInfo("ANDROID", "Android Browser", Prefix.WEBKIT, versions(android)));
        browsers.add(new BrowserInfo("IE_MOBILE", "IE Mobile", Prefix.MS, versions(ieMobile)));
        browsers.add(new BrowserInfo("IOS_SAFARI", "Safari on iOS", Prefix.WEBKIT, versions(iosSafari)));
        browsers.add(new BrowserInfo("OPERA_MINI", "Opera Mini", Prefix.O, versions(operaMini)));

        EnumWriter writer = new EnumWriter();

        writer.generator(BrowserEnum.class);
        writer.enumClass(Browser.class);
        writer.template("browser-enum.ftl");
        writer.data("browsers", browsers);

        writer.write();
    }

    private static String versions(Map browser) {
        List<String> all = (List<String>)browser.get("versions");
        List<Double> filtered = Lists.newArrayList();

        for (int i = 0; i < all.size() - 2; i++) { // skip the last two, as they are "future" versions
            if (all.get(i) != null) {
                for (String s : Splitter.on("-").split(all.get(i))) filtered.add(Double.valueOf(s));
            }
        }

        Collections.sort(filtered);
        Collections.reverse(filtered);

        return Joiner.on(",").join(filtered);
    }

    public static final class BrowserInfo {
        private final Prefix prefix;
        private final String enumName;
        private final String displayName;
        private final String versions;

        public BrowserInfo(String enumName, String displayName, Prefix prefix, String versions) {
            this.prefix = prefix;
            this.enumName = enumName;
            this.displayName = displayName;
            this.versions = versions;
        }

        public String getPrefix() {
            return String.format("Prefix.%s", prefix.name());
        }

        public String getEnumName() {
            return enumName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getVersions() {
            return versions;
        }
    }
}
