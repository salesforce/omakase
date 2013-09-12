/**
 * ADD LICENSE
 */
package com.salesforce.omakase.test.util.tool;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;
import com.salesforce.omakase.ast.declaration.value.Keyword;

import java.util.Collections;
import java.util.List;

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

        StringBuilder builder = new StringBuilder(1024);

        for (int i = 0; i < list.size(); i++) {
            String p = list.get(i);
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
        "auto",
        "avoid",
        "avoid-column",
        "avoid-page",
        "balance",
        "baseline",
        "below",
        "block",
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
        "decimal",
        "decimal-leading-zero",
        "desktop",
        "end",
        "expanded",
        "field",
        "fill",
        "fixed",
        "grid",
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
        "italic",
        "justify",
        "left",
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
        "max-height",
        "menu",
        "menu-item",
        "middle",
        "multiple",
        "none",
        "normal",
        "nowrap",
        "open",
        "optimizeLegibility",
        "optimizeSpeed",
        "outline-tree",
        "outside",
        "page",
        "pre",
        "pre-line",
        "pre-wrap",
        "read-only",
        "read-write",
        "relative",
        "repeat",
        "reverse",
        "right",
        "round",
        "rows",
        "rtl",
        "scroll",
        "semi-condensed",
        "semi-expanded",
        "separate",
        "small-caps",
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
        "visible");
}
