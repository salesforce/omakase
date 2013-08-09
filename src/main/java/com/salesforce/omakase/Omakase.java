/**
 * ADD LICENSE
 */
package com.salesforce.omakase;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.concurrent.NotThreadSafe;

import com.salesforce.omakase.parser.Parser;
import com.salesforce.omakase.parser.Stream;
import com.salesforce.omakase.parser.StylesheetParser;
import com.salesforce.omakase.plugin.Plugin;

/**
 * TODO Description
 * 
 * <p> This library is <em>not</em> thread-safe.
 * 
 * @author nmcwilliams
 */
@NotThreadSafe
public final class Omakase {
    /** do not construct */
    private Omakase() {}

    /**
     * TODO Description
     * 
     * @param source
     *            TODO
     * @return TODO
     */
    public static Omakase.Request source(CharSequence source) {
        checkNotNull(source, "source cannot be null");
        return new Request(source);
    }

    /**
     * TODO Description
     */
    public static final class Request {
        private static final Parser parser = new StylesheetParser();
        private final Context context = new Context();
        private final Stream stream;

        Request(CharSequence source) {
            this.stream = new Stream(source.toString());
        }

        /**
         * TODO Description
         * 
         * @param plugins
         *            TODO
         * @return TODO
         */
        public Request request(Plugin... plugins) {
            context.plugins(plugins);
            return this;
        }

        /**
         * TODO Description
         * 
         * @return TODO
         */
        public Context process() {
            parser.parse(stream, context);
            return context;
        }
    }
}
