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

package com.salesforce.omakase.test.util.perf;

import com.salesforce.omakase.Omakase;
import com.salesforce.omakase.data.Browser;
import com.salesforce.omakase.plugin.basic.PrefixPruner;
import com.salesforce.omakase.plugin.basic.Prefixer;

/**
 * Omakase, testing auto prefixer.
 *
 * @author nmcwilliams
 */
public final class OmakaseFullPrefixer implements PerfTestParser {
    @Override
    public String code() {
        return "prefixer";
    }

    @Override
    public String name() {
        return "omakase[prefixer]";
    }

    @Override
    public void parse(String input) {
        Prefixer prefixer = Prefixer.customBrowserSupport();
        prefixer.support().all(Browser.CHROME);
        prefixer.support().all(Browser.FIREFOX);
        prefixer.support().all(Browser.SAFARI);
        prefixer.support().all(Browser.OPERA);
        prefixer.rearrange(true);

        PrefixPruner pruner = PrefixPruner.prunePrefixedAtRules();

        Omakase.source(input).use(PluginSet.normal()).use(prefixer).use(pruner).process();
    }
}

