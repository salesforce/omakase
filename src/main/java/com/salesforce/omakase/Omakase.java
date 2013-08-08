/**
 * ADD LICENSE
 */
package com.salesforce.omakase;

import javax.annotation.concurrent.NotThreadSafe;

import com.salesforce.omakase.parser.Stream;
import com.salesforce.omakase.parser.StylesheetParser;
import com.salesforce.omakase.plugin.Plugin;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
@NotThreadSafe
public final class Omakase {
    private Omakase() {}

    /**
     * TODO Description
     * 
     * @param source
     *            TODO
     * @return TODO
     */
    public static Omakase.Request source(CharSequence source) {
        return new Request(source);
    }

    /**
     * TODO Description
     */
    public static final class Request {
        private final Stream stream;
        private final Context context = new Context();

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
            new StylesheetParser().parse(stream, context);
            return context;
        }
    }
}
