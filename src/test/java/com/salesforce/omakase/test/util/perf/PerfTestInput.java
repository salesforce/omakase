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

package com.salesforce.omakase.test.util.perf;

@SuppressWarnings("ALL")
public final class PerfTestInput {
    public static final String NORMAL = ".uiButton{\n" +
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

    public static final String PREFIX = ".uiButton{\n" +
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
        "    background:-moz-linear-gradient(#F8F8F9,#DDDFE1);\n" +
        "    background:linear-gradient(#F8F8F9,#DDDFE1);\n" +
        "    box-shadow:0 1px 3px rgba(0, 0, 0, 0.7),0 1px 0 rgba(0, 0, 0, 0.3) ;\n" +
        "    text-shadow:0 1px 1px #FFF; \n" +
        "}\n" +
        "\n" +
        ".uiButton.default:hover,\n" +
        ".uiButton.default:focus{\n" +
        "    background:#757D8A;\n" +
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
        "    box-shadow:none;\n" +
        "    text-shadow:none;\n" +
        "}\n" +
        ".uiButton.default:disabled .label{\n" +
        "    color:#888;\n" +
        "}\n" +
        ".uiButton.default:disabled .label:hover{\n" +
        "    color:#888;\n" +
        "}" +
        "::selection {\n" +
        "  color:red;\n" +
        "}\n" +
        "\n" +
        ".THIS {\n" +
        "  color: blue;\n" +
        "}\n" +
        "\n" +
        "@keyframes test {\n" +
        "  from { top: 0%; }\n" +
        "  50% { top: 50%; }\n" +
        "  to { top: 100%; }  \n" +
        "}\n" +
        "\n" +
        "@-moz-keyframes test {\n" +
        "  from { top: 0%; }\n" +
        "  50% { top: 50%; }\n" +
        "  to { top: 100%; }    \n" +
        "}\n" +
        "\n" +
        "@media all and (min-width:800px) {\n" +
        "  .THIS {\n" +
        "    color: yellow;\n" +
        "  }\n" +
        "}";
}
