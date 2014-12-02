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

package com.salesforce.omakase.test.util;

import com.salesforce.omakase.Omakase;
import com.salesforce.omakase.plugin.basic.AutoRefiner;
import com.salesforce.omakase.writer.StyleWriter;
import com.salesforce.omakase.writer.WriterMode;

import java.io.IOException;

/**
 * Utility for visually checking the results of writing processed CSS, in every available mode, both when refined and unrefined.
 *
 * @author nmcwilliams
 */
public final class QuickWriter {
    private QuickWriter() {}

    /**
     * Parses and writes out the given input in every available mode, both refined and unrefined.
     *
     * @param input
     *     The CSS source code.
     *
     * @throws IOException
     *     If an I/O error occurs.
     */
    public static void writeAllModes(CharSequence input) throws IOException {
        StyleWriter writer = new StyleWriter();
        Omakase.source(input)
            .use(writer)
            .process();

        writer.mode(WriterMode.VERBOSE);
        System.out.println("VERBOSE (unrefined)");
        writer.writeTo(System.out);

        System.out.println();
        System.out.println();

        writer.mode(WriterMode.INLINE);
        System.out.println("INLINE (unrefined)");
        writer.writeTo(System.out);

        System.out.println();
        System.out.println();

        writer.mode(WriterMode.COMPRESSED);
        System.out.println("COMPRESSED (unrefined)");
        writer.writeTo(System.out);

        System.out.println("\n");

        writer = new StyleWriter();
        Omakase.source(input)
            .use(new AutoRefiner().all())
            .use(writer)
            .process();

        writer.mode(WriterMode.VERBOSE);
        System.out.println("VERBOSE (refined)");
        writer.writeTo(System.out);

        System.out.println();
        System.out.println();

        writer.mode(WriterMode.INLINE);
        System.out.println("INLINE (refined)");
        writer.writeTo(System.out);

        System.out.println();
        System.out.println();

        writer.mode(WriterMode.COMPRESSED);
        System.out.println("COMPRESSED (refined)");
        writer.writeTo(System.out);
    }
}
