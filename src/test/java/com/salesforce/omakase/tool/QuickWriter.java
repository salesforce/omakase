/**
 * ADD LICENSE
 */
package com.salesforce.omakase.tool;

import java.io.IOException;

import com.salesforce.omakase.Omakase;
import com.salesforce.omakase.plugin.basic.AutoRefiner;
import com.salesforce.omakase.writer.StyleWriter;
import com.salesforce.omakase.writer.WriterMode;

/**
 * Utility for visually checking the results of writing processed CSS, in every available mode, both when refined and
 * unrefined.
 * 
 * @author nmcwilliams
 */
public class QuickWriter {
    /**
     * Parses and writes out the given input in every available mode, both refined and unrefined.
     * 
     * @param input
     *            The CSS source code.
     * @throws IOException
     *             If an I/O error occurs.
     */
    public static void writeAllModes(CharSequence input) throws IOException {
        StyleWriter writer = new StyleWriter();
        Omakase.source(input)
            .request(writer)
            .process();

        writer.mode(WriterMode.VERBOSE);
        System.out.println("VERBOSE (unrefined)");
        writer.write(System.out);

        System.out.println();
        System.out.println();

        writer.mode(WriterMode.INLINE);
        System.out.println("INLINE (unrefined)");
        writer.write(System.out);

        System.out.println();
        System.out.println();

        writer.mode(WriterMode.COMPRESSED);
        System.out.println("COMPRESSED (unrefined)");
        writer.write(System.out);

        System.out.println("\n");

        writer = new StyleWriter();
        Omakase.source(input)
            .request(new AutoRefiner().all())
            .request(writer)
            .process();

        writer.mode(WriterMode.VERBOSE);
        System.out.println("VERBOSE (refined)");
        writer.write(System.out);

        System.out.println();
        System.out.println();

        writer.mode(WriterMode.INLINE);
        System.out.println("INLINE (refined)");
        writer.write(System.out);

        System.out.println();
        System.out.println();

        writer.mode(WriterMode.COMPRESSED);
        System.out.println("COMPRESSED (refined)");
        writer.write(System.out);
    }
}
