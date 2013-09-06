/**
 * ADD LICENSE
 */
package com.salesforce.omakase;

import com.google.common.collect.Lists;
import com.salesforce.omakase.broadcaster.Broadcaster;
import com.salesforce.omakase.error.ErrorLevel;
import com.salesforce.omakase.error.ErrorManager;
import com.salesforce.omakase.error.ThrowingErrorManager;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.ParserFactory;
import com.salesforce.omakase.parser.Stream;
import com.salesforce.omakase.plugin.Plugin;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TESTME Main entry point for the Omakase CSS Parser.
 * <p/>
 * For usage information, see the readme.md file, or check out (link).
 * <p/>
 * Please note that the parser does not currently support the following:
 * <p/>
 * <ul> <li>@namespace</li>
 * <p/>
 * <p/>
 * <li>@import <li>@charset <li>@page <li>@font-face <li>@media (media queries) <li>cdo and cdc <li>escaping (in most cases)
 * <li>!important </ul> This library is <em>not</em> thread-safe. Don't even try it without reviewing and fixing every class in
 * this library.
 *
 * @author nmcwilliams
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
     * @return The processed request (see {@link Request}), usually you don't need this reference unless you can't inline/chain
     *         the whole call).
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
     * Use {@link #errorManager(ErrorManager)} to specify a custom error manager. Otherwise {@link ThrowingErrorManager} is used
     * by default.
     */
    public static final class Request {
        private final Context context;
        private final Stream stream;
        private ErrorManager em;

        Request(CharSequence source) {
            this.context = new Context();
            this.em = new ThrowingErrorManager();
            this.stream = new Stream(source.toString());
        }

        /**
         * Registers a plugin to process or utilize the parsed source code. This is equivalent to {@link #request(Plugin...)}.
         * Choose based on which reads better for your usage ("request" is preferred, however "add" is more fluent when you can't
         * inline the whole request and must make individual calls instead).
         *
         * @param plugins
         *     The plugins to add.
         *
         * @return this, for chaining.
         */
        public Request add(Plugin... plugins) {
            return request(plugins);
        }

        /**
         * Registers a plugin to process or utilize the parsed source code. This is equivalent to {@link #add(Plugin...)}. Choose
         * based on which reads better for your usage ("request" is preferred, however "add" is more fluent when you can't inline
         * the whole request and must make individual calls instead).
         *
         * @param plugins
         *     The plugins to add.
         *
         * @return this, for chaining.
         */
        public Request request(Plugin... plugins) {
            return request(Lists.newArrayList(plugins));
        }

        /**
         * Registers a plugin to process or utilize the parsed source code. This is equivalent to {@link #request(Iterable)}.
         * Choose based on which reads better for your usage ("request" is preferred, however "add" is more fluent when you can't
         * inline the whole request and must make individual calls instead).
         *
         * @param plugins
         *     The plugins to add.
         *
         * @return this, for chaining.
         */
        public Request add(Iterable<? extends Plugin> plugins) {
            return request(plugins);
        }

        /**
         * Registers a plugin to process or utilize the parsed source code. This method is equivalent to {@link #add(Iterable)}.
         * Choose based on which reads better for your usage ("request" is preferred, however "add" is more fluent when you can't
         * inline the whole request and must make individual calls instead).
         *
         * @param plugins
         *     The plugins to add.
         *
         * @return this, for chaining.
         */
        public Request request(Iterable<? extends Plugin> plugins) {
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
        public Request errorManager(ErrorManager em) {
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
         * Processes the CSS source code, invoking registered plugins as applicable. It's only expected that you call this method
         * at most once. To process difference source code, or to reprocess the same source code under different conditions or
         * plugins, start new with {@link Omakase#source(CharSequence)}.
         *
         * @return The {@link PluginRegistry} containing all registered plugins. This allows you to retrieve plugins if applicable
         *         for further processing or information retrieval.
         */
        public PluginRegistry process() {
            context.before(em);

            try {
                ParserFactory.stylesheetParser().parse(stream, context);
            } catch (ParserException e) {
                em.report(ErrorLevel.FATAL, e);
            }

            context.after();
            return context;
        }
    }
}
