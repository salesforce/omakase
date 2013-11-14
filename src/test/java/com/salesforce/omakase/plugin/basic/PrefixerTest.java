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

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link Prefixer}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class PrefixerTest {
    // new supported prefixable units should test each of the following scenarios:

    // required, not present (add)
    // required, present, rearrange true (move)
    // required, present, rearrange false (leave)
    // not required, not present (noop)
    // not required, present, remove true (remove)
    // not required, present, remove false, rearrange true (move)
    // not required, present, remove false, rearrange false (leave)

    @Test
    public void customShouldNotSupportAnythingByDefault() {
        assertThat(Prefixer.customBrowserSupport().support().supportedBrowsers()).isEmpty();
    }

    /** TODO border-radius */

    // required, not present (add)

    // required, present, rearrange true (move)

    // required, present, rearrange false (leave)

    // not required, not present (noop)

    // not required, present, remove true (remove)

    // not required, present, remove false, rearrange true (move)

    // not required, present, remove false, rearrange false (leave)

    /** TODO border-top-right-radius */

    /** TODO border-top-left-radius */

    /** TODO border-bottom-right-radius */

    /** TODO border-bottom-left-radius */

    /** TODO calc */
}
