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

package com.salesforce.omakase.plugin.prefixer;

import com.salesforce.omakase.data.Browser;
import com.salesforce.omakase.data.PrefixTables;

/**
 * A collection of {@link PrefixBehavior}s.
 * <p/>
 * These behaviors contain prefix information for features where {@link PrefixTables} is not enough, e.g., when a feature has
 * multiple and significantly different specs and implementations across browsers.
 *
 * @author nmcwilliams
 */
final class PrefixBehaviors {
    private PrefixBehaviors() {}

    // ----- FLEXBOX -----
    /**
     * Flexbox 2009 behavior.
     */
    public static final PrefixBehavior FLEX_2009 = new PrefixBehavior()
        .put(Browser.CHROME, 20)
        .put(Browser.SAFARI, 6)
        .put(Browser.IOS_SAFARI, 6.1)
        .put(Browser.ANDROID, 4.3)
        .put(Browser.FIREFOX, 21);

    /**
     * Flexbox 2011 (aka 2012, aka tweener) behavior (basically, IE10).
     */
    public static final PrefixBehavior FLEX_2011 = new PrefixBehavior()
        .put(Browser.IE, 10);

    /**
     * Standard, final flexbox spec with prefix.
     */
    public static final PrefixBehavior FLEX_FINAL = new PrefixBehavior()
        .put(Browser.CHROME, 28)
        .put(Browser.SAFARI, 8)
        .put(Browser.IOS_SAFARI, 8.4);

    /**
     * Standard, final flexbox spec with prefix. Same as {@link #FLEX_FINAL} but includes IE10.
     */
    public static final PrefixBehavior FLEX_FINAL_PLUS = new PrefixBehavior()
        .put(Browser.CHROME, 28)
        .put(Browser.SAFARI, 8)
        .put(Browser.IOS_SAFARI, 8.4)
        .put(Browser.IE, 10);

    /**
     * Standard, final flexbox spec with prefix. This is similar to {@link #FLEX_FINAL_PLUS}, except in this case IE10 doesn't use
     * the final spec property name/behavior but the 2011 one. So this is most accurately thought of as final + 2011.
     */
    public static final PrefixBehavior FLEX_FINAL_HYBRID = FLEX_FINAL_PLUS;
}
