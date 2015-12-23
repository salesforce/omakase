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
