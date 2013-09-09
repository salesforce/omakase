/**
 * ADD LICENSE
 */
package com.salesforce.omakase.writer;

import com.salesforce.omakase.ast.Rule;

/**
 * The various levels of compression and minification for output.
 *
 * @author nmcwilliams
 */
public enum WriterMode {
    /** Outputs newlines, whitespace, etc... Usually for development mode or testing. */
    VERBOSE,

    /** Outputs each {@link Rule} on a single line, mostly minified. Useful for testing or debugging. */
    INLINE,

    /** Outputs fully minified and compressed code. Usually for production environments. */
    COMPRESSED
}
