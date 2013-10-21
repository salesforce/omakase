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

package com.salesforce.omakase.ast.selector;

import com.google.common.collect.Sets;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link Selectors}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class SelectorsTest {
    SelectorPart part;

    @Test
    public void asClassSelectorPresent() {
        part = new ClassSelector("test");
        assertThat(Selectors.asClassSelector(part).isPresent()).isTrue();
    }

    @Test
    public void asClassSelectorAbsent() {
        part = new IdSelector("test");
        assertThat(Selectors.asClassSelector(part).isPresent()).isFalse();
    }

    @Test
    public void asIdSelectorPresent() {
        part = new IdSelector("test");
        assertThat(Selectors.asIdSelector(part).isPresent()).isTrue();
    }

    @Test
    public void asIdSelectorAbsent() {
        part = new TypeSelector("test");
        assertThat(Selectors.asTypeSelector(part).isPresent()).isTrue();
    }

    @Test
    public void asTypeSelectorPresent() {
        part = new TypeSelector("test");
        assertThat(Selectors.asTypeSelector(part).isPresent()).isTrue();
    }

    @Test
    public void asTypeSelectorAbsent() {
        part = new ClassSelector("test");
        assertThat(Selectors.asTypeSelector(part).isPresent()).isFalse();
    }

    @Test
    public void hasClassSelectorTrue() {
        Iterable<SelectorPart> parts = Sets.<SelectorPart>newHashSet(new ClassSelector("test"));
        assertThat(Selectors.hasClassSelector(parts, "test")).isTrue();
    }

    @Test
    public void hasClassSelectorFalse() {
        Iterable<SelectorPart> parts = Sets.<SelectorPart>newHashSet(new ClassSelector("test"));
        assertThat(Selectors.hasClassSelector(parts, "boo")).isFalse();
    }

    @Test
    public void hasIdSelectorTrue() {
        Iterable<SelectorPart> parts = Sets.<SelectorPart>newHashSet(new IdSelector("test"));
        assertThat(Selectors.hasIdSelector(parts, "test")).isTrue();
    }

    @Test
    public void hasIdSelectorFalse() {
        Iterable<SelectorPart> parts = Sets.<SelectorPart>newHashSet(new IdSelector("test"));
        assertThat(Selectors.hasIdSelector(parts, "bad")).isFalse();
    }

    @Test
    public void hasTypeSelectorTrue() {
        Iterable<SelectorPart> parts = Sets.<SelectorPart>newHashSet(new TypeSelector("test"));
        assertThat(Selectors.hasTypeSelector(parts, "test")).isTrue();
    }

    @Test
    public void hasTypeSelectorFalse() {
        Iterable<SelectorPart> parts = Sets.<SelectorPart>newHashSet(new TypeSelector("test"));
        assertThat(Selectors.hasTypeSelector(parts, "bud")).isFalse();
    }
}
