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
import com.salesforce.omakase.ast.declaration.NumericalValue;
import com.salesforce.omakase.broadcast.annotation.Observe;
import com.salesforce.omakase.broadcast.annotation.Restrict;
import com.salesforce.omakase.broadcast.annotation.Rework;
import com.salesforce.omakase.data.Property;
import com.salesforce.omakase.plugin.Plugin;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests usage of the {@link Restrict} annotation.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class FilteringTest {
    private static final String INPUT = ".test {color:red}";

    private Reworker reworker;
    private Observer observer;

    @Before
    public void setup() {
        reworker = new Reworker();
        observer = new Observer();
        Omakase.source(INPUT).use(reworker).use(observer).process();
    }

    @Test
    public void testDynamicUnitsTrue() {
        assertThat(observer.dynamicTrueCount).isEqualTo(2);
    }

    @Test
    public void testDynamicUnitsFalse() {
        assertThat(observer.dynamicFalseCount).isEqualTo(1);
    }

    @Test
    public void testRawUnitsTrue() {
        assertThat(observer.rawTrueCount).isEqualTo(2);
    }

    @Test
    public void testRawUnitsFalse() {
        assertThat(observer.rawFalseCount).isEqualTo(1);
    }

    private static final class Reworker implements Plugin {
        @Rework
        public void rework(Declaration d) {
            if (d.isProperty(Property.COLOR)) {
                d.append(new Declaration(Property.MARGIN, NumericalValue.of(5, "px")));
            }
        }
    }

    @SuppressWarnings("UnusedParameters")
    private static final class Observer implements Plugin {
        int dynamicTrueCount;
        int dynamicFalseCount;
        int rawTrueCount;
        int rawFalseCount;

        @Observe
        @Restrict(dynamicUnits = true)
        public void dynamicTrue(Declaration d) {
            dynamicTrueCount++;
        }

        @Observe
        @Restrict(dynamicUnits = false)
        public void dynamicFalse(Declaration d) {
            dynamicFalseCount++;
        }

        @Observe
        @Restrict(rawUnits = true)
        public void rawTrue(Declaration d) {
            rawTrueCount++;
        }

        @Observe
        @Restrict(rawUnits = false)
        public void rawFalse(Declaration d) {
            rawFalseCount++;
        }
    }
}
