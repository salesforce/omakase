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

package com.salesforce.omakase.ast.atrule;

import com.salesforce.omakase.ast.Status;
import com.salesforce.omakase.ast.declaration.KeywordValue;
import com.salesforce.omakase.ast.declaration.PropertyName;
import com.salesforce.omakase.ast.declaration.PropertyValue;
import com.salesforce.omakase.ast.declaration.QuotationMode;
import com.salesforce.omakase.ast.declaration.StringValue;
import com.salesforce.omakase.ast.declaration.UrlFunctionValue;
import com.salesforce.omakase.data.Keyword;
import com.salesforce.omakase.data.Property;
import com.salesforce.omakase.test.StatusChangingBroadcaster;
import com.salesforce.omakase.writer.StyleWriter;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link FontFaceBlock}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings({"JavaDoc", "FieldCanBeLocal", "SpellCheckingInspection"})
public class FontFaceBlockTest {
    private PropertyName samplePropertyName;
    private PropertyValue samplePropertyValue;
    private FontDescriptor descriptor;
    private FontFaceBlock block;

    @Before
    public void setup() {
        samplePropertyName = PropertyName.of(Property.FONT_FAMILY);
        samplePropertyValue = PropertyValue.of(StringValue.of(QuotationMode.DOUBLE, "My Font"));
        descriptor = new FontDescriptor(samplePropertyName, samplePropertyValue);
        block = new FontFaceBlock();
    }

    @Test
    public void testAddFontDescriptor() {
        block.fontDescriptors().append(descriptor);
        assertThat(block.fontDescriptors()).containsExactly(descriptor);
    }

    @Test
    public void testPropagateBroadcast() {
        assertThat(descriptor.status()).isSameAs(Status.UNBROADCASTED);
        assertThat(block.status()).isSameAs(Status.UNBROADCASTED);

        block.fontDescriptors().append(descriptor);

        block.propagateBroadcast(new StatusChangingBroadcaster());
        assertThat(descriptor.status()).isNotSameAs(Status.UNBROADCASTED);
        assertThat(block.status()).isNotSameAs(Status.UNBROADCASTED);
    }

    @Test
    public void isWritableTrue() {
        block.fontDescriptors().append(descriptor);
        assertThat(block.isWritable()).isTrue();
    }

    @Test
    public void isNotWritableWhenNoFontDescriptors() {
        assertThat(block.isWritable()).isFalse();
    }

    @Test
    public void writeVerbose() {
        block = new FontFaceBlock();

        PropertyName n1 = PropertyName.of(Property.FONT_FAMILY);
        KeywordValue v1 = KeywordValue.of("MyFont");

        PropertyName n2 = PropertyName.of(Property.SRC);
        UrlFunctionValue v2 = new UrlFunctionValue("MyFont.ttf");

        PropertyName n3 = PropertyName.of(Property.FONT_WEIGHT);
        KeywordValue v3 = KeywordValue.of(Keyword.BOLD);

        block.fontDescriptors().append(new FontDescriptor(n1, PropertyValue.of(v1)));
        block.fontDescriptors().append(new FontDescriptor(n2, PropertyValue.of(v2)));
        block.fontDescriptors().append(new FontDescriptor(n3, PropertyValue.of(v3)));

        String expected = " {\n" +
            "  font-family: MyFont;\n" +
            "  src: url(MyFont.ttf);\n" +
            "  font-weight: bold;\n" +
            "}";

        assertThat(StyleWriter.verbose().writeSingle(block)).isEqualTo(expected);
    }

    @Test
    public void writeInline() {
        block = new FontFaceBlock();

        PropertyName n1 = PropertyName.of(Property.FONT_FAMILY);
        KeywordValue v1 = KeywordValue.of("MyFont");

        PropertyName n2 = PropertyName.of(Property.SRC);
        UrlFunctionValue v2 = new UrlFunctionValue("MyFont.ttf");

        PropertyName n3 = PropertyName.of(Property.FONT_WEIGHT);
        KeywordValue v3 = KeywordValue.of(Keyword.BOLD);

        block.fontDescriptors().append(new FontDescriptor(n1, PropertyValue.of(v1)));
        block.fontDescriptors().append(new FontDescriptor(n2, PropertyValue.of(v2)));
        block.fontDescriptors().append(new FontDescriptor(n3, PropertyValue.of(v3)));

        String expected = " {font-family:MyFont; src:url(MyFont.ttf); font-weight:bold}";

        assertThat(StyleWriter.inline().writeSingle(block)).isEqualTo(expected);
    }

    @Test
    public void writeCompressed() {
        block = new FontFaceBlock();

        PropertyName n1 = PropertyName.of(Property.FONT_FAMILY);
        KeywordValue v1 = KeywordValue.of("MyFont");

        PropertyName n2 = PropertyName.of(Property.SRC);
        UrlFunctionValue v2 = new UrlFunctionValue("MyFont.ttf");

        PropertyName n3 = PropertyName.of(Property.FONT_WEIGHT);
        KeywordValue v3 = KeywordValue.of(Keyword.BOLD);

        block.fontDescriptors().append(new FontDescriptor(n1, PropertyValue.of(v1)));
        block.fontDescriptors().append(new FontDescriptor(n2, PropertyValue.of(v2)));
        block.fontDescriptors().append(new FontDescriptor(n3, PropertyValue.of(v3)));

        String expected = "{font-family:MyFont;src:url(MyFont.ttf);font-weight:bold}";

        assertThat(StyleWriter.compressed().writeSingle(block)).isEqualTo(expected);
    }

    @Test
    public void testCopy() {
        block.fontDescriptors().append(descriptor);
        FontFaceBlock copy = block.copy();
        assertThat(copy.fontDescriptors()).hasSameSizeAs(block.fontDescriptors());
    }
}
