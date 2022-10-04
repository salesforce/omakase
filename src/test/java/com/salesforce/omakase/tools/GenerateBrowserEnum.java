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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.salesforce.omakase.data.Browser;
import com.salesforce.omakase.data.Prefix;

import freemarker.template.TemplateException;

/**
 * Handles updating the {@link Browser} enum.
 * <p>
 * Run the main method or use 'script/omakase.sh'.
 * <p>
 * Source of this data is from caniuse.com [https://github.com/Fyrd/caniuse]
 *
 * @author nmcwilliams
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public final class GenerateBrowserEnum {
    private static final String BROWSERS_ENDPOINT = "https://raw.github.com/Fyrd/caniuse/master/data.json";

    public static void main(String[] args) throws Exception {
        run();
    }

    public static boolean run() throws IOException, TemplateException {
        System.out.println("downloading browser data from caniuse.com [https://github.com/Fyrd/caniuse]...");
        URLConnection connection = new URL(BROWSERS_ENDPOINT).openConnection();
        connection.setUseCaches(false);
        final Map map;
        try (final InputStream is = connection.getInputStream()) {
            map = new ObjectMapper()
                // This adds support for using byte code to perform
                // deserialization:
                // https://github.com/FasterXML/jackson-modules-base/tree/master/afterburner
                // Need to set setUseValueClassLoader to false to fix a class
                // loader issue in Java 9
                // https://github.com/FasterXML/jackson-modules-base/issues/37
                // This means it will only look at public properties.
                .registerModule(new AfterburnerModule().setUseValueClassLoader(false))
                .readValue(is, Map.class);
        }

        String earliestString = (String)Iterables.get(((Map)map.get("eras")).keySet(), 0);
        Matcher matcher = Pattern.compile("e-([0-9]+)").matcher(earliestString);
        if (!matcher.matches()) throw new RuntimeException("json format from source has changed!");
        int earliest = Integer.parseInt(matcher.group(1));

        Map agents = (Map)map.get("agents");

        Map ie = (Map)agents.get("ie");
        Map edge = (Map)agents.get("edge");
        Map opera = (Map)agents.get("opera");
        Map chrome = (Map)agents.get("chrome");
        Map safari = (Map)agents.get("safari");
        Map firefox = (Map)agents.get("firefox");
        Map android = (Map)agents.get("android");
        Map ieMobile = (Map)agents.get("ie_mob");
        Map iosSafari = (Map)agents.get("ios_saf");

        final List<BrowserInfo> browsers = ImmutableList.of(
            new BrowserInfo("ie"     , "IE"        , "Internet Explorer", Prefix.MS    , versions(ie, earliest)),
            new BrowserInfo("edge"   , "EDGE"      , "Microsoft Edge"   , Prefix.MS    , versions(edge, earliest)),
            new BrowserInfo("opera"  , "OPERA"     , "Opera"            , Prefix.WEBKIT, versions(opera, earliest)),
            new BrowserInfo("chrome" , "CHROME"    , "Google Chrome"    , Prefix.WEBKIT, versions(chrome, earliest)),
            new BrowserInfo("safari" , "SAFARI"    , "Safari"           , Prefix.WEBKIT, versions(safari, earliest)),
            new BrowserInfo("firefox", "FIREFOX"   , "Firefox"          , Prefix.MOZ   , versions(firefox, earliest)),
            new BrowserInfo("android", "ANDROID"   , "Android Browser"  , Prefix.WEBKIT, versions(android, earliest)),
            new BrowserInfo("ie_mob" , "IE_MOBILE" , "IE Mobile"        , Prefix.MS    , versions(ieMobile, earliest)),
            new BrowserInfo("ios_saf", "IOS_SAFARI", "Safari on iOS"    , Prefix.WEBKIT, versions(iosSafari, earliest))
        );

        SourceWriter writer = new SourceWriter()
              .generator(GenerateBrowserEnum.class)
              .classToWrite(Browser.class)
              .template("browser-enum.ftl")
              .data("browsers", browsers);

        return writer.write();
    }

    private static String versions(Map browser, int indexOfCurrent) {
        List<String> all = (List<String>)browser.get("versions");
        List<Double> filtered = new ArrayList<>();

        for (int i = 0; i <= indexOfCurrent; i++) { // skip the last two, as they are "future" versions
            if (all.get(i) != null) {
                for (String s : Splitter.on('-').split(all.get(i))) {
                    if (s.indexOf(".") == s.lastIndexOf('.')) { // hacky deal with something like Android 4.4.3. Just skip for now
                        filtered.add(Double.valueOf(s));
                    }
                }
            }
        }

        Collections.sort(filtered);
        Collections.reverse(filtered);

        return Joiner.on(",").join(filtered);
    }

    public static final class BrowserInfo {
        private final String key;
        private final Prefix prefix;
        private final String enumName;
        private final String displayName;
        private final String versions;

        public BrowserInfo(String key, String enumName, String displayName, Prefix prefix, String versions) {
            this.key = key;
            this.prefix = prefix;
            this.enumName = enumName;
            this.displayName = displayName;
            this.versions = versions;
        }

        public String getKey() {
            return key;
        }

        public String getPrefix() {
            return "Prefix." + prefix.name();
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
