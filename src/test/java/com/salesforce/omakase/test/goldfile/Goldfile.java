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

package com.salesforce.omakase.test.goldfile;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.salesforce.omakase.Omakase;
import com.salesforce.omakase.plugin.basic.AutoRefiner;
import com.salesforce.omakase.plugin.basic.SyntaxTree;
import com.salesforce.omakase.plugin.other.UnquotedIEFilterPlugin;
import com.salesforce.omakase.plugin.validator.StandardValidation;
import com.salesforce.omakase.writer.StyleWriter;
import com.salesforce.omakase.writer.WriterMode;

import java.io.File;
import java.io.IOException;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Utility for executing goldfile tests.
 * <p/>
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
     * @param mode
     *     The compression level.
     * @param autoRefine
     *     Whether we should add an {@link AutoRefiner}.
     *
     * @throws IOException
     *     If there is a problem with teh files.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void test(String name, WriterMode mode, boolean autoRefine) throws IOException {
        // grab the source to parse
        File sourceFile = sourceFile(name);
        assertThat(sourceFile.exists()).describedAs("Source file not found: " + sourceFile.getPath()).isTrue();
        String source = fileContents(sourceFile);

        // grab the expected parse results
        File expectedFile = resultsFile(name, autoRefine, mode);
        String expected = expectedFile.exists() ? fileContents(expectedFile) : null;

        // parsing setup
        StyleWriter writer = new StyleWriter().mode(mode);
        AutoRefiner refiner = new AutoRefiner().all();
        SyntaxTree tree = new SyntaxTree();

        Omakase.Request request = Omakase.source(source);
        request.add(tree);
        request.add(writer);
        request.add(new StandardValidation(false));
        request.add(new UnquotedIEFilterPlugin());
        if (autoRefine) request.add(refiner);

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
            "The expected file has been (re)written with the actual results to %s", expectedFile.getPath());
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
     *     Whether we should add an {@link AutoRefiner}.
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
