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

package com.salesforce.omakase;

import com.google.common.collect.Lists;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.error.ErrorLevel;
import com.salesforce.omakase.error.ErrorManager;
import com.salesforce.omakase.error.ThrowingErrorManager;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.ParserFactory;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.parser.token.TokenFactory;
import com.salesforce.omakase.plugin.Plugin;
import com.salesforce.omakase.writer.StyleWriter;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Main entry point for the Omakase CSS Parser.
 * <p/>
 * Example:
 * <code><pre>
 *     StandardValidation validation = new StandardValidation();
 *     StyleWriter verbose = StyleWriter.verbose();
 *     Omakase.source(input).request(validation).request(verbose).process();
 *     verbose.writeTo(System.out);
 * </pre></code>
 * Example:
 * <code><pre>
 *     StandardValidation validation = new StandardValidation();
 *     StyleWriter inline = StyleWriter.inline();
 * <p/>
 *     Omakase.source(input)
 *         .request(validation)
 *         .request(inline)
 *         .request(new Plugin() {
 *             {@code @}Rework
 *             public void rework(Selector selector) {
 *                 ...
 *             }
 *         })
 *         .process();
 * <p/>
 *     inline.writeTo(System.out);
 * </pre></code>
 * For more usage information, see the readme.md file, or check out (link). Please note that the parser does not currently support
 * the following:
 * <p/>
 * {@code cdo and cdc, escaping (in most cases)}
 *
 * @author nmcwilliams
 * @see Omakase.Request
 */
public final class Omakase {
    /** do not construct */
    private Omakase() {}

    /**
     * Specifies the CSS source to parse.
     * <p/>
     * Omakase calls begin with this method, you then usually add some plugins and then finally end with {@code .process()}
     *
     * @param source
     *     The CSS source code.
     *
     * @return The processed request (see {@link Request}). Usually you don't need this reference unless you can't inline/chain
     * the whole call).
     */
    public static Omakase.Request source(CharSequence source) {
        checkNotNull(source, "source cannot be null");
        return new Request(source);
    }

    /**
     * Represents a request to process CSS.
     * <p/>
     * This object allows you to add plugins in order to specify the validation, rework, etc... performed on the processed code.
     * See {@link Plugin} for more information.
     * <p/>
     * Use {@link #use(ErrorManager)} to specify a custom error manager. Otherwise {@link ThrowingErrorManager} is used by
     * default.
     */
    public static final class Request {
        private final Context context;
        private final Source source;

        private TokenFactory tokenFactory;
        private ErrorManager em;

        Request(CharSequence source) {
            this.context = new Context();
            this.source = new Source(source.toString());
            this.em = new ThrowingErrorManager();
        }

        /**
         * Registers a plugin to process or utilize the parsed source code.
         *
         * @param plugins
         *     The plugin(s) to add.
         *
         * @return this, for chaining.
         */
        public Request use(Plugin... plugins) {
            return use(Lists.newArrayList(plugins));
        }

        /**
         * Registers a plugin to process or utilize the parsed source code.
         *
         * @param plugins
         *     The plugins to add.
         *
         * @return this, for chaining.
         */
        public Request use(Iterable<? extends Plugin> plugins) {
            context.register(plugins);
            return this;
        }

        /**
         * Specifies a custom error manager to use. If not specified, {@link ThrowingErrorManager} is used by default.
         *
         * @param em
         *     The error manager.
         *
         * @return this, for chaining.
         */
        public Request use(ErrorManager em) {
            this.em = checkNotNull(em, "the error manager cannot be null");
            return this;
        }

        /**
         * Specifies a {@link Broadcaster} to wrap around the default one. Doing this allows you to decorate the broadcast
         * functionality with your own behavior or information gathering.
         *
         * @param broadcaster
         *     Wrap the default broadcaster inside of this one.
         *
         * @return this, for chaining.
         */
        public Request broadcaster(Broadcaster broadcaster) {
            context.broadcaster(broadcaster);
            return this;
        }

        /**
         * Processes the CSS source code, invoking registered plugins as applicable. It's  expected that you call this method at
         * most once. To process difference source code, or to reprocess the same source code under different conditions or
         * plugins, start new with {@link Omakase#source(CharSequence)}. It's perfectly acceptable to reprocess the result of a
         * previous parsing operation (e.g., using the output from a {@link StyleWriter}).
         *
         * @return The {@link PluginRegistry} containing all registered plugins. This allows you to retrieve plugins if applicable
         * for further processing or information retrieval.
         */
        public PluginRegistry process() {
            try {
                context.errorManager(em);
                context.before();
                ParserFactory.stylesheetParser().parse(source, context, context.createRefiner());
                context.after();
            } catch (ParserException e) {
                em.report(ErrorLevel.FATAL, e);
            }

            return context;
        }
    }
}
