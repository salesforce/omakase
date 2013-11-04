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
import com.salesforce.omakase.plugin.basic.AutoPrefix;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

/**
 * Handles updating the data used by the {@link AutoPrefix} plugin.
 * <p/>
 * Run the main method or use 'bin/run.sh'.
 * <p/>
 * Source of this data is from caniuse.com [https://github.com/Fyrd/caniuse]
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public final class AutoPrefixUpdater {
    private static final String BROWSERS_ENDPOINT = "https://raw.github.com/Fyrd/caniuse/master/data.json";

    private AutoPrefixUpdater() {}

    public static void main(String[] args) throws Exception {
        browsers();
        prefixes();
    }

    private static void browsers() throws IOException {
        URLConnection connection = new URL(BROWSERS_ENDPOINT).openConnection();
        connection.setUseCaches(false);
        Map map = new ObjectMapper().readValue(connection.getInputStream(), Map.class);
        System.out.println(map);

    }

    private static void prefixes() {
    }
}
