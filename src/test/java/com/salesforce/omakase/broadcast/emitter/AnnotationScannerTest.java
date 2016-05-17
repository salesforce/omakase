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

package com.salesforce.omakase.broadcast.emitter;

import com.google.common.collect.Maps;
import com.salesforce.omakase.ast.selector.ClassSelector;
import com.salesforce.omakase.broadcast.annotation.Observe;
import com.salesforce.omakase.broadcast.annotation.Restrict;
import com.salesforce.omakase.broadcast.annotation.Rework;
import com.salesforce.omakase.broadcast.annotation.Validate;
import com.salesforce.omakase.error.ErrorManager;
import com.salesforce.omakase.plugin.Plugin;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Map;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link AnnotationScanner}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings({"JavaDoc", "ConstantConditions"})
public class AnnotationScannerTest {
    @Rule public final ExpectedException exception = ExpectedException.none();

    private AnnotationScanner scanner;

    @Before
    public void setup() {
        scanner = new AnnotationScanner();
    }

    @Test
    public void findsRework() {
        Map<String, Subscription> map = Maps.newHashMap();
        for (Subscription subscription : scanner.scan(new AllValid()).get(ClassSelector.class)) {
            map.put(subscription.method().getName(), subscription);
        }

        Subscription preprocess = map.get("rework");
        assertThat(preprocess != null).describedAs("expected to find method annotated with @Rework");
        assertThat(preprocess.phase()).isSameAs(SubscriptionPhase.PROCESS);
    }

    @Test
    public void errorsIfInvalidRework() {
        exception.expect(Exception.class);
        scanner.scan(new InvalidRework());
    }

    @Test
    public void findsObserve() {
        Map<String, Subscription> map = Maps.newHashMap();
        for (Subscription subscription : scanner.scan(new AllValid()).get(ClassSelector.class)) {
            map.put(subscription.method().getName(), subscription);
        }

        Subscription preprocess = map.get("observe");
        assertThat(preprocess != null).describedAs("expected to find method annotated with @Observe");
        assertThat(preprocess.phase()).isSameAs(SubscriptionPhase.PROCESS);
    }

    @Test
    public void errorsIfInvalidObserve() {
        exception.expect(Exception.class);
        scanner.scan(new InvalidObserve());
    }

    @Test
    public void findsValidate() {
        Map<String, Subscription> map = Maps.newHashMap();
        for (Subscription subscription : scanner.scan(new AllValid()).get(ClassSelector.class)) {
            map.put(subscription.method().getName(), subscription);
        }

        Subscription preprocess = map.get("validate");
        assertThat(preprocess != null).describedAs("expected to find method annotated with @Validate");
        assertThat(preprocess.phase()).isSameAs(SubscriptionPhase.VALIDATE);
    }

    @Test
    public void errorsIfInvalidValidate() {
        exception.expect(Exception.class);
        scanner.scan(new InvalidValidate());
    }

    @Test
    public void findsRestrict() {
        Map<String, Subscription> map = Maps.newHashMap();
        for (Subscription subscription : scanner.scan(new AllValid()).get(ClassSelector.class)) {
            map.put(subscription.method().getName(), subscription);
        }

        Subscription preprocess = map.get("observe2");
        assertThat(preprocess != null).describedAs("expected to find method annotated with @Restrict");
        assertThat(preprocess.restriction().isPresent()).isTrue();
    }

    @Test
    public void noRestrict() {
        Map<String, Subscription> map = Maps.newHashMap();
        for (Subscription subscription : scanner.scan(new AllValid()).get(ClassSelector.class)) {
            map.put(subscription.method().getName(), subscription);
        }

        Subscription preprocess = map.get("observe");
        assertThat(preprocess.restriction().isPresent()).isFalse();
    }

    @SuppressWarnings("UnusedParameters")
    public static final class AllValid implements Plugin {
        @Observe
        public void observe(ClassSelector cs) {}

        @Rework
        public void rework(ClassSelector cs) {}

        @Validate
        public void validate(ClassSelector cs, ErrorManager em) {}

        @Observe
        @Restrict(dynamicUnits = false)
        public void observe2(ClassSelector cs) {}
    }

    public static final class InvalidObserve implements Plugin {
        @Observe
        public void observe() {}
    }

    public static final class InvalidRework implements Plugin {
        @Rework
        public void rework() {}
    }

    public static final class InvalidValidate implements Plugin {
        @Validate
        public void validate() {}
    }
}
