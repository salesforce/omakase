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

import com.salesforce.omakase.SupportMatrix;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link AbstractTerm}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class AbstractTermTest {
    @Rule public final ExpectedException exception = ExpectedException.none();

    @Test
    public void isWritableWhenDetached() {
        TestTerm term = new TestTerm();
        assertThat(term.isWritable()).isTrue();
    }

    @Test
    public void doesntAllowExplicitDetachment() {
        TestTerm term = new TestTerm();
        exception.expect(UnsupportedOperationException.class);
        term.detach();
    }

    private static final class TestTerm extends AbstractTerm {
        @Override
        public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        }

        @Override
        protected TermListMember makeCopy(Prefix prefix, SupportMatrix support) {
            throw new UnsupportedOperationException();
        }
    }
}
