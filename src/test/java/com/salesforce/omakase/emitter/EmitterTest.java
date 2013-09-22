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

import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.selector.ClassSelector;
import com.salesforce.omakase.ast.selector.SimpleSelector;
import com.salesforce.omakase.error.ThrowingErrorManager;
import com.salesforce.omakase.plugin.Plugin;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.*;

/**
 * Unit tests for {@link Emitter}
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class EmitterTest {
    @Test
    public void defaultPhase() {
        Emitter emitter = new Emitter();
        assertThat(emitter.phase()).isSameAs(SubscriptionPhase.PREPROCESS);
    }

    @Test
    public void setAndGetPhase() {
        Emitter emitter = new Emitter();
        emitter.phase(SubscriptionPhase.VALIDATE);
        assertThat(emitter.phase()).isSameAs(SubscriptionPhase.VALIDATE);
    }

    @Test
    public void hierarchy() {
        Emitter emitter = new Emitter();
        EmitterPlugin plugin = new EmitterPlugin();
        emitter.register(plugin);
        emitter.phase(SubscriptionPhase.PROCESS);

        emitter.emit(new ClassSelector("test"), new ThrowingErrorManager());

        assertThat(plugin.calledClassSelector).isTrue();
        assertThat(plugin.calledSimpleSelector).isTrue();
        assertThat(plugin.calledSyntax).isTrue();
    }

    @Test
    public void samePluginTwice() {
        Emitter emitter = new Emitter();
        EmitterPlugin2 plugin = new EmitterPlugin2();
        emitter.register(plugin);
        emitter.register(plugin);

        emitter.emit(new ClassSelector("test"), new ThrowingErrorManager());

        assertThat(plugin.count).isEqualTo(1);
    }

    @SuppressWarnings("UnusedParameters")
    public static final class EmitterPlugin implements Plugin {
        boolean calledSyntax;
        boolean calledSimpleSelector;
        boolean calledClassSelector;

        @Observe
        public void syntax(Syntax s) {
            this.calledSyntax = true;
        }

        @Observe
        public void simpleSelector(SimpleSelector s) {
            this.calledSimpleSelector = true;
        }

        @Observe
        public void classSelector(ClassSelector s) {
            this.calledClassSelector = true;
        }
    }

    public static final class EmitterPlugin2 implements Plugin {
        int count;

        @PreProcess
        public void preprocess(ClassSelector cs) {
            count++;
        }
    }
}
