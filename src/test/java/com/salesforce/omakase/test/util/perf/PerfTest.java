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

package com.salesforce.omakase.test.util.perf;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Performance testing of this parser (more extensive tests + csv writer is in perf-test branch).
 *
 * @author nmcwilliams
 */
@SuppressWarnings("ALL")
public final class PerfTest {

    // MODES

    /** set of possible parsers configurations to test */
    private static final Set<PerfTestParser> PARSERS = ImmutableSet.of(
        new OmakaseThin(),
        new OmakaseFull(),
        new OmakaseFullPrefixer()
    );
    // OPTIONS

    /** whether to prime the code before measuring performance */
    private static final boolean PRIME = true;

    /** run tests for multiple LOC variations */
    private static final boolean USE_FACTORS = true;

    /** LOC variations (multiplication) */
    private static final List<Integer> MULTI_FACTORS = ImmutableList.of(1, 2, 4, 6, 8, 10, 12, 16, 18, 20, 22, 24, 26,
        28, 30, 35, 40, 45, 50, 60, 70, 80, 100, 120, 140, 200);

    /** Single LOC variation (multiplication) */
    private static final List<Integer> SINGLE_FACTOR = ImmutableList.of(200);

    /** prints individual parse times next to the average */
    private static final boolean SHOW_INDIVIDUAL = true;

    /** default input string to use if none specified */
    private static final String DEFAULT_INPUT = "simple";

    // END OPTIONS

    /** main method with setup */
    public static void main(String[] args) {
        PerfTestParser parser = Iterables.get(PARSERS, 0);
        String inputKey = DEFAULT_INPUT;
        boolean useFactors = USE_FACTORS;

        // check for args specifying the mode and input to use
        if (args.length == 1) {
            // "-" separated, mode then input. input is optional
            Iterable<String> split = Splitter.on("-").trimResults().split(args[0]);

            // mode
            for (PerfTestParser p : PARSERS) {
                if (p.code().startsWith(Iterables.get(split, 0, null))) {
                    parser = p;
                    break;
                }
            }

            // input
            inputKey = Iterables.get(split, 1, inputKey);

            // disable factors
            if ("single".equals(Iterables.get(split, 2, null))) {
                useFactors = false;
            }
        }

        System.out.printf("\nRunning tests for %s - %s %s:\n", parser.name(), inputKey, env());
        test(parser, PerfTestInput.MAP.get(inputKey), useFactors);
        System.out.println("\ndone " + env());
    }

    /** prints memory usage */
    private static String env() {
        int mb = 1024 * 1024;
        Runtime runtime = Runtime.getRuntime();

        long max = runtime.maxMemory() / mb;
        long avail = runtime.totalMemory() / mb;
        long used = (runtime.totalMemory() - runtime.freeMemory()) / mb;
        long free = runtime.freeMemory() / mb;

        return "(used:" + used + " free:" + free + " max:" + max + " avail:" + avail + ")";
    }

    /** prime the parser then test it with each specified factor */
    private static void test(PerfTestParser parser, String input, boolean useFactors) {
        final List<Integer> factors = useFactors ? MULTI_FACTORS : SINGLE_FACTOR;

        // prime
        if (PRIME) {
            if (useFactors) System.out.println("\nPriming...\n");
            for (int i = 0; i < 200; i++) parser.parse(input);
        }

        // compound the source according to each factor and time it
        for (Integer factor : factors) {
            String actual = "";
            for (int i = 0; i < factor; i++) actual = actual + "\n" + input;

            int loc = actual.trim().split("\n").length;
            time(loc, parser, actual);
        }
    }

    /** prints out the average parse time with the given source */
    public static void time(int loc, PerfTestParser parser, String input) {
        // do the parsing
        List<Long> parseTimes = Lists.newArrayList();
        for (int i = 0; i < 5; i++) {
            long start = System.nanoTime();
            parser.parse(input);
            long end = System.nanoTime();
            parseTimes.add(TimeUnit.NANOSECONDS.toMillis(end - start));
        }

        // calculate the average
        long total = 0;
        for (Long time : parseTimes) {
            total += time;
        }

        // output
        long avg = total / parseTimes.size();
        System.out.println(String.format("%-12s %-15s %s", avg + "ms", "(" + loc + " loc)", (SHOW_INDIVIDUAL ? parseTimes : "")));
    }
}
