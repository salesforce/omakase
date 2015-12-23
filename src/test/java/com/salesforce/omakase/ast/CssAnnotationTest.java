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

package com.salesforce.omakase.ast;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link CssAnnotation}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class CssAnnotationTest {
    @Test
    public void testName() {
        CssAnnotation a = new CssAnnotation("test");
        assertThat(a.name()).isEqualTo("test");
    }

    @Test
    public void argumentWhenNoArgs() {
        CssAnnotation a = new CssAnnotation("test");
        assertThat(a.argument().isPresent()).isFalse();
    }

    @Test
    public void argumentWhenOneArg() {
        CssAnnotation a = new CssAnnotation("test", "one");
        assertThat(a.argument().get()).isEqualTo("one");
    }

    @Test
    public void argumentWhenMultipleArgs() {
        CssAnnotation a = new CssAnnotation("test", "one", "two");
        assertThat(a.argument().get()).isEqualTo("one");
    }

    @Test
    public void argumentAtIndexInBounds() {
        CssAnnotation a = new CssAnnotation("test", "one", "two");
        assertThat(a.argument(1).get()).isEqualTo("two");
    }

    @Test
    public void argumentAtIndexOutOfBounds() {
        CssAnnotation a = new CssAnnotation("test", "one", "two");
        assertThat(a.argument(2).isPresent()).isFalse();
    }

    @Test
    public void argumentAtIndexNoArgs() {
        CssAnnotation a = new CssAnnotation("test");
        assertThat(a.argument(2).isPresent()).isFalse();
    }

    @Test
    public void allArgumentsNoArgsPresent() {
        CssAnnotation a = new CssAnnotation("test");
        assertThat(a.arguments()).isEmpty();
    }

    @Test
    public void allArgumentsArgsPresent() {
        CssAnnotation a = new CssAnnotation("test", "one", "two");
        assertThat(a.arguments()).contains("one", "two");
    }

    @Test
    public void hasArgumentFalseNoArgs() {
        CssAnnotation a = new CssAnnotation("test");
        assertThat(a.hasArgument("one")).isFalse();
    }

    @Test
    public void hasArgumentFalseDifferentArg() {
        CssAnnotation a = new CssAnnotation("test", "a");
        assertThat(a.hasArgument("one")).isFalse();
    }

    @Test
    public void hasArgumentTrue() {
        CssAnnotation a = new CssAnnotation("test", "one");
        assertThat(a.hasArgument("one")).isTrue();
    }

    @Test
    public void toComment() {
        CssAnnotation a = new CssAnnotation("test");
        assertThat(a.toComment(true).content()).isEqualTo("@test");
    }

    @Test
    public void toCommentCachedTrue() {
        CssAnnotation a = new CssAnnotation("test");
        Comment c = a.toComment(true);
        assertThat(a.toComment(true)).isSameAs(c);
    }

    @Test
    public void toCommentCachedFalse() {
        CssAnnotation a = new CssAnnotation("test");
        Comment c = a.toComment(true);
        assertThat(a.toComment(false)).isNotSameAs(c);
    }

    @Test
    public void toCommentCachedPreviouslyCalledNotCached() {
        CssAnnotation a = new CssAnnotation("test");
        Comment c = a.toComment(false);
        assertThat(a.toComment(true)).isNotSameAs(c);
    }

    @Test
    public void toStringNoArgs() {
        CssAnnotation a = new CssAnnotation("test");
        assertThat(a.toString()).isEqualTo("@test");
    }

    @Test
    public void toStringWithArgs() {
        CssAnnotation a = new CssAnnotation("test", "one", "two", "three");
        assertThat(a.toString()).isEqualTo("@test one two three");
    }

    @Test
    public void equalsTrueNoArgs() {
        CssAnnotation a = new CssAnnotation("test");
        CssAnnotation b = new CssAnnotation("test");
        assertThat(a).isEqualTo(b);
    }

    @Test
    public void equalsFalseNoArgs() {
        CssAnnotation a = new CssAnnotation("test");
        CssAnnotation b = new CssAnnotation("it");
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    public void equalsTrueWithArgs() {
        CssAnnotation a = new CssAnnotation("test", "one", "two");
        CssAnnotation b = new CssAnnotation("test", "one", "two");
        assertThat(a).isEqualTo(b);
    }

    @Test
    public void equalsFalseWithArgs() {
        CssAnnotation a = new CssAnnotation("test", "one", "two");
        CssAnnotation b = new CssAnnotation("test", "one");
        CssAnnotation c = new CssAnnotation("test");
        assertThat(a).isNotEqualTo(b);
        assertThat(a).isNotEqualTo(c);
        assertThat(b).isNotEqualTo(c);
    }

    @Test
    public void equalsFalseDifferentObj() {
        assertThat(new CssAnnotation("test").equals("test")).isFalse();
    }

    @Test
    public void hashCodeTest() {
        CssAnnotation a = new CssAnnotation("test");
        CssAnnotation b = new CssAnnotation("test");
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }
}
