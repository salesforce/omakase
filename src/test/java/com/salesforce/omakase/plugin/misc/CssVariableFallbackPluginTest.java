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

package com.salesforce.omakase.plugin.misc;

import com.salesforce.omakase.Omakase;
import com.salesforce.omakase.writer.StyleWriter;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

@SuppressWarnings("JavaDoc")
public class CssVariableFallbackPluginTest {

    private void check(String pre, String post) {
        StyleWriter writer = StyleWriter.compressed();
        StringBuilder builder = new StringBuilder();
        Omakase.source(pre).use(new CssVariableFallbackPlugin()).use(writer).process();
        assertThat(writer.write()).isEqualTo(post);
    }

    @Test
    public void noFallbackProperty() {
        check(
            ".test{/* @css-var-fallback */--sds-c-avatar-radius-border: 10%;}",
            ".test{--sds-c-avatar-radius-border:10%}");
    }

    @Test
    public void withFallbackProperty() {
        check(
            ".test{/* @css-var-fallback border-radius */--sds-c-avatar-radius-border: 10%;}",
            ".test{--sds-c-avatar-radius-border:10%;border-radius:10%}");
    }

    @Test
    public void withComplexValue() {
        check(
            ".test{/* @css-var-fallback border-radius */--sds-c-avatar-radius-border: var(--sds-g-radius-border, 10%);}",
            ".test{--sds-c-avatar-radius-border:var(--sds-g-radius-border, 10%);border-radius:10%}");
    }

    @Test
    public void withNestedValue() {
        check(
            ".test{/* @css-var-fallback border-radius */--sds-c-avatar-radius-border: var(--sds-g-radius-border, var(--sds-g-border, var(--pickles, 10%)));}",
            ".test{--sds-c-avatar-radius-border:var(--sds-g-radius-border, var(--sds-g-border, var(--pickles, 10%)));border-radius:10%}");
    }

    @Test
    public void withMultipleAnnotations() {
        check(
            ".test{/* @css-var-fallback background-color */--sds-c-button-color-background: var(--sds-c-button-color-background-disabled, transparent);"
            + "/* @css-var-fallback border-color */--sds-c-button-color-border: var(--sds-c-button-color-border-disabled, transparent);"
            + "/* @css-var-fallback color */--sds-c-button-color-text: var(--sds-c-button-color-text-disabled, #{$color-text-button-default-disabled});"
            + "}",
            ".test{--sds-c-button-color-background:var(--sds-c-button-color-background-disabled, transparent);background-color:transparent;--sds-c-button-color-border:var(--sds-c-button-color-border-disabled, transparent);border-color:transparent;--sds-c-button-color-text:var(--sds-c-button-color-text-disabled, #{$color-text-button-default-disabled});color:#{$color-text-button-default-disabled}}");
    }

    @Test
    public void noAnnotationValueOnly() {
        check(
            ".test{color: var(--sds-c-button-inverse-color-text-active, $color-text-button-default);}",
            ".test{color:$color-text-button-default;color:var(--sds-c-button-inverse-color-text-active, $color-text-button-default)}");
    }
}
