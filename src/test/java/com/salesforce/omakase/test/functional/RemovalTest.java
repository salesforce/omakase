/*
 * Copyright (C) 2014 salesforce.com, inc.
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

package com.salesforce.omakase.test.functional;

import com.salesforce.omakase.Omakase;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.broadcast.annotation.Rework;
import com.salesforce.omakase.broadcast.annotation.Validate;
import com.salesforce.omakase.error.ErrorManager;
import com.salesforce.omakase.plugin.Plugin;
import com.salesforce.omakase.plugin.basic.AutoRefiner;
import com.salesforce.omakase.plugin.validator.StandardValidation;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests that once a unit is destroyed, it stops being broadcasted to any further plugins
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class RemovalTest {
    private int count;

    public void increment() {
        count++;
    }

    @Before
    public void before() {
        count = 0;
    }

    @Test
    public void destroyedNoLongerBroadcasted() {
        Omakase
            .source(".test{color:red}")
            .request(new AutoRefiner().all())
            .request(new StandardValidation())
            .request(new Plugin() {
                @Rework
                public void test(Declaration d) {
                    increment();
                    d.destroy();
                }
            })
            .request(new Plugin() {
                @Rework
                public void test(Declaration d) {
                    increment();
                }
            })
            .request(new Plugin() {
                @Validate
                public void test(Declaration d, ErrorManager em) {
                    increment();
                }
            })
            .process();

        assertThat(count).isEqualTo(1);
    }
}
