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

package com.salesforce.omakase.ast.declaration.value;

import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.declaration.Property;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/** Unit tests for {@link Keyword}. */
@SuppressWarnings("JavaDoc")
public class KeywordTest {
    @Test
    public void isOnlyValueInDeclarationTrue() {
        Declaration d = new Declaration(Property.DISPLAY, KeywordValue.of(Keyword.NONE));
        assertThat(Keyword.NONE.isOnlyValueIn(d)).isTrue();
    }

    @Test
    public void isOnlyValueInDeclarationFalse() {
        Declaration d = new Declaration(Property.DISPLAY, KeywordValue.of(Keyword.BLOCK));
        assertThat(Keyword.NONE.isOnlyValueIn(d)).isFalse();
    }

    @Test
    public void isOnlyValueInPropertyValueTrue() {
        PropertyValue pv = TermList.singleValue(KeywordValue.of(Keyword.NONE));
        assertThat(Keyword.NONE.isOnlyValueIn(pv)).isTrue();
    }

    @Test
    public void isOnlyValueInPropertyValueFalse() {
        PropertyValue pv = TermList.singleValue(KeywordValue.of(Keyword.BLOCK));
        assertThat(Keyword.NONE.isOnlyValueIn(pv)).isFalse();
    }
}
