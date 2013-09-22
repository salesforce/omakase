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

package com.salesforce.omakase.emitter;

import com.google.common.collect.Maps;
import com.salesforce.omakase.ast.selector.ClassSelector;
import com.salesforce.omakase.error.ErrorManager;
import com.salesforce.omakase.plugin.Plugin;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Map;

import static org.fest.assertions.api.Assertions.*;

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
    public void findsPreProcess() {
        Map<String, Subscription> map = Maps.newHashMap();
        for (Subscription subscription : scanner.scan(new AllValid()).get(ClassSelector.class)) {
            map.put(subscription.method().getName(), subscription);
        }

        Subscription preprocess = map.get("preprocess");
        assertThat(preprocess != null).describedAs("expected to find method annotated with @PreProcess");
        assertThat(preprocess.phase()).isSameAs(SubscriptionPhase.PREPROCESS);
    }

    @Test
    public void errorsIfInvalidPreprocess() {
        exception.expect(Exception.class);
        scanner.scan(new InvalidPreprocess());
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

    @SuppressWarnings("UnusedParameters")
    public static final class AllValid implements Plugin {
        @PreProcess
        public void preprocess(ClassSelector cs) {
        }

        @Observe
        public void observe(ClassSelector cs) {
        }

        @Rework
        public void rework(ClassSelector cs) {
        }

        @Validate
        public void validate(ClassSelector cs, ErrorManager em) {
        }
    }

    public static final class InvalidPreprocess implements Plugin {
        @PreProcess
        public void preprocess() {
        }
    }

    public static final class InvalidObserve implements Plugin {
        @Observe
        public void observe() {
        }
    }

    public static final class InvalidRework implements Plugin {
        @Rework
        public void rework() {
        }
    }

    public static final class InvalidValidate implements Plugin {
        @Validate
        public void validate() {
        }
    }
}
