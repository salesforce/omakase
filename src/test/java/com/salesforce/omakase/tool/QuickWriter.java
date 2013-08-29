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
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class QuickWriter {
    /**
     * TODO Description
     * 
     * @param input
     *            TODO
     * @throws IOException
     *             TODO
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
