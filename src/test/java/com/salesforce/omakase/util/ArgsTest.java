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

package com.salesforce.omakase.util;

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Test;

/**
 * Unit tests for {@link Args}.
 *
 * @author nmcwilliams
 */
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
    public void trimQuotesSimpleSingleQuote() {
        assertThat(Args.trimQuotesSimple("'one, two'")).isEqualTo("one, two");
    }

    @Test
    public void trimQuotesSimpleDoubleQuote() {
        assertThat(Args.trimQuotesSimple("\"one, two\"")).isEqualTo("one, two");
    }

    @Test
    public void trimQuotesSimpleMismatchedQuote() {
        assertThat(Args.trimQuotesSimple("'one, two\"")).isEqualTo("'one, two\"");
    }

    @Test
    public void trimQuotesSimpleClosedBeforeEnd() {
        assertThat(Args.trimQuotesSimple("'one' + 'two'")).isEqualTo("'one' + 'two'");
    }

    @Test
    public void trimQuotesSimpleStartsAndEndsWithSameNonQuoteChar() {
        assertThat(Args.trimQuotesSimple("1one, two1")).isEqualTo("1one, two1");
    }

    @Test
    public void trimQuotesSimpleTrimmed() {
        assertThat(Args.trimQuotesSimple("'  one, two  '")).isEqualTo("one, two");
    }

    @Test
    public void trimDoubleQuotes() {
        assertThat(Args.trimDoubleQuotes("\"one, two\"")).isEqualTo("one, two");
    }

    @Test
    public void trimDoubleQuotesMismatchedQuote() {
        assertThat(Args.trimDoubleQuotes("\"one, two'")).isEqualTo("\"one, two'");
    }

    @Test
    public void trimDoubleQuotesClosedBeforeEnd() {
        assertThat(Args.trimDoubleQuotes("\"one\" + \"two\"")).isEqualTo("\"one\" + \"two\"");
    }

    @Test
    public void trimDoubleQuotesStartsAndEndsWithSameNonQuoteChar() {
        assertThat(Args.trimDoubleQuotes("1one, two1")).isEqualTo("1one, two1");
    }

    @Test
    public void trimDoubleQuotesTrimmed() {
        assertThat(Args.trimDoubleQuotes("\"  one, two  \"")).isEqualTo("one, two");
    }
}
