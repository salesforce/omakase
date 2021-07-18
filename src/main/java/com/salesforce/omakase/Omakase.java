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

package com.salesforce.omakase;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.Lists;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.emitter.SubscriptionException;
import com.salesforce.omakase.error.DefaultErrorManager;
import com.salesforce.omakase.error.ErrorManager;
import com.salesforce.omakase.error.ProblemSummaryException;
import com.salesforce.omakase.parser.Grammar;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.plugin.Plugin;

/**
 * Main entry point for the Omakase CSS Parser.
 * <p>
 * Example:
 * <pre><code>
 *     StyleWriter verbose = StyleWriter.verbose();
 *     StandardValidation validation = new StandardValidation();
 *     Omakase.source(input).use(verbose).use(validation).process();
 *     verbose.writeTo(System.out);
 * </code></pre>
 * Example:
 * <pre><code>
 *     StandardValidation validation = new StandardValidation();
 *     StyleWriter inline = StyleWriter.inline();
 *
 *     Omakase.source(input)
 *         .use(new Plugin() {
 *            {@literal @}Rework
 *             public void rework(Selector selector) {
 *                 ...
 *             }
 *         })
 *         .use(new MyCustomPlugin())
 *         .use(inline)
 *         .use(validation)
 *         .process();
 *
 *     inline.writeTo(System.out);
 * </code></pre>
 * Unless you are specifically optimizing for performance you should usually add {@link StandardValidation}, and it should
 * be added after any custom plugins.
 * <p>
 * For more usage information, see the readme.md file. Please note that the parser does not currently support the following:
 * <p>
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
     * <p>
     * Omakase calls begin with this method, you then usually add some plugins and then finally end with {@code .process()}.
     * <p>
     * Unless you are specifically optimizing for performance you should usually add {@link StandardValidation}, and it should
     * be added after any custom plugins.
     *
     * @param source
     *     The CSS source code.
     *
     * @return The processed request (see {@link Request}).
     */
    public static Omakase.Request source(CharSequence source) {
        checkNotNull(source, "source cannot be null");
        return new Request(source);
    }

    /**
     * Represents a CSS parsing operation.
     * <p>
     * This object allows you to add plugins in order to specify the validation, rework, etc... performed on the processed code.
     * See {@link Plugin} for more information.
     * <p>
     * Use {@link #use(ErrorManager)} to specify a custom error manager. Otherwise {@link DefaultErrorManager} is used by
     * default.
     */
    public static final class Request {
        private final Context context;
        private final Source source;

        private ErrorManager em;

        Request(CharSequence source) {
            this.context = new Context();
            this.source = new Source(source.toString());
            this.em = new DefaultErrorManager();
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
         * Specifies a custom error manager to use. If not specified, {@link DefaultErrorManager} is used by default.
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
         * Processes the CSS source code, invoking registered plugins as applicable.
         * <p>
         * It's expected that you call this method at most once per instance. To process different source code, or to reprocess
         * the same source code under different conditions or plugins, start new with {@link Omakase#source(CharSequence)}. It's
         * perfectly acceptable to reprocess the result of a previous parsing operation (e.g., using the output from a {@link
         * StyleWriter}).
         *
         * @return The {@link PluginRegistry} containing all registered plugins. This allows you to retrieve plugins if applicable
         * for further processing or information retrieval.
         */
        public PluginRegistry process() {
            try {
                Grammar grammar = context.beforeParsing(this.em);
                grammar.parser().stylesheetParser().parse(source, grammar, context.broadcaster());
                context.afterParsing();
            } catch (ParserException e) {
                em.report(e);
            } catch (SubscriptionException e) {
                em.report(e);
            }

            if (em.autoSummarize() && em.hasErrors()) {
                throw new ProblemSummaryException(em.summarize());
            }

            return context;
        }
    }
}
