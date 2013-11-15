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

import com.salesforce.omakase.Omakase;
import com.salesforce.omakase.data.Browser;
import com.salesforce.omakase.error.FatalException;
import com.salesforce.omakase.plugin.basic.Prefixer;
import com.salesforce.omakase.plugin.validator.StandardValidation;
import com.salesforce.omakase.writer.StyleWriter;
import com.salesforce.omakase.writer.WriterMode;

import java.io.IOException;
import java.util.Scanner;

/**
 * Interactive shell for css parsing.
 *
 * @author nmcwilliams
 */
@SuppressWarnings({"JavaDoc", "ConstantConditions"})
public class InteractiveShell {
    private final Scanner console = new Scanner(System.in);
    private final StyleWriter writer = StyleWriter.inline();

    private StringBuilder builder = new StringBuilder(512);
    private Prefixer prefixer;
    private boolean continuous;

    public void run() throws IOException {
        System.out.println(Colors.yellow("Omakase Interactive Shell"));
        System.out.println("enter " + Colors.red("!") + " on a new line to finish");
        System.out.println("enter " + Colors.red("!c") + " for continuous mode (ctrl+c to exit)");
        System.out.println("enter " + Colors.red("!verbose") + " for verbose output");
        System.out.println("enter " + Colors.red("!inline") + " for inline output");
        System.out.println("enter " + Colors.red("!compressed") + " for compressed output");
        System.out.println("enter " + Colors.red("!prefix-current") + " for default prefixing");
        System.out.println("enter " + Colors.red("!prefix-all") + " for all prefixing");
        System.out.println("enter " + Colors.red("!prefix-prune") + " to enable prefix pruning");
        System.out.println("enter " + Colors.red("!prefix-rearrange") + " to enable prefix rearranging");
        System.out.println("enter " + Colors.red("!prefix-off") + " to remove prefixing support");

        System.out.println();

        do {
            loop();
            output();
            if (continuous) System.out.println("---------------------------\n");
        } while (continuous);
    }

    private void loop() {
        builder = new StringBuilder(512);

        while (true) {
            String next = console.nextLine();

            if (next.startsWith("!")) {
                if (next.equals("!")) {
                    break;
                } else if (next.equals("!c")) {
                    continuous = !continuous;
                    System.out.print(Colors.grey("continous mode is ") + Colors.red(continuous ? "on" : "off"));
                    if (continuous) {
                        System.out.print(Colors.grey(" (ctrl+c or !c again to stop)"));
                    }
                    System.out.println("\n");
                } else if (next.equals("!verbose")) {
                    writer.mode(WriterMode.VERBOSE);
                    System.out.println(Colors.grey("switched to verbose output\n"));
                } else if (next.equals("!inline")) {
                    writer.mode(WriterMode.INLINE);
                    System.out.println(Colors.grey("switched to inline output\n"));
                } else if (next.equals("!compressed")) {
                    writer.mode(WriterMode.COMPRESSED);
                    System.out.println(Colors.grey("switched to compressed output\n"));
                } else if (next.equals("!prefix-current")) {
                    prefixer = Prefixer.defaultBrowserSupport();
                    System.out.println(Colors.grey("added prefixing support\n"));
                } else if (next.equals("!prefix-all")) {
                    prefixer = Prefixer.customBrowserSupport();
                    prefixer.support().all(Browser.CHROME);
                    prefixer.support().all(Browser.FIREFOX);
                    prefixer.support().all(Browser.SAFARI);
                    prefixer.support().all(Browser.OPERA);
                    System.out.println(Colors.grey("added prefixing support\n"));
                } else if (next.equals("!prefix-prune") && prefixer != null) {
                    prefixer.prune(!prefixer.prune());
                    System.out.print(Colors.grey("prefix pruning is ") + Colors.red(prefixer.prune() ? "on" : "off"));
                    if (prefixer.prune()) {
                        System.out.print(Colors.grey(" (!prefix-prune again to turn off)"));
                    }
                    System.out.println("\n");
                } else if (next.equals("!prefix-rearrange")) {
                    prefixer.rearrange(!prefixer.rearrange());
                    System.out.print(Colors.grey("prefix rearranging is ") + Colors.red(prefixer.rearrange() ? "on" : "off"));
                    if (prefixer.rearrange()) {
                        System.out.print(Colors.grey(" (!prefix-rearrange again to turn off)"));
                    }
                    System.out.println("\n");
                } else if (next.equals("!prefix-off")) {
                    prefixer = null;
                    System.out.println(Colors.grey("automatic prefixing turned off\n"));
                }
            } else {
                boolean quickExit = false;

                if (next.endsWith("!")) {
                    next = next.substring(0, next.length() - 1);
                    quickExit = true;
                }

                builder.append(next).append("\n");

                if (quickExit) {
                    break;
                }
            }
        }
    }

    private void output() throws IOException {
        String input = builder.toString().trim();
        if (input.isEmpty()) return;

        System.out.println("\n---------------------------");
        if (!continuous) System.out.println();

        try {
            Omakase.Request request = Omakase.source(input);
            request.add(new StandardValidation());
            request.add(writer);
            if (prefixer != null) request.add(prefixer);
            request.process();
        } catch (FatalException e) {
            System.out.print(Colors.red(e.getMessage()));
            System.out.println("\n");
            System.exit(1);
        }

        if (continuous) System.out.println(Colors.BLUE);
        writer.writeTo(System.out);
        if (continuous) {
            System.out.println(Colors.RESET + "\n");
        } else {
            System.out.println("\n");
        }

    }
}
