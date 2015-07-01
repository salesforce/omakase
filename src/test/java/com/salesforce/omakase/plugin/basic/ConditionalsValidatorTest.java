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

package com.salesforce.omakase.plugin.basic;

import com.salesforce.omakase.Omakase;
import com.salesforce.omakase.PluginRegistry;
import com.salesforce.omakase.error.FatalException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link ConditionalsValidator}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class ConditionalsValidatorTest {
    @Rule public final ExpectedException exception = ExpectedException.none();

    @Test
    public void validatesBadBlockSyntax() {
        String src = "@if {.class{color:red}}";
        exception.expect(FatalException.class);
        Omakase.source(src).use(new ConditionalsValidator()).process();
    }

    @Test
    public void validatesBadInnerSyntax() {
        String src = "@if(ie7) {.class{color:red}";
        exception.expect(FatalException.class);
        Omakase.source(src).use(new ConditionalsValidator()).process();
    }

    @Test
    public void noErrorIfConditionAllowed() {
        String src = "@if(ie7) {.class{color:red}}";
        Omakase.source(src).use(new ConditionalsValidator("ie7")).process();
        // no error
    }

    @Test
    public void errorsIfConditionNotAllowed() {
        String src = "@if(ie8) {.class{color:red}}";
        exception.expect(FatalException.class);
        exception.expectMessage("Invalid condition");
        Omakase.source(src).use(new ConditionalsValidator("ie7")).process();
    }

    @Test
    public void errorsIfSomeConditionsNotAllowed() {
        String src = "@if(ie7 || mobile) {.class{color:red}}";
        exception.expect(FatalException.class);
        exception.expectMessage("Invalid condition");
        Omakase.source(src).use(new ConditionalsValidator("ie7", "desktop")).process();
    }

    @Test
    public void dependenciesWhenConditionalsPresent() {
        String src = "@if(ie8) {.class{color:red}}";
        PluginRegistry registry = Omakase.source(src)
            .use(new Conditionals())
            .use(new ConditionalsValidator())
            .process();

        assertThat(registry.retrieve(Conditionals.class).get().config().isPassthroughMode()).isFalse();
    }

    @Test
    public void dependenciesWhenConditionalsNotPresent() {
        String src = "@if(ie8) {.class{color:red}}";
        PluginRegistry registry = Omakase.source(src)
            .use(new ConditionalsValidator())
            .process();

        assertThat(registry.retrieve(Conditionals.class).get().config().isPassthroughMode()).isTrue();
    }
}
