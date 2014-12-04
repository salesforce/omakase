/*
 * Copyright (C) 2014 salesforce.com, inc.
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

package com.salesforce.omakase.test.sample.custom.atrule;

import com.salesforce.omakase.Omakase;
import com.salesforce.omakase.ast.atrule.MediaQueryList;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import com.salesforce.omakase.broadcast.annotation.Validate;
import com.salesforce.omakase.plugin.SyntaxPlugin;
import com.salesforce.omakase.plugin.validator.StandardValidation;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;

/**
 * An example of using the sample custom at-rule classes in this package.
 * <p/>
 * The custom at-rule allows for a "query token", in the format of <code>@query medium | all and (min-width:800px)</code>.
 * Subsequent media queries can refer to this token, allowing our CSS to be DRY.
 * <p/>
 * We give the parser an instance of the {@link QueryTokenPlugin}. This plugin is a {@link SyntaxPlugin} that registers our {@link
 * QueryTokenRefiner}. The refiner handles actually parsing the custom at-rule tokens as well as the media query references to
 * them. Unlike the other samples, no custom AST objects are necessary, although we could have used them if we wanted.
 * <p/>
 * Note that in this example we are parsing 4 distinct sources (they could be 4 different files on disk). The first file contains
 * the query token definitions for subsequent usage, and the following files utilize them even though they are parsed separately.
 * This is possible because we reuse the same {@link QueryTokenPlugin} instance when parsing each file.
 * <p/>
 * Things to try:
 * <p/>
 * <b>1)</b> Make a query token definition invalid (e.g., remove the pipe). <b>2)</b> Write an additional plugin that subscribes
 * to {@link MediaQueryList}, and note that it gets called with each substituted token. <b>3)</b> Create a QueryToken custom AST
 * object class that is annotated with {@link Subscribable}, and have the refiner create and broadcast those. Write a plugin that
 * subscribes to the QueryToken AST object (with {@link Validate}) and have it count the number of times the same query token is
 * used, throwing an error if it is above some threshold (this would be an example of css linting).
 *
 * @author nmcwilliams
 */
@SuppressWarnings({"UtilityClassWithoutPrivateConstructor", "JavaDoc"})
public final class SampleUsage {
    public static void main(String[] args) throws IOException {
        // css file 1, defines media query tokens
        String file1 = "" +
            "@query medium  | all and (min-width:800px);\n" +
            "@query large   | all and (min-width:1200px);\n" +
            "@query x-large | all and (min-width:1600px);";

        // sample file 2
        String file2 = "" +
            ".box {\n" +
            "  display: inline-block;\n" +
            "  width: 100%;\n" +
            "}\n" +
            "\n" +
            "@media medium {\n" +
            "  .box { width: 500px }  \n" +
            "}\n" +
            "\n" +
            "@media large {\n" +
            "  .box { width: 800px }\n" +
            "}\n" +
            "\n" +
            "@media x-large {\n" +
            "  .box { width: 1000px }\n" +
            "}";

        // same file 3
        String file3 = "" +
            ".widget {\n" +
            "  padding: 5px;\n" +
            "}\n" +
            "\n" +
            "@media large {\n" +
            "  .widget { padding: 10px }\n" +
            "}";

        // sample file 4
        String file4 = "" +
            ".extra {\n" +
            "  display: none;\n" +
            "}\n" +
            "\n" +
            "@media x-large {\n" +
            "  .extra { display: block }\n" +
            "}";

        System.out.println("Sample Custom At Rule");

        System.out.println("\nINPUT FILE 1:\n--------------------");
        System.out.println(file1);

        System.out.println("\nINPUT FILE 2:\n--------------------");
        System.out.println(file2);

        System.out.println("\nINPUT FILE 3:\n--------------------");
        System.out.println(file3);

        System.out.println("\nINPUT FILE 4:\n--------------------");
        System.out.println(file4);

        // setup the plugins we want
        StyleWriter inline = StyleWriter.inline();
        StandardValidation validation = new StandardValidation();
        QueryTokenPlugin queryTokens = new QueryTokenPlugin();

        // parse files
        Omakase.source(file1).use(validation).use(inline).use(queryTokens).process();
        System.out.println("\nOUTPUT FILE 1:\n--------------------");
        inline.writeTo(System.out);

        Omakase.source(file2).use(validation).use(inline).use(queryTokens).process();
        System.out.println("\n\nOUTPUT FILE 2:\n--------------------");
        inline.writeTo(System.out);

        Omakase.source(file3).use(validation).use(inline).use(queryTokens).process();
        System.out.println("\n\nOUTPUT FILE 3:\n--------------------");
        inline.writeTo(System.out);

        Omakase.source(file4).use(validation).use(inline).use(queryTokens).process();
        System.out.println("\n\nOUTPUT FILE 4:\n--------------------");
        inline.writeTo(System.out);
    }
}
