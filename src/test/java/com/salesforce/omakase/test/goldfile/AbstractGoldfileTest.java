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

import com.salesforce.omakase.writer.WriterMode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.runners.Parameterized.*;

/**
 * Base class for goldfile tests.
 *
 * @author nmcwilliams
 */
@RunWith(Parameterized.class)
@SuppressWarnings("JavaDoc")
public abstract class AbstractGoldfileTest {
    @Parameters(name = "mode={0} refined={1}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][]{
            {WriterMode.VERBOSE, true},
            {WriterMode.VERBOSE, false},
            {WriterMode.INLINE, true},
            {WriterMode.INLINE, false},
            {WriterMode.COMPRESSED, true},
            {WriterMode.COMPRESSED, false},
        });
    }

    @Parameter(0) public WriterMode mode;
    @Parameter(1) public boolean autoRefine;

    public abstract String name();

    @Test
    public void goldfile() throws IOException {
        Goldfile.test(name(), mode, autoRefine);
    }
}
