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

package com.salesforce.omakase.test;

import com.salesforce.omakase.Omakase;
import com.salesforce.omakase.plugin.basic.AutoRefiner;
import com.salesforce.omakase.plugin.basic.SyntaxTree;
import com.salesforce.omakase.plugin.validator.StandardValidation;
import com.salesforce.omakase.test.util.EchoLogger;
import com.salesforce.omakase.writer.StyleWriter;

/** Temp test for debugging. */
@SuppressWarnings({"JavaDoc", "UnusedDeclaration", "UnusedAssignment"})
public class DebugTest {

    public void develop() {
        AutoRefiner refinement = new AutoRefiner().all();
        EchoLogger logging = new EchoLogger();
        SyntaxTree tree = new SyntaxTree();
        StyleWriter writer = StyleWriter.compressed();

        Omakase.source(SRC0)
            .request(tree)
                // .request(refinement)
            .request(logging)
            .request(new StandardValidation())
            .request(writer)
            .process();

        System.out.println();
        System.out.println(tree.toString());

//        QuickWriter
//            .writeAllModes(".class {\n" +
//                "    content: \" (\" attr(href) \")\"\n" +
//                "}");
    }

    public static final String SRC0 = ".class /*comment*/ {" +
        "  color: red\n" +
        "}\n" +
        "@media(min-width: 800px) {\n" +
        "  .abc {\n" +
        "    color: red;\n" +
        "  }\n" +
        "}\n";
    public static final String SRC1 = ".testing #is > fun p { padding: 3px; color: red; margin: 10px 5px; }";
    public static final String SRC2 = ".testing #is > fun p { padding: 3px; color: red; margin: 10px 5px; background-color: " +
        "#fffeee; -moz-border-radius: 3px 4px 5px 6px; }";
    public static final String SRC3 = ".uiButton{\n" +
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
