/*
 * Copyright (C) 2015 salesforce.com, inc.
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

package com.salesforce.omakase.test.goldfile;

import com.google.common.collect.ImmutableSet;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.broadcast.annotation.Rework;
import com.salesforce.omakase.data.Browser;
import com.salesforce.omakase.data.Property;
import com.salesforce.omakase.plugin.Plugin;
import com.salesforce.omakase.plugin.prefixer.Prefixer;

/**
 * Goldfile auto-prefixing flexbox.
 *
 * @author nmcwilliams
 */
public class FlexboxGoldfileTest extends AbstractGoldfileTest {
    @Override
    public String name() {
        return "flexbox";
    }

    @Override
    protected Iterable<Plugin> plugins() {
        Prefixer prefixer = Prefixer.customBrowserSupport();
        prefixer.support().all(Browser.FIREFOX);
        prefixer.support().all(Browser.IE);
        prefixer.support().all(Browser.CHROME);
        prefixer.support().all(Browser.SAFARI);
        prefixer.support().all(Browser.IOS_SAFARI);
        prefixer.support().all(Browser.OPERA);
        prefixer.rearrange(true);

        // when auto refine is false, only these declarations should get prefixed
        Plugin singleRefine = new Plugin() {
            @Rework
            public void refine(Declaration declaration) {
                if (declaration.isProperty(Property.ALIGN_ITEMS)) {
                    declaration.refine();
                }
            }
        };

        return ImmutableSet.<Plugin>of(singleRefine, prefixer);
    }
}
