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

package com.salesforce.omakase.test.util.tool;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.salesforce.omakase.ast.declaration.Keyword;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Utility to take list of css names and fromStrings them to the {@link Keyword} enum.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public final class KeywordToEnum {
    private KeywordToEnum() {}

    public static void main(String[] args) {
        Collections.sort(list);

        Set<String> used = Sets.newHashSet();

        StringBuilder builder = new StringBuilder(1024);

        for (int i = 0; i < list.size(); i++) {
            String p = list.get(i);

            if (used.contains(p)) throw new RuntimeException("'" + p + "' is already defined");
            used.add(p);

            builder.append("/** CSS keyword named '").append(p).append("' */").append("\n");
            builder.append(CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_UNDERSCORE, p));
            builder.append("(\"").append(p).append("\")");

            if (i < (list.size() - 1)) {
                builder.append(",");
            } else {
                builder.append(";");
            }
            builder.append("\n\n");
        }

        System.out.println(builder.toString());
    }

    static final List<String> list = Lists.newArrayList(
        "above",
        "absolute",
        "all",
        "always",
        "aqua",
        "auto",
        "avoid",
        "avoid-column",
        "avoid-page",
        "balance",
        "baseline",
        "below",
        "black",
        "block",
        "blue",
        "bold",
        "bolder",
        "border-box",
        "both",
        "bottom",
        "box",
        "break-all",
        "button",
        "caps-height",
        "center",
        "central",
        "circle",
        "collapse",
        "column",
        "combo-box",
        "condensed",
        "continuous",
        "dashed",
        "decimal",
        "decimal-leading-zero",
        "desktop",
        "dotted",
        "double",
        "end",
        "expanded",
        "field",
        "fill",
        "fixed",
        "fuchsia",
        "gray",
        "green",
        "grid",
        "groove",
        "hidden",
        "hidden",
        "hide",
        "higher",
        "horizontal",
        "hyperlink",
        "inherit",
        "initial",
        "inline-axis",
        "inline-block",
        "inline-box",
        "inline-grid",
        "inline-table",
        "inset",
        "italic",
        "justify",
        "left",
        "lime",
        "line",
        "list-item",
        "list-menu",
        "lower",
        "lower-alpha",
        "lower-greek",
        "lower-latin",
        "lower-roman",
        "lowercase",
        "ltr",
        "manual",
        "maroon",
        "max-height",
        "menu",
        "menu-item",
        "middle",
        "multiple",
        "navy",
        "none",
        "normal",
        "nowrap",
        "olive",
        "open",
        "optimizeLegibility",
        "optimizeSpeed",
        "outline-tree",
        "outset",
        "outside",
        "page",
        "pre",
        "pre-line",
        "pre-wrap",
        "purple",
        "read-only",
        "read-write",
        "red",
        "relative",
        "repeat",
        "reverse",
        "ridge",
        "right",
        "round",
        "rows",
        "rtl",
        "scroll",
        "semi-condensed",
        "semi-expanded",
        "separate",
        "silver",
        "small-caps",
        "solid",
        "square",
        "start",
        "stroke",
        "sub",
        "super",
        "tab",
        "table",
        "table-caption",
        "table-cell",
        "table-column",
        "table-column-group",
        "table-footer-group",
        "table-header-group",
        "table-row",
        "table-row-group",
        "teal",
        "text",
        "text-after-edge",
        "text-before-edge",
        "text-bottom",
        "text-top",
        "toggle",
        "tooltip",
        "top",
        "ultra-condensed",
        "ultra-expanded",
        "upper-alpha",
        "upper-latin",
        "upper-roman",
        "uppercase",
        "vertical",
        "visible",
        "white",
        "yellow");
}
