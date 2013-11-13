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
    // OPTIONS

    /** parsers that can be tests */
    private static final Set<PerfTestParser> PARSERS = ImmutableSet.of(
        new PerfTestOmakaseThin(),
        new PerfTestOmakaseFull()
    );

    /** prime the code before measuring performance */
    private static final boolean PRIME = true;

    /** run tests for multiple LOC variations */
    private static final boolean USE_FACTORS = true;

    /** LOC variations (multiplication) */
    private static final List<Integer> MULTI_FACTORS = ImmutableList.of(1, 2, 4, 6, 8, 10, 12, 16, 18, 20, 22, 24, 26,
        28, 30, 35, 40, 45, 50, 60, 70, 80, 100, 120, 140, 200);

    /** Single LOC variation (multiplication) */
    private static final List<Integer> SINGLE_FACTOR = ImmutableList.of(200);

    /** print all inputs to average */
    private static final boolean SHOW_ALL = true;

    // END OPTIONS

    /** main method with setup */
    public static void main(String[] args) {
        PerfTestParser parser = Iterables.get(PARSERS, 0);

        if (args.length == 1 && !args[0].isEmpty()) {
            for (PerfTestParser p : PARSERS) {
                if (p.code() == args[0].charAt(0)) parser = p;
            }
        }

        System.out.println("\nRunning tests for " + parser.name() + " " + env() + ":");
        test(parser, PerfTestInput.NORMAL);
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
    private static void test(PerfTestParser parser, String input) {
        final List<Integer> factors = USE_FACTORS ? MULTI_FACTORS : SINGLE_FACTOR;

        // prime
        if (PRIME) {
            System.out.println("\nPriming...\n");
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
        System.out.println(String.format("%-12s %-15s %s", avg + "ms", "(" + loc + " loc)", (SHOW_ALL ? parseTimes : "")));
    }
}
