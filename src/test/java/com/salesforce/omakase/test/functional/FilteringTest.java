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
