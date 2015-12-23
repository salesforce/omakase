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

import com.salesforce.omakase.ast.selector.*;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link Selectors}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings({"JavaDoc", "FieldCanBeLocal", "UnusedDeclaration"})
public class SelectorsTest {
    private SelectorPart part;
    private SelectorPart obj1;
    private SelectorPart obj2;
    private SelectorPart obj3;
    private SelectorPart obj4;
    private SelectorPart obj5;
    private SelectorPart obj6;
    private Combinator combinator;
    private Selector full;

    private void fill() {
        obj1 = new IdSelector("test");
        obj2 = new IdSelector("test");
        obj3 = new IdSelector("test");
        obj4 = new IdSelector("test");
        obj5 = new IdSelector("test");
        obj6 = new IdSelector("test");

        combinator = new Combinator(CombinatorType.DESCENDANT);
        full = new Selector(obj1, obj2, obj3, combinator, obj4, obj5, obj6);
    }

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
    public void asPseudoSelectorPresent() {
        part = new PseudoElementSelector("selection");
        assertThat(Selectors.asPseudoElementSelector(part).isPresent()).isTrue();
    }

    @Test
    public void asPseudoSelectorAbsent() {
        part = new ClassSelector("test");
        assertThat(Selectors.asPseudoElementSelector(part).isPresent()).isFalse();
    }

    @Test
    public void asPseudoClassSelectorPresent() {
        part = new PseudoClassSelector("selection");
        assertThat(Selectors.asPseudoClassSelector(part).isPresent()).isTrue();
    }

    @Test
    public void asPseudoClassSelectorAbsent() {
        part = new ClassSelector("test");
        assertThat(Selectors.asPseudoClassSelector(part).isPresent()).isFalse();
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
    public void hasPseudoSelectorTrueExactFalseIsPrefixed() {
        Selector selector = new Selector(new PseudoElementSelector("-webkit-selection"));
        assertThat(Selectors.hasPseudoElementSelector(selector, "selection", false)).isTrue();
    }

    @Test
    public void hasPseudoSelectorTrueExactFalseIsNotPrefixed() {
        Selector selector = new Selector(new PseudoElementSelector("selection"));
        assertThat(Selectors.hasPseudoElementSelector(selector, "selection", false)).isTrue();
    }

    @Test
    public void hasPseudoSelectorFalseExactFalseIsPrefixed() {
        Selector selector = new Selector(new PseudoElementSelector("-webkit-selection"));
        assertThat(Selectors.hasPseudoElementSelector(selector, "bud", false)).isFalse();
    }

    @Test
    public void hasPseudoSelectorFalseExactFalseIsNotPrefixed() {
        Selector selector = new Selector(new PseudoElementSelector("selection"));
        assertThat(Selectors.hasPseudoElementSelector(selector, "bud", false)).isFalse();
    }

    @Test
    public void hasPseudoSelectorTrueExactTrueIsPrefixed() {
        Selector selector = new Selector(new PseudoElementSelector("-webkit-selection"));
        assertThat(Selectors.hasPseudoElementSelector(selector, "selection", true)).isFalse();
    }

    @Test
    public void hasPseudoSelectorTrueExactTrueIsNotPrefixed() {
        Selector selector = new Selector(new PseudoElementSelector("selection"));
        assertThat(Selectors.hasPseudoElementSelector(selector, "selection", true)).isTrue();
    }

    @Test
    public void hasPseudoSelectorFalseExactTrueIsPrefixed() {
        Selector selector = new Selector(new PseudoElementSelector("-webkit-selection"));
        assertThat(Selectors.hasPseudoElementSelector(selector, "bud", true)).isFalse();
    }

    @Test
    public void hasPseudoSelectorFalseExactTrueIsNotPrefixed() {
        Selector selector = new Selector(new PseudoElementSelector("selection"));
        assertThat(Selectors.hasPseudoElementSelector(selector, "bud", true)).isFalse();
    }

    @Test
    public void hasPseudoClassSelectorTrueExactFalseIsPrefixed() {
        Selector selector = new Selector(new PseudoClassSelector("-webkit-blah"));
        assertThat(Selectors.hasPseudoClassSelector(selector, "blah", false)).isTrue();
    }

    @Test
    public void hasPseudoClassSelectorTrueExactFalseIsNotPrefixed() {
        Selector selector = new Selector(new PseudoClassSelector("blah"));
        assertThat(Selectors.hasPseudoClassSelector(selector, "blah", false)).isTrue();
    }

    @Test
    public void hasPseudoClassSelectorFalseExactFalseIsPrefixed() {
        Selector selector = new Selector(new PseudoClassSelector("-webkit-blah"));
        assertThat(Selectors.hasPseudoClassSelector(selector, "bud", false)).isFalse();
    }

    @Test
    public void hasPseudoClassSelectorFalseExactFalseIsNotPrefixed() {
        Selector selector = new Selector(new PseudoClassSelector("blah"));
        assertThat(Selectors.hasPseudoClassSelector(selector, "bud", false)).isFalse();
    }

    @Test
    public void hasPseudoClassSelectorTrueExactTrueIsPrefixed() {
        Selector selector = new Selector(new PseudoClassSelector("-webkit-blah"));
        assertThat(Selectors.hasPseudoClassSelector(selector, "blah", true)).isFalse();
    }

    @Test
    public void hasPseudoClassSelectorTrueExactTrueIsNotPrefixed() {
        Selector selector = new Selector(new PseudoClassSelector("blah"));
        assertThat(Selectors.hasPseudoClassSelector(selector, "blah", true)).isTrue();
    }

    @Test
    public void hasPseudoClassSelectorFalseExactTrueIsPrefixed() {
        Selector selector = new Selector(new PseudoClassSelector("-webkit-blah"));
        assertThat(Selectors.hasPseudoClassSelector(selector, "bud", true)).isFalse();
    }

    @Test
    public void hasPseudoClassSelectorFalseExactTrueIsNotPrefixed() {
        Selector selector = new Selector(new PseudoClassSelector("blah"));
        assertThat(Selectors.hasPseudoClassSelector(selector, "bud", true)).isFalse();
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

    @Test
    public void findPseudoSelectorExact() {
        PseudoElementSelector s1 = new PseudoElementSelector("-moz-findme");
        PseudoElementSelector s2 = new PseudoElementSelector("findme");
        Selector selector = new Selector(s1, s2);
        assertThat(Selectors.findPseudoElementSelector(selector, "findme", true).get()).isSameAs(s2);
    }

    @Test
    public void findPseudoSelectorNotExact() {
        PseudoElementSelector s1 = new PseudoElementSelector("-moz-findme");
        PseudoElementSelector s2 = new PseudoElementSelector("findme");
        Selector selector = new Selector(s1, s2);
        assertThat(Selectors.findPseudoElementSelector(selector, "findme", false).get()).isSameAs(s1);
    }

    @Test
    public void findPseudoClassExact() {
        PseudoClassSelector s1 = new PseudoClassSelector("-moz-findme");
        PseudoClassSelector s2 = new PseudoClassSelector("findme");
        Selector selector = new Selector(s1, s2);
        assertThat(Selectors.findPseudoClassSelector(selector, "findme", true).get()).isSameAs(s2);
    }

    @Test
    public void findPseudoClassNotExact() {
        PseudoClassSelector s1 = new PseudoClassSelector("-moz-findme");
        PseudoClassSelector s2 = new PseudoClassSelector("findme");
        Selector selector = new Selector(s1, s2);
        assertThat(Selectors.findPseudoClassSelector(selector, "findme", false).get()).isSameAs(s1);
    }

    @Test
    public void adjoiningWhenFirst() {
        fill();
        assertThat(Selectors.adjoining(obj1)).containsExactly(obj1, obj2, obj3);
    }

    @Test
    public void adjoiningWhenMiddle() {
        fill();
        assertThat(Selectors.adjoining(obj2)).containsExactly(obj1, obj2, obj3);
    }

    @Test
    public void adjoiningWhenEnd() {
        fill();
        assertThat(Selectors.adjoining(obj3)).containsExactly(obj1, obj2, obj3);
    }

    @Test
    public void adjoiningWhenCombinator() {
        fill();
        assertThat(Selectors.adjoining(combinator)).containsExactly(combinator);
    }

    @Test
    public void adjoiningWhenDetached() {
        fill();
        obj2.destroy();
        assertThat(Selectors.adjoining(obj2)).containsExactly(obj2);
    }

    @Test
    public void testFilterExisting() {
        ClassSelector s1 = new ClassSelector("test1");
        ClassSelector s2 = new ClassSelector("test2");
        IdSelector s3 = new IdSelector("test3");
        ClassSelector s4 = new ClassSelector("test4");
        Selector selector = new Selector(s1, s2, Combinator.descendant(), s3, Combinator.adjacent(), s4);
        Iterable<ClassSelector> filtered = Selectors.filter(ClassSelector.class, selector);

        assertThat(filtered).containsExactly(s1, s2, s4);
    }

    @Test
    public void testFilterEmpty() {
        ClassSelector s1 = new ClassSelector("test1");
        ClassSelector s2 = new ClassSelector("test2");
        PseudoClassSelector s3 = new PseudoClassSelector("hover");
        Selector selector = new Selector(s1, Combinator.descendant(), s2, s3);
        Iterable<IdSelector> filtered = Selectors.filter(IdSelector.class, selector);

        assertThat(filtered).isEmpty();
    }
}
