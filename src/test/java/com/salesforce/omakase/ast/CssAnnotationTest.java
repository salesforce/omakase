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

import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.EnumSet;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Fail.fail;

/**
 * Unit tests for {@link CssAnnotation}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class CssAnnotationTest {
    @org.junit.Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void getName() {
        CssAnnotation a = new CssAnnotation("test");
        assertThat(a.name()).isEqualTo("test");
    }

    @Test
    public void getRawArgsNotPresent() {
        CssAnnotation a = new CssAnnotation("test");
        assertThat(a.rawArgs().isPresent()).isFalse();
    }

    @Test
    public void getRawArgsPresent() {
        CssAnnotation a = new CssAnnotation("test", "foo");
        assertThat(a.rawArgs().get()).isEqualTo("foo");
    }

    @Test
    public void getSpaceSeparatedNoArgs() {
        CssAnnotation a = new CssAnnotation("test");
        assertThat(a.spaceSeparatedArgs().isEmpty());
    }

    @Test
    public void getSpaceSeparatedSingleArg() {
        CssAnnotation a = new CssAnnotation("test", "foo");
        assertThat(a.spaceSeparatedArgs()).containsExactly("foo");
    }

    @Test
    public void getSpaceSeparatedMultipleArgs() {
        CssAnnotation a = new CssAnnotation("test", "foo bar baz");
        assertThat(a.spaceSeparatedArgs()).containsExactly("foo", "bar", "baz");
    }

    @Test
    public void getCommaSeparatedNoArgs() {
        CssAnnotation a = new CssAnnotation("test");
        assertThat(a.commaSeparatedArgs().isEmpty());
    }

    @Test
    public void getCommaSeparatedSingleArg() {
        CssAnnotation a = new CssAnnotation("test", "foo");
        assertThat(a.commaSeparatedArgs()).containsExactly("foo");
    }

    @Test
    public void getCommaSeparatedMultipleArgs() {
        CssAnnotation a = new CssAnnotation("test", "foo, bar,baz,bip,  ");
        assertThat(a.commaSeparatedArgs()).containsExactly("foo", "bar", "baz", "bip");
    }

    @Test
    public void getKeyValueNoArgs() {
        CssAnnotation a = new CssAnnotation("test");
        assertThat(a.keyValueArgs(' ').isEmpty());
    }

    @Test
    public void getKeyValueSpacesEmptyArgs() {
        CssAnnotation a = new CssAnnotation("test", " ");
        assertThat(a.keyValueArgs(' ').isEmpty());

        a = new CssAnnotation("test", "");
        assertThat(a.keyValueArgs(' ').isEmpty());
    }

    @Test
    public void getKeyValueSpacesSingleArg() {
        CssAnnotation a = new CssAnnotation("test", "foo bar");
        ImmutableMap<String, String> map = a.keyValueArgs(' ');
        assertThat(map).hasSize(1);
        assertThat(map.get("foo")).isEqualTo("bar");
    }

    @Test
    public void getKeyValueSpacesMultiArgs() {
        CssAnnotation a = new CssAnnotation("test", "foo bar, baz boo, bim bop");
        ImmutableMap<String, String> map = a.keyValueArgs(' ');
        assertThat(map).hasSize(3);
        assertThat(map.get("foo")).isEqualTo("bar");
        assertThat(map.get("baz")).isEqualTo("boo");
        assertThat(map.get("bim")).isEqualTo("bop");
    }

    @Test
    public void getKeyValueSpacesImpliedNameOnly() {
        CssAnnotation a = new CssAnnotation("test", "foo");
        ImmutableMap<String, String> map = a.keyValueArgs(' ');
        assertThat(map).hasSize(1);
        assertThat(map.get("name")).isEqualTo("foo");
    }

    @Test
    public void getKeyValueSpacesImpliedNameMultiArgs() {
        CssAnnotation a = new CssAnnotation("test", "foo, baz boo, bim bop");
        ImmutableMap<String, String> map = a.keyValueArgs(' ');
        assertThat(map).hasSize(3);
        assertThat(map.get("name")).isEqualTo("foo");
        assertThat(map.get("baz")).isEqualTo("boo");
        assertThat(map.get("bim")).isEqualTo("bop");
    }

    @Test
    public void getKeyValueEqualsSingleArg() {
        CssAnnotation a = new CssAnnotation("test", "foo=bar");
        ImmutableMap<String, String> map = a.keyValueArgs('=');
        assertThat(map).hasSize(1);
        assertThat(map.get("foo")).isEqualTo("bar");
    }

    @Test
    public void getKeyValueEqualsMultiArgs() {
        CssAnnotation a = new CssAnnotation("test", "foo=bar, baz=boo, bim=bop");
        ImmutableMap<String, String> map = a.keyValueArgs('=');
        assertThat(map).hasSize(3);
        assertThat(map.get("foo")).isEqualTo("bar");
        assertThat(map.get("baz")).isEqualTo("boo");
        assertThat(map.get("bim")).isEqualTo("bop");
    }

    @Test
    public void getKeyValueEqualsMultiArgsSpaceAround() {
        CssAnnotation a = new CssAnnotation("test", "foo = bar, baz = boo, bim = bop");
        ImmutableMap<String, String> map = a.keyValueArgs('=');
        assertThat(map).hasSize(3);
        assertThat(map.get("foo")).isEqualTo("bar");
        assertThat(map.get("baz")).isEqualTo("boo");
        assertThat(map.get("bim")).isEqualTo("bop");
    }

    @Test
    public void getKeyValuEqualsImpliedNameOnly() {
        CssAnnotation a = new CssAnnotation("test", "foo");
        ImmutableMap<String, String> map = a.keyValueArgs('=');
        assertThat(map).hasSize(1);
        assertThat(map.get("name")).isEqualTo("foo");
    }

    @Test
    public void getKeyValuEqualsImpliedNameMultipleArgs() {
        CssAnnotation a = new CssAnnotation("test", "foo, baz=boo, bim=bop");
        ImmutableMap<String, String> map = a.keyValueArgs('=');
        assertThat(map).hasSize(3);
        assertThat(map.get("name")).isEqualTo("foo");
        assertThat(map.get("baz")).isEqualTo("boo");
        assertThat(map.get("bim")).isEqualTo("bop");
    }

    @Test
    public void getKeyValueColonMultiArgs() {
        CssAnnotation a = new CssAnnotation("test", "foo:bar, baz:boo, bim:bop");
        ImmutableMap<String, String> map = a.keyValueArgs(':');
        assertThat(map).hasSize(3);
        assertThat(map.get("foo")).isEqualTo("bar");
        assertThat(map.get("baz")).isEqualTo("boo");
        assertThat(map.get("bim")).isEqualTo("bop");
    }

    public enum TestEnum {FOO, BAR, BAZ_QUX}

    @Test
    public void getFromEnumNoArgs() {
        CssAnnotation a = new CssAnnotation("test");
        assertThat(a.fromEnum(TestEnum.class)).isEmpty();
    }

    @Test
    public void getFromEnumOneArg() {
        CssAnnotation a = new CssAnnotation("test", "bar");
        EnumSet<TestEnum> set = a.fromEnum(TestEnum.class);
        assertThat(set).containsExactly(TestEnum.BAR);
    }

    @Test
    public void getFromEnumMultiArgsLowerCamel() {
        CssAnnotation a = new CssAnnotation("test", "bar bazQux");
        EnumSet<TestEnum> set = a.fromEnum(TestEnum.class);
        assertThat(set).containsExactly(TestEnum.BAR, TestEnum.BAZ_QUX);
    }

    @Test
    public void getFromEnumMultiArgsTitleCase() {
        CssAnnotation a = new CssAnnotation("test", "BAR BAZ_QUX");
        EnumSet<TestEnum> set = a.fromEnum(TestEnum.class);
        assertThat(set).containsExactly(TestEnum.BAR, TestEnum.BAZ_QUX);
    }

    @Test
    public void getFromEnumNoMatchThrowsError() {
        CssAnnotation a = new CssAnnotation("test", "bar baz");

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Invalid");
        EnumSet<TestEnum> set = a.fromEnum(TestEnum.class);
    }

    public enum TestEnum2 {foo, bar_um}

    @Test
    public void getFromEnumCustomCastFormat() {
        CssAnnotation a = new CssAnnotation("test", "foo barUm");
        EnumSet<TestEnum2> set = a.fromEnum(TestEnum2.class, CaseFormat.LOWER_UNDERSCORE, CaseFormat.LOWER_CAMEL);
        assertThat(set).containsExactly(TestEnum2.foo, TestEnum2.bar_um);
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
        CssAnnotation a = new CssAnnotation("test", "one two three");
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
        CssAnnotation a = new CssAnnotation("test", "one two");
        CssAnnotation b = new CssAnnotation("test", "one two");
        assertThat(a).isEqualTo(b);
    }

    @Test
    public void equalsFalseWithArgs() {
        CssAnnotation a = new CssAnnotation("test", "one two");
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
