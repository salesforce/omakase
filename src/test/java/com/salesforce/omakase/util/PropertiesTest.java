/*
 * Copyright (c) 2016, salesforce.com, inc.
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

import static com.salesforce.omakase.data.Property.BORDER_BOTTOM_LEFT_RADIUS;
import static com.salesforce.omakase.data.Property.BORDER_BOTTOM_RIGHT_RADIUS;
import static com.salesforce.omakase.data.Property.BORDER_RADIUS;
import static com.salesforce.omakase.data.Property.BORDER_TOP_COLOR;
import static com.salesforce.omakase.data.Property.BORDER_TOP_LEFT_RADIUS;
import static com.salesforce.omakase.data.Property.BORDER_TOP_RIGHT_RADIUS;
import static com.salesforce.omakase.data.Property.BORDER_TOP_STYLE;
import static com.salesforce.omakase.data.Property.BORDER_TOP_WIDTH;
import static com.salesforce.omakase.data.Property.PADDING;
import static com.salesforce.omakase.data.Property.PADDING_BOTTOM;
import static com.salesforce.omakase.data.Property.PADDING_LEFT;
import static com.salesforce.omakase.data.Property.PADDING_RIGHT;
import static com.salesforce.omakase.data.Property.PADDING_TOP;
import static org.fest.assertions.api.Assertions.assertThat;

import java.util.EnumSet;

import org.junit.Test;

import com.salesforce.omakase.data.Property;

/**
 * Unit tests for {@link Properties}.
 *
 * @author nmcwilliams
 */
public class PropertiesTest {
    @Test
    public void expandSingle() {
        EnumSet<Property> matched = Properties.expand("border-*-radius");
        assertThat(matched).containsOnly(BORDER_BOTTOM_LEFT_RADIUS, BORDER_BOTTOM_RIGHT_RADIUS,
            BORDER_TOP_LEFT_RADIUS, BORDER_TOP_RIGHT_RADIUS);
    }

    @Test
    public void expandMultiple() {
        EnumSet<Property> matched = Properties.expand("*-top-*");
        assertThat(matched).containsOnly(BORDER_TOP_COLOR, BORDER_TOP_LEFT_RADIUS, BORDER_TOP_RIGHT_RADIUS, BORDER_TOP_STYLE,
            BORDER_TOP_WIDTH);
    }

    @Test
    public void expandStartOnly() {
        EnumSet<Property> matched = Properties.expand("*radius");
        assertThat(matched).containsOnly(BORDER_BOTTOM_LEFT_RADIUS, BORDER_BOTTOM_RIGHT_RADIUS, BORDER_RADIUS,
            BORDER_TOP_LEFT_RADIUS, BORDER_TOP_RIGHT_RADIUS);
    }

    @Test
    public void expandEndOnly() {
        EnumSet<Property> matched = Properties.expand("padding*");
        assertThat(matched).containsOnly(PADDING, PADDING_TOP, PADDING_BOTTOM, PADDING_LEFT, PADDING_RIGHT);
    }

    @Test
    public void expandNoMatches() {
        EnumSet<Property> matched = Properties.expand("nope*");
        assertThat(matched).isEmpty();
    }

    @Test
    public void expandNoStars() {
        EnumSet<Property> matched = Properties.expand("font");
        assertThat(matched).containsOnly(Property.FONT);
    }
}