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
import com.salesforce.omakase.ast.declaration.RawFunction;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.annotation.Rework;
import com.salesforce.omakase.parser.refiner.FunctionRefiner;
import com.salesforce.omakase.parser.refiner.MasterRefiner;
import com.salesforce.omakase.plugin.Plugin;
import com.salesforce.omakase.plugin.basic.AutoRefiner;
import com.salesforce.omakase.plugin.validator.StandardValidation;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Raw function refinement ordering has had issues. need a high-level test.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class RawFunctionRefinementTest {
    @Test
    public void destroyedNoLongerBroadcasted() {
        Omakase
            .source(".test{color:test(abc)}")
            .use(new AutoRefiner().all())
            .use(new StandardValidation())
            .use(new Ref())
            .use(new Plugin() {
                @Rework
                public void test(RawFunction raw) {
                    raw.args("xyz");
                }
            })

            .process();
    }

    private static final class Ref implements Plugin, FunctionRefiner {
        @Override
        public boolean refine(RawFunction raw, Broadcaster broadcaster, MasterRefiner refiner) {
            assertThat(raw.args()).isEqualTo("xyz");
            return false;
        }
    }
}
