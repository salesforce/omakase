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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

import java.util.Map;

/**
 * Enum of all recognized CSS properties. Use {@link #toString()} to get the CSS-output representation.
 * <p/>
 * THIS FILE IS GENERATED. DO NOT EDIT DIRECTLY.
 * <p/>
 * See class com.salesforce.omakase.util.tool.PropertyToEnum for instructions on updating.
 */
public enum Property {
    /** CSS property named 'align-content' */
    ALIGN_CONTENT("align-content"),

    /** CSS property named 'align-items' */
    ALIGN_ITEMS("align-items"),

    /** CSS property named 'align-self' */
    ALIGN_SELF("align-self"),

    /** CSS property named 'alignment-adjust' */
    ALIGNMENT_ADJUST("alignment-adjust"),

    /** CSS property named 'alignment-baseline' */
    ALIGNMENT_BASELINE("alignment-baseline"),

    /** CSS property named 'animation' */
    ANIMATION("animation"),

    /** CSS property named 'animation-delay' */
    ANIMATION_DELAY("animation-delay"),

    /** CSS property named 'animation-direction' */
    ANIMATION_DIRECTION("animation-direction"),

    /** CSS property named 'animation-duration' */
    ANIMATION_DURATION("animation-duration"),

    /** CSS property named 'animation-fill-mode' */
    ANIMATION_FILL_MODE("animation-fill-mode"),

    /** CSS property named 'animation-iteration-count' */
    ANIMATION_ITERATION_COUNT("animation-iteration-count"),

    /** CSS property named 'animation-name' */
    ANIMATION_NAME("animation-name"),

    /** CSS property named 'animation-play-state' */
    ANIMATION_PLAY_STATE("animation-play-state"),

    /** CSS property named 'animation-timing-function' */
    ANIMATION_TIMING_FUNCTION("animation-timing-function"),

    /** CSS property named 'appearance' */
    APPEARANCE("appearance"),

    /** CSS property named 'azimuth' */
    AZIMUTH("azimuth"),

    /** CSS property named 'backface-visibility' */
    BACKFACE_VISIBILITY("backface-visibility"),

    /** CSS property named 'background' */
    BACKGROUND("background"),

    /** CSS property named 'background-attachment' */
    BACKGROUND_ATTACHMENT("background-attachment"),

    /** CSS property named 'background-clip' */
    BACKGROUND_CLIP("background-clip"),

    /** CSS property named 'background-color' */
    BACKGROUND_COLOR("background-color"),

    /** CSS property named 'background-image' */
    BACKGROUND_IMAGE("background-image"),

    /** CSS property named 'background-origin' */
    BACKGROUND_ORIGIN("background-origin"),

    /** CSS property named 'background-position' */
    BACKGROUND_POSITION("background-position"),

    /** CSS property named 'background-repeat' */
    BACKGROUND_REPEAT("background-repeat"),

    /** CSS property named 'background-size' */
    BACKGROUND_SIZE("background-size"),

    /** CSS property named 'baseline-shift' */
    BASELINE_SHIFT("baseline-shift"),

    /** CSS property named 'behavior' */
    BEHAVIOR("behavior"),

    /** CSS property named 'binding' */
    BINDING("binding"),

    /** CSS property named 'bleed' */
    BLEED("bleed"),

    /** CSS property named 'bookmark-label' */
    BOOKMARK_LABEL("bookmark-label"),

    /** CSS property named 'bookmark-level' */
    BOOKMARK_LEVEL("bookmark-level"),

    /** CSS property named 'bookmark-state' */
    BOOKMARK_STATE("bookmark-state"),

    /** CSS property named 'bookmark-target' */
    BOOKMARK_TARGET("bookmark-target"),

    /** CSS property named 'border' */
    BORDER("border"),

    /** CSS property named 'border-bottom' */
    BORDER_BOTTOM("border-bottom"),

    /** CSS property named 'border-bottom-color' */
    BORDER_BOTTOM_COLOR("border-bottom-color"),

    /** CSS property named 'border-bottom-left-radius' */
    BORDER_BOTTOM_LEFT_RADIUS("border-bottom-left-radius"),

    /** CSS property named 'border-bottom-right-radius' */
    BORDER_BOTTOM_RIGHT_RADIUS("border-bottom-right-radius"),

    /** CSS property named 'border-bottom-style' */
    BORDER_BOTTOM_STYLE("border-bottom-style"),

    /** CSS property named 'border-bottom-width' */
    BORDER_BOTTOM_WIDTH("border-bottom-width"),

    /** CSS property named 'border-collapse' */
    BORDER_COLLAPSE("border-collapse"),

    /** CSS property named 'border-color' */
    BORDER_COLOR("border-color"),

    /** CSS property named 'border-image' */
    BORDER_IMAGE("border-image"),

    /** CSS property named 'border-image-outset' */
    BORDER_IMAGE_OUTSET("border-image-outset"),

    /** CSS property named 'border-image-repeat' */
    BORDER_IMAGE_REPEAT("border-image-repeat"),

    /** CSS property named 'border-image-slice' */
    BORDER_IMAGE_SLICE("border-image-slice"),

    /** CSS property named 'border-image-source' */
    BORDER_IMAGE_SOURCE("border-image-source"),

    /** CSS property named 'border-image-width' */
    BORDER_IMAGE_WIDTH("border-image-width"),

    /** CSS property named 'border-left' */
    BORDER_LEFT("border-left"),

    /** CSS property named 'border-left-color' */
    BORDER_LEFT_COLOR("border-left-color"),

    /** CSS property named 'border-left-style' */
    BORDER_LEFT_STYLE("border-left-style"),

    /** CSS property named 'border-left-width' */
    BORDER_LEFT_WIDTH("border-left-width"),

    /** CSS property named 'border-radius' */
    BORDER_RADIUS("border-radius"),

    /** CSS property named 'border-right' */
    BORDER_RIGHT("border-right"),

    /** CSS property named 'border-right-color' */
    BORDER_RIGHT_COLOR("border-right-color"),

    /** CSS property named 'border-right-style' */
    BORDER_RIGHT_STYLE("border-right-style"),

    /** CSS property named 'border-right-width' */
    BORDER_RIGHT_WIDTH("border-right-width"),

    /** CSS property named 'border-spacing' */
    BORDER_SPACING("border-spacing"),

    /** CSS property named 'border-style' */
    BORDER_STYLE("border-style"),

    /** CSS property named 'border-top' */
    BORDER_TOP("border-top"),

    /** CSS property named 'border-top-color' */
    BORDER_TOP_COLOR("border-top-color"),

    /** CSS property named 'border-top-left-radius' */
    BORDER_TOP_LEFT_RADIUS("border-top-left-radius"),

    /** CSS property named 'border-top-right-radius' */
    BORDER_TOP_RIGHT_RADIUS("border-top-right-radius"),

    /** CSS property named 'border-top-style' */
    BORDER_TOP_STYLE("border-top-style"),

    /** CSS property named 'border-top-width' */
    BORDER_TOP_WIDTH("border-top-width"),

    /** CSS property named 'border-width' */
    BORDER_WIDTH("border-width"),

    /** CSS property named 'bottom' */
    BOTTOM("bottom"),

    /** CSS property named 'box-align' */
    BOX_ALIGN("box-align"),

    /** CSS property named 'box-decoration-break' */
    BOX_DECORATION_BREAK("box-decoration-break"),

    /** CSS property named 'box-direction' */
    BOX_DIRECTION("box-direction"),

    /** CSS property named 'box-flex' */
    BOX_FLEX("box-flex"),

    /** CSS property named 'box-flex-group' */
    BOX_FLEX_GROUP("box-flex-group"),

    /** CSS property named 'box-lines' */
    BOX_LINES("box-lines"),

    /** CSS property named 'box-ordinal-group' */
    BOX_ORDINAL_GROUP("box-ordinal-group"),

    /** CSS property named 'box-orient' */
    BOX_ORIENT("box-orient"),

    /** CSS property named 'box-pack' */
    BOX_PACK("box-pack"),

    /** CSS property named 'box-shadow' */
    BOX_SHADOW("box-shadow"),

    /** CSS property named 'box-sizing' */
    BOX_SIZING("box-sizing"),

    /** CSS property named 'break-after' */
    BREAK_AFTER("break-after"),

    /** CSS property named 'break-before' */
    BREAK_BEFORE("break-before"),

    /** CSS property named 'break-inside' */
    BREAK_INSIDE("break-inside"),

    /** CSS property named 'calc' */
    CALC("calc"),

    /** CSS property named 'caption-side' */
    CAPTION_SIDE("caption-side"),

    /** CSS property named 'clear' */
    CLEAR("clear"),

    /** CSS property named 'clip' */
    CLIP("clip"),

    /** CSS property named 'color' */
    COLOR("color"),

    /** CSS property named 'color-profile' */
    COLOR_PROFILE("color-profile"),

    /** CSS property named 'column-count' */
    COLUMN_COUNT("column-count"),

    /** CSS property named 'column-fill' */
    COLUMN_FILL("column-fill"),

    /** CSS property named 'column-gap' */
    COLUMN_GAP("column-gap"),

    /** CSS property named 'column-rule' */
    COLUMN_RULE("column-rule"),

    /** CSS property named 'column-rule-color' */
    COLUMN_RULE_COLOR("column-rule-color"),

    /** CSS property named 'column-rule-style' */
    COLUMN_RULE_STYLE("column-rule-style"),

    /** CSS property named 'column-rule-width' */
    COLUMN_RULE_WIDTH("column-rule-width"),

    /** CSS property named 'column-span' */
    COLUMN_SPAN("column-span"),

    /** CSS property named 'column-width' */
    COLUMN_WIDTH("column-width"),

    /** CSS property named 'columns' */
    COLUMNS("columns"),

    /** CSS property named 'content' */
    CONTENT("content"),

    /** CSS property named 'counter-increment' */
    COUNTER_INCREMENT("counter-increment"),

    /** CSS property named 'counter-reset' */
    COUNTER_RESET("counter-reset"),

    /** CSS property named 'crop' */
    CROP("crop"),

    /** CSS property named 'cue' */
    CUE("cue"),

    /** CSS property named 'cue-after' */
    CUE_AFTER("cue-after"),

    /** CSS property named 'cue-before' */
    CUE_BEFORE("cue-before"),

    /** CSS property named 'cursor' */
    CURSOR("cursor"),

    /** CSS property named 'direction' */
    DIRECTION("direction"),

    /** CSS property named 'display' */
    DISPLAY("display"),

    /** CSS property named 'dominant-baseline' */
    DOMINANT_BASELINE("dominant-baseline"),

    /** CSS property named 'drop-initial-after-adjust' */
    DROP_INITIAL_AFTER_ADJUST("drop-initial-after-adjust"),

    /** CSS property named 'drop-initial-after-align' */
    DROP_INITIAL_AFTER_ALIGN("drop-initial-after-align"),

    /** CSS property named 'drop-initial-before-adjust' */
    DROP_INITIAL_BEFORE_ADJUST("drop-initial-before-adjust"),

    /** CSS property named 'drop-initial-before-align' */
    DROP_INITIAL_BEFORE_ALIGN("drop-initial-before-align"),

    /** CSS property named 'drop-initial-size' */
    DROP_INITIAL_SIZE("drop-initial-size"),

    /** CSS property named 'drop-initial-value' */
    DROP_INITIAL_VALUE("drop-initial-value"),

    /** CSS property named 'elevation' */
    ELEVATION("elevation"),

    /** CSS property named 'empty-cells' */
    EMPTY_CELLS("empty-cells"),

    /** CSS property named 'filter' */
    FILTER("filter"),

    /** CSS property named 'fit' */
    FIT("fit"),

    /** CSS property named 'fit-position' */
    FIT_POSITION("fit-position"),

    /** CSS property named 'flex' */
    FLEX("flex"),

    /** CSS property named 'flex-basis' */
    FLEX_BASIS("flex-basis"),

    /** CSS property named 'flex-direction' */
    FLEX_DIRECTION("flex-direction"),

    /** CSS property named 'flex-flow' */
    FLEX_FLOW("flex-flow"),

    /** CSS property named 'flex-grow' */
    FLEX_GROW("flex-grow"),

    /** CSS property named 'flex-shrink' */
    FLEX_SHRINK("flex-shrink"),

    /** CSS property named 'flex-wrap' */
    FLEX_WRAP("flex-wrap"),

    /** CSS property named 'float' */
    FLOAT("float"),

    /** CSS property named 'float-offset' */
    FLOAT_OFFSET("float-offset"),

    /** CSS property named 'font' */
    FONT("font"),

    /** CSS property named 'font-family' */
    FONT_FAMILY("font-family"),

    /** CSS property named 'font-size' */
    FONT_SIZE("font-size"),

    /** CSS property named 'font-size-adjust' */
    FONT_SIZE_ADJUST("font-size-adjust"),

    /** CSS property named 'font-stretch' */
    FONT_STRETCH("font-stretch"),

    /** CSS property named 'font-style' */
    FONT_STYLE("font-style"),

    /** CSS property named 'font-variant' */
    FONT_VARIANT("font-variant"),

    /** CSS property named 'font-weight' */
    FONT_WEIGHT("font-weight"),

    /** CSS property named 'grid-cell-stacking' */
    GRID_CELL_STACKING("grid-cell-stacking"),

    /** CSS property named 'grid-column' */
    GRID_COLUMN("grid-column"),

    /** CSS property named 'grid-column-align' */
    GRID_COLUMN_ALIGN("grid-column-align"),

    /** CSS property named 'grid-column-sizing' */
    GRID_COLUMN_SIZING("grid-column-sizing"),

    /** CSS property named 'grid-column-span' */
    GRID_COLUMN_SPAN("grid-column-span"),

    /** CSS property named 'grid-columns' */
    GRID_COLUMNS("grid-columns"),

    /** CSS property named 'grid-flow' */
    GRID_FLOW("grid-flow"),

    /** CSS property named 'grid-layer' */
    GRID_LAYER("grid-layer"),

    /** CSS property named 'grid-row' */
    GRID_ROW("grid-row"),

    /** CSS property named 'grid-row-align' */
    GRID_ROW_ALIGN("grid-row-align"),

    /** CSS property named 'grid-row-sizing' */
    GRID_ROW_SIZING("grid-row-sizing"),

    /** CSS property named 'grid-row-span' */
    GRID_ROW_SPAN("grid-row-span"),

    /** CSS property named 'grid-rows' */
    GRID_ROWS("grid-rows"),

    /** CSS property named 'hanging-punctuation' */
    HANGING_PUNCTUATION("hanging-punctuation"),

    /** CSS property named 'height' */
    HEIGHT("height"),

    /** CSS property named 'hyphenate-after' */
    HYPHENATE_AFTER("hyphenate-after"),

    /** CSS property named 'hyphenate-before' */
    HYPHENATE_BEFORE("hyphenate-before"),

    /** CSS property named 'hyphenate-character' */
    HYPHENATE_CHARACTER("hyphenate-character"),

    /** CSS property named 'hyphenate-lines' */
    HYPHENATE_LINES("hyphenate-lines"),

    /** CSS property named 'hyphenate-resource' */
    HYPHENATE_RESOURCE("hyphenate-resource"),

    /** CSS property named 'hyphens' */
    HYPHENS("hyphens"),

    /** CSS property named 'icon' */
    ICON("icon"),

    /** CSS property named 'image-orientation' */
    IMAGE_ORIENTATION("image-orientation"),

    /** CSS property named 'image-rendering' */
    IMAGE_RENDERING("image-rendering"),

    /** CSS property named 'image-resolution' */
    IMAGE_RESOLUTION("image-resolution"),

    /** CSS property named 'inline-box-align' */
    INLINE_BOX_ALIGN("inline-box-align"),

    /** CSS property named 'justify-content' */
    JUSTIFY_CONTENT("justify-content"),

    /** CSS property named 'left' */
    LEFT("left"),

    /** CSS property named 'letter-spacing' */
    LETTER_SPACING("letter-spacing"),

    /** CSS property named 'line-break' */
    LINE_BREAK("line-break"),

    /** CSS property named 'line-height' */
    LINE_HEIGHT("line-height"),

    /** CSS property named 'line-stacking' */
    LINE_STACKING("line-stacking"),

    /** CSS property named 'line-stacking-ruby' */
    LINE_STACKING_RUBY("line-stacking-ruby"),

    /** CSS property named 'line-stacking-shift' */
    LINE_STACKING_SHIFT("line-stacking-shift"),

    /** CSS property named 'line-stacking-strategy' */
    LINE_STACKING_STRATEGY("line-stacking-strategy"),

    /** CSS property named 'linear-gradient' */
    LINEAR_GRADIENT("linear-gradient"),

    /** CSS property named 'list-style' */
    LIST_STYLE("list-style"),

    /** CSS property named 'list-style-image' */
    LIST_STYLE_IMAGE("list-style-image"),

    /** CSS property named 'list-style-position' */
    LIST_STYLE_POSITION("list-style-position"),

    /** CSS property named 'list-style-type' */
    LIST_STYLE_TYPE("list-style-type"),

    /** CSS property named 'margin' */
    MARGIN("margin"),

    /** CSS property named 'margin-bottom' */
    MARGIN_BOTTOM("margin-bottom"),

    /** CSS property named 'margin-left' */
    MARGIN_LEFT("margin-left"),

    /** CSS property named 'margin-right' */
    MARGIN_RIGHT("margin-right"),

    /** CSS property named 'margin-top' */
    MARGIN_TOP("margin-top"),

    /** CSS property named 'mark' */
    MARK("mark"),

    /** CSS property named 'mark-after' */
    MARK_AFTER("mark-after"),

    /** CSS property named 'mark-before' */
    MARK_BEFORE("mark-before"),

    /** CSS property named 'marks' */
    MARKS("marks"),

    /** CSS property named 'marquee-direction' */
    MARQUEE_DIRECTION("marquee-direction"),

    /** CSS property named 'marquee-play-count' */
    MARQUEE_PLAY_COUNT("marquee-play-count"),

    /** CSS property named 'marquee-speed' */
    MARQUEE_SPEED("marquee-speed"),

    /** CSS property named 'marquee-style' */
    MARQUEE_STYLE("marquee-style"),

    /** CSS property named 'max-height' */
    MAX_HEIGHT("max-height"),

    /** CSS property named 'max-width' */
    MAX_WIDTH("max-width"),

    /** CSS property named 'min-height' */
    MIN_HEIGHT("min-height"),

    /** CSS property named 'min-width' */
    MIN_WIDTH("min-width"),

    /** CSS property named 'move-to' */
    MOVE_TO("move-to"),

    /** CSS property named 'nav-down' */
    NAV_DOWN("nav-down"),

    /** CSS property named 'nav-index' */
    NAV_INDEX("nav-index"),

    /** CSS property named 'nav-left' */
    NAV_LEFT("nav-left"),

    /** CSS property named 'nav-right' */
    NAV_RIGHT("nav-right"),

    /** CSS property named 'nav-up' */
    NAV_UP("nav-up"),

    /** CSS property named 'opacity' */
    OPACITY("opacity"),

    /** CSS property named 'order' */
    ORDER("order"),

    /** CSS property named 'orphans' */
    ORPHANS("orphans"),

    /** CSS property named 'outline' */
    OUTLINE("outline"),

    /** CSS property named 'outline-color' */
    OUTLINE_COLOR("outline-color"),

    /** CSS property named 'outline-offset' */
    OUTLINE_OFFSET("outline-offset"),

    /** CSS property named 'outline-style' */
    OUTLINE_STYLE("outline-style"),

    /** CSS property named 'outline-width' */
    OUTLINE_WIDTH("outline-width"),

    /** CSS property named 'overflow' */
    OVERFLOW("overflow"),

    /** CSS property named 'overflow-style' */
    OVERFLOW_STYLE("overflow-style"),

    /** CSS property named 'overflow-x' */
    OVERFLOW_X("overflow-x"),

    /** CSS property named 'overflow-y' */
    OVERFLOW_Y("overflow-y"),

    /** CSS property named 'padding' */
    PADDING("padding"),

    /** CSS property named 'padding-bottom' */
    PADDING_BOTTOM("padding-bottom"),

    /** CSS property named 'padding-left' */
    PADDING_LEFT("padding-left"),

    /** CSS property named 'padding-right' */
    PADDING_RIGHT("padding-right"),

    /** CSS property named 'padding-top' */
    PADDING_TOP("padding-top"),

    /** CSS property named 'page' */
    PAGE("page"),

    /** CSS property named 'page-break-after' */
    PAGE_BREAK_AFTER("page-break-after"),

    /** CSS property named 'page-break-before' */
    PAGE_BREAK_BEFORE("page-break-before"),

    /** CSS property named 'page-break-inside' */
    PAGE_BREAK_INSIDE("page-break-inside"),

    /** CSS property named 'page-policy' */
    PAGE_POLICY("page-policy"),

    /** CSS property named 'pause' */
    PAUSE("pause"),

    /** CSS property named 'pause-after' */
    PAUSE_AFTER("pause-after"),

    /** CSS property named 'pause-before' */
    PAUSE_BEFORE("pause-before"),

    /** CSS property named 'perspective' */
    PERSPECTIVE("perspective"),

    /** CSS property named 'perspective-origin' */
    PERSPECTIVE_ORIGIN("perspective-origin"),

    /** CSS property named 'phonemes' */
    PHONEMES("phonemes"),

    /** CSS property named 'pitch' */
    PITCH("pitch"),

    /** CSS property named 'pitch-range' */
    PITCH_RANGE("pitch-range"),

    /** CSS property named 'play-during' */
    PLAY_DURING("play-during"),

    /** CSS property named 'pointer-events' */
    POINTER_EVENTS("pointer-events"),

    /** CSS property named 'position' */
    POSITION("position"),

    /** CSS property named 'presentation-level' */
    PRESENTATION_LEVEL("presentation-level"),

    /** CSS property named 'punctuation-trim' */
    PUNCTUATION_TRIM("punctuation-trim"),

    /** CSS property named 'quotes' */
    QUOTES("quotes"),

    /** CSS property named 'radial-gradient' */
    RADIAL_GRADIENT("radial-gradient"),

    /** CSS property named 'rendering-intent' */
    RENDERING_INTENT("rendering-intent"),

    /** CSS property named 'repeating-linear-gradient' */
    REPEATING_LINEAR_GRADIENT("repeating-linear-gradient"),

    /** CSS property named 'repeating-radial-gradient' */
    REPEATING_RADIAL_GRADIENT("repeating-radial-gradient"),

    /** CSS property named 'resize' */
    RESIZE("resize"),

    /** CSS property named 'rest' */
    REST("rest"),

    /** CSS property named 'rest-after' */
    REST_AFTER("rest-after"),

    /** CSS property named 'rest-before' */
    REST_BEFORE("rest-before"),

    /** CSS property named 'richness' */
    RICHNESS("richness"),

    /** CSS property named 'right' */
    RIGHT("right"),

    /** CSS property named 'rotation' */
    ROTATION("rotation"),

    /** CSS property named 'rotation-point' */
    ROTATION_POINT("rotation-point"),

    /** CSS property named 'ruby-align' */
    RUBY_ALIGN("ruby-align"),

    /** CSS property named 'ruby-overhang' */
    RUBY_OVERHANG("ruby-overhang"),

    /** CSS property named 'ruby-position' */
    RUBY_POSITION("ruby-position"),

    /** CSS property named 'ruby-span' */
    RUBY_SPAN("ruby-span"),

    /** CSS property named 'size' */
    SIZE("size"),

    /** CSS property named 'speak' */
    SPEAK("speak"),

    /** CSS property named 'speak-header' */
    SPEAK_HEADER("speak-header"),

    /** CSS property named 'speak-numeral' */
    SPEAK_NUMERAL("speak-numeral"),

    /** CSS property named 'speak-punctuation' */
    SPEAK_PUNCTUATION("speak-punctuation"),

    /** CSS property named 'speech-rate' */
    SPEECH_RATE("speech-rate"),

    /** CSS property named 'src' */
    SRC("src"),

    /** CSS property named 'stress' */
    STRESS("stress"),

    /** CSS property named 'string-set' */
    STRING_SET("string-set"),

    /** CSS property named 'tab-size' */
    TAB_SIZE("tab-size"),

    /** CSS property named 'table-layout' */
    TABLE_LAYOUT("table-layout"),

    /** CSS property named 'target' */
    TARGET("target"),

    /** CSS property named 'target-name' */
    TARGET_NAME("target-name"),

    /** CSS property named 'target-new' */
    TARGET_NEW("target-new"),

    /** CSS property named 'target-position' */
    TARGET_POSITION("target-position"),

    /** CSS property named 'text-align' */
    TEXT_ALIGN("text-align"),

    /** CSS property named 'text-align-last' */
    TEXT_ALIGN_LAST("text-align-last"),

    /** CSS property named 'text-decoration' */
    TEXT_DECORATION("text-decoration"),

    /** CSS property named 'text-emphasis' */
    TEXT_EMPHASIS("text-emphasis"),

    /** CSS property named 'text-height' */
    TEXT_HEIGHT("text-height"),

    /** CSS property named 'text-indent' */
    TEXT_INDENT("text-indent"),

    /** CSS property named 'text-justify' */
    TEXT_JUSTIFY("text-justify"),

    /** CSS property named 'text-outline' */
    TEXT_OUTLINE("text-outline"),

    /** CSS property named 'text-overflow' */
    TEXT_OVERFLOW("text-overflow"),

    /** CSS property named 'text-rendering' */
    TEXT_RENDERING("text-rendering"),

    /** CSS property named 'text-shadow' */
    TEXT_SHADOW("text-shadow"),

    /** CSS property named 'text-transform' */
    TEXT_TRANSFORM("text-transform"),

    /** CSS property named 'text-wrap' */
    TEXT_WRAP("text-wrap"),

    /** CSS property named 'top' */
    TOP("top"),

    /** CSS property named 'transform' */
    TRANSFORM("transform"),

    /** CSS property named 'transform-origin' */
    TRANSFORM_ORIGIN("transform-origin"),

    /** CSS property named 'transform-style' */
    TRANSFORM_STYLE("transform-style"),

    /** CSS property named 'transition' */
    TRANSITION("transition"),

    /** CSS property named 'transition-delay' */
    TRANSITION_DELAY("transition-delay"),

    /** CSS property named 'transition-duration' */
    TRANSITION_DURATION("transition-duration"),

    /** CSS property named 'transition-property' */
    TRANSITION_PROPERTY("transition-property"),

    /** CSS property named 'transition-timing-function' */
    TRANSITION_TIMING_FUNCTION("transition-timing-function"),

    /** CSS property named 'unicode-bidi' */
    UNICODE_BIDI("unicode-bidi"),

    /** CSS property named 'user-modify' */
    USER_MODIFY("user-modify"),

    /** CSS property named 'user-select' */
    USER_SELECT("user-select"),

    /** CSS property named 'vertical-align' */
    VERTICAL_ALIGN("vertical-align"),

    /** CSS property named 'visibility' */
    VISIBILITY("visibility"),

    /** CSS property named 'voice-balance' */
    VOICE_BALANCE("voice-balance"),

    /** CSS property named 'voice-duration' */
    VOICE_DURATION("voice-duration"),

    /** CSS property named 'voice-family' */
    VOICE_FAMILY("voice-family"),

    /** CSS property named 'voice-pitch' */
    VOICE_PITCH("voice-pitch"),

    /** CSS property named 'voice-pitch-range' */
    VOICE_PITCH_RANGE("voice-pitch-range"),

    /** CSS property named 'voice-rate' */
    VOICE_RATE("voice-rate"),

    /** CSS property named 'voice-stress' */
    VOICE_STRESS("voice-stress"),

    /** CSS property named 'voice-volume' */
    VOICE_VOLUME("voice-volume"),

    /** CSS property named 'volume' */
    VOLUME("volume"),

    /** CSS property named 'white-space' */
    WHITE_SPACE("white-space"),

    /** CSS property named 'white-space-collapse' */
    WHITE_SPACE_COLLAPSE("white-space-collapse"),

    /** CSS property named 'widows' */
    WIDOWS("widows"),

    /** CSS property named 'width' */
    WIDTH("width"),

    /** CSS property named 'word-break' */
    WORD_BREAK("word-break"),

    /** CSS property named 'word-spacing' */
    WORD_SPACING("word-spacing"),

    /** CSS property named 'word-wrap' */
    WORD_WRAP("word-wrap"),

    /** CSS property named 'z-index' */
    Z_INDEX("z-index"),

    /** CSS property named 'zoom' */
    ZOOM("zoom"),

    ;

    /** reverse lookup map */
    private static final Map<String, Property> map;
    static {
        Builder<String, Property> builder = ImmutableMap.builder();
        for (Property pn : Property.values()) {
            builder.put(pn.toString(), pn);
        }
        map = builder.build();
    }

    private final String name;

    Property(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Gets the  property associated with the given name
     *
     * @param name
     *     Name of the property.
     *
     * @return The matching {@link Property}.
     */
    public static Property lookup(String name) {
        return map.get(name);
    }
}
