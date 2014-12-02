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

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;
import com.salesforce.omakase.Omakase;
import com.salesforce.omakase.data.Browser;
import com.salesforce.omakase.error.FatalException;
import com.salesforce.omakase.plugin.basic.Prefixer;
import com.salesforce.omakase.plugin.other.UnquotedIEFilterPlugin;
import com.salesforce.omakase.plugin.validator.StandardValidation;
import com.salesforce.omakase.writer.StyleWriter;
import com.salesforce.omakase.writer.WriterMode;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.collect.ImmutableMap.Builder;

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
                } catch (FatalException e) {
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
                System.out.print(Colors.grey("continous mode is ") + Colors.red(on ? "on" : "off"));
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

        SUBLIME("!subl", "to use the sublime text editor (subl)") {
            @Override
            void execute(final Context ctx) throws IOException, InterruptedException {
                System.out.println("Sublime Text edit mode. File will be refreshed with results on save.\n");

                FileWatcher watcher = new FileWatcher(ctx);
                Timer timer = new Timer(true);
                timer.schedule(watcher, 0, 50);

                Runtime.getRuntime().exec("subl " + watcher.file() + ":2");
            }
        },

        MATE("!mate", "to use the textmate editor (mate)") {
            @Override
            void execute(final Context ctx) throws IOException, InterruptedException {
                System.out.println("Textmate edit mode. File will be refreshed with results on save.\n");

                FileWatcher watcher = new FileWatcher(ctx);
                Timer timer = new Timer(true);
                timer.schedule(watcher, 0, 50);

                Runtime.getRuntime().exec("mate " + watcher.file() + " --line 2");
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
        @SuppressWarnings("NonFinalFieldInEnum") boolean on;

        Command(String key, String description) {
            this.key = key;
            this.description = description;
        }

        abstract void execute(Context ctx) throws Exception;

        public static Optional<Command> get(String command) {
            return Optional.fromNullable(map.get(command));
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
            request.use(new StandardValidation());
            request.use(new UnquotedIEFilterPlugin());
            request.use(writer);
            if (prefixer != null) request.use(prefixer);
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
                    String output = "";
                    try {
                        output = ctx.process();
                    } catch (FatalException e) {
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
