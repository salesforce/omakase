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

import com.salesforce.omakase.test.util.perf.PerfTest;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;

/**
 * Omakase CLI. See script/omakase.sh.
 *
 * @author nmcwilliams
 */
@SuppressWarnings({"JavaDoc", "FieldMayBeFinal", "FieldCanBeLocal"})
public class Run {
    public static final String USAGE = "Usage: omakase [options]";

    @Option(name = "-b", aliases = "--build", usage = "build the project")
    private boolean build;

    @Option(name = "-p", aliases = "--perf", usage = "performance test (ex. \"-p full\", \"-p full-heavy\")", metaVar = "<mode-input>")
    private String perf;

    @Option(name = "-d", aliases = "--deploy", usage = "build and deploy jars (requires additional setup, see deploy.md)")
    private boolean deploy;

    @Option(name = "-u", aliases = "--update", usage = "regenerate data enum, data class and prefixes source files")
    private boolean update;

    @Option(name = "-s", aliases = {"--syntax", "--sub"}, usage = "print the subscribable syntax table")
    private boolean sub;

    @Option(name = "-v", aliases = "--prefixed-def", usage = "print what is auto-prefixed by Prefixer.defaultBrowserSupport()")
    private boolean prefixedDef;

    @Option(name = "-w", aliases = "--prefixed-all", usage = "print all properties, at-rules, etc...that are supported by Prefixer")
    private boolean prefixedAll;

    @Option(name = "-i", aliases = {"--interactive", "--shell"}, usage = "interactive shell")
    private boolean interactive;

    @Option(name = "-h", aliases = "--help", usage = "print this help message")
    private boolean help;

    public static void main(String[] args) throws Exception {
        new Run().cli(args);
    }

    public void cli(String[] args) throws Exception {
        CmdLineParser parser = new CmdLineParser(this);
        parser.setUsageWidth(250);

        try {
            if (args.length == 0) throw new CmdLineException(parser, USAGE);

            parser.parseArgument(args);

            if (build) {
                if (!exec("mvn clean install")) {
                    System.out.println("\n" + Colors.red("build was not successful!"));
                }
            } else if (perf != null) {
                PerfTest.main(new String[]{perf});
            } else if (deploy) {
                if (!exec("mvn deploy")) {
                    System.out.println("\n" + Colors.red("could not deploy to the internal aura maven repo"));
                } else if (!exec("mvn deploy -P external")) {
                    System.out.println("\n" + Colors.red("could not deploy to the external aura maven repo"));
                } else if (!exec("mvn deploy -P sfdc")) {
                    System.out.println("\n" + Colors.red("could not deploy to the sfdc nexus repo"));
                }
            } else if (update) {
                new GeneratePrefixEnum().run();
                new GenerateKeywordEnum().run();
                new GeneratePropertyEnum().run();
                boolean updated = new GenerateBrowserEnum().run();

                if (updated) {
                    System.out.println("Browser.java was updated. Forcing recompilation of sources...");
                    if (!exec("mvn compile test-compile")) {
                        System.out.println("\n" + Colors.red("error regenerating java sources"));
                    }
                    System.out.println(Colors.yellow("Browser.java was updated and recompiled. \n" +
                        "Please run this command again to ensure changes are picked up. \n" +
                        "(Updated prefix data will not occur unless you do this!)"));
                    System.exit(0);
                }

                new GeneratePrefixTablesClass().run();
                System.out.println(Colors.yellow("all data generated successfully"));
            } else if (sub) {
                new PrintSubscribableSyntaxTable().run();
            } else if (prefixedDef) {
                new PrintDefaultPrefixed().run();
            } else if (prefixedAll) {
                new PrintAllPrefixed().run();
            } else if (interactive) {
                new InteractiveShell().run();
            } else if (help) {
                throw new CmdLineException(parser, USAGE);
            }
        } catch (CmdLineException e) {
            System.out.println();
            System.out.println("  " + e.getMessage());
            System.out.println();

            System.out.println("  Options:\n");

            StringWriter writer = new StringWriter();
            parser.printUsage(writer, null);

            String usage = writer.toString();
            usage = "   " + usage.replaceAll("\n", "\n   ");
            usage = usage.replaceAll(":", " ");

            System.out.println(usage);
        }
    }

    public boolean exec(String cmd) throws IOException, InterruptedException {
        Process proc = Runtime.getRuntime().exec(cmd);
        BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        BufferedReader err = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

        String s;

        while ((s = in.readLine()) != null) System.out.println(s);
        while ((s = err.readLine()) != null) System.err.println(s);

        int code = proc.waitFor();
        return code == 0;
    }
}
