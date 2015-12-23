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

package com.salesforce.omakase.test.util.perf;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

@SuppressWarnings({"UtilityClassWithoutPrivateConstructor", "JavaDoc"})
public final class PerfTestInput {
    public static final Map<String, String> MAP = ImmutableMap.<String, String>builder()
        // BASIC
        .put("simple", "body {\n" +
            "  background: blue;\n" +
            "  margin: 20px 10px;\n" +
            "}\n" +
            "\n" +
            "a {\n" +
            "\tcolor: blue;\n" +
            "\ttext-decoration: none;\n" +
            "}\n" +
            "\n" +
            "a:hover {\n" +
            "\ttext-decoration: underline;\n" +
            "}\n" +
            "\n" +
            ".class {\n" +
            "  color: #000;\n" +
            "  background-color: #f3f3f3;\n" +
            "  text-align: center;\n" +
            "  padding: 4px;\n" +
            "  border: 1px dashed #fff;\n" +
            "  line-height: 1.65em;\n" +
            "  margin: 20px auto 12px;\n" +
            "  width: 50%;\n" +
            "}\n" +
            "\n" +
            ".class .class .class {\n" +
            "  font-family: \"Century Schoolbook\", \"Gill Sans\", \"Gill Sans MT\", Calibri, sans-serif;\n" +
            "  display: inline-block;\n" +
            "}\n" +
            "\n" +
            "#id {\n" +
            "  margin-top: 20px;\n" +
            "}\n" +
            "\n" +
            "#id p {\n" +
            "  margin: 8px 0;\n" +
            "}\n" +
            "\n" +
            ".something-something,\n" +
            ".something-something-something,\n" +
            ".one-more-for-good-measure {\n" +
            "  font-weight: bold;\n" +
            "  display: block;\t\n" +
            "}")

        // UI BUTTON
        .put("button", ".uiButton{\n" +
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
            "}")

        // HEAVY (USE WITH PREFIXER)
        .put("heavy", ".class {\n" +
            "\tdisplay: inline-block;\n" +
            "\tcursor: pointer;\n" +
            "}\n" +
            "\n" +
            "::selection {\n" +
            "\tcolor: red;\n" +
            "}\n" +
            "         \n" +
            ".class .label {\n" +
            "\tdisplay: block;\n" +
            "}\n" +
            "         \n" +
            ".class.default {\n" +
            "\tfont-weight: bold;\n" +
            "\tfont-size: .9em;\n" +
            "\tmargin: 2px 3px;\n" +
            "\tpadding: 4px 6px;\n" +
            "\ttext-decoration: none;\n" +
            "\ttext-align: center;\n" +
            "\tborder-radius: 4px;\n" +
            "\tborder: 0;\n" +
            "\tborder-top: 1px solid rgba(255,255,255,.45);\n" +
            "\tbackground: #DDDFE1;\n" +
            "\tbackground: -webkit-gradient(linear, 0% 0%, 0% 100%, from(#F8F8F9), to(#DDDFE1));\n" +
            "\tbackground: -webkit-linear-gradient(#F8F8F9,#DDDFE1);\n" +
            "\tbackground: -moz-linear-gradient(#F8F8F9,#DDDFE1);\n" +
            "\tbackground: linear-gradient(#F8F8F9,#DDDFE1);\n" +
            "\t-webkit-box-shadow: 0 1px 3px rgba(0, 0, 0, 0.7),0 1px 0 rgba(0, 0, 0, 0.3);\n" +
            "\tbox-shadow: 0 1px 3px rgba(0, 0, 0, 0.7),0 1px 0 rgba(0, 0, 0, 0.3) ;\n" +
            "\ttext-shadow: 0 1px 1px #FFF;\n" +
            "}\n" +
            "         \n" +
            ".class.default:hover, \n" +
            ".class.default:focus {\n" +
            "\tfont-weight: bold;\n" +
            "\tfont-size: .9em;\n" +
            "\tmargin: 2px 3px;\n" +
            "\tpadding: 4px 6px;\n" +
            "\ttext-decoration: none;\n" +
            "\ttext-align: center;\n" +
            "\tbackground: #757D8A;\n" +
            "\tbackground: #757D8A linear-gradient(#7F8792,#535B68);\n" +
            "\ttext-shadow: 0 -1px 1px rgba(0, 0, 0, 0.5);\n" +
            "\tdisplay: inline-block;\n" +
            "\tcolor: red;\n" +
            "\tfont: Arial;\n" +
            "\tline-height: 1.3;\n" +
            "\twidth: calc(50% - 2px);\n" +
            "}\n" +
            "\n" +
            ".class.default .label {\n" +
            "\twidth: 20px;\n" +
            "\tbox-sizing: border-box;\n" +
            "}\n" +
            "\n" +
            ".class#one {\n" +
            "\tborder-radius: 3px;\n" +
            "\tbox-shadow: 3px 3px 5px 6px #ddd;\n" +
            "}\n" +
            "\n" +
            ".class.default:disabled {\n" +
            "\tcursor: default;\n" +
            "\tbackground: #B9B9B9;\n" +
            "\t-webkit-box-shadow: none;\n" +
            "\tbox-shadow: none;\n" +
            "\ttext-shadow: none;\n" +
            "}\n" +
            "\n" +
            "@keyframes test { \n" +
            "  from { top: 0%; } \n" +
            "  50% { top: 50%; } \n" +
            "  to { top: 100%; }   \n" +
            "} \n" +
            " \n" +
            "@-moz-keyframes test { \n" +
            "  from { top: 0%; } \n" +
            "  50% { top: 50%; } \n" +
            "  to { top: 100%; }     \n" +
            "} \n" +
            " \n" +
            "@media all and (min-width:800px) { \n" +
            "  .class { \n" +
            "    color: yellow; \n" +
            "  } \n" +
            "}\n" +
            "\n" +
            ".class1 {\n" +
            "\ttransition: border-radius 2ms, color 1ms;\n" +
            "\tcolor: red;\n" +
            "\tborder-radius: 3px;\n" +
            "}\n" +
            "\n" +
            ".class2 {\n" +
            "\twidth: calc(50% - 2px);\n" +
            "}\n"+
            ".test {\n" +
            "  display: flex;\n" +
            "  flex-wrap: wrap;\n" +
            "  justify-content: center;\n" +
            "  align-items: center;\n" +
            "}\n" +
            "\n" +
            ".test div {\n" +
            "  flex: 2 1 200px;\n" +
            "}")

        .build();
}
