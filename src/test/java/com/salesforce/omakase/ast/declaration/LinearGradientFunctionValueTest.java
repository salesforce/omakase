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
import com.salesforce.omakase.writer.StyleWriter;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link LinearGradientFunctionValue}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class LinearGradientFunctionValueTest {
    private LinearGradientFunctionValue function;

    @Test
    public void getArgs() {
        function = new LinearGradientFunctionValue("red, yellow");
        assertThat(function.args()).isEqualTo("red, yellow");
    }

    @Test
    public void setArgs() {
        function = new LinearGradientFunctionValue("red, yellow");
        function.args("yellow, red");
        assertThat(function.args()).isEqualTo("yellow, red");
    }

    @Test
    public void defaultNotRepeating() {
        function = new LinearGradientFunctionValue("red, yellow");
        assertThat(function.repeating()).isFalse();
    }

    @Test
    public void setRepeating() {
        function = new LinearGradientFunctionValue("red, yellow").repeating(true);
        assertThat(function.repeating()).isTrue();
    }

    @Test
    public void nameWhenNotRepeating() {
        function = new LinearGradientFunctionValue("red, yellow");
        assertThat(function.name()).isEqualTo("linear-gradient");
    }

    @Test
    public void nameWhenRepeating() {
        function = new LinearGradientFunctionValue("red, yellow").repeating(true);
        assertThat(function.name()).isEqualTo("repeating-linear-gradient");
    }

    @Test
    public void testWrite() {
        function = new LinearGradientFunctionValue("to top, red, #fcc");
        assertThat(StyleWriter.writeSingle(function)).isEqualTo("linear-gradient(to top, red, #fcc)");
    }

    @Test
    public void testWriteWhenRepeating() {
        function = new LinearGradientFunctionValue("red, yellow").repeating(true);
        assertThat(StyleWriter.writeSingle(function)).isEqualTo("repeating-linear-gradient(red, yellow)");
    }

    @Test
    public void testCopy() {
        function = new LinearGradientFunctionValue("red, yellow");
        function.comments(Lists.newArrayList("test"));
        LinearGradientFunctionValue copy = (LinearGradientFunctionValue)function.copy();
        assertThat(copy.args()).isEqualTo(function.args());
        assertThat(copy.comments()).hasSameSizeAs(function.comments());
        assertThat(copy.repeating()).isEqualTo(function.repeating());
    }

    @Test
    public void testCopyWhenRepeating() {
        function = new LinearGradientFunctionValue("red, yellow");
        function.comments(Lists.newArrayList("test"));
        function.repeating(true);
        LinearGradientFunctionValue copy = (LinearGradientFunctionValue)function.copy();
        assertThat(copy.args()).isEqualTo(function.args());
        assertThat(copy.comments()).hasSameSizeAs(function.comments());
        assertThat(copy.repeating()).isEqualTo(function.repeating());
    }

    @Test
    public void copyWithPrefix() {
        SupportMatrix support = new SupportMatrix();
        support.browser(Browser.FIREFOX, 15);

        function = new LinearGradientFunctionValue("red, yellow");
        function.comments(Lists.newArrayList("test"));

        FunctionValue copy = (FunctionValue)function.copy(Prefix.MOZ, support);
        assertThat(copy).isInstanceOf(GenericFunctionValue.class);

        GenericFunctionValue gfv = (GenericFunctionValue)copy;

        assertThat(gfv.name()).isEqualTo("-moz-linear-gradient");
        assertThat(gfv.args()).isEqualTo(function.args());
        assertThat(gfv.comments()).hasSameSizeAs(function.comments());
    }

    @Test
    public void copyWithPrefixWhenRepeating() {
        SupportMatrix support = new SupportMatrix();
        support.browser(Browser.FIREFOX, 15);

        function = new LinearGradientFunctionValue("red, yellow");
        function.repeating(true);
        function.comments(Lists.newArrayList("test"));

        FunctionValue copy = (FunctionValue)function.copy(Prefix.MOZ, support);
        assertThat(copy).isInstanceOf(GenericFunctionValue.class);

        GenericFunctionValue gfv = (GenericFunctionValue)copy;

        assertThat(gfv.name()).isEqualTo("-moz-repeating-linear-gradient");
        assertThat(gfv.args()).isEqualTo(function.args());
        assertThat(gfv.comments()).hasSameSizeAs(function.comments());
    }

    @Test
    public void copyWithPrefixToBottom() {
        SupportMatrix support = new SupportMatrix().browser(Browser.FIREFOX, 15);
        function = new LinearGradientFunctionValue("to bottom, red, yellow");
        GenericFunctionValue copy = (GenericFunctionValue)function.copy(Prefix.MOZ, support);
        assertThat(copy.args()).isEqualTo("top, red, yellow");
    }

    @Test
    public void copyWithPrefixToTop() {
        SupportMatrix support = new SupportMatrix().browser(Browser.FIREFOX, 15);
        function = new LinearGradientFunctionValue("to top, red, yellow");
        GenericFunctionValue copy = (GenericFunctionValue)function.copy(Prefix.MOZ, support);
        assertThat(copy.args()).isEqualTo("bottom, red, yellow");
    }

    @Test
    public void copyWithPrefixToRight() {
        SupportMatrix support = new SupportMatrix().browser(Browser.FIREFOX, 15);
        function = new LinearGradientFunctionValue("to right, red, yellow");
        GenericFunctionValue copy = (GenericFunctionValue)function.copy(Prefix.MOZ, support);
        assertThat(copy.args()).isEqualTo("left, red, yellow");
    }

    @Test
    public void copyWithPrefixToLeft() {
        SupportMatrix support = new SupportMatrix().browser(Browser.FIREFOX, 15);
        function = new LinearGradientFunctionValue("to left, red, yellow");
        GenericFunctionValue copy = (GenericFunctionValue)function.copy(Prefix.MOZ, support);
        assertThat(copy.args()).isEqualTo("right, red, yellow");
    }

    @Test
    public void copyWithPrefixToTopRight() {
        SupportMatrix support = new SupportMatrix().browser(Browser.FIREFOX, 15);
        function = new LinearGradientFunctionValue("to top right, red, yellow");
        GenericFunctionValue copy = (GenericFunctionValue)function.copy(Prefix.MOZ, support);
        assertThat(copy.args()).isEqualTo("bottom left, red, yellow");
    }

    @Test
    public void copyWithPrefixHasAngle() {
        SupportMatrix support = new SupportMatrix().browser(Browser.FIREFOX, 15);
        function = new LinearGradientFunctionValue("50deg, red, yellow");
        GenericFunctionValue copy = (GenericFunctionValue)function.copy(Prefix.MOZ, support);
        assertThat(copy.args()).isEqualTo("40deg, red, yellow");
    }

    @Test
    public void copyWithPrefixHasHighAngle() {
        SupportMatrix support = new SupportMatrix().browser(Browser.FIREFOX, 15);
        function = new LinearGradientFunctionValue("355deg, red, yellow");
        GenericFunctionValue copy = (GenericFunctionValue)function.copy(Prefix.MOZ, support);
        assertThat(copy.args()).isEqualTo("95deg, red, yellow");
    }

    @Test
    public void copyWithPrefixHasNegativeAngle() {
        SupportMatrix support = new SupportMatrix().browser(Browser.FIREFOX, 15);
        function = new LinearGradientFunctionValue("-45deg, red, yellow");
        GenericFunctionValue copy = (GenericFunctionValue)function.copy(Prefix.MOZ, support);
        assertThat(copy.args()).isEqualTo("135deg, red, yellow");
    }

    @Test
    public void copyWithPrefixNotPrefixable() {
        SupportMatrix support = new SupportMatrix().browser(Browser.FIREFOX, 15);
        function = new LinearGradientFunctionValue("red, yellow");
        FunctionValue copy = (FunctionValue)function.copy(Prefix.WEBKIT, support);
        assertThat(copy.name()).isEqualTo("linear-gradient");
    }
}
