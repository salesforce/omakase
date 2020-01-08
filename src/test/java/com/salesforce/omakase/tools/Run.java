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

package com.salesforce.omakase.tools;

import com.salesforce.omakase.tools.perf.RunPerfTest;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

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

    @Option(name = "-p", aliases = "--perf", usage = "performance test", metaVar = "<args>")
    private boolean perf;

    @Option(name = "-u", aliases = "--update", usage = "regenerate data enum, data class and prefixes source files")
    private boolean update;

    @Option(name = "-l", aliases = "--local-only", usage = "only regenerate local data, no prefix data (used with -u option)")
    private boolean local;

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

    @Argument
    private List<String> arguments = new ArrayList<>();

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
            } else if (perf) {
                if (arguments.isEmpty()) {
                    RunPerfTest.printUsage();
                } else {
                    RunPerfTest.run(arguments);
                }
            } else if (update) {
                new GeneratePrefixEnum().run();
                new GenerateKeywordEnum().run();
                new GeneratePropertyEnum().run();

                if (!local) {
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
                }
                System.out.println(Colors.yellow("all data generated successfully!"));

                System.out.println("\nIf output does not reflect expected changes, try a mvn clean install first to recompile " +
                    "any changed code");
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
