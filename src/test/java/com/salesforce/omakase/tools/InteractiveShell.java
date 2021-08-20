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

import static com.google.common.base.Charsets.UTF_8;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.io.Files;
import com.salesforce.omakase.Omakase;
import com.salesforce.omakase.data.Browser;
import com.salesforce.omakase.error.DefaultErrorManager;
import com.salesforce.omakase.error.ProblemSummaryException;
import com.salesforce.omakase.plugin.core.StandardValidation;
import com.salesforce.omakase.plugin.prefixer.Prefixer;
import com.salesforce.omakase.plugin.syntax.UnquotedIEFilterPlugin;
import com.salesforce.omakase.writer.StyleWriter;
import com.salesforce.omakase.writer.WriterMode;

/**
 * Interactive shell for css parsing.
 *
 * @author nmcwilliams
 */
public class InteractiveShell {

    /**
     * Starts the shell.
     *
     * @throws Exception
     *     if something bad happens.
     */
    public void run() throws Exception {
        System.out.println(Colors.yellow("Omakase Interactive Shell"));

        // print out options
        for (Command command : Command.values()) {
            System.out.println("enter " + Colors.red(command.key) + " " + command.description);
        }
        System.out.println();

        // start the loop
        new Context().start();
    }

    /** Options for css processing */
    private enum Command {

        PROCESS("!", "on a new line to finish") {
            @Override
            void execute(Context ctx) throws Exception {
                try {
                    System.out.println("\n----------result----------\n");
                    String output = ctx.process();
                    System.out.println(output);
                    System.out.println();
                } catch (ProblemSummaryException e) {
                    System.out.print(Colors.red(e.getMessage()));
                    System.out.println("\n");
                }

                ctx.terminate = true;
                if (Command.CONTINUOUS.on) {
                    System.out.println(Colors.grey("-----------new------------\n"));
                    ctx.start();
                }
            }
        },

        CONTINUOUS("!c", "for continuous mode (ctrl+c to exit)") {
            @Override
            void execute(Context ctx) {
                on = !on;
                System.out.print(Colors.grey("continuous mode is ") + Colors.red(on ? "on" : "off"));
                if (on) System.out.print(Colors.grey(" (ctrl+c or !c again to stop)"));
                System.out.println("\n");
            }
        },

        VERBOSE("!verbose", "for verbose output") {
            @Override
            void execute(Context ctx) throws Exception {
                ctx.writer.mode(WriterMode.VERBOSE);
                System.out.println(Colors.grey("switched to verbose output\n"));
            }
        },
        INLINE("!inline", "for inline output") {
            @Override
            void execute(Context ctx) throws Exception {
                ctx.writer.mode(WriterMode.INLINE);
                System.out.println(Colors.grey("switched to inline output\n"));
            }
        },

        COMPRESSED("!compressed", "for compressed output") {
            @Override
            void execute(Context ctx) throws Exception {
                ctx.writer.mode(WriterMode.COMPRESSED);
                System.out.println(Colors.grey("switched to compressed output\n"));
            }
        },

        PREFIX_CURRENT("!prefix-current", "for default prefixing") {
            @Override
            void execute(Context ctx) throws Exception {
                ctx.prefixer = Prefixer.defaultBrowserSupport();
                System.out.println(Colors.grey("added prefixing support\n"));
            }
        },

        PREFIX_ALL("!prefix-all", "for all prefixing") {
            @Override
            void execute(Context ctx) throws Exception {
                ctx.prefixer = Prefixer.customBrowserSupport();
                ctx.prefixer.support().all(Browser.CHROME);
                ctx.prefixer.support().all(Browser.FIREFOX);
                ctx.prefixer.support().all(Browser.SAFARI);
                ctx.prefixer.support().all(Browser.OPERA);
                System.out.println(Colors.grey("added prefixing support\n"));
            }
        },
        PREFIX_PRUNE("!prefix-prune", "to enable prefix pruning") {
            @Override
            void execute(Context ctx) throws Exception {
                if (ctx.prefixer == null) return;
                ctx.prefixer.prune(!ctx.prefixer.prune());
                System.out.print(Colors.grey("prefix pruning is ") + Colors.red(ctx.prefixer.prune() ? "on" : "off"));
                if (ctx.prefixer.prune()) {
                    System.out.print(Colors.grey(" (!prefix-prune again to turn off)"));
                }
                System.out.println("\n");
            }
        },

        PREFIX_REARRANGE("!prefix-rearrange", "to enable prefix rearranging") {
            @Override
            void execute(Context ctx) throws Exception {
                if (ctx.prefixer == null) return;
                ctx.prefixer.rearrange(!ctx.prefixer.rearrange());
                System.out.print(Colors.grey("prefix rearranging is ") + Colors.red(ctx.prefixer.rearrange() ? "on" : "off"));
                if (ctx.prefixer.rearrange()) {
                    System.out.print(Colors.grey(" (!prefix-rearrange again to turn off)"));
                }
                System.out.println("\n");
            }
        },

        PREFIX_OFF("!prefix-off", "to remove prefixing support") {
            @Override
            void execute(Context ctx) throws Exception {
                ctx.prefixer = null;
            }
        },

        SUBLIME("!subl", "to use the Sublime Text editor (subl)") {
            @Override
            void execute(final Context ctx) throws IOException, InterruptedException {
                System.out.println("Sublime Text edit mode. File will be refreshed with results on save.\n");

                FileWatcher watcher = new FileWatcher(ctx);
                Timer timer = new Timer(true);
                timer.schedule(watcher, 0, 50);

                Runtime.getRuntime().exec("subl " + watcher.file() + ":2");
            }
        },

        ATOM("!atom", "to use the Atom editor (atom)") {
            @Override
            void execute(final Context ctx) throws IOException, InterruptedException {
                System.out.println("Atom edit mode. File will be refreshed with results on save.\n");

                FileWatcher watcher = new FileWatcher(ctx);
                Timer timer = new Timer(true);
                timer.schedule(watcher, 0, 50);

                Runtime.getRuntime().exec("atom " + watcher.file() + ":2");
            }
        };

        /** reverse lookup map */
        static final Map<String, Command> map;

        static {
            Builder<String, Command> builder = ImmutableMap.builder();
            for (Command c : Command.values()) builder.put(c.key, c);
            map = builder.build();
        }

        final String key;
        final String description;
        boolean on;

        Command(String key, String description) {
            this.key = key;
            this.description = description;
        }

        abstract void execute(Context ctx) throws Exception;

        public static Optional<Command> get(String command) {
            return Optional.ofNullable(map.get(command));
        }
    }

    /** handles event loop and css processing */
    private static final class Context {
        private final Scanner console = new Scanner(System.in);

        protected StringBuilder buffer = new StringBuilder(512);
        protected StyleWriter writer = StyleWriter.inline();
        protected Prefixer prefixer;
        protected boolean terminate;

        public void start() throws Exception {
            buffer = new StringBuilder(512);
            terminate = false;

            while (true) {
                String next = console.nextLine();

                if (next.startsWith("!")) {
                    Optional<Command> command = Command.get(next);
                    if (command.isPresent()) {
                        command.get().execute(this);
                        if (terminate) break;
                    } else {
                        System.out.println(Colors.grey("Unknown command '" + next + "'\n"));
                    }
                } else {
                    buffer.append(next).append("\n");
                }
            }
        }

        public String process() {
            String input = buffer.toString().trim();
            if (input.isEmpty()) return "";

            Omakase.Request request = Omakase.source(input);
            if (prefixer != null) {
                request.use(prefixer);
            }
            request.use(writer);
            request.use(new UnquotedIEFilterPlugin());
            request.use(new StandardValidation());
            request.use(new DefaultErrorManager().rethrow(false));
            request.process();

            return writer.write();
        }
    }

    /** used by file editor commands */
    private static final class FileWatcher extends TimerTask {
        private static final String INPUT = "/*------------input------------*/";
        private static final String RESULT = "/*------------result-----------*/";

        private final File file;
        private final Context ctx;
        private long lastMod;

        @SuppressWarnings("deprecation")
        public FileWatcher(Context ctx) throws IOException {
            this.ctx = ctx;

            file = File.createTempFile("omakase-", ".css");
            file.deleteOnExit();

            String initial = INPUT + "\n" + ctx.buffer + "\n\n" + RESULT;
            Files.write(initial, file, UTF_8);

            lastMod = file.lastModified();
        }

        public File file() {
            return file;
        }

        @SuppressWarnings("deprecation")
        @Override
        public void run() {
            long newLastMod = file.lastModified();
            if (lastMod != newLastMod) {
                lastMod = newLastMod;
                try {
                    // grab and format the input from the editor
                    String input = Files.toString(file, UTF_8);
                    int index = input.indexOf(RESULT);
                    if (index > -1) {
                        input = input.substring(0, input.indexOf(RESULT, index));
                    }
                    input = input.trim();
                    ctx.buffer = new StringBuilder(input);

                    // process the css and place output into the editor
                    String output;
                    try {
                        output = ctx.process();
                    } catch (ProblemSummaryException e) {
                        output = e.getMessage();
                    }

                    output = input + "\n\n" + RESULT + "\n" + output;
                    Files.write(output, file, UTF_8);
                    System.out.println(Colors.grey("File updated\n"));

                    // reset the buffer
                    ctx.buffer = new StringBuilder(512);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
