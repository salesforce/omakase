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

package com.salesforce.omakase;

import com.salesforce.omakase.data.Browser;
import com.salesforce.omakase.test.util.Util;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.*;

/**
 * Unit tests for {@link BrowserVersion}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class BrowserVersionTest {

    @Test
    public void getBrowser() {
        BrowserVersion bv = new BrowserVersion(Browser.CHROME, 12);
        assertThat(bv.browser()).isSameAs(Browser.CHROME);
    }

    @Test
    public void getVersion() {
        BrowserVersion bv = new BrowserVersion(Browser.CHROME, 12);
        assertThat(bv.version()).isEqualTo(12);
    }

    @Test
    public void toStringTest() {
        BrowserVersion bv = new BrowserVersion(Browser.CHROME, 12);
        assertThat(bv.toString()).isNotEqualTo(Util.originalToString(bv));
    }

    @Test
    public void equalsTrue() {
        BrowserVersion bv1 = new BrowserVersion(Browser.CHROME, 12);
        BrowserVersion bv2 = new BrowserVersion(Browser.CHROME, 12);
        assertThat(bv1).isEqualTo(bv2);
    }

    @Test
    public void equalsFalse() {
        BrowserVersion bv1 = new BrowserVersion(Browser.CHROME, 12);
        BrowserVersion bv2 = new BrowserVersion(Browser.CHROME, 13);
        assertThat(bv1).isNotEqualTo(bv2);
    }
}
