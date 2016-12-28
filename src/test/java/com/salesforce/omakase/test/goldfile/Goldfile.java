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

package com.salesforce.omakase.test.goldfile;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.salesforce.omakase.Omakase;
import com.salesforce.omakase.plugin.Plugin;
import com.salesforce.omakase.plugin.core.AutoRefine;
import com.salesforce.omakase.plugin.core.StandardValidation;
import com.salesforce.omakase.plugin.syntax.UnquotedIEFilterPlugin;
import com.salesforce.omakase.writer.StyleWriter;
import com.salesforce.omakase.writer.WriterMode;

import java.io.File;
import java.io.IOException;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Utility for executing goldfile tests.
 * <p>
 * A goldfile test matches an expected input CSS source file to an expected output file.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public final class Goldfile {
    private Goldfile() {}

    /**
     * Tests that parsing a particular source CSS file matches the expected output result.
     *
     * @param name
     *     The name of the file (excluding the path and extension).
     * @param writer
     *     The StyleWriter setup with the right compression level
     * @param autoRefine
     *     Whether we should add an {@link AutoRefine}.
     *
     * @throws IOException
     *     If there is a problem with teh files.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void test(String name, StyleWriter writer, boolean autoRefine, Iterable<Plugin> plugins) throws IOException {
        // grab the source to parse
        File sourceFile = sourceFile(name);
        assertThat(sourceFile.exists()).describedAs("Source file not found: " + sourceFile.getPath()).isTrue();
        String source = fileContents(sourceFile);

        // grab the expected parse results
        File expectedFile = resultsFile(name, autoRefine, writer.mode());
        String expected = expectedFile.exists() ? fileContents(expectedFile) : null;

        // parsing setup

        Omakase.Request request = Omakase.source(source);
        request.use(writer);
        request.use(new StandardValidation(false));
        request.use(new UnquotedIEFilterPlugin());
        request.use(plugins);

        if (autoRefine) {
           request.use(AutoRefine.everything());
        }

        // do the parsing and get results
        request.process();
        String result = writer.write();

        // test that the results match the expected file contents
        if (!result.equals(expected)) {
            // delete the existing file and create it new so we can rewrite to it
            if (expectedFile.exists()) expectedFile.delete();
            Files.createParentDirs(expectedFile);
            expectedFile.createNewFile();

            // write the contents to the new file
            Files.write(result, expectedFile, Charsets.UTF_8);
        }

        String msg = String.format("Goldfile did not match expected value." +
            " The expected file has been (re)written with the actual results to %s", expectedFile.getPath());
        assertThat(result).describedAs(msg).isEqualTo(expected);
    }

    /**
     * Gets the contents of a {@link File} as a string.
     *
     * @param file
     *     The file.
     *
     * @return The contents of the file.
     *
     * @throws IOException
     *     If something goes wrong reading the file.
     */
    public static String fileContents(File file) throws IOException {
        return Files.toString(file, Charsets.UTF_8);
    }

    /** Gets the goldfile "sources" folder. */
    public static File sourcesFolder() {
        return new File(Goldfile.class.getResource("/goldfile/sources").getFile());
    }

    /** Gets the goldfile "results" folder. */
    public static File resultsFolder() {
        return new File(Goldfile.class.getResource("/goldfile/results").getFile());
    }

    /** Gets the goldfile "results" folder, but in the source directory instead of target directory. */
    public static File resultsFolderInSource() {
        return new File(resultsFolder().getPath().replace("target/test-classes", "src/test/resources"));
    }

    /**
     * Gets a source CSS file.
     *
     * @param name
     *     The name of the file (excluding the path and extension).
     *
     * @return The file.
     */
    public static File sourceFile(String name) {
        return new File(sourcesFolder(), name + ".css");
    }

    /**
     * Gets a results CSS file (within the "src/test/resources" directory, not target!
     *
     * @param name
     *     The name of the file (excluding the path and extension).
     * @param autoRefine
     *     Whether we should add an {@link AutoRefine}.
     * @param mode
     *     The compression level.
     *
     * @return The file.
     */
    public static File resultsFile(String name, boolean autoRefine, WriterMode mode) {
        String path = name;
        path += autoRefine ? "_refined" : "_unrefined";
        switch (mode) {
        case VERBOSE:
            path += "_verbose";
            break;
        case INLINE:
            path += "_inline";
            break;
        case COMPRESSED:
            path += "_compressed";
            break;
        }

        return new File(resultsFolderInSource(), path + ".css");
    }
}
