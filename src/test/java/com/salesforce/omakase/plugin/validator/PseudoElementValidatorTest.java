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

package com.salesforce.omakase.plugin.validator;

import com.google.common.collect.ImmutableList;
import com.salesforce.omakase.Message;
import com.salesforce.omakase.error.ErrorLevel;

import static com.salesforce.omakase.test.util.TemplatesHelper.fillSelector;

/** Unit tests for {@link PseudoElementValidator}. */
public class PseudoElementValidatorTest extends SimpleValidatorTest<PseudoElementValidator> {
    @Override
    public Iterable<String> validSources() {
        return ImmutableList.of(
            fillSelector("p"),
            fillSelector(".class"),
            fillSelector(".class:hover"),
            fillSelector(".class:before"),
            fillSelector(".class::before"),
            fillSelector(".class:after"),
            fillSelector(".class1, class2:before, class3:after"),
            fillSelector("#id>.class + #id2 a:hover::before")
        );
    }

    @Override
    public Iterable<String> invalidSources() {
        return ImmutableList.of(
            fillSelector(".class:before .class2"),
            fillSelector(".class:after .class2"),
            fillSelector(".class::before .class2"),
            fillSelector(".class::selection .class2"),
            fillSelector(".class:before > .class2"),
            fillSelector("#id1, .class:before .class2, class3"),
            fillSelector("*::before:hover"),
            fillSelector("p div p div p p p:before .class1 + class2:hover"),
            fillSelector("a:hover::before .class:before")
        );
    }

    @Override
    public String failureMessage() {
        return Message.PSEUDO_ELEMENT_LAST.message();
    }

    @Override
    public ErrorLevel failureLevel() {
        return ErrorLevel.FATAL;
    }
}
