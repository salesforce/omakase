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

import com.google.common.collect.Lists;
import com.salesforce.omakase.SupportMatrix;
import com.salesforce.omakase.data.Browser;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.data.Property;
import com.salesforce.omakase.test.util.Util;
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
    private static final Prefix PREFIX = Prefix.WEBKIT;
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
        assertThat(prefixed.prefix().get()).isSameAs(PREFIX);
    }

    @Test
    public void prefixAbsent() {
        assertThat(unprefixed.prefix().isPresent()).isFalse();
    }

    @Test
    public void setsPrefixWhenPreviouslyUnset() {
        unprefixed.prefix(Prefix.MOZ);
        assertThat(unprefixed.prefix().get()).isSameAs(Prefix.MOZ);
    }

    @Test
    public void removePrefix() {
        prefixed.removePrefix();
        assertThat(prefixed.prefix().isPresent()).isFalse();
    }

    @Test
    public void hasPrefixTrue() {
        PropertyName pn = PropertyName.using(Property.DISPLAY);
        pn.prefix(Prefix.MOZ);
        assertThat(pn.hasPrefix(Prefix.MOZ)).isTrue();
    }

    @Test
    public void hasPrefixFalse() {
        PropertyName pn = PropertyName.using(Property.DISPLAY);
        pn.prefix(Prefix.MOZ);
        assertThat(pn.hasPrefix(Prefix.WEBKIT)).isFalse();
    }

    @Test
    public void hasPrefixFalseWhenPrefixNotPresent() {
        assertThat(unprefixed.hasPrefix(Prefix.WEBKIT)).isFalse();
    }

    @Test
    public void asPropertyAbsentWhenPrefixed() {
        assertThat(prefixed.asProperty().isPresent()).isFalse();
    }

    @Test
    public void asPropertyUnknownProperty() {
        assertThat(PropertyName.using("blah").asProperty().isPresent()).isFalse();
    }

    @Test
    public void asPropertyKnownProperty() {
        assertThat(PropertyName.using(Property.DISPLAY).asProperty().get()).isSameAs(Property.DISPLAY);
    }

    @Test
    public void matchesIgnorePrefixTrueWhenPrefixed() {
        assertThat(prefixed.matchesIgnorePrefix(Property.BORDER_RADIUS)).isTrue();
    }

    @Test
    public void matchesIgnorePrefixTrueWhenNotPrefixed() {
        assertThat(unprefixed.matchesIgnorePrefix(Property.BORDER_RADIUS)).isTrue();
    }

    @Test
    public void matchesIgnorePrefixFalseWhenPrefixed() {
        assertThat(prefixed.matchesIgnorePrefix(Property.BORDER)).isFalse();
    }

    @Test
    public void matchesIgnorePrefixFalseWhenNotPrefixed() {
        assertThat(unprefixed.matchesIgnorePrefix(Property.BORDER)).isFalse();
    }

    @Test
    public void matchesPropertyNameIgnorePrefix() {
        PropertyName pn1 = PropertyName.using("-webkit-border-radius");
        PropertyName pn2 = PropertyName.using("-moz-border-radius");
        assertThat(pn1.matchesIgnorePrefix(pn2)).isTrue();
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
    public void matchesSameInstance() {
        assertThat(unprefixed.matches(unprefixed)).isTrue();
    }

    @Test
    public void matchesAnotherPropertyNameWithSameName() {
        assertThat(unprefixed.matches(PropertyName.using(NAME))).isTrue();
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
    public void doesNotMatchPropertyNameWithDifferentName() {
        assertThat(unprefixed.matches(PropertyName.using("zyx"))).isFalse();
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
    public void starHackIsTrue() {
        PropertyName name = PropertyName.using(STARHACK_NAME);
        assertThat(name.hasStarHack()).isTrue();
    }

    @Test
    public void starHackIsStrippedFromProperty() {
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
    public void starHackIsFalse() {
        PropertyName name = PropertyName.using("color");
        assertThat(name.hasStarHack()).isFalse();
    }

    @Test
    public void copyTest() {
        PropertyName name = PropertyName.using("*-webkit-border-radius");
        assertThat(name.isPrefixed()).isTrue();
        assertThat(name.hasStarHack()).isTrue();
        name.comments(Lists.newArrayList("test"));

        PropertyName copy = name.copy();
        assertThat(copy.unprefixedName()).isEqualTo(name.unprefixedName());
        assertThat(copy.prefix().get()).isEqualTo(name.prefix().get());
        assertThat(copy.hasStarHack()).isEqualTo(name.hasStarHack());
        assertThat(copy.comments()).hasSameSizeAs(name.comments());
    }

    @Test
    public void copyWithPrefixRequired() {
        PropertyName name = PropertyName.using("border-radius");
        name.comments(Lists.newArrayList("test"));
        SupportMatrix support = new SupportMatrix();
        support.browser(Browser.SAFARI, 4);

        PropertyName copy = name.copyWithPrefix(Prefix.WEBKIT, support);
        assertThat(copy.unprefixedName()).isEqualTo(name.unprefixedName());
        assertThat(copy.prefix().get()).isSameAs(Prefix.WEBKIT);
        assertThat(copy.hasStarHack()).isFalse();
        assertThat(copy.comments()).hasSameSizeAs(name.comments());
    }

    @Test
    public void copyWithPrefixNotRequired() {
        PropertyName name = PropertyName.using("border-radius");
        name.comments(Lists.newArrayList("test"));
        SupportMatrix support = new SupportMatrix();

        PropertyName copy = name.copyWithPrefix(Prefix.WEBKIT, support);
        assertThat(copy.unprefixedName()).isEqualTo(name.unprefixedName());
        assertThat(copy.isPrefixed()).isFalse();
        assertThat(copy.comments()).hasSameSizeAs(name.comments());
    }

    @Test
    public void copyUnknownProperty() {
        PropertyName name = PropertyName.using("blah");
        PropertyName copy = name.copyWithPrefix(Prefix.WEBKIT, new SupportMatrix());
        assertThat(copy.name()).isEqualTo(name.name());
    }

    @Test
    public void toStringTest() {
        assertThat(unprefixed.toString()).isNotEqualTo(Util.originalToString(unprefixed));
    }
}
