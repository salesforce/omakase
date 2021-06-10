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

package com.salesforce.omakase.plugin.misc;

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Test;

import com.salesforce.omakase.Omakase;
import com.salesforce.omakase.writer.StyleWriter;

@SuppressWarnings("JavaDoc")
public class DirectionFlipPluginTest {

    private void check(String ltr, String rtl) {
        StyleWriter writer = StyleWriter.compressed();
        Omakase.source(ltr).use(new DirectionFlipPlugin()).use(writer).process();
        assertThat(writer.write()).isEqualTo(rtl);
    }

    @Test
    public void noFlip() {
        check(
            ".test{/*@noflip*/direction:left}",
            ".test{direction:left}");
    }

    @Test
    public void leftPositionToRight() {
        check(
            ".test{left:5px}",
            ".test{right:5px}");
    }

    @Test
    public void direction() {
        check(
            ".test{direction:left}",
            ".test{direction:right}");
    }

    @Test
    public void testFloat() {
        check(
            ".test{float:left}",
            ".test{float:right}");
    }

    @Test
    public void border() {
        check(
            ".test{border-left:0}",
            ".test{border-right:0}");
    }

    @Test
    public void padding3Vals() {
        check(
            ".test{padding:0 1px 2px}",
            ".test{padding:0 1px 2px}");
    }

    @Test
    public void padding4Vals() {
        check(
            ".test{padding:7px 12px 2px 4px}",
            ".test{padding:7px 4px 2px 12px}");
    }

    @Test
    public void background() {
        check(
            ".test{background:15% 7px #000}",
            ".test{background:85% 7px #000}");
    }

    @Test
    public void backgroundPositionX() {
        check(
            ".test{background-position-x:20%}",
            ".test{background-position-x:80%}");
    }

    @Test
    public void backgroundPositionY() {
        check(
            ".test{background-position-y:25%}",
            ".test{background-position-y:25%}");
    }

    @Test
    public void backgroundPositionShorthand() {
        check(
            ".test{background-position:21% 0}",
            ".test{background-position:79% 0}");
    }

    @Test
    public void cursor() {
        check(
            ".test{cursor:e-resize}",
            ".test{cursor:w-resize}");
    }

    @Test
    public void cursor2() {
        check(
            ".test{cursor:nesw-resize}",
            ".test{cursor:nwse-resize}");
    }

    @Test
    public void cursor3() {
        check(
            ".test{cursor:sw-resize}",
            ".test{cursor:se-resize}");
    }

    @Test
    public void borderRadius() {
        check(
            ".test{border-radius:5px 10px}",
            ".test{border-radius:10px 5px}");
    }

    @Test
    public void borderRadius3() {
        // top left, top right and bottom left, bottom right
        check(
            ".test{border-radius:1px 2px 3px}",
            ".test{border-radius:2px 1px 2px 3px}");
    }

    @Test
    public void borderRadius3x2() {
        // top left, top right and bottom left, bottom right
        check(
            ".test{border-radius:1px 2px 3px/4px 5px 6px}",
            ".test{border-radius:2px 1px 2px 3px/5px 4px 5px 6px}");
    }

    @Test
    public void borderRadius4() {
        // top left, top right, bottom right, bottom left
        check(
            ".test{border-radius:1px 2px 3px 4px}",
            ".test{border-radius:2px 1px 4px 3px}");
    }

    @Test
    public void borderRadius4x2() {
        // top left, top right, bottom right, bottom left
        check(
            ".test{border-radius:1px 2px 3px 4px/5px 6px 7px 8px}",
            ".test{border-radius:2px 1px 4px 3px/6px 5px 8px 7px}");
    }

    @Test
    public void borderRadius2Plus4() {
        check(
            ".test{border-radius:15px 10%/20rem 5px 15rem 4%}",
            ".test{border-radius:10% 15px/5px 20rem 4% 15rem}");
    }

    @Test
    public void borderTopRadius() {
        check(
            ".test{border-top-left-radius:.5em 2em}",
            ".test{border-top-right-radius:.5em 2em}");
    }

    @Test
    public void borderRadiusPrefixed() {
        check(
            ".test{-moz-border-radius:5px 10px}",
            ".test{-moz-border-radius:10px 5px}");
    }

    @Test
    public void borderRadiusNA() {
        check(
            ".test{border-radius:inherit}",
            ".test{border-radius:inherit}");
    }

    @Test
    public void testBorderRadiusNA2() {
        check(
            ".test{border-radius:5rem/10rem}",
            ".test{border-radius:5rem/10rem}");
    }

    @Test
    public void borderColor() {
        check(
            ".test{border-color:red black #333 green}",
            ".test{border-color:red green #333 black}");
    }

    @Test
    public void borderWidthShorthand() {
        check(
            ".test{border-width:1px 2px 3px 4px}",
            ".test{border-width:1px 4px 3px 2px}");
    }
}
