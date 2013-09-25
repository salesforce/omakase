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

package com.salesforce.omakase.plugin;

import com.salesforce.omakase.Omakase;
import com.salesforce.omakase.PluginRegistry;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests that {@link PostProcessingPlugin}s are handled correctly.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class PostProcessingPluginTest {

    @Test
    public void callsMethod() {
        PostProcessingTest test = new PostProcessingTest();
        Omakase.source("p{color:red}").request(test).process();
        assertThat(test.called).isTrue();
    }

    public static final class PostProcessingTest implements PostProcessingPlugin {
        boolean called;

        @Override
        public void postProcess(PluginRegistry registry) {
            called = true;
        }
    }
}
