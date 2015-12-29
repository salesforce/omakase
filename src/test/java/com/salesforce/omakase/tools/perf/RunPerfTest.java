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

package com.salesforce.omakase.tools.perf;

import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Performance testing of this parser (more extensive tests + csv writer is in perf-test branch).
 *
 * @author nmcwilliams
 */
@SuppressWarnings("ALL")
public final class RunPerfTest {

    /** set of possible parsers configurations to test */
    private static final Set<PerfTest> PARSERS = ImmutableSet.<PerfTest>of(
        new OmakasePerf()
    );

    /** LOC variations (multiplication) */
    private static final List<Integer> FACTORS = ImmutableList.of(1, 2, 4, 6, 8, 10, 12, 16, 18, 20, 22, 24, 26,
        28, 30, 35, 40, 45, 50, 60, 70, 80, 100, 120, 140, 200);

    public static void printUsage() {
        System.out.println("Help With Running Performance Tests:\n");

        System.out.println("The perf test args take the format of <parser> <mode> [options...]");

        System.out.println("\nAvailable parsers:");
        for (PerfTest p : PARSERS) {
            System.out.println(p.name());
        }

        System.out.println("\nAvailable modes:");
        for (Mode m : Mode.values()) {
            System.out.println(CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_HYPHEN, m.name()));
        }

        System.out.println("\nAvailable options:");
        System.out.println("no-prime (don't prime the jvm before testing)");

        System.out.println("\nExamples:");
        System.out.println("omakase -p omakase light");
        System.out.println("omakase -p omakase normal");
        System.out.println("omakase -p omakase heavy");
        System.out.println("omakase -p omakase prefix-heavy");
        System.out.println("omakase -p omakase prefix-heavy no-prime");
    }

    /** main method with setup */
    public static void run(List<String> args) {
        // find parser
        PerfTest parser = null;

        String parserArg = args.get(0);
        if (parserArg == null) {
            System.err.println("missing parser option (must be first)");
            System.exit(1);
        }

        for (PerfTest p : PARSERS) {
            if (p.name().startsWith(parserArg)) {
                parser = p;
                break;
            }
        }

        if (parser == null) {
            System.err.println("unable to find parser '" + parserArg + "'");
            System.exit(1);
        }

        // find mode
        String modeArg = args.get(1);
        if (parserArg == null) {
            System.err.println("missing mode option (must be second)");
            System.exit(1);
        }

        modeArg = CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_UNDERSCORE, modeArg.toUpperCase());
        Mode mode = Mode.valueOf(modeArg);

        if (mode == null) {
            System.err.println("unable to find mode '" + modeArg + "'");
            System.exit(1);
        }

        // other options
        boolean prime = !args.contains("no-prime");

        System.out.printf("\nRunning tests for %s - %s:\n", parser.name(), args.get(1));
        test(parser, mode, prime);
        System.out.println("\ndone");
    }

    /** prime the parser then test it with each specified factor */
    private static void test(PerfTest parser, Mode mode, boolean prime) {
        // prime
        if (prime) {
            System.out.println("\nPriming...\n");
            for (int i = 0; i < 500; i++) parser.parse(mode);
        }

        // compound the source according to each factor then time how long it takes to parse
        for (Integer factor : FACTORS) {
            String input = "";
            for (int i = 0; i < factor; i++) input = input + "\n" + mode.source();

            int loc = input.trim().split("\n").length;

            time(loc, parser, mode, input);
        }
    }

    /** prints out the parse time for the given source */
    public static void time(int loc, PerfTest parser, Mode mode, String input) {
        // do the parsing
        List<Long> parseTimes = Lists.newArrayList();
        for (int i = 0; i < 5; i++) {
            long start = System.nanoTime();
            parser.parse(mode, input);
            long end = System.nanoTime();
            parseTimes.add(TimeUnit.NANOSECONDS.toMillis(end - start));
        }

        // calculate the average
        long total = 0;
        for (Long time : parseTimes) {
            total += time;
        }

        // why min? while it's true that the average real world time is going to be higher than the minimum run,
        // when it comes to measuring performance it's more efficient to check the fastest measured time. Various
        // system variances can skew the average and thus it won't always reflect when code changes cause a small but
        // measurable performance impact. Checking the fastest time that the code can run is going to give a more accurate
        // reflection of the impact of a code change, which is the main purpose of this perf test.
        long min = Collections.min(parseTimes);

        System.out.println(String.format("%-12s %-15s %s", min + "ms", "(" + loc + " loc)", parseTimes));
    }
}
