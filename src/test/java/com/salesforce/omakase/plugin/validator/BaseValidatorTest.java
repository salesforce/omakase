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

import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.salesforce.omakase.Omakase;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.error.ErrorLevel;
import com.salesforce.omakase.error.ErrorManager;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.plugin.Plugin;
import com.salesforce.omakase.plugin.basic.AutoRefiner;
import com.salesforce.omakase.plugin.basic.SyntaxTree;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Base test for validation unit tests.
 * <p/>
 * This will only work if the validator has a no-org constructor.
 */
@SuppressWarnings("JavaDoc")
public class BaseValidatorTest<T extends Plugin> {
    protected Plugin plugin;

    @SuppressWarnings("unchecked")
    public BaseValidatorTest() {
        try {
            Class<T> klass = (Class<T>)(new TypeToken<T>(getClass()) {}).getRawType();
            this.plugin = klass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("error creating new instance of validator", e);
        }
    }

    /**
     * Tests that the given input source does not cause any validation errors.
     *
     * @param src
     *     The source to test.
     */
    protected void expectSuccess(String src) {
        ValidationErrorManager em = testValidation(src);
        assertThat(em.message).describedAs("Expected this source to pass validation: " + src).isNullOrEmpty();

    }

    /**
     * Tests that all of the given sources pass validation.
     *
     * @param sources
     *     Sources to test.
     */
    protected void expectAllSuccess(String... sources) {
        expectAllSuccess(Lists.newArrayList(sources));
    }

    /**
     * Tests that all of the given sources pass validation.
     *
     * @param sources
     *     Sources to test.
     */
    protected void expectAllSuccess(Iterable<String> sources) {
        for (String source : sources) {
            expectSuccess(source);
        }
    }

    /**
     * Tests that the given input source fails validation with the given level and message.
     *
     * @param src
     *     The source to test.
     * @param level
     *     The expected error level.
     * @param messageContains
     *     The error message should contain this string.
     */
    protected void expectFailure(String src, ErrorLevel level, String messageContains) {
        ValidationErrorManager em = testValidation(src);
        assertThat(em.message)
            .describedAs("Expected this source to fail validation: " + src)
            .containsIgnoringCase(messageContains);

        assertThat(em.level).describedAs(src).isSameAs(level);
    }

    /**
     * Tests that all of the given sources fail validation with the given {@link ErrorLevel} and message.
     *
     * @param sources
     *     The sources to test.
     * @param level
     *     The expected error level.
     * @param messageContains
     *     The error message should contain this string.
     */
    protected void expectAllToFail(ErrorLevel level, String messageContains, String... sources) {
        expectAllToFail(level, messageContains, Lists.newArrayList(sources));
    }

    /**
     * Tests that all of the given sources fail validation with the given {@link ErrorLevel} and message.
     *
     * @param sources
     *     The sources to test.
     * @param level
     *     The expected error level.
     * @param messageContains
     *     The error message should contain this string.
     */
    protected void expectAllToFail(ErrorLevel level, String messageContains, Iterable<String> sources) {
        for (String source : sources) {
            expectFailure(source, level, messageContains);
        }
    }

    /**
     * Process the given source.
     *
     * @param src
     *     The source to test.
     *
     * @return The error manager with the validation results.
     */
    protected ValidationErrorManager testValidation(String src) {
        ValidationErrorManager em = new ValidationErrorManager();
        Omakase.source(src)
            .request(em)
            .request(new AutoRefiner().all())
            .request(new SyntaxTree())
            .request(plugin)
            .process();

        return em;
    }

    /** Helper error manager. */
    public static final class ValidationErrorManager implements ErrorManager {
        String message;
        ErrorLevel level;

        @Override
        public void report(ErrorLevel level, ParserException exception) {
            this.level = level;
            this.message = exception.getMessage();
        }

        @Override
        public void report(ErrorLevel level, Syntax cause, String message) {
            this.level = level;
            this.message = message;
        }

        @Override
        public String getSourceName() {
            return null;
        }
    }
}
