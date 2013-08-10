/**
 * ADD LICENSE
 */
package com.salesforce.omakase;

import java.util.List;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.phloc.css.ECSSVersion;
import com.phloc.css.reader.CSSReader;

/**
 * Performance testing of this parser with others.
 * 
 * @author nmcwilliams
 */
@SuppressWarnings("javadoc")
public class PerfTest {
    // OPTIONS

    /** prime the code before measuring performance */
    private static final boolean PRIME = true;

    /** run tests for multiple LOC variations */
    private static final boolean USE_FACTORS = true;

    /** run phloc */
    private static final boolean PHLOC = true;

    /** output statistics */
    private static final boolean CONSOLE = true;

    /** LOC variations (multiplication) */
    private static final List<Integer> MULTI_FACTORS = ImmutableList.of(1, 2, 4, 6, 8, 10, 12, 16, 18, 20, 22, 24, 26,
        28, 30, 35, 40, 45, 50, 60, 70, 80, 100, 120, 140, 200);

    /** Single LOC variation (multiplication) */
    private static final List<Integer> SINGLE_FACTOR = ImmutableList.of(200);

    // END OPTIONS

    enum Mode {
        phloc,
        omakase
    }

    private static final Pattern commentPattern = Pattern.compile("\\/\\*[^*]*\\*+([^/*][^*]*\\*+)*\\/");

    /** main method with setup */
    public static void main(String[] args) {
        env();
        run();
    }

    public static void env() {
        int mb = 1024 * 1024;
        Runtime runtime = Runtime.getRuntime();

        print("##### Heap utilization statistics [MB] #####");
        print("Used Memory:" + (runtime.totalMemory() - runtime.freeMemory()) / mb);
        print("Free Memory:" + runtime.freeMemory() / mb);
        print("Total Memory:" + runtime.totalMemory() / mb);
        print("Max Memory:" + runtime.maxMemory() / mb);
    }

    /** test execution */
    public static void run() {
        final List<Integer> factors = USE_FACTORS ? MULTI_FACTORS : SINGLE_FACTOR;
        final String original = CSS;

        // prime
        if (PRIME) {
            print("\nPriming");
            for (int i = 0; i < 150; i++) {
                if (PHLOC) parse(Mode.phloc, original);
                parse(Mode.omakase, original);
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

            // take an average of 7 runs
            final int runs = 7;

            // phloc
            if (PHLOC) {
                List<Long> phlocParseTimes = Lists.newArrayListWithCapacity(runs);
                for (int i = 0; i < runs; i++) {
                    long start = System.currentTimeMillis();
                    parse(Mode.phloc, actual);
                    long end = System.currentTimeMillis();
                    phlocParseTimes.add(end - start);
                }
                long phlocTotal = 0;
                for (Long time : phlocParseTimes) {
                    phlocTotal += time;
                }
                long phlocParseAvg = phlocTotal / phlocParseTimes.size();
                print("phloc: " + phlocParseAvg + "ms");
            }

            // omakase
            List<Long> omakaseParseTimes = Lists.newArrayListWithCapacity(runs);
            for (int i = 0; i < runs; i++) {
                long start = System.currentTimeMillis();
                parse(Mode.omakase, actual);
                long end = System.currentTimeMillis();
                omakaseParseTimes.add(end - start);
            }
            long omakaseTotal = 0;
            for (Long time : omakaseParseTimes) {
                omakaseTotal += time;
            }
            long omakaseParseAvg = omakaseTotal / omakaseParseTimes.size();
            print("omakase: " + omakaseParseAvg + "ms");
        }

        print("\ndone");
    }

    public static void parse(Mode mode, String src) {
        if (mode == Mode.phloc) {
            CSSReader.readFromString(src, Charsets.UTF_8, ECSSVersion.LATEST);
        } else if (mode == Mode.omakase) {
            Omakase.source(src).process();
        }
    }

    /**
     * TODO Description
     * 
     * @param string
     */
    private static void print(String string) {
        if (CONSOLE) System.out.println(string);
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
