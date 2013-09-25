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

package com.salesforce.omakase.ast.declaration;

import com.salesforce.omakase.writer.StyleWriter;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;

import static org.fest.assertions.api.Assertions.*;

/** Unit tests for {@link PropertyName}. */
@SuppressWarnings("JavaDoc")
public class PropertyNameTest {
    private static final String PREFIX = "-webkit-";
    private static final String NAME = "border-radius";
    private static final String STARHACK_NAME = "*color";

    @Rule public final ExpectedException exception = ExpectedException.none();

    private PropertyName prefixed;
    private PropertyName unprefixed;

    @Before
    public void setup() {
        prefixed = PropertyName.using(PREFIX + NAME);
        unprefixed = PropertyName.using(NAME);
    }

    @Test
    public void nameWithPrefix() {
        assertThat(prefixed.name()).isEqualTo(PREFIX + NAME);
    }

    @Test
    public void nameWithoutPrefix() {
        assertThat(unprefixed.name()).isEqualTo(NAME);
    }

    @Test
    public void unprefixedNameWhenPrefixIsPresent() {
        assertThat(prefixed.unprefixedName()).isEqualTo(NAME);
    }

    @Test
    public void unprefixedNameWhenPrefixIsAbsent() {
        assertThat(unprefixed.unprefixedName()).isEqualTo(NAME);
    }

    @Test
    public void isPrefixedTrue() {
        assertThat(prefixed.isPrefixed()).isTrue();
    }

    @Test
    public void isPrefixedFalse() {
        assertThat(unprefixed.isPrefixed()).isFalse();
    }

    @Test
    public void prefixPresent() {
        assertThat(prefixed.prefix().get()).isEqualTo(PREFIX);
    }

    @Test
    public void prefixAbsent() {
        assertThat(unprefixed.prefix().isPresent()).isFalse();
    }

    @Test
    public void setsPrefixWhenPreviouslyUnset() {
        unprefixed.prefix(PREFIX);
        assertThat(prefixed.prefix().get()).isEqualTo(PREFIX);
    }

    @Test
    public void setsPrefixWhenCurrentlySet() {
        prefixed.prefix("-moz-");
        assertThat(prefixed.prefix().get()).isEqualTo("-moz-");
        assertThat(prefixed.name()).isEqualTo("-moz-" + NAME);
    }

    @Test
    public void prefixMustStartWithDash() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("prefixes must start");
        unprefixed.prefix("moz-");
    }

    @Test
    public void prefixMustEndWithDash() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("prefixes must end");
        unprefixed.prefix("-moz");
    }

    @Test
    public void removePrefix() {
        prefixed.removePrefix();
        assertThat(prefixed.prefix().isPresent()).isFalse();
    }

    @Test
    public void writeForPropertyWithPrefix() throws IOException {
        StyleWriter writer = StyleWriter.compressed();
        assertThat(writer.writeSnippet(prefixed)).isEqualTo(PREFIX + NAME);
    }

    @Test
    public void writeForPropertyWithoutPrefix() throws IOException {
        StyleWriter writer = StyleWriter.compressed();
        assertThat(writer.writeSnippet(unprefixed)).isEqualTo(NAME);
    }

    @Test
    public void constructorMethodStringNameOnly() {
        assertThat(PropertyName.using("color").name()).isEqualTo("color");
    }

    @Test
    public void constructorMethodStringAndPosition() {
        assertThat(PropertyName.using(5, 5, "color").line()).isEqualTo(5);
    }

    @Test
    public void constructorMethodPropertyOnly() {
        assertThat(PropertyName.using(Property.COLOR).name()).isEqualTo(Property.COLOR.toString());
    }

    @Test
    public void constructorMethodPropertyAndPosition() {
        assertThat(PropertyName.using(5, 5, Property.DISPLAY).line()).isEqualTo(5);
    }

    @Test
    public void consistentHashCode() {
        assertThat(prefixed.hashCode()).isEqualTo(PropertyName.using(5, 5, PREFIX + NAME).hashCode());
    }

    @Test
    public void equalsSameInstance() {
        assertThat(unprefixed.equals(unprefixed)).isTrue();
    }

    @Test
    public void equalsAnotherPropertyNameWithSameName() {
        assertThat(unprefixed.equals(PropertyName.using(NAME))).isTrue();
    }

    @Test
    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    public void doesntEqualAnotherType() {
        assertThat(unprefixed.equals("aa")).isFalse();
    }

    @Test
    public void matchesPropertyWithSameName() {
        PropertyName name = PropertyName.using("display");
        assertThat(name.matches(Property.DISPLAY)).isTrue();
    }

    @Test
    public void matchesStringWithSameName() {
        PropertyName name = PropertyName.using("display");
        assertThat(name.matches("display")).isTrue();
    }

    @Test
    public void doesNotEqualPropertyNameWithDifferentName() {
        assertThat(unprefixed.equals(PropertyName.using("zyx"))).isFalse();
    }

    @Test
    public void doesNotMatchPropertyWithDifferentName() {
        PropertyName name = PropertyName.using("display");
        assertThat(name.matches(Property.COLOR)).isFalse();
    }

    @Test
    public void doesNotMatchStringWithDifferentName() {
        PropertyName name = PropertyName.using("display");
        assertThat(name.matches("color")).isFalse();
    }

    @Test
    public void starHackIsTrue() throws IOException {
        PropertyName name = PropertyName.using(STARHACK_NAME);
        assertThat(name.hasStarHack()).isTrue();
    }

    @Test
    public void starHackIsStrippedFromProperty() throws IOException {
        PropertyName name = PropertyName.using(STARHACK_NAME);
        assertThat(name.matches("color")).isTrue();
    }

    @Test
    public void starHackIsWrittenWithStar() throws IOException {
        PropertyName name = PropertyName.using(STARHACK_NAME);
        StyleWriter writer = StyleWriter.compressed();
        assertThat(writer.writeSnippet(name)).isEqualTo(STARHACK_NAME);
    }

    @Test
    public void starHackIsFalse() throws IOException {
        PropertyName name = PropertyName.using("color");
        assertThat(name.hasStarHack()).isFalse();
    }

}
