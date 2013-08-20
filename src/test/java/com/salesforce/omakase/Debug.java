/**
 * ADD LICENSE
 */
package com.salesforce.omakase;

import org.junit.Test;

import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.plugin.standard.AutoRefiner;
import com.salesforce.omakase.plugin.standard.SyntaxTree;
import com.salesforce.omakase.util.EchoLogger;

/**
 * Temp test for debugging.
 */
@SuppressWarnings("javadoc")
public class Debug {
    @Test
    public void develop() {
        EchoLogger logger = new EchoLogger();
        SyntaxTree tree = new SyntaxTree();
        AutoRefiner autoRefiner = new AutoRefiner().include(Selector.class).include(Declaration.class);
        Omakase.source(srca).request(tree, autoRefiner, logger).process();
        System.out.println(tree.toString());
    }

    public static final String srca = "#id p::before{ padding: 3px; color: red; margin: 10px 5px; }";
    public static final String src1 = ".testing #is > fun p { padding: 3px; color: red; margin: 10px 5px; }";
    public static final String src2 = ".testing #is > fun p { padding: 3px; color: red; margin: 10px 5px; background-color: #fffeee; -moz-border-radius: 3px 4px 5px 6px; }";

    public static final String src3 = ".uiButton{\n" +
            "    display:inline-block;\n" +
            "    cursor:pointer;\n" +
            "}\n" +
            "\n" +
            ".uiButton .label{\n" +
            "    display:block;\n" +
            "}\n" +
            "\n" +
            ".uiButton.default{\n" +
            "    font-weight: bold;\n" +
            "    font-size: .9em;\n" +
            "    margin: 2px 3px;\n" +
            "    padding: 4px 6px;\n" +
            "    text-decoration:none;\n" +
            "    text-align:center;\n" +
            "    border-radius:4px;\n" +
            "    border:0;\n" +
            "    border-top:1px solid rgba(255,255,255,.45);\n" +
            "    background:#DDDFE1;\n" +
            "    background:-webkit-gradient(linear, 0% 0%, 0% 100%, from(#F8F8F9), to(#DDDFE1));\n" +
            "    background:-webkit-linear-gradient(#F8F8F9,#DDDFE1);\n" +
            "    background:-moz-linear-gradient(#F8F8F9,#DDDFE1);\n" +
            "    background:linear-gradient(#F8F8F9,#DDDFE1);\n" +
            "    -webkit-box-shadow:0 1px 3px rgba(0, 0, 0, 0.7),0 1px 0 rgba(0, 0, 0, 0.3);\n" +
            "    box-shadow:0 1px 3px rgba(0, 0, 0, 0.7),0 1px 0 rgba(0, 0, 0, 0.3) ;\n" +
            "    text-shadow:0 1px 1px #FFF; \n" +
            "}\n" +
            "\n" +
            ".uiButton.default:hover,\n" +
            ".uiButton.default:focus{\n" +
            "    background:#757D8A;\n" +
            "    background:#757D8A -webkit-gradient(linear, 0% 0%, 0% 100%, from(#7F8792), to(#535B68));\n" +
            "    background:#757D8A -webkit-linear-gradient(#7F8792,#535B68);\n" +
            "    background:#757D8A -moz-linear-gradient(#7F8792,#535B68);\n" +
            "    background:#757D8A linear-gradient(#7F8792,#535B68);\n" +
            "    text-shadow:0 -1px 1px rgba(0, 0, 0, 0.5);\n" +
            "}\n" +
            ".uiButton.default .label{\n" +
            "    white-space:nowrap;\n" +
            "    color: #3A3D42;\n" +
            "}\n" +
            ".uiButton.default:hover .label,\n" +
            ".uiButton.default:focus .label{\n" +
            "    color: #FFF;\n" +
            "}\n" +
            ".uiButton.default:disabled{\n" +
            "    cursor:default;\n" +
            "    background:#B9B9B9;\n" +
            "    -webkit-box-shadow:none;\n" +
            "    box-shadow:none;\n" +
            "    text-shadow:none;\n" +
            "}\n" +
            ".uiButton.default:disabled .label{\n" +
            "    color:#888;\n" +
            "}\n" +
            ".uiButton.default:disabled .label:hover{\n" +
            "    color:#888;\n" +
            "}";

}
