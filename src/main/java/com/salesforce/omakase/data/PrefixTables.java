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

package com.salesforce.omakase.data;

import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;

/**
 * Contains the last version of a browser that requires a prefix for various CSS properties.
 * <p/>
 * THIS FILE IS GENERATED. DO NOT EDIT DIRECTLY.
 * <p/>
 * See class com.salesforce.omakase.test.util.tool.GeneratePrefixTablesClass for instructions on updating.
 */
@SuppressWarnings("AutoBoxing")
public final class PrefixTables {
    static final Table<Property, Browser, Double> PROPERTIES;
    static final Table<Keyword, Browser, Double> KEYWORDS;
    static final Table<String, Browser, Double> AT_RULES;
    static final Table<String, Browser, Double> SELECTORS;
    static final Table<String, Browser, Double> FUNCTIONS;

    static {
        ImmutableTable.Builder<Property, Browser, Double> builder = ImmutableTable.builder();

        builder.put(Property.BORDER_RADIUS, Browser.CHROME, 4.0);
        builder.put(Property.BORDER_TOP_LEFT_RADIUS, Browser.CHROME, 4.0);
        builder.put(Property.BORDER_TOP_RIGHT_RADIUS, Browser.CHROME, 4.0);
        builder.put(Property.BORDER_BOTTOM_LEFT_RADIUS, Browser.CHROME, 4.0);
        builder.put(Property.BORDER_BOTTOM_RIGHT_RADIUS, Browser.CHROME, 4.0);
        builder.put(Property.BORDER_RADIUS, Browser.SAFARI, 4.0);
        builder.put(Property.BORDER_TOP_LEFT_RADIUS, Browser.SAFARI, 4.0);
        builder.put(Property.BORDER_TOP_RIGHT_RADIUS, Browser.SAFARI, 4.0);
        builder.put(Property.BORDER_BOTTOM_LEFT_RADIUS, Browser.SAFARI, 4.0);
        builder.put(Property.BORDER_BOTTOM_RIGHT_RADIUS, Browser.SAFARI, 4.0);
        builder.put(Property.BORDER_RADIUS, Browser.FIREFOX, 3.6);
        builder.put(Property.BORDER_TOP_LEFT_RADIUS, Browser.FIREFOX, 3.6);
        builder.put(Property.BORDER_TOP_RIGHT_RADIUS, Browser.FIREFOX, 3.6);
        builder.put(Property.BORDER_BOTTOM_LEFT_RADIUS, Browser.FIREFOX, 3.6);
        builder.put(Property.BORDER_BOTTOM_RIGHT_RADIUS, Browser.FIREFOX, 3.6);
        builder.put(Property.BORDER_RADIUS, Browser.ANDROID, 2.1);
        builder.put(Property.BORDER_TOP_LEFT_RADIUS, Browser.ANDROID, 2.1);
        builder.put(Property.BORDER_TOP_RIGHT_RADIUS, Browser.ANDROID, 2.1);
        builder.put(Property.BORDER_BOTTOM_LEFT_RADIUS, Browser.ANDROID, 2.1);
        builder.put(Property.BORDER_BOTTOM_RIGHT_RADIUS, Browser.ANDROID, 2.1);
        builder.put(Property.BORDER_RADIUS, Browser.IOS_SAFARI, 3.2);
        builder.put(Property.BORDER_TOP_LEFT_RADIUS, Browser.IOS_SAFARI, 3.2);
        builder.put(Property.BORDER_TOP_RIGHT_RADIUS, Browser.IOS_SAFARI, 3.2);
        builder.put(Property.BORDER_BOTTOM_LEFT_RADIUS, Browser.IOS_SAFARI, 3.2);
        builder.put(Property.BORDER_BOTTOM_RIGHT_RADIUS, Browser.IOS_SAFARI, 3.2);
        builder.put(Property.BACKGROUND_CLIP, Browser.OPERA, 10.1);
        builder.put(Property.BACKGROUND_ORIGIN, Browser.OPERA, 10.1);
        builder.put(Property.BACKGROUND_SIZE, Browser.OPERA, 10.1);
        builder.put(Property.BACKGROUND_CLIP, Browser.FIREFOX, 3.6);
        builder.put(Property.BACKGROUND_ORIGIN, Browser.FIREFOX, 3.6);
        builder.put(Property.BACKGROUND_SIZE, Browser.FIREFOX, 3.6);
        builder.put(Property.BACKGROUND_CLIP, Browser.ANDROID, 2.3);
        builder.put(Property.BACKGROUND_ORIGIN, Browser.ANDROID, 2.3);
        builder.put(Property.BACKGROUND_SIZE, Browser.ANDROID, 2.3);
        builder.put(Property.BORDER_IMAGE, Browser.OPERA, 12.1);
        builder.put(Property.BORDER_IMAGE_SOURCE, Browser.OPERA, 12.1);
        builder.put(Property.BORDER_IMAGE_WIDTH, Browser.OPERA, 12.1);
        builder.put(Property.BORDER_IMAGE_SLICE, Browser.OPERA, 12.1);
        builder.put(Property.BORDER_IMAGE_REPEAT, Browser.OPERA, 12.1);
        builder.put(Property.BORDER_IMAGE_OUTSET, Browser.OPERA, 12.1);
        builder.put(Property.BORDER_IMAGE, Browser.CHROME, 15.0);
        builder.put(Property.BORDER_IMAGE_SOURCE, Browser.CHROME, 15.0);
        builder.put(Property.BORDER_IMAGE_WIDTH, Browser.CHROME, 15.0);
        builder.put(Property.BORDER_IMAGE_SLICE, Browser.CHROME, 15.0);
        builder.put(Property.BORDER_IMAGE_REPEAT, Browser.CHROME, 15.0);
        builder.put(Property.BORDER_IMAGE_OUTSET, Browser.CHROME, 15.0);
        builder.put(Property.BORDER_IMAGE, Browser.SAFARI, 5.1);
        builder.put(Property.BORDER_IMAGE_SOURCE, Browser.SAFARI, 5.1);
        builder.put(Property.BORDER_IMAGE_WIDTH, Browser.SAFARI, 5.1);
        builder.put(Property.BORDER_IMAGE_SLICE, Browser.SAFARI, 5.1);
        builder.put(Property.BORDER_IMAGE_REPEAT, Browser.SAFARI, 5.1);
        builder.put(Property.BORDER_IMAGE_OUTSET, Browser.SAFARI, 5.1);
        builder.put(Property.BORDER_IMAGE, Browser.FIREFOX, 14.0);
        builder.put(Property.BORDER_IMAGE_SOURCE, Browser.FIREFOX, 14.0);
        builder.put(Property.BORDER_IMAGE_WIDTH, Browser.FIREFOX, 14.0);
        builder.put(Property.BORDER_IMAGE_SLICE, Browser.FIREFOX, 14.0);
        builder.put(Property.BORDER_IMAGE_REPEAT, Browser.FIREFOX, 14.0);
        builder.put(Property.BORDER_IMAGE_OUTSET, Browser.FIREFOX, 14.0);
        builder.put(Property.BORDER_IMAGE, Browser.ANDROID, 4.3);
        builder.put(Property.BORDER_IMAGE_SOURCE, Browser.ANDROID, 4.3);
        builder.put(Property.BORDER_IMAGE_WIDTH, Browser.ANDROID, 4.3);
        builder.put(Property.BORDER_IMAGE_SLICE, Browser.ANDROID, 4.3);
        builder.put(Property.BORDER_IMAGE_REPEAT, Browser.ANDROID, 4.3);
        builder.put(Property.BORDER_IMAGE_OUTSET, Browser.ANDROID, 4.3);
        builder.put(Property.BORDER_IMAGE, Browser.IOS_SAFARI, 5.1);
        builder.put(Property.BORDER_IMAGE_SOURCE, Browser.IOS_SAFARI, 5.1);
        builder.put(Property.BORDER_IMAGE_WIDTH, Browser.IOS_SAFARI, 5.1);
        builder.put(Property.BORDER_IMAGE_SLICE, Browser.IOS_SAFARI, 5.1);
        builder.put(Property.BORDER_IMAGE_REPEAT, Browser.IOS_SAFARI, 5.1);
        builder.put(Property.BORDER_IMAGE_OUTSET, Browser.IOS_SAFARI, 5.1);
        builder.put(Property.BORDER_IMAGE, Browser.OPERA_MINI, 8.0);
        builder.put(Property.BORDER_IMAGE_SOURCE, Browser.OPERA_MINI, 8.0);
        builder.put(Property.BORDER_IMAGE_WIDTH, Browser.OPERA_MINI, 8.0);
        builder.put(Property.BORDER_IMAGE_SLICE, Browser.OPERA_MINI, 8.0);
        builder.put(Property.BORDER_IMAGE_REPEAT, Browser.OPERA_MINI, 8.0);
        builder.put(Property.BORDER_IMAGE_OUTSET, Browser.OPERA_MINI, 8.0);
        builder.put(Property.BOX_SHADOW, Browser.CHROME, 9.0);
        builder.put(Property.BOX_SHADOW, Browser.SAFARI, 5.0);
        builder.put(Property.BOX_SHADOW, Browser.FIREFOX, 3.6);
        builder.put(Property.BOX_SHADOW, Browser.ANDROID, 3.0);
        builder.put(Property.BOX_SHADOW, Browser.IOS_SAFARI, 4.3);
        builder.put(Property.ANIMATION, Browser.OPERA, 29.0);
        builder.put(Property.ANIMATION_DELAY, Browser.OPERA, 29.0);
        builder.put(Property.ANIMATION_DIRECTION, Browser.OPERA, 29.0);
        builder.put(Property.ANIMATION_DURATION, Browser.OPERA, 29.0);
        builder.put(Property.ANIMATION_FILL_MODE, Browser.OPERA, 29.0);
        builder.put(Property.ANIMATION_ITERATION_COUNT, Browser.OPERA, 29.0);
        builder.put(Property.ANIMATION_NAME, Browser.OPERA, 29.0);
        builder.put(Property.ANIMATION_PLAY_STATE, Browser.OPERA, 29.0);
        builder.put(Property.ANIMATION_TIMING_FUNCTION, Browser.OPERA, 29.0);
        builder.put(Property.ANIMATION, Browser.CHROME, 42.0);
        builder.put(Property.ANIMATION_DELAY, Browser.CHROME, 42.0);
        builder.put(Property.ANIMATION_DIRECTION, Browser.CHROME, 42.0);
        builder.put(Property.ANIMATION_DURATION, Browser.CHROME, 42.0);
        builder.put(Property.ANIMATION_FILL_MODE, Browser.CHROME, 42.0);
        builder.put(Property.ANIMATION_ITERATION_COUNT, Browser.CHROME, 42.0);
        builder.put(Property.ANIMATION_NAME, Browser.CHROME, 42.0);
        builder.put(Property.ANIMATION_PLAY_STATE, Browser.CHROME, 42.0);
        builder.put(Property.ANIMATION_TIMING_FUNCTION, Browser.CHROME, 42.0);
        builder.put(Property.ANIMATION, Browser.SAFARI, 8.0);
        builder.put(Property.ANIMATION_DELAY, Browser.SAFARI, 8.0);
        builder.put(Property.ANIMATION_DIRECTION, Browser.SAFARI, 8.0);
        builder.put(Property.ANIMATION_DURATION, Browser.SAFARI, 8.0);
        builder.put(Property.ANIMATION_FILL_MODE, Browser.SAFARI, 8.0);
        builder.put(Property.ANIMATION_ITERATION_COUNT, Browser.SAFARI, 8.0);
        builder.put(Property.ANIMATION_NAME, Browser.SAFARI, 8.0);
        builder.put(Property.ANIMATION_PLAY_STATE, Browser.SAFARI, 8.0);
        builder.put(Property.ANIMATION_TIMING_FUNCTION, Browser.SAFARI, 8.0);
        builder.put(Property.ANIMATION, Browser.FIREFOX, 15.0);
        builder.put(Property.ANIMATION_DELAY, Browser.FIREFOX, 15.0);
        builder.put(Property.ANIMATION_DIRECTION, Browser.FIREFOX, 15.0);
        builder.put(Property.ANIMATION_DURATION, Browser.FIREFOX, 15.0);
        builder.put(Property.ANIMATION_FILL_MODE, Browser.FIREFOX, 15.0);
        builder.put(Property.ANIMATION_ITERATION_COUNT, Browser.FIREFOX, 15.0);
        builder.put(Property.ANIMATION_NAME, Browser.FIREFOX, 15.0);
        builder.put(Property.ANIMATION_PLAY_STATE, Browser.FIREFOX, 15.0);
        builder.put(Property.ANIMATION_TIMING_FUNCTION, Browser.FIREFOX, 15.0);
        builder.put(Property.ANIMATION, Browser.ANDROID, 40.0);
        builder.put(Property.ANIMATION_DELAY, Browser.ANDROID, 40.0);
        builder.put(Property.ANIMATION_DIRECTION, Browser.ANDROID, 40.0);
        builder.put(Property.ANIMATION_DURATION, Browser.ANDROID, 40.0);
        builder.put(Property.ANIMATION_FILL_MODE, Browser.ANDROID, 40.0);
        builder.put(Property.ANIMATION_ITERATION_COUNT, Browser.ANDROID, 40.0);
        builder.put(Property.ANIMATION_NAME, Browser.ANDROID, 40.0);
        builder.put(Property.ANIMATION_PLAY_STATE, Browser.ANDROID, 40.0);
        builder.put(Property.ANIMATION_TIMING_FUNCTION, Browser.ANDROID, 40.0);
        builder.put(Property.ANIMATION, Browser.IOS_SAFARI, 8.4);
        builder.put(Property.ANIMATION_DELAY, Browser.IOS_SAFARI, 8.4);
        builder.put(Property.ANIMATION_DIRECTION, Browser.IOS_SAFARI, 8.4);
        builder.put(Property.ANIMATION_DURATION, Browser.IOS_SAFARI, 8.4);
        builder.put(Property.ANIMATION_FILL_MODE, Browser.IOS_SAFARI, 8.4);
        builder.put(Property.ANIMATION_ITERATION_COUNT, Browser.IOS_SAFARI, 8.4);
        builder.put(Property.ANIMATION_NAME, Browser.IOS_SAFARI, 8.4);
        builder.put(Property.ANIMATION_PLAY_STATE, Browser.IOS_SAFARI, 8.4);
        builder.put(Property.ANIMATION_TIMING_FUNCTION, Browser.IOS_SAFARI, 8.4);
        builder.put(Property.TRANSITION, Browser.OPERA, 12.0);
        builder.put(Property.TRANSITION_PROPERTY, Browser.OPERA, 12.0);
        builder.put(Property.TRANSITION_DURATION, Browser.OPERA, 12.0);
        builder.put(Property.TRANSITION_DELAY, Browser.OPERA, 12.0);
        builder.put(Property.TRANSITION_TIMING_FUNCTION, Browser.OPERA, 12.0);
        builder.put(Property.TRANSITION, Browser.CHROME, 25.0);
        builder.put(Property.TRANSITION_PROPERTY, Browser.CHROME, 25.0);
        builder.put(Property.TRANSITION_DURATION, Browser.CHROME, 25.0);
        builder.put(Property.TRANSITION_DELAY, Browser.CHROME, 25.0);
        builder.put(Property.TRANSITION_TIMING_FUNCTION, Browser.CHROME, 25.0);
        builder.put(Property.TRANSITION, Browser.SAFARI, 6.0);
        builder.put(Property.TRANSITION_PROPERTY, Browser.SAFARI, 6.0);
        builder.put(Property.TRANSITION_DURATION, Browser.SAFARI, 6.0);
        builder.put(Property.TRANSITION_DELAY, Browser.SAFARI, 6.0);
        builder.put(Property.TRANSITION_TIMING_FUNCTION, Browser.SAFARI, 6.0);
        builder.put(Property.TRANSITION, Browser.FIREFOX, 15.0);
        builder.put(Property.TRANSITION_PROPERTY, Browser.FIREFOX, 15.0);
        builder.put(Property.TRANSITION_DURATION, Browser.FIREFOX, 15.0);
        builder.put(Property.TRANSITION_DELAY, Browser.FIREFOX, 15.0);
        builder.put(Property.TRANSITION_TIMING_FUNCTION, Browser.FIREFOX, 15.0);
        builder.put(Property.TRANSITION, Browser.ANDROID, 4.3);
        builder.put(Property.TRANSITION_PROPERTY, Browser.ANDROID, 4.3);
        builder.put(Property.TRANSITION_DURATION, Browser.ANDROID, 4.3);
        builder.put(Property.TRANSITION_DELAY, Browser.ANDROID, 4.3);
        builder.put(Property.TRANSITION_TIMING_FUNCTION, Browser.ANDROID, 4.3);
        builder.put(Property.TRANSITION, Browser.IOS_SAFARI, 6.1);
        builder.put(Property.TRANSITION_PROPERTY, Browser.IOS_SAFARI, 6.1);
        builder.put(Property.TRANSITION_DURATION, Browser.IOS_SAFARI, 6.1);
        builder.put(Property.TRANSITION_DELAY, Browser.IOS_SAFARI, 6.1);
        builder.put(Property.TRANSITION_TIMING_FUNCTION, Browser.IOS_SAFARI, 6.1);
        builder.put(Property.TRANSFORM, Browser.IE, 9.0);
        builder.put(Property.TRANSFORM_ORIGIN, Browser.IE, 9.0);
        builder.put(Property.TRANSFORM_STYLE, Browser.IE, 9.0);
        builder.put(Property.TRANSFORM, Browser.OPERA, 22.0);
        builder.put(Property.TRANSFORM_ORIGIN, Browser.OPERA, 22.0);
        builder.put(Property.TRANSFORM_STYLE, Browser.OPERA, 22.0);
        builder.put(Property.TRANSFORM, Browser.CHROME, 35.0);
        builder.put(Property.TRANSFORM_ORIGIN, Browser.CHROME, 35.0);
        builder.put(Property.TRANSFORM_STYLE, Browser.CHROME, 35.0);
        builder.put(Property.TRANSFORM, Browser.SAFARI, 8.0);
        builder.put(Property.TRANSFORM_ORIGIN, Browser.SAFARI, 8.0);
        builder.put(Property.TRANSFORM_STYLE, Browser.SAFARI, 8.0);
        builder.put(Property.TRANSFORM, Browser.FIREFOX, 15.0);
        builder.put(Property.TRANSFORM_ORIGIN, Browser.FIREFOX, 15.0);
        builder.put(Property.TRANSFORM_STYLE, Browser.FIREFOX, 15.0);
        builder.put(Property.TRANSFORM, Browser.ANDROID, 4.4);
        builder.put(Property.TRANSFORM_ORIGIN, Browser.ANDROID, 4.4);
        builder.put(Property.TRANSFORM_STYLE, Browser.ANDROID, 4.4);
        builder.put(Property.TRANSFORM, Browser.IOS_SAFARI, 8.4);
        builder.put(Property.TRANSFORM_ORIGIN, Browser.IOS_SAFARI, 8.4);
        builder.put(Property.TRANSFORM_STYLE, Browser.IOS_SAFARI, 8.4);
        builder.put(Property.PERSPECTIVE, Browser.OPERA, 22.0);
        builder.put(Property.PERSPECTIVE_ORIGIN, Browser.OPERA, 22.0);
        builder.put(Property.BACKFACE_VISIBILITY, Browser.OPERA, 22.0);
        builder.put(Property.PERSPECTIVE, Browser.CHROME, 35.0);
        builder.put(Property.PERSPECTIVE_ORIGIN, Browser.CHROME, 35.0);
        builder.put(Property.BACKFACE_VISIBILITY, Browser.CHROME, 35.0);
        builder.put(Property.PERSPECTIVE, Browser.SAFARI, 8.0);
        builder.put(Property.PERSPECTIVE_ORIGIN, Browser.SAFARI, 8.0);
        builder.put(Property.BACKFACE_VISIBILITY, Browser.SAFARI, 8.0);
        builder.put(Property.PERSPECTIVE, Browser.FIREFOX, 15.0);
        builder.put(Property.PERSPECTIVE_ORIGIN, Browser.FIREFOX, 15.0);
        builder.put(Property.BACKFACE_VISIBILITY, Browser.FIREFOX, 15.0);
        builder.put(Property.PERSPECTIVE, Browser.ANDROID, 4.4);
        builder.put(Property.PERSPECTIVE_ORIGIN, Browser.ANDROID, 4.4);
        builder.put(Property.BACKFACE_VISIBILITY, Browser.ANDROID, 4.4);
        builder.put(Property.PERSPECTIVE, Browser.IOS_SAFARI, 8.4);
        builder.put(Property.PERSPECTIVE_ORIGIN, Browser.IOS_SAFARI, 8.4);
        builder.put(Property.BACKFACE_VISIBILITY, Browser.IOS_SAFARI, 8.4);
        builder.put(Property.BOX_SIZING, Browser.CHROME, 9.0);
        builder.put(Property.BOX_SIZING, Browser.SAFARI, 5.0);
        builder.put(Property.BOX_SIZING, Browser.FIREFOX, 28.0);
        builder.put(Property.BOX_SIZING, Browser.ANDROID, 3.0);
        builder.put(Property.BOX_SIZING, Browser.IOS_SAFARI, 4.3);
        builder.put(Property.COLUMNS, Browser.OPERA, 30.0);
        builder.put(Property.COLUMN_WIDTH, Browser.OPERA, 30.0);
        builder.put(Property.COLUMN_GAP, Browser.OPERA, 30.0);
        builder.put(Property.COLUMN_RULE, Browser.OPERA, 30.0);
        builder.put(Property.COLUMN_COUNT, Browser.OPERA, 30.0);
        builder.put(Property.COLUMN_RULE_COLOR, Browser.OPERA, 30.0);
        builder.put(Property.COLUMN_RULE_WIDTH, Browser.OPERA, 30.0);
        builder.put(Property.COLUMN_RULE_STYLE, Browser.OPERA, 30.0);
        builder.put(Property.COLUMN_SPAN, Browser.OPERA, 30.0);
        builder.put(Property.COLUMN_FILL, Browser.OPERA, 30.0);
        builder.put(Property.COLUMNS, Browser.CHROME, 44.0);
        builder.put(Property.COLUMN_WIDTH, Browser.CHROME, 44.0);
        builder.put(Property.COLUMN_GAP, Browser.CHROME, 44.0);
        builder.put(Property.COLUMN_RULE, Browser.CHROME, 44.0);
        builder.put(Property.COLUMN_COUNT, Browser.CHROME, 44.0);
        builder.put(Property.COLUMN_RULE_COLOR, Browser.CHROME, 44.0);
        builder.put(Property.COLUMN_RULE_WIDTH, Browser.CHROME, 44.0);
        builder.put(Property.COLUMN_RULE_STYLE, Browser.CHROME, 44.0);
        builder.put(Property.COLUMN_SPAN, Browser.CHROME, 44.0);
        builder.put(Property.COLUMN_FILL, Browser.CHROME, 44.0);
        builder.put(Property.COLUMNS, Browser.SAFARI, 8.0);
        builder.put(Property.COLUMN_WIDTH, Browser.SAFARI, 8.0);
        builder.put(Property.COLUMN_GAP, Browser.SAFARI, 8.0);
        builder.put(Property.COLUMN_RULE, Browser.SAFARI, 8.0);
        builder.put(Property.COLUMN_COUNT, Browser.SAFARI, 8.0);
        builder.put(Property.COLUMN_RULE_COLOR, Browser.SAFARI, 8.0);
        builder.put(Property.COLUMN_RULE_WIDTH, Browser.SAFARI, 8.0);
        builder.put(Property.COLUMN_RULE_STYLE, Browser.SAFARI, 8.0);
        builder.put(Property.COLUMN_SPAN, Browser.SAFARI, 8.0);
        builder.put(Property.COLUMN_FILL, Browser.SAFARI, 8.0);
        builder.put(Property.COLUMNS, Browser.FIREFOX, 40.0);
        builder.put(Property.COLUMN_WIDTH, Browser.FIREFOX, 40.0);
        builder.put(Property.COLUMN_GAP, Browser.FIREFOX, 40.0);
        builder.put(Property.COLUMN_RULE, Browser.FIREFOX, 40.0);
        builder.put(Property.COLUMN_COUNT, Browser.FIREFOX, 40.0);
        builder.put(Property.COLUMN_RULE_COLOR, Browser.FIREFOX, 40.0);
        builder.put(Property.COLUMN_RULE_WIDTH, Browser.FIREFOX, 40.0);
        builder.put(Property.COLUMN_RULE_STYLE, Browser.FIREFOX, 40.0);
        builder.put(Property.COLUMN_SPAN, Browser.FIREFOX, 40.0);
        builder.put(Property.COLUMN_FILL, Browser.FIREFOX, 40.0);
        builder.put(Property.COLUMNS, Browser.ANDROID, 40.0);
        builder.put(Property.COLUMN_WIDTH, Browser.ANDROID, 40.0);
        builder.put(Property.COLUMN_GAP, Browser.ANDROID, 40.0);
        builder.put(Property.COLUMN_RULE, Browser.ANDROID, 40.0);
        builder.put(Property.COLUMN_COUNT, Browser.ANDROID, 40.0);
        builder.put(Property.COLUMN_RULE_COLOR, Browser.ANDROID, 40.0);
        builder.put(Property.COLUMN_RULE_WIDTH, Browser.ANDROID, 40.0);
        builder.put(Property.COLUMN_RULE_STYLE, Browser.ANDROID, 40.0);
        builder.put(Property.COLUMN_SPAN, Browser.ANDROID, 40.0);
        builder.put(Property.COLUMN_FILL, Browser.ANDROID, 40.0);
        builder.put(Property.COLUMNS, Browser.IOS_SAFARI, 8.4);
        builder.put(Property.COLUMN_WIDTH, Browser.IOS_SAFARI, 8.4);
        builder.put(Property.COLUMN_GAP, Browser.IOS_SAFARI, 8.4);
        builder.put(Property.COLUMN_RULE, Browser.IOS_SAFARI, 8.4);
        builder.put(Property.COLUMN_COUNT, Browser.IOS_SAFARI, 8.4);
        builder.put(Property.COLUMN_RULE_COLOR, Browser.IOS_SAFARI, 8.4);
        builder.put(Property.COLUMN_RULE_WIDTH, Browser.IOS_SAFARI, 8.4);
        builder.put(Property.COLUMN_RULE_STYLE, Browser.IOS_SAFARI, 8.4);
        builder.put(Property.COLUMN_SPAN, Browser.IOS_SAFARI, 8.4);
        builder.put(Property.COLUMN_FILL, Browser.IOS_SAFARI, 8.4);
        builder.put(Property.ALIGN_CONTENT, Browser.IE, 10.0);
        builder.put(Property.ALIGN_ITEMS, Browser.IE, 10.0);
        builder.put(Property.ALIGN_SELF, Browser.IE, 10.0);
        builder.put(Property.FLEX, Browser.IE, 10.0);
        builder.put(Property.FLEX_BASIS, Browser.IE, 10.0);
        builder.put(Property.FLEX_DIRECTION, Browser.IE, 10.0);
        builder.put(Property.FLEX_FLOW, Browser.IE, 10.0);
        builder.put(Property.FLEX_GROW, Browser.IE, 10.0);
        builder.put(Property.FLEX_SHRINK, Browser.IE, 10.0);
        builder.put(Property.FLEX_WRAP, Browser.IE, 10.0);
        builder.put(Property.JUSTIFY_CONTENT, Browser.IE, 10.0);
        builder.put(Property.ORDER, Browser.IE, 10.0);
        builder.put(Property.ALIGN_CONTENT, Browser.OPERA, 16.0);
        builder.put(Property.ALIGN_ITEMS, Browser.OPERA, 16.0);
        builder.put(Property.ALIGN_SELF, Browser.OPERA, 16.0);
        builder.put(Property.FLEX, Browser.OPERA, 16.0);
        builder.put(Property.FLEX_BASIS, Browser.OPERA, 16.0);
        builder.put(Property.FLEX_DIRECTION, Browser.OPERA, 16.0);
        builder.put(Property.FLEX_FLOW, Browser.OPERA, 16.0);
        builder.put(Property.FLEX_GROW, Browser.OPERA, 16.0);
        builder.put(Property.FLEX_SHRINK, Browser.OPERA, 16.0);
        builder.put(Property.FLEX_WRAP, Browser.OPERA, 16.0);
        builder.put(Property.JUSTIFY_CONTENT, Browser.OPERA, 16.0);
        builder.put(Property.ORDER, Browser.OPERA, 16.0);
        builder.put(Property.ALIGN_CONTENT, Browser.CHROME, 28.0);
        builder.put(Property.ALIGN_ITEMS, Browser.CHROME, 28.0);
        builder.put(Property.ALIGN_SELF, Browser.CHROME, 28.0);
        builder.put(Property.FLEX, Browser.CHROME, 28.0);
        builder.put(Property.FLEX_BASIS, Browser.CHROME, 28.0);
        builder.put(Property.FLEX_DIRECTION, Browser.CHROME, 28.0);
        builder.put(Property.FLEX_FLOW, Browser.CHROME, 28.0);
        builder.put(Property.FLEX_GROW, Browser.CHROME, 28.0);
        builder.put(Property.FLEX_SHRINK, Browser.CHROME, 28.0);
        builder.put(Property.FLEX_WRAP, Browser.CHROME, 28.0);
        builder.put(Property.JUSTIFY_CONTENT, Browser.CHROME, 28.0);
        builder.put(Property.ORDER, Browser.CHROME, 28.0);
        builder.put(Property.ALIGN_CONTENT, Browser.SAFARI, 8.0);
        builder.put(Property.ALIGN_ITEMS, Browser.SAFARI, 8.0);
        builder.put(Property.ALIGN_SELF, Browser.SAFARI, 8.0);
        builder.put(Property.FLEX, Browser.SAFARI, 8.0);
        builder.put(Property.FLEX_BASIS, Browser.SAFARI, 8.0);
        builder.put(Property.FLEX_DIRECTION, Browser.SAFARI, 8.0);
        builder.put(Property.FLEX_FLOW, Browser.SAFARI, 8.0);
        builder.put(Property.FLEX_GROW, Browser.SAFARI, 8.0);
        builder.put(Property.FLEX_SHRINK, Browser.SAFARI, 8.0);
        builder.put(Property.FLEX_WRAP, Browser.SAFARI, 8.0);
        builder.put(Property.JUSTIFY_CONTENT, Browser.SAFARI, 8.0);
        builder.put(Property.ORDER, Browser.SAFARI, 8.0);
        builder.put(Property.ALIGN_CONTENT, Browser.FIREFOX, 21.0);
        builder.put(Property.ALIGN_ITEMS, Browser.FIREFOX, 21.0);
        builder.put(Property.ALIGN_SELF, Browser.FIREFOX, 21.0);
        builder.put(Property.FLEX, Browser.FIREFOX, 21.0);
        builder.put(Property.FLEX_BASIS, Browser.FIREFOX, 21.0);
        builder.put(Property.FLEX_DIRECTION, Browser.FIREFOX, 21.0);
        builder.put(Property.FLEX_FLOW, Browser.FIREFOX, 21.0);
        builder.put(Property.FLEX_GROW, Browser.FIREFOX, 21.0);
        builder.put(Property.FLEX_SHRINK, Browser.FIREFOX, 21.0);
        builder.put(Property.FLEX_WRAP, Browser.FIREFOX, 21.0);
        builder.put(Property.JUSTIFY_CONTENT, Browser.FIREFOX, 21.0);
        builder.put(Property.ORDER, Browser.FIREFOX, 21.0);
        builder.put(Property.ALIGN_CONTENT, Browser.ANDROID, 4.3);
        builder.put(Property.ALIGN_ITEMS, Browser.ANDROID, 4.3);
        builder.put(Property.ALIGN_SELF, Browser.ANDROID, 4.3);
        builder.put(Property.FLEX, Browser.ANDROID, 4.3);
        builder.put(Property.FLEX_BASIS, Browser.ANDROID, 4.3);
        builder.put(Property.FLEX_DIRECTION, Browser.ANDROID, 4.3);
        builder.put(Property.FLEX_FLOW, Browser.ANDROID, 4.3);
        builder.put(Property.FLEX_GROW, Browser.ANDROID, 4.3);
        builder.put(Property.FLEX_SHRINK, Browser.ANDROID, 4.3);
        builder.put(Property.FLEX_WRAP, Browser.ANDROID, 4.3);
        builder.put(Property.JUSTIFY_CONTENT, Browser.ANDROID, 4.3);
        builder.put(Property.ORDER, Browser.ANDROID, 4.3);
        builder.put(Property.ALIGN_CONTENT, Browser.IE_MOBILE, 10.0);
        builder.put(Property.ALIGN_ITEMS, Browser.IE_MOBILE, 10.0);
        builder.put(Property.ALIGN_SELF, Browser.IE_MOBILE, 10.0);
        builder.put(Property.FLEX, Browser.IE_MOBILE, 10.0);
        builder.put(Property.FLEX_BASIS, Browser.IE_MOBILE, 10.0);
        builder.put(Property.FLEX_DIRECTION, Browser.IE_MOBILE, 10.0);
        builder.put(Property.FLEX_FLOW, Browser.IE_MOBILE, 10.0);
        builder.put(Property.FLEX_GROW, Browser.IE_MOBILE, 10.0);
        builder.put(Property.FLEX_SHRINK, Browser.IE_MOBILE, 10.0);
        builder.put(Property.FLEX_WRAP, Browser.IE_MOBILE, 10.0);
        builder.put(Property.JUSTIFY_CONTENT, Browser.IE_MOBILE, 10.0);
        builder.put(Property.ORDER, Browser.IE_MOBILE, 10.0);
        builder.put(Property.ALIGN_CONTENT, Browser.IOS_SAFARI, 8.4);
        builder.put(Property.ALIGN_ITEMS, Browser.IOS_SAFARI, 8.4);
        builder.put(Property.ALIGN_SELF, Browser.IOS_SAFARI, 8.4);
        builder.put(Property.FLEX, Browser.IOS_SAFARI, 8.4);
        builder.put(Property.FLEX_BASIS, Browser.IOS_SAFARI, 8.4);
        builder.put(Property.FLEX_DIRECTION, Browser.IOS_SAFARI, 8.4);
        builder.put(Property.FLEX_FLOW, Browser.IOS_SAFARI, 8.4);
        builder.put(Property.FLEX_GROW, Browser.IOS_SAFARI, 8.4);
        builder.put(Property.FLEX_SHRINK, Browser.IOS_SAFARI, 8.4);
        builder.put(Property.FLEX_WRAP, Browser.IOS_SAFARI, 8.4);
        builder.put(Property.JUSTIFY_CONTENT, Browser.IOS_SAFARI, 8.4);
        builder.put(Property.ORDER, Browser.IOS_SAFARI, 8.4);
        builder.put(Property.USER_SELECT, Browser.IE, 11.0);
        builder.put(Property.USER_SELECT, Browser.OPERA, 30.0);
        builder.put(Property.USER_SELECT, Browser.CHROME, 44.0);
        builder.put(Property.USER_SELECT, Browser.SAFARI, 8.0);
        builder.put(Property.USER_SELECT, Browser.FIREFOX, 40.0);
        builder.put(Property.USER_SELECT, Browser.ANDROID, 40.0);
        builder.put(Property.USER_SELECT, Browser.IE_MOBILE, 11.0);
        builder.put(Property.USER_SELECT, Browser.IOS_SAFARI, 8.4);
        builder.put(Property.HYPHENS, Browser.IE, 11.0);
        builder.put(Property.HYPHENS, Browser.SAFARI, 8.0);
        builder.put(Property.HYPHENS, Browser.FIREFOX, 40.0);
        builder.put(Property.HYPHENS, Browser.IOS_SAFARI, 8.4);
        builder.put(Property.TAB_SIZE, Browser.OPERA, 12.1);
        builder.put(Property.TAB_SIZE, Browser.FIREFOX, 40.0);
        builder.put(Property.TAB_SIZE, Browser.OPERA_MINI, 8.0);
        builder.put(Property.APPEARANCE, Browser.FIREFOX, 40.0);
        builder.put(Property.APPEARANCE, Browser.CHROME, 44.0);
        builder.put(Property.APPEARANCE, Browser.SAFARI, 8.0);
        builder.put(Property.APPEARANCE, Browser.ANDROID, 40.0);
        builder.put(Property.APPEARANCE, Browser.IOS_SAFARI, 8.4);

        PROPERTIES = builder.build();
    }

    static {
        ImmutableTable.Builder<Keyword, Browser, Double> builder = ImmutableTable.builder();

        builder.put(Keyword.FLEX, Browser.IE, 10.0);
        builder.put(Keyword.INLINE_FLEX, Browser.IE, 10.0);
        builder.put(Keyword.FLEX, Browser.OPERA, 16.0);
        builder.put(Keyword.INLINE_FLEX, Browser.OPERA, 16.0);
        builder.put(Keyword.FLEX, Browser.CHROME, 28.0);
        builder.put(Keyword.INLINE_FLEX, Browser.CHROME, 28.0);
        builder.put(Keyword.FLEX, Browser.SAFARI, 8.0);
        builder.put(Keyword.INLINE_FLEX, Browser.SAFARI, 8.0);
        builder.put(Keyword.FLEX, Browser.FIREFOX, 21.0);
        builder.put(Keyword.INLINE_FLEX, Browser.FIREFOX, 21.0);
        builder.put(Keyword.FLEX, Browser.ANDROID, 4.3);
        builder.put(Keyword.INLINE_FLEX, Browser.ANDROID, 4.3);
        builder.put(Keyword.FLEX, Browser.IE_MOBILE, 10.0);
        builder.put(Keyword.INLINE_FLEX, Browser.IE_MOBILE, 10.0);
        builder.put(Keyword.FLEX, Browser.IOS_SAFARI, 8.4);
        builder.put(Keyword.INLINE_FLEX, Browser.IOS_SAFARI, 8.4);

        KEYWORDS = builder.build();
    }

    static {
        ImmutableTable.Builder<String, Browser, Double> builder = ImmutableTable.builder();

        builder.put("keyframes", Browser.OPERA, 29.0);
        builder.put("keyframes", Browser.CHROME, 42.0);
        builder.put("keyframes", Browser.SAFARI, 8.0);
        builder.put("keyframes", Browser.FIREFOX, 15.0);
        builder.put("keyframes", Browser.ANDROID, 40.0);
        builder.put("keyframes", Browser.IOS_SAFARI, 8.4);

        AT_RULES = builder.build();
    }

    static {
        ImmutableTable.Builder<String, Browser, Double> builder = ImmutableTable.builder();

        builder.put("selection", Browser.FIREFOX, 40.0);
        builder.put("placeholder", Browser.IE, 11.0);
        builder.put("placeholder", Browser.OPERA, 30.0);
        builder.put("placeholder", Browser.CHROME, 44.0);
        builder.put("placeholder", Browser.SAFARI, 8.0);
        builder.put("placeholder", Browser.FIREFOX, 40.0);
        builder.put("placeholder", Browser.ANDROID, 40.0);
        builder.put("placeholder", Browser.IE_MOBILE, 11.0);
        builder.put("placeholder", Browser.IOS_SAFARI, 8.4);

        SELECTORS = builder.build();
    }

    static {
        ImmutableTable.Builder<String, Browser, Double> builder = ImmutableTable.builder();

        builder.put("calc", Browser.CHROME, 25.0);
        builder.put("calc", Browser.SAFARI, 6.0);
        builder.put("calc", Browser.FIREFOX, 15.0);
        builder.put("calc", Browser.IOS_SAFARI, 6.1);
        builder.put("linear-gradient", Browser.OPERA, 12.0);
        builder.put("repeating-linear-gradient", Browser.OPERA, 12.0);
        builder.put("linear-gradient", Browser.CHROME, 25.0);
        builder.put("repeating-linear-gradient", Browser.CHROME, 25.0);
        builder.put("linear-gradient", Browser.SAFARI, 6.0);
        builder.put("repeating-linear-gradient", Browser.SAFARI, 6.0);
        builder.put("linear-gradient", Browser.FIREFOX, 15.0);
        builder.put("repeating-linear-gradient", Browser.FIREFOX, 15.0);
        builder.put("linear-gradient", Browser.ANDROID, 4.3);
        builder.put("repeating-linear-gradient", Browser.ANDROID, 4.3);
        builder.put("linear-gradient", Browser.IOS_SAFARI, 6.1);
        builder.put("repeating-linear-gradient", Browser.IOS_SAFARI, 6.1);

        FUNCTIONS = builder.build();
    }

    private PrefixTables() {}
}
