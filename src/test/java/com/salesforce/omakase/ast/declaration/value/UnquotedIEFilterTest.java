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

import com.salesforce.omakase.writer.StyleWriter;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link UnquotedIEFilter}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class UnquotedIEFilterTest {
    private static final String FILTER = "progid:DXImageTransform.Microsoft.Shadow(color='#969696', Direction=145, Strength=3)";
    @Rule public final ExpectedException exception = ExpectedException.none();

    @Test
    public void testGetContent() {
        UnquotedIEFilter ief = new UnquotedIEFilter(1, 1, FILTER);
        assertThat(ief.content()).isEqualTo(FILTER);
    }

    @Test
    public void errorsOnSetImportant() {
        UnquotedIEFilter ief = new UnquotedIEFilter(1, 1, FILTER);
        exception.expect(UnsupportedOperationException.class);
        ief.important(true);
    }

    @Test
    public void isImportantAlwaysFalse() {
        UnquotedIEFilter ief = new UnquotedIEFilter(1, 1, FILTER + " !important");
        assertThat(ief.isImportant()).isFalse();
    }

    @Test
    public void write() throws IOException {
        UnquotedIEFilter ief = new UnquotedIEFilter(1, 1, FILTER);
        assertThat(StyleWriter.compressed().writeSnippet(ief)).isEqualTo(FILTER);
    }

}
