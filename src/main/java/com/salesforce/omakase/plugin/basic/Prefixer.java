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

package com.salesforce.omakase.plugin.basic;

import com.salesforce.omakase.SupportMatrix;
import com.salesforce.omakase.plugin.Plugin;

import static com.salesforce.omakase.data.Browser.*;

/**
 * TESTME
 * <p/>
 * TODO description
 *
 * @author nmcwilliams
 */
public class Prefixer implements Plugin {
    private final SupportMatrix support = new SupportMatrix();

    private Prefixer() {}

    public SupportMatrix support() {
        return support;
    }

    public static Prefixer defaultSupport() {
        Prefixer prefixer = new Prefixer();
        prefixer.support()
            .last(IOS_SAFARI, 4)
            .last(FIREFOX, 2)
            .last(ANDROID, 3)
            .last(CHROME, 2)
            .browser(IE, 7)
            .browser(IE, 8)
            .browser(IE, 9)
            .browser(IE, 10)
            .browser(IE, 11)
            .latest(SAFARI)
            .latest(IE_MOBILE)
            .latest(OPERA_MINI);

        return prefixer;
    }

    public static Prefixer customSupport() {
        return new Prefixer();
    }
}
