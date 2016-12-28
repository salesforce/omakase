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

package com.salesforce.omakase.test.util;

import com.salesforce.omakase.Omakase;
import com.salesforce.omakase.plugin.core.AutoRefine;
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
            .use(AutoRefine.everything())
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
