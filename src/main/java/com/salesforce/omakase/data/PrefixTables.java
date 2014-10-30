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
    static final Table<String, Browser, Double> FUNCTIONS;
    static final Table<String, Browser, Double> AT_RULES;
    static final Table<String, Browser, Double> SELECTORS;

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
        builder.put(Property.BOX_SHADOW, Browser.CHROME, 9.0);
        builder.put(Property.BOX_SHADOW, Browser.SAFARI, 5.0);
        builder.put(Property.BOX_SHADOW, Browser.FIREFOX, 3.6);
        builder.put(Property.BOX_SHADOW, Browser.ANDROID, 3.0);
        builder.put(Property.BOX_SHADOW, Browser.IOS_SAFARI, 4.3);
        builder.put(Property.ANIMATION, Browser.OPERA, 25.0);
        builder.put(Property.ANIMATION_DELAY, Browser.OPERA, 25.0);
        builder.put(Property.ANIMATION_DIRECTION, Browser.OPERA, 25.0);
        builder.put(Property.ANIMATION_DURATION, Browser.OPERA, 25.0);
        builder.put(Property.ANIMATION_FILL_MODE, Browser.OPERA, 25.0);
        builder.put(Property.ANIMATION_ITERATION_COUNT, Browser.OPERA, 25.0);
        builder.put(Property.ANIMATION_NAME, Browser.OPERA, 25.0);
        builder.put(Property.ANIMATION_PLAY_STATE, Browser.OPERA, 25.0);
        builder.put(Property.ANIMATION_TIMING_FUNCTION, Browser.OPERA, 25.0);
        builder.put(Property.ANIMATION, Browser.CHROME, 38.0);
        builder.put(Property.ANIMATION_DELAY, Browser.CHROME, 38.0);
        builder.put(Property.ANIMATION_DIRECTION, Browser.CHROME, 38.0);
        builder.put(Property.ANIMATION_DURATION, Browser.CHROME, 38.0);
        builder.put(Property.ANIMATION_FILL_MODE, Browser.CHROME, 38.0);
        builder.put(Property.ANIMATION_ITERATION_COUNT, Browser.CHROME, 38.0);
        builder.put(Property.ANIMATION_NAME, Browser.CHROME, 38.0);
        builder.put(Property.ANIMATION_PLAY_STATE, Browser.CHROME, 38.0);
        builder.put(Property.ANIMATION_TIMING_FUNCTION, Browser.CHROME, 38.0);
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
        builder.put(Property.ANIMATION, Browser.ANDROID, 4.4);
        builder.put(Property.ANIMATION_DELAY, Browser.ANDROID, 4.4);
        builder.put(Property.ANIMATION_DIRECTION, Browser.ANDROID, 4.4);
        builder.put(Property.ANIMATION_DURATION, Browser.ANDROID, 4.4);
        builder.put(Property.ANIMATION_FILL_MODE, Browser.ANDROID, 4.4);
        builder.put(Property.ANIMATION_ITERATION_COUNT, Browser.ANDROID, 4.4);
        builder.put(Property.ANIMATION_NAME, Browser.ANDROID, 4.4);
        builder.put(Property.ANIMATION_PLAY_STATE, Browser.ANDROID, 4.4);
        builder.put(Property.ANIMATION_TIMING_FUNCTION, Browser.ANDROID, 4.4);
        builder.put(Property.ANIMATION, Browser.IOS_SAFARI, 8.1);
        builder.put(Property.ANIMATION_DELAY, Browser.IOS_SAFARI, 8.1);
        builder.put(Property.ANIMATION_DIRECTION, Browser.IOS_SAFARI, 8.1);
        builder.put(Property.ANIMATION_DURATION, Browser.IOS_SAFARI, 8.1);
        builder.put(Property.ANIMATION_FILL_MODE, Browser.IOS_SAFARI, 8.1);
        builder.put(Property.ANIMATION_ITERATION_COUNT, Browser.IOS_SAFARI, 8.1);
        builder.put(Property.ANIMATION_NAME, Browser.IOS_SAFARI, 8.1);
        builder.put(Property.ANIMATION_PLAY_STATE, Browser.IOS_SAFARI, 8.1);
        builder.put(Property.ANIMATION_TIMING_FUNCTION, Browser.IOS_SAFARI, 8.1);
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
        builder.put(Property.TRANSFORM, Browser.IOS_SAFARI, 8.1);
        builder.put(Property.TRANSFORM_ORIGIN, Browser.IOS_SAFARI, 8.1);
        builder.put(Property.TRANSFORM_STYLE, Browser.IOS_SAFARI, 8.1);
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
        builder.put(Property.PERSPECTIVE, Browser.IOS_SAFARI, 8.1);
        builder.put(Property.PERSPECTIVE_ORIGIN, Browser.IOS_SAFARI, 8.1);
        builder.put(Property.BACKFACE_VISIBILITY, Browser.IOS_SAFARI, 8.1);
        builder.put(Property.BOX_SIZING, Browser.CHROME, 9.0);
        builder.put(Property.BOX_SIZING, Browser.SAFARI, 5.0);
        builder.put(Property.BOX_SIZING, Browser.FIREFOX, 28.0);
        builder.put(Property.BOX_SIZING, Browser.ANDROID, 3.0);
        builder.put(Property.BOX_SIZING, Browser.IOS_SAFARI, 4.3);
        builder.put(Property.COLUMNS, Browser.OPERA, 25.0);
        builder.put(Property.COLUMN_WIDTH, Browser.OPERA, 25.0);
        builder.put(Property.COLUMN_GAP, Browser.OPERA, 25.0);
        builder.put(Property.COLUMN_RULE, Browser.OPERA, 25.0);
        builder.put(Property.COLUMN_COUNT, Browser.OPERA, 25.0);
        builder.put(Property.COLUMN_RULE_COLOR, Browser.OPERA, 25.0);
        builder.put(Property.COLUMN_RULE_WIDTH, Browser.OPERA, 25.0);
        builder.put(Property.COLUMN_RULE_STYLE, Browser.OPERA, 25.0);
        builder.put(Property.COLUMN_SPAN, Browser.OPERA, 25.0);
        builder.put(Property.COLUMN_FILL, Browser.OPERA, 25.0);
        builder.put(Property.COLUMNS, Browser.CHROME, 38.0);
        builder.put(Property.COLUMN_WIDTH, Browser.CHROME, 38.0);
        builder.put(Property.COLUMN_GAP, Browser.CHROME, 38.0);
        builder.put(Property.COLUMN_RULE, Browser.CHROME, 38.0);
        builder.put(Property.COLUMN_COUNT, Browser.CHROME, 38.0);
        builder.put(Property.COLUMN_RULE_COLOR, Browser.CHROME, 38.0);
        builder.put(Property.COLUMN_RULE_WIDTH, Browser.CHROME, 38.0);
        builder.put(Property.COLUMN_RULE_STYLE, Browser.CHROME, 38.0);
        builder.put(Property.COLUMN_SPAN, Browser.CHROME, 38.0);
        builder.put(Property.COLUMN_FILL, Browser.CHROME, 38.0);
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
        builder.put(Property.COLUMNS, Browser.FIREFOX, 33.0);
        builder.put(Property.COLUMN_WIDTH, Browser.FIREFOX, 33.0);
        builder.put(Property.COLUMN_GAP, Browser.FIREFOX, 33.0);
        builder.put(Property.COLUMN_RULE, Browser.FIREFOX, 33.0);
        builder.put(Property.COLUMN_COUNT, Browser.FIREFOX, 33.0);
        builder.put(Property.COLUMN_RULE_COLOR, Browser.FIREFOX, 33.0);
        builder.put(Property.COLUMN_RULE_WIDTH, Browser.FIREFOX, 33.0);
        builder.put(Property.COLUMN_RULE_STYLE, Browser.FIREFOX, 33.0);
        builder.put(Property.COLUMN_SPAN, Browser.FIREFOX, 33.0);
        builder.put(Property.COLUMN_FILL, Browser.FIREFOX, 33.0);
        builder.put(Property.COLUMNS, Browser.ANDROID, 4.4);
        builder.put(Property.COLUMN_WIDTH, Browser.ANDROID, 4.4);
        builder.put(Property.COLUMN_GAP, Browser.ANDROID, 4.4);
        builder.put(Property.COLUMN_RULE, Browser.ANDROID, 4.4);
        builder.put(Property.COLUMN_COUNT, Browser.ANDROID, 4.4);
        builder.put(Property.COLUMN_RULE_COLOR, Browser.ANDROID, 4.4);
        builder.put(Property.COLUMN_RULE_WIDTH, Browser.ANDROID, 4.4);
        builder.put(Property.COLUMN_RULE_STYLE, Browser.ANDROID, 4.4);
        builder.put(Property.COLUMN_SPAN, Browser.ANDROID, 4.4);
        builder.put(Property.COLUMN_FILL, Browser.ANDROID, 4.4);
        builder.put(Property.COLUMNS, Browser.IOS_SAFARI, 8.1);
        builder.put(Property.COLUMN_WIDTH, Browser.IOS_SAFARI, 8.1);
        builder.put(Property.COLUMN_GAP, Browser.IOS_SAFARI, 8.1);
        builder.put(Property.COLUMN_RULE, Browser.IOS_SAFARI, 8.1);
        builder.put(Property.COLUMN_COUNT, Browser.IOS_SAFARI, 8.1);
        builder.put(Property.COLUMN_RULE_COLOR, Browser.IOS_SAFARI, 8.1);
        builder.put(Property.COLUMN_RULE_WIDTH, Browser.IOS_SAFARI, 8.1);
        builder.put(Property.COLUMN_RULE_STYLE, Browser.IOS_SAFARI, 8.1);
        builder.put(Property.COLUMN_SPAN, Browser.IOS_SAFARI, 8.1);
        builder.put(Property.COLUMN_FILL, Browser.IOS_SAFARI, 8.1);
        builder.put(Property.USER_SELECT, Browser.IE, 11.0);
        builder.put(Property.USER_SELECT, Browser.OPERA, 25.0);
        builder.put(Property.USER_SELECT, Browser.CHROME, 38.0);
        builder.put(Property.USER_SELECT, Browser.SAFARI, 8.0);
        builder.put(Property.USER_SELECT, Browser.FIREFOX, 33.0);
        builder.put(Property.USER_SELECT, Browser.ANDROID, 4.4);
        builder.put(Property.USER_SELECT, Browser.IE_MOBILE, 10.0);
        builder.put(Property.USER_SELECT, Browser.IOS_SAFARI, 8.1);
        builder.put(Property.HYPHENS, Browser.IE, 11.0);
        builder.put(Property.HYPHENS, Browser.SAFARI, 8.0);
        builder.put(Property.HYPHENS, Browser.FIREFOX, 33.0);
        builder.put(Property.HYPHENS, Browser.IOS_SAFARI, 8.1);
        builder.put(Property.TAB_SIZE, Browser.OPERA, 12.1);
        builder.put(Property.TAB_SIZE, Browser.FIREFOX, 33.0);
        builder.put(Property.TAB_SIZE, Browser.OPERA_MINI, 8.0);
        builder.put(Property.APPEARANCE, Browser.FIREFOX, 33.0);
        builder.put(Property.APPEARANCE, Browser.CHROME, 38.0);
        builder.put(Property.APPEARANCE, Browser.SAFARI, 8.0);
        builder.put(Property.APPEARANCE, Browser.ANDROID, 4.4);
        builder.put(Property.APPEARANCE, Browser.IOS_SAFARI, 8.1);

        PROPERTIES = builder.build();
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

    static {
        ImmutableTable.Builder<String, Browser, Double> builder = ImmutableTable.builder();

        builder.put("keyframes", Browser.OPERA, 25.0);
        builder.put("keyframes", Browser.CHROME, 38.0);
        builder.put("keyframes", Browser.SAFARI, 8.0);
        builder.put("keyframes", Browser.FIREFOX, 15.0);
        builder.put("keyframes", Browser.ANDROID, 4.4);
        builder.put("keyframes", Browser.IOS_SAFARI, 8.1);

        AT_RULES = builder.build();
    }

    static {
        ImmutableTable.Builder<String, Browser, Double> builder = ImmutableTable.builder();

        builder.put("selection", Browser.FIREFOX, 33.0);
        builder.put("placeholder", Browser.IE, 11.0);
        builder.put("placeholder", Browser.OPERA, 25.0);
        builder.put("placeholder", Browser.CHROME, 38.0);
        builder.put("placeholder", Browser.SAFARI, 8.0);
        builder.put("placeholder", Browser.FIREFOX, 33.0);
        builder.put("placeholder", Browser.ANDROID, 4.4);
        builder.put("placeholder", Browser.IE_MOBILE, 10.0);
        builder.put("placeholder", Browser.IOS_SAFARI, 8.1);

        SELECTORS = builder.build();
    }

    private PrefixTables() {}
}
