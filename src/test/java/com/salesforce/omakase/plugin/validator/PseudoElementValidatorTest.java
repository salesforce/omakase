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

package com.salesforce.omakase.plugin.validator;

import com.google.common.collect.ImmutableList;
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
            fillSelector("#id>.class + #id2 a:hover::before"),
            fillSelector(".test div::before:hover"),
            fillSelector("*::before:hover")
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
            fillSelector("*::before:hover::after"),
            fillSelector("p div p div p p p:before .class1 + class2:hover"),
            fillSelector("a:hover::before .class:before"),
            fillSelector(".test div::before:hover a")
        );
    }

    @Override
    public String failureMessage() {
        return "Only pseudo-classes are allowed after the pseudo-element";
    }

    @Override
    public ErrorLevel failureLevel() {
        return ErrorLevel.FATAL;
    }
}
