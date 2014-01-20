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

package com.salesforce.omakase.util;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link Args}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class ArgsTest {

    @Test
    public void getEmptyString() {
        assertThat(Args.get("")).isEmpty();
        assertThat(Args.get(" ")).isEmpty();
    }

    @Test
    public void getOneHasParenthesis() {
        assertThat(Args.get("(one)")).containsExactly("one");
    }

    @Test
    public void getOneNoParenthesis() {
        assertThat(Args.get("one")).containsExactly("one");
    }

    @Test
    public void getSeveralHasParenthesis() {
        assertThat(Args.get("(one, two, three)")).containsExactly("one", "two", "three");
    }

    @Test
    public void getSeveralNoParenthesis() {
        assertThat(Args.get("one, two, three")).containsExactly("one", "two", "three");
    }

    @Test
    public void getResultsAreTrimmed() {
        assertThat(Args.get("(   one   )")).containsExactly("one");
    }

    @Test
    public void getSkipsEmptyArg() {
        assertThat(Args.get("one, ,, three")).containsExactly("one", "three");
    }

    @Test
    public void trimParensEmptyString() {
        assertThat(Args.trimParens("")).isEqualTo("");
    }

    @Test
    public void trimParensAbsentDoesnttTrimResult() {
        assertThat(Args.trimParens("  ")).isEqualTo("  ");
    }

    @Test
    public void trimParensPresent() {
        assertThat(Args.trimParens("(123)")).isEqualTo("123");
    }

    @Test
    public void trimParensAbsent() {
        assertThat(Args.trimParens("12)3")).isEqualTo("12)3");
    }

    @Test
    public void trimParensPresentTrimsInsideParens() {
        assertThat(Args.trimParens("(  123 )")).isEqualTo("123");
    }

    @Test
    public void trimParensLeadingWhitespace() {
        assertThat(Args.trimParens("   (123)")).isEqualTo("123");
    }

    @Test
    public void trimParensTrailingWhitespace() {
        assertThat(Args.trimParens("(123)   ")).isEqualTo("123");
    }

    @Test
    public void extractStartsWithName() {
        assertThat(Args.extract("test(one)")).isEqualTo("one");
        assertThat(Args.extract("test(one, two)")).isEqualTo("one, two");
        assertThat(Args.extract("test(one-def)")).isEqualTo("one-def");
    }

    @Test
    public void extractNoNamePresent() {
        assertThat(Args.extract("(one)")).isEqualTo("one");
        assertThat(Args.extract("(one, two )")).isEqualTo("one, two ");
        assertThat(Args.extract("(one-def) ")).isEqualTo("one-def");
    }

    @Test
    public void trimQuotesSingleQuote() {
        assertThat(Args.trimQuotesSimple("'one, two'")).isEqualTo("one, two");
    }

    @Test
    public void trimQuotesDoubleQuote() {
        assertThat(Args.trimQuotesSimple("\"one, two\"")).isEqualTo("one, two");
    }

    @Test
    public void trimQuotesMismatchedQuote() {
        assertThat(Args.trimQuotesSimple("'one, two\"")).isEqualTo("'one, two\"");
    }

    @Test
    public void trimQuotesClosedBeforeEnd() {
        assertThat(Args.trimQuotesSimple("'one' + 'two'")).isEqualTo("'one' + 'two'");
    }

    @Test
    public void trimQuotesStartsAndEndsWithSameNonQuoteChar() {
        assertThat(Args.trimQuotesSimple("1one, two1")).isEqualTo("1one, two1");
    }

    @Test
    public void trimQuotesTrimmed() {
        assertThat(Args.trimQuotesSimple("'  one, two  '")).isEqualTo("one, two");
    }
}
