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

import com.google.common.collect.Sets;
import com.salesforce.omakase.ast.AbstractSyntax;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.parser.refiner.FunctionValueRefinerStrategy;
import com.salesforce.omakase.parser.refiner.Refiner;
import com.salesforce.omakase.parser.refiner.RefinerStrategy;
import com.salesforce.omakase.test.StatusChangingBroadcaster;
import com.salesforce.omakase.test.util.Util;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;

import static org.fest.assertions.api.Assertions.*;

/** Unit tests for {@link FunctionValue}. */
@SuppressWarnings("JavaDoc")
public class FunctionValueTest {
    @Rule public final ExpectedException exception = ExpectedException.none();

    FunctionValue value;

    @Test
    public void positioning() {
        value = new FunctionValue(5, 2, "url", "home.png", new Refiner(new QueryableBroadcaster()));
        assertThat(value.line()).isEqualTo(5);
        assertThat(value.column()).isEqualTo(2);
    }

    @Test
    public void getsName() {
        value = new FunctionValue("url", "home.png");
        assertThat(value.name()).isEqualTo("url");
    }

    @Test
    public void setsName() {
        value = new FunctionValue("rgb", "255,255,255");
        value.name("rgba");
        assertThat(value.name()).isEqualTo("rgba");
    }

    @Test
    public void getsArgs() {
        value = new FunctionValue("url", "home.png");
        assertThat(value.args()).isEqualTo("home.png");
    }

    @Test
    public void setsArgs() {
        value = new FunctionValue("rgb", "255,255,255");
        value.args("255, 255, 255, 0.5");
        assertThat(value.args()).isEqualTo("255, 255, 255, 0.5");
    }

    @Test
    public void defaultNotRefined() {
        value = new FunctionValue("url", "home.png");
        assertThat(value.isRefined()).isFalse();
    }

    @Test
    public void isRefinedTrue() {
        Refiner refiner = new Refiner(new StatusChangingBroadcaster(), Sets.<RefinerStrategy>newHashSet(new CustomFunctionStrategy()));
        value = new FunctionValue(1, 1, "url", "home.png", refiner);
        value.refine();
        assertThat(value.isRefined()).isTrue();
    }

    @Test
    public void setRefinedValueMakesRefinedTrue() {
        value = new FunctionValue("url", "home.png");
        value.refinedValue(new CustomFunction());
        assertThat(value.isRefined()).isTrue();
    }

    @Test
    public void errorIfChangingNameAfterRefined() {
        value = new FunctionValue("url", "home.png");
        value.refinedValue(new CustomFunction());

        exception.expect(IllegalStateException.class);
        value.name("test");
    }

    @Test
    public void errorIfChangingArgsAfterRefined() {
        value = new FunctionValue("url", "home.png");
        value.refinedValue(new CustomFunction());

        exception.expect(IllegalStateException.class);
        value.args("test");
    }

    @Test
    public void refine() {
        Refiner refiner = new Refiner(new StatusChangingBroadcaster(), Sets.<RefinerStrategy>newHashSet(new CustomFunctionStrategy()));
        value = new FunctionValue(1, 1, "url", "home.png", refiner);
        value.refine();
        assertThat(value.refinedValue().get()).isInstanceOf(CustomFunction.class);
    }

    @Test
    public void refineWhenNoRefinerPresent() {
        value = new FunctionValue("url", "home.png");
        assertThat(value.refine()).isFalse();
    }

    @Test
    public void isWritableWhenRefined() {
        value = new FunctionValue("url", "home.png");
        value.refinedValue(new CustomFunctionNotWritable());
        assertThat(value.isWritable()).isFalse();
        value.refine();
    }

    @Test
    public void alwaysWritableWhenNotRefined() {
        Refiner refiner = new Refiner(new StatusChangingBroadcaster(), Sets.<RefinerStrategy>newHashSet(new CustomFunctionStrategy()));
        value = new FunctionValue(1, 1, "url", "home.png", refiner);
        assertThat(value.isWritable()).isTrue();
    }

    @Test
    public void writeVerbose() throws IOException {
        value = new FunctionValue("url", "home.png");
        assertThat(StyleWriter.verbose().writeSnippet(value)).isEqualTo("url(home.png)");
    }

    @Test
    public void writeInline() throws IOException {
        value = new FunctionValue("url", "home.png");
        assertThat(StyleWriter.verbose().writeSnippet(value)).isEqualTo("url(home.png)");
    }

    @Test
    public void writeCompressed() throws IOException {
        value = FunctionValue.of("url", "home.png");
        assertThat(StyleWriter.verbose().writeSnippet(value)).isEqualTo("url(home.png)");
    }

    @Test
    public void writeEmptyArgs() throws IOException {
        value = new FunctionValue("xyz", "");
        assertThat(StyleWriter.verbose().writeSnippet(value)).isEqualTo("xyz()");
    }

    @Test
    public void writeWhenRefined() throws IOException {
        Refiner refiner = new Refiner(new StatusChangingBroadcaster(), Sets.<RefinerStrategy>newHashSet(new CustomFunctionStrategy()));
        value = new FunctionValue(1, 1, "url", "home.png", refiner);
        value.refine();
        assertThat(StyleWriter.verbose().writeSnippet(value)).isEqualTo("customfunction");
    }

    @Test
    public void toStringTest() {
        value = new FunctionValue("xyz", "home.png");
        assertThat(value.toString()).isNotEqualTo(Util.originalToString(value));
    }

    public static final class CustomFunction extends AbstractSyntax implements RefinedFunctionValue {
        @Override
        public boolean isCustom() {
            return true;
        }

        @Override
        public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
            appendable.append("customfunction");
        }
    }

    public static final class CustomFunctionNotWritable extends AbstractSyntax implements RefinedFunctionValue {
        @Override
        public boolean isCustom() {
            return true;
        }

        @Override
        public boolean isWritable() {
            return false;
        }

        @Override
        public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
            appendable.append("oops");
        }
    }

    public static final class CustomFunctionStrategy implements FunctionValueRefinerStrategy {
        @Override
        public boolean refine(FunctionValue functionValue, Broadcaster broadcaster, Refiner refiner) {
            functionValue.refinedValue(new CustomFunction());
            return true;
        }
    }
}
