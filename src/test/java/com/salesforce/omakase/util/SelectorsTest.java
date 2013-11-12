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

package com.salesforce.omakase.util;

import com.salesforce.omakase.ast.selector.ClassSelector;
import com.salesforce.omakase.ast.selector.IdSelector;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.ast.selector.SelectorPart;
import com.salesforce.omakase.ast.selector.TypeSelector;
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
        Selector selector = new Selector(new ClassSelector("test"));
        assertThat(Selectors.hasClassSelector(selector, "test")).isTrue();
    }

    @Test
    public void hasClassSelectorFalse() {
        Selector selector = new Selector(new ClassSelector("test"));
        assertThat(Selectors.hasClassSelector(selector, "boo")).isFalse();
    }

    @Test
    public void hasIdSelectorTrue() {
        Selector selector = new Selector(new IdSelector("test"));
        assertThat(Selectors.hasIdSelector(selector, "test")).isTrue();
    }

    @Test
    public void hasIdSelectorFalse() {
        Selector selector = new Selector(new IdSelector("test"));
        assertThat(Selectors.hasIdSelector(selector, "bad")).isFalse();
    }

    @Test
    public void hasTypeSelectorTrue() {
        Selector selector = new Selector(new TypeSelector("test"));
        assertThat(Selectors.hasTypeSelector(selector, "test")).isTrue();
    }

    @Test
    public void hasTypeSelectorFalse() {
        Selector selector = new Selector(new TypeSelector("test"));
        assertThat(Selectors.hasTypeSelector(selector, "bud")).isFalse();
    }

    @Test
    public void findClassSelector() {
        ClassSelector s1 = new ClassSelector("test");
        ClassSelector s2 = new ClassSelector("findme");
        Selector selector = new Selector(s1, s2);
        assertThat(Selectors.findClassSelector(selector, "findme").get()).isSameAs(s2);
    }

    @Test
    public void findIdSelectors() {
        IdSelector s1 = new IdSelector("test");
        IdSelector s2 = new IdSelector("findme");
        Selector selector = new Selector(s1, s2);
        assertThat(Selectors.findIdSelector(selector, "findme").get()).isSameAs(s2);
    }

    @Test
    public void findTypeSelector() {
        TypeSelector s1 = new TypeSelector("test");
        TypeSelector s2 = new TypeSelector("findme");
        Selector selector = new Selector(s1, s2);
        assertThat(Selectors.findTypeSelector(selector, "findme").get()).isSameAs(s2);
    }
}
