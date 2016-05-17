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

import com.google.common.collect.Lists;
import com.salesforce.omakase.ast.Status;
import com.salesforce.omakase.ast.declaration.KeywordValue;
import com.salesforce.omakase.ast.declaration.PropertyName;
import com.salesforce.omakase.ast.declaration.PropertyValue;
import com.salesforce.omakase.ast.declaration.QuotationMode;
import com.salesforce.omakase.ast.declaration.StringValue;
import com.salesforce.omakase.data.Property;
import com.salesforce.omakase.test.StatusChangingBroadcaster;
import com.salesforce.omakase.util.Values;
import com.salesforce.omakase.writer.StyleWriter;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link FontDescriptor}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class FontDescriptorTest {
    private PropertyName samplePropertyName;
    private PropertyValue samplePropertyValue;
    private FontDescriptor descriptor;

    @Before
    public void setup() {
        samplePropertyName = PropertyName.of(Property.FONT_FAMILY);
        samplePropertyValue = PropertyValue.of(StringValue.of(QuotationMode.DOUBLE, "My Font"));
        descriptor = new FontDescriptor(samplePropertyName, samplePropertyValue);
    }

    @Test
    public void getPropertyName() {
        assertThat(descriptor.propertyName()).isSameAs(samplePropertyName);
    }

    @Test
    public void isStringPropertyTrue() {
        assertThat(descriptor.isProperty("font-family")).isTrue();
    }

    @Test
    public void isStringPropertyFalse() {
        assertThat(descriptor.isProperty("border")).isFalse();
    }

    @Test
    public void isPropertyTrue() {
        assertThat(descriptor.isProperty(Property.FONT_FAMILY)).isTrue();
    }

    @Test
    public void isPropertyFalse() {
        descriptor = new FontDescriptor(samplePropertyName, samplePropertyValue);
        assertThat(descriptor.isProperty(Property.BORDER)).isFalse();
    }

    @Test
    public void getName() {
        assertThat(descriptor.name()).isEqualTo("font-family");
    }

    @Test
    public void setPropertyValueTerm() {
        KeywordValue newValue = KeywordValue.of("MyFont");
        descriptor.propertyValue(newValue);
        assertThat(Values.asKeyword(descriptor.propertyValue()).isPresent()).isTrue();
    }

    @Test
    public void setPropertyValueFull() {
        PropertyValue newValue = PropertyValue.of(KeywordValue.of("MyFont"));
        descriptor.propertyValue(newValue);
        assertThat(descriptor.propertyValue()).isSameAs(newValue);
    }

    @Test
    public void newPropertyValueIsBroadcasted() {
        FontFaceBlock block = new FontFaceBlock(1, 1, new StatusChangingBroadcaster());

        PropertyValue newValue = PropertyValue.of(KeywordValue.of("MyFont"));
        descriptor.propertyValue(newValue);

        assertThat(newValue.status()).isSameAs(Status.UNBROADCASTED);
        block.fontDescriptors().append(descriptor);
        assertThat(newValue.status()).isNotSameAs(Status.UNBROADCASTED);
    }

    @Test
    public void changedPropertyValueIsBroadcasted() {
        FontFaceBlock block = new FontFaceBlock(1, 1, new StatusChangingBroadcaster());
        block.fontDescriptors().append(descriptor);

        PropertyValue newValue = PropertyValue.of(KeywordValue.of("MyFont"));
        assertThat(newValue.status()).isSameAs(Status.UNBROADCASTED);

        descriptor.propertyValue(newValue);
        assertThat(newValue.status()).isNotSameAs(Status.UNBROADCASTED);
    }

    @Test
    public void setPropertyValueDoesntBroadcastAlreadyBroadcasted() {
        StatusChangingBroadcaster broadcaster = new StatusChangingBroadcaster();
        FontFaceBlock block = new FontFaceBlock(1, 1, broadcaster);
        descriptor.status(Status.PROCESSED);

        PropertyValue newValue = PropertyValue.of(KeywordValue.of("MyFont"));
        newValue.status(Status.PROCESSED);
        descriptor.propertyValue(newValue);

        block.fontDescriptors().append(descriptor);
        assertThat(broadcaster.all).isEmpty();
    }

    @Test
    public void writeVerbose() {
        StyleWriter writer = StyleWriter.verbose();
        assertThat(writer.writeSingle(descriptor)).isEqualTo("font-family: \"My Font\"");
    }

    @Test
    public void writeInline() {
        StyleWriter writer = StyleWriter.inline();
        assertThat(writer.writeSingle(descriptor)).isEqualTo("font-family:\"My Font\"");
    }

    @Test
    public void writeCompressed() {
        StyleWriter writer = StyleWriter.compressed();
        assertThat(writer.writeSingle(descriptor)).isEqualTo("font-family:\"My Font\"");
    }

    @Test
    public void isWritableTrue() {
        assertThat(descriptor.isWritable()).isTrue();
    }

    @Test
    public void isNotWritableWhenPropertyValueNotWritable() {
        samplePropertyValue.members().clear();
        assertThat(descriptor.isWritable()).isFalse();
    }

    @Test
    public void copy() {
        descriptor.comments(Lists.newArrayList("test"));

        FontDescriptor copy = descriptor.copy();
        assertThat(copy.isProperty(samplePropertyName.asProperty().get()));
        assertThat(Values.asString(copy.propertyValue()).isPresent()).isTrue();
        assertThat(copy.comments()).hasSameSizeAs(descriptor.comments());
    }
}
