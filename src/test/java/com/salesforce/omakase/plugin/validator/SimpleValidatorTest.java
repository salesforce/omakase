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

import com.salesforce.omakase.error.ErrorLevel;
import com.salesforce.omakase.plugin.Plugin;
import org.junit.Test;

/** For testing validators that only validate one thing. */
@SuppressWarnings("JavaDoc")
public abstract class SimpleValidatorTest<T extends Plugin> extends BaseValidatorTest<T> {
    public abstract Iterable<String> validSources();

    public abstract Iterable<String> invalidSources();

    public abstract String failureMessage();

    public abstract ErrorLevel failureLevel();

    @Test
    public void allValidSourcesPassValidation() {
        expectAllSuccess(validSources());
    }

    @Test
    public void allInvalidSourcesFailValidation() {
        expectAllToFail(failureLevel(), failureMessage(), invalidSources());
    }
}
