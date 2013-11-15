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

package com.salesforce.omakase.test.util.tool;

/**
 * ANSI colors for console output.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public final class Colors {
    public static final String RESET = "\u001B[0m";
    public static final String BLACK = "\u001B[0;30m";
    public static final String DARK_GREY = "\u001B[1;30m";
    public static final String RED = "\u001B[0;31m";
    public static final String LIGHT_RED = "\u001B[1;31m";
    public static final String GREEN = "\u001B[0;32m";
    public static final String LIGHT_GREEN = "\u001B[1;32m";
    public static final String BROWN = "\u001B[0;33m";
    public static final String YELLOW = "\u001B[1;33m";
    public static final String BLUE = "\u001B[0;34m";
    public static final String LIGHT_BLUE = "\u001B[1;34m";
    public static final String PURPLE = "\u001B[0;35m";
    public static final String LIGHT_PURPLE = "\u001B[1;35m";
    public static final String CYAN = "\u001B[0;36m";
    public static final String LIGHT_CYAN = "\u001B[1;36m";
    public static final String GREY = "\u001B[0;37m";
    public static final String WHITE = "\u001B[1;37m";

    private Colors() {}

    public static String red(String msg, Object... args) {
        return RED + (args.length > 0 ? String.format(msg, args) : msg) + RESET;
    }

    public static String yellow(String msg, Object... args) {
        return YELLOW + (args.length > 0 ? String.format(msg, args) : msg) + RESET;
    }

    public static String lightBlue(String msg, Object... args) {
        return LIGHT_BLUE + (args.length > 0 ? String.format(msg, args) : msg) + RESET;
    }

    public static String lightGreen(String msg, Object... args) {
        return LIGHT_GREEN + (args.length > 0 ? String.format(msg, args) : msg) + RESET;
    }

    public static String lightPurple(String msg, Object... args) {
        return LIGHT_PURPLE + (args.length > 0 ? String.format(msg, args) : msg) + RESET;
    }

    public static String grey(String msg, Object... args) {
        return DARK_GREY + (args.length > 0 ? String.format(msg, args) : msg) + RESET;
    }
}
