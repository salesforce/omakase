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

package com.salesforce.omakase.plugin.prefixer;

import com.salesforce.omakase.data.Browser;
import com.salesforce.omakase.data.PrefixTables;

/**
 * A collection of {@link PrefixBehavior}s.
 * <p>
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
