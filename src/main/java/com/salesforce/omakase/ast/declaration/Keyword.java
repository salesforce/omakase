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

import com.google.common.base.Optional;

/**
 * Enum of all recognized CSS keywords. Generated using KeywordToEnum.java.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("UnusedDeclaration")
public enum Keyword {
    /** CSS keyword named 'above' */
    ABOVE("above"),

    /** CSS keyword named 'absolute' */
    ABSOLUTE("absolute"),

    /** CSS keyword named 'all' */
    ALL("all"),

    /** CSS keyword named 'always' */
    ALWAYS("always"),

    /** CSS keyword named 'aqua' */
    AQUA("aqua"),

    /** CSS keyword named 'auto' */
    AUTO("auto"),

    /** CSS keyword named 'avoid' */
    AVOID("avoid"),

    /** CSS keyword named 'avoid-column' */
    AVOID_COLUMN("avoid-column"),

    /** CSS keyword named 'avoid-page' */
    AVOID_PAGE("avoid-page"),

    /** CSS keyword named 'balance' */
    BALANCE("balance"),

    /** CSS keyword named 'baseline' */
    BASELINE("baseline"),

    /** CSS keyword named 'below' */
    BELOW("below"),

    /** CSS keyword named 'black' */
    BLACK("black"),

    /** CSS keyword named 'block' */
    BLOCK("block"),

    /** CSS keyword named 'blue' */
    BLUE("blue"),

    /** CSS keyword named 'bold' */
    BOLD("bold"),

    /** CSS keyword named 'bolder' */
    BOLDER("bolder"),

    /** CSS keyword named 'border-box' */
    BORDER_BOX("border-box"),

    /** CSS keyword named 'both' */
    BOTH("both"),

    /** CSS keyword named 'bottom' */
    BOTTOM("bottom"),

    /** CSS keyword named 'box' */
    BOX("box"),

    /** CSS keyword named 'break-all' */
    BREAK_ALL("break-all"),

    /** CSS keyword named 'button' */
    BUTTON("button"),

    /** CSS keyword named 'caps-height' */
    CAPS_HEIGHT("caps-height"),

    /** CSS keyword named 'center' */
    CENTER("center"),

    /** CSS keyword named 'central' */
    CENTRAL("central"),

    /** CSS keyword named 'circle' */
    CIRCLE("circle"),

    /** CSS keyword named 'collapse' */
    COLLAPSE("collapse"),

    /** CSS keyword named 'column' */
    COLUMN("column"),

    /** CSS keyword named 'combo-box' */
    COMBO_BOX("combo-box"),

    /** CSS keyword named 'condensed' */
    CONDENSED("condensed"),

    /** CSS keyword named 'continuous' */
    CONTINUOUS("continuous"),

    /** CSS keyword named 'dashed' */
    DASHED("dashed"),

    /** CSS keyword named 'decimal' */
    DECIMAL("decimal"),

    /** CSS keyword named 'decimal-leading-zero' */
    DECIMAL_LEADING_ZERO("decimal-leading-zero"),

    /** CSS keyword named 'desktop' */
    DESKTOP("desktop"),

    /** CSS keyword named 'dotted' */
    DOTTED("dotted"),

    /** CSS keyword named 'double' */
    DOUBLE("double"),

    /** CSS keyword named 'end' */
    END("end"),

    /** CSS keyword named 'expanded' */
    EXPANDED("expanded"),

    /** CSS keyword named 'field' */
    FIELD("field"),

    /** CSS keyword named 'fill' */
    FILL("fill"),

    /** CSS keyword named 'fixed' */
    FIXED("fixed"),

    /** CSS keyword named 'fuchsia' */
    FUCHSIA("fuchsia"),

    /** CSS keyword named 'gray' */
    GRAY("gray"),

    /** CSS keyword named 'green' */
    GREEN("green"),

    /** CSS keyword named 'grid' */
    GRID("grid"),

    /** CSS keyword named 'groove' */
    GROOVE("groove"),

    /** CSS keyword named 'hidden' */
    HIDDEN("hidden"),

    /** CSS keyword named 'hide' */
    HIDE("hide"),

    /** CSS keyword named 'higher' */
    HIGHER("higher"),

    /** CSS keyword named 'horizontal' */
    HORIZONTAL("horizontal"),

    /** CSS keyword named 'hyperlink' */
    HYPERLINK("hyperlink"),

    /** CSS keyword named 'inherit' */
    INHERIT("inherit"),

    /** CSS keyword named 'initial' */
    INITIAL("initial"),

    /** CSS keyword named 'inline-axis' */
    INLINE_AXIS("inline-axis"),

    /** CSS keyword named 'inline-block' */
    INLINE_BLOCK("inline-block"),

    /** CSS keyword named 'inline-box' */
    INLINE_BOX("inline-box"),

    /** CSS keyword named 'inline-grid' */
    INLINE_GRID("inline-grid"),

    /** CSS keyword named 'inline-table' */
    INLINE_TABLE("inline-table"),

    /** CSS keyword named 'inset' */
    INSET("inset"),

    /** CSS keyword named 'italic' */
    ITALIC("italic"),

    /** CSS keyword named 'justify' */
    JUSTIFY("justify"),

    /** CSS keyword named 'left' */
    LEFT("left"),

    /** CSS keyword named 'lime' */
    LIME("lime"),

    /** CSS keyword named 'line' */
    LINE("line"),

    /** CSS keyword named 'list-item' */
    LIST_ITEM("list-item"),

    /** CSS keyword named 'list-menu' */
    LIST_MENU("list-menu"),

    /** CSS keyword named 'lower' */
    LOWER("lower"),

    /** CSS keyword named 'lower-alpha' */
    LOWER_ALPHA("lower-alpha"),

    /** CSS keyword named 'lower-greek' */
    LOWER_GREEK("lower-greek"),

    /** CSS keyword named 'lower-latin' */
    LOWER_LATIN("lower-latin"),

    /** CSS keyword named 'lower-roman' */
    LOWER_ROMAN("lower-roman"),

    /** CSS keyword named 'lowercase' */
    LOWERCASE("lowercase"),

    /** CSS keyword named 'ltr' */
    LTR("ltr"),

    /** CSS keyword named 'manual' */
    MANUAL("manual"),

    /** CSS keyword named 'maroon' */
    MAROON("maroon"),

    /** CSS keyword named 'max-height' */
    MAX_HEIGHT("max-height"),

    /** CSS keyword named 'menu' */
    MENU("menu"),

    /** CSS keyword named 'menu-item' */
    MENU_ITEM("menu-item"),

    /** CSS keyword named 'middle' */
    MIDDLE("middle"),

    /** CSS keyword named 'multiple' */
    MULTIPLE("multiple"),

    /** CSS keyword named 'navy' */
    NAVY("navy"),

    /** CSS keyword named 'none' */
    NONE("none"),

    /** CSS keyword named 'normal' */
    NORMAL("normal"),

    /** CSS keyword named 'nowrap' */
    NOWRAP("nowrap"),

    /** CSS keyword named 'olive' */
    OLIVE("olive"),

    /** CSS keyword named 'open' */
    OPEN("open"),

    /** CSS keyword named 'optimizeLegibility' */
    OPTIMIZELEGIBILITY("optimizeLegibility"),

    /** CSS keyword named 'optimizeSpeed' */
    OPTIMIZESPEED("optimizeSpeed"),

    /** CSS keyword named 'outline-tree' */
    OUTLINE_TREE("outline-tree"),

    /** CSS keyword named 'outset' */
    OUTSET("outset"),

    /** CSS keyword named 'outside' */
    OUTSIDE("outside"),

    /** CSS keyword named 'page' */
    PAGE("page"),

    /** CSS keyword named 'pre' */
    PRE("pre"),

    /** CSS keyword named 'pre-line' */
    PRE_LINE("pre-line"),

    /** CSS keyword named 'pre-wrap' */
    PRE_WRAP("pre-wrap"),

    /** CSS keyword named 'purple' */
    PURPLE("purple"),

    /** CSS keyword named 'read-only' */
    READ_ONLY("read-only"),

    /** CSS keyword named 'read-write' */
    READ_WRITE("read-write"),

    /** CSS keyword named 'red' */
    RED("red"),

    /** CSS keyword named 'relative' */
    RELATIVE("relative"),

    /** CSS keyword named 'repeat' */
    REPEAT("repeat"),

    /** CSS keyword named 'reverse' */
    REVERSE("reverse"),

    /** CSS keyword named 'ridge' */
    RIDGE("ridge"),

    /** CSS keyword named 'right' */
    RIGHT("right"),

    /** CSS keyword named 'round' */
    ROUND("round"),

    /** CSS keyword named 'rows' */
    ROWS("rows"),

    /** CSS keyword named 'rtl' */
    RTL("rtl"),

    /** CSS keyword named 'scroll' */
    SCROLL("scroll"),

    /** CSS keyword named 'semi-condensed' */
    SEMI_CONDENSED("semi-condensed"),

    /** CSS keyword named 'semi-expanded' */
    SEMI_EXPANDED("semi-expanded"),

    /** CSS keyword named 'separate' */
    SEPARATE("separate"),

    /** CSS keyword named 'silver' */
    SILVER("silver"),

    /** CSS keyword named 'small-caps' */
    SMALL_CAPS("small-caps"),

    /** CSS keyword named 'solid' */
    SOLID("solid"),

    /** CSS keyword named 'square' */
    SQUARE("square"),

    /** CSS keyword named 'start' */
    START("start"),

    /** CSS keyword named 'stroke' */
    STROKE("stroke"),

    /** CSS keyword named 'sub' */
    SUB("sub"),

    /** CSS keyword named 'super' */
    SUPER("super"),

    /** CSS keyword named 'tab' */
    TAB("tab"),

    /** CSS keyword named 'table' */
    TABLE("table"),

    /** CSS keyword named 'table-caption' */
    TABLE_CAPTION("table-caption"),

    /** CSS keyword named 'table-cell' */
    TABLE_CELL("table-cell"),

    /** CSS keyword named 'table-column' */
    TABLE_COLUMN("table-column"),

    /** CSS keyword named 'table-column-group' */
    TABLE_COLUMN_GROUP("table-column-group"),

    /** CSS keyword named 'table-footer-group' */
    TABLE_FOOTER_GROUP("table-footer-group"),

    /** CSS keyword named 'table-header-group' */
    TABLE_HEADER_GROUP("table-header-group"),

    /** CSS keyword named 'table-row' */
    TABLE_ROW("table-row"),

    /** CSS keyword named 'table-row-group' */
    TABLE_ROW_GROUP("table-row-group"),

    /** CSS keyword named 'teal' */
    TEAL("teal"),

    /** CSS keyword named 'text' */
    TEXT("text"),

    /** CSS keyword named 'text-after-edge' */
    TEXT_AFTER_EDGE("text-after-edge"),

    /** CSS keyword named 'text-before-edge' */
    TEXT_BEFORE_EDGE("text-before-edge"),

    /** CSS keyword named 'text-bottom' */
    TEXT_BOTTOM("text-bottom"),

    /** CSS keyword named 'text-top' */
    TEXT_TOP("text-top"),

    /** CSS keyword named 'toggle' */
    TOGGLE("toggle"),

    /** CSS keyword named 'tooltip' */
    TOOLTIP("tooltip"),

    /** CSS keyword named 'top' */
    TOP("top"),

    /** CSS keyword named 'ultra-condensed' */
    ULTRA_CONDENSED("ultra-condensed"),

    /** CSS keyword named 'ultra-expanded' */
    ULTRA_EXPANDED("ultra-expanded"),

    /** CSS keyword named 'upper-alpha' */
    UPPER_ALPHA("upper-alpha"),

    /** CSS keyword named 'upper-latin' */
    UPPER_LATIN("upper-latin"),

    /** CSS keyword named 'upper-roman' */
    UPPER_ROMAN("upper-roman"),

    /** CSS keyword named 'uppercase' */
    UPPERCASE("uppercase"),

    /** CSS keyword named 'vertical' */
    VERTICAL("vertical"),

    /** CSS keyword named 'visible' */
    VISIBLE("visible"),

    /** CSS keyword named 'white' */
    WHITE("white"),

    /** CSS keyword named 'yellow' */
    YELLOW("yellow");

    private final String keyword;

    Keyword(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Gets whether this keyword is the only value of the given {@link Declaration}.
     * <p/>
     * Example:
     * <pre>
     * {@code Keyword.NONE.isOnlyValueIn(theDeclaration);}
     * </pre>
     *
     * @param declaration
     *     Check if this {@link Declaration}'s value only consists of this keyword.
     *
     * @return True if this keyword is the only value in the {@link Declaration}.
     */
    public boolean isOnlyValueIn(Declaration declaration) {
        return isOnlyValueIn(declaration.propertyValue());
    }

    /**
     * Gets whether the given {@link PropertyValue} only consists of one {@link Term} which is a {@link KeywordValue} with this
     * {@link Keyword}.
     * <p/>
     * Example:
     * <pre>
     * {@code Keyword.NONE.isOnlyValueIn(thePropertyValue);}
     * </pre>
     *
     * @param value
     *     The {@link PropertyValue} to check.
     *
     * @return True if this keyword is the only value in the {@link PropertyValue}.
     */
    public boolean isOnlyValueIn(PropertyValue value) {
        Optional<KeywordValue> keywordValue = Value.asKeyword(value);
        return keywordValue.isPresent() && keywordValue.get().keyword().equals(keyword);
    }

    @Override
    public String toString() {
        return keyword;
    }
}
