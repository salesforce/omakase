/**
 * ADD LICENSE
 */
package com.salesforce.omakase.util;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.salesforce.omakase.Omakase;
import com.salesforce.omakase.Omakase.Request;
import com.salesforce.omakase.plugin.basic.AutoRefiner;
import com.salesforce.omakase.plugin.basic.SyntaxTree;

/**
 * Performance testing of this parser (more extensive tests + csv writer is in perf-test branch).
 *
 * @author nmcwilliams
 */
@SuppressWarnings("javadoc")
public class PerfTestLight {
    // OPTIONS

    /** prime the code before measuring performance */
    private static final boolean PRIME = true;

    /** run tests for multiple LOC variations */
    private static final boolean USE_FACTORS = true;

    /** output statistics */
    private static final boolean CONSOLE = true;

    /** LOC variations (multiplication) */
    private static final List<Integer> MULTI_FACTORS = ImmutableList.of(1, 2, 4, 6, 8, 10, 12, 16, 18, 20, 22, 24, 26,
        28, 30, 35, 40, 45, 50, 60, 70, 80, 100, 120, 140, 200);

    /** Single LOC variation (multiplication) */
    private static final List<Integer> SINGLE_FACTOR = ImmutableList.of(200);

    // END OPTIONS

    enum Mode {
        OMAKASE,
        OMAKASE_FULL
    }

    private static final Pattern commentPattern = Pattern.compile("/\\*[^*]*\\*+([^/*][^*]*\\*+)*/");

    /** main method with setup */
    public static void main(String[] args) throws IOException {
        env(true);
        run();
        env(true);
    }

    public static void env(boolean full) {
        int mb = 1024 * 1024;
        Runtime runtime = Runtime.getRuntime();

        print("##### Heap utilization statistics [MB] #####");
        if (full) print("Max Memory:" + runtime.maxMemory() / mb);
        if (full) print("Availiable Memory:" + runtime.totalMemory() / mb);
        print("Used Memory:" + (runtime.totalMemory() - runtime.freeMemory()) / mb);
        print("Free Memory:" + runtime.freeMemory() / mb);
    }

    /**
     * test execution
     *
     * @throws IOException
     */
    public static void run() throws IOException {
        final List<Integer> factors = USE_FACTORS ? MULTI_FACTORS : SINGLE_FACTOR;
        final String original = CSS;

        // prime
        if (PRIME) {
            print("\nPriming");
            for (int i = 0; i < 200; i++) {
                parse(Mode.OMAKASE, original);
                parse(Mode.OMAKASE_FULL, original);
            }
        }

        // run measured tests
        for (Integer factor : factors) {
            // prepare source
            String actual = "";
            for (int i = 0; i < factor; i++) {
                actual = actual + "\n" + original;
            }

            // calculate LOC
            int loc = commentPattern.matcher(actual).replaceAll("").trim().split("\n").length;
            print("\nLOC: " + loc);

            PerfRun perfRun = new PerfRun();
            perfRun.linesOfCode = loc;

            // take an average of 7 runs
            final int runs = 7;

            // omakase thin
            List<Long> omakaseParseTimes = Lists.newArrayListWithCapacity(runs);
            for (int i = 0; i < runs; i++) {
                long start = System.currentTimeMillis();
                parse(Mode.OMAKASE, actual);
                long end = System.currentTimeMillis();
                omakaseParseTimes.add(end - start);
            }
            long omakaseTotal = 0;
            for (Long time : omakaseParseTimes) {
                omakaseTotal += time;
            }
            long omakaseParseAvg = omakaseTotal / omakaseParseTimes.size();
            print("omakase thin: " + omakaseParseAvg + "ms");
            perfRun.omakaseThin = omakaseParseAvg;

            // omakase full
            List<Long> omakaseParseTimes2 = Lists.newArrayListWithCapacity(runs);
            for (int i = 0; i < runs; i++) {
                long start = System.currentTimeMillis();
                parse(Mode.OMAKASE_FULL, actual);
                long end = System.currentTimeMillis();
                omakaseParseTimes2.add(end - start);
            }
            long omakaseTotal2 = 0;
            for (Long time : omakaseParseTimes2) {
                omakaseTotal2 += time;
            }
            long omakaseParseAvg2 = omakaseTotal2 / omakaseParseTimes2.size();
            print("omakase full: " + omakaseParseAvg2 + "ms");
            perfRun.omakaseFull = omakaseParseAvg2;

        }

        print("\ndone");
    }

    public static void parse(Mode mode, String src) {
        if (mode == Mode.OMAKASE) {
            Omakase.source(src).process();
        } else if (mode == Mode.OMAKASE_FULL) {
            Request request = Omakase.source(src);

            request.add(new SyntaxTree());

            AutoRefiner autoRefiner = new AutoRefiner().all();
            request.add(autoRefiner);

            request.process();
        }
    }

    private static void print(String string) {
        if (CONSOLE) System.out.println(string);
    }

    public static final class PerfRun {
        long linesOfCode;
        long phloc;
        long gss;
        long omakaseFull;
        long omakaseThin;

        public long getLinesOfCode() {
            return linesOfCode;
        }

        public long getPhloc() {
            return phloc;
        }

        public long getGSS() {
            return gss;
        }

        public long getOmakaseFull() {
            return omakaseFull;
        }

        public long getOmakaseThin() {
            return omakaseThin;
        }
    }

    private static final String CSS = ".uiButton{\n" +
            "    display:inline-block;\n" +
            "    cursor:pointer;\n" +
            "}\n" +
            "\n" +
            ".uiButton .label{\n" +
            "    display:block;\n" +
            "}\n" +
            "\n" +
            ".uiButton.default{\n" +
            "    font-weight: bold;\n" +
            "    font-size: .9em;\n" +
            "    margin: 2px 3px;\n" +
            "    padding: 4px 6px;\n" +
            "    text-decoration:none;\n" +
            "    text-align:center;\n" +
            "    border-radius:4px;\n" +
            "    border:0;\n" +
            "    border-top:1px solid rgba(255,255,255,.45);\n" +
            "    background:#DDDFE1;\n" +
            "    background:-webkit-gradient(linear, 0% 0%, 0% 100%, from(#F8F8F9), to(#DDDFE1));\n" +
            "    background:-webkit-linear-gradient(#F8F8F9,#DDDFE1);\n" +
            "    background:-moz-linear-gradient(#F8F8F9,#DDDFE1);\n" +
            "    background:linear-gradient(#F8F8F9,#DDDFE1);\n" +
            "    -webkit-box-shadow:0 1px 3px rgba(0, 0, 0, 0.7),0 1px 0 rgba(0, 0, 0, 0.3);\n" +
            "    box-shadow:0 1px 3px rgba(0, 0, 0, 0.7),0 1px 0 rgba(0, 0, 0, 0.3) ;\n" +
            "    text-shadow:0 1px 1px #FFF; \n" +
            "}\n" +
            "\n" +
            ".uiButton.default:hover,\n" +
            ".uiButton.default:focus{\n" +
            "    background:#757D8A;\n" +
            "    background:#757D8A -webkit-gradient(linear, 0% 0%, 0% 100%, from(#7F8792), to(#535B68));\n" +
            "    background:#757D8A -webkit-linear-gradient(#7F8792,#535B68);\n" +
            "    background:#757D8A -moz-linear-gradient(#7F8792,#535B68);\n" +
            "    background:#757D8A linear-gradient(#7F8792,#535B68);\n" +
            "    text-shadow:0 -1px 1px rgba(0, 0, 0, 0.5);\n" +
            "}\n" +
            ".uiButton.default .label{\n" +
            "    white-space:nowrap;\n" +
            "    color: #3A3D42;\n" +
            "}\n" +
            ".uiButton.default:hover .label,\n" +
            ".uiButton.default:focus .label{\n" +
            "    color: #FFF;\n" +
            "}\n" +
            ".uiButton.default:disabled{\n" +
            "    cursor:default;\n" +
            "    background:#B9B9B9;\n" +
            "    -webkit-box-shadow:none;\n" +
            "    box-shadow:none;\n" +
            "    text-shadow:none;\n" +
            "}\n" +
            ".uiButton.default:disabled .label{\n" +
            "    color:#888;\n" +
            "}\n" +
            ".uiButton.default:disabled .label:hover{\n" +
            "    color:#888;\n" +
            "}";
}
