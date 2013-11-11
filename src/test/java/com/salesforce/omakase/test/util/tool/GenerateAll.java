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
 * Runs all source code generators.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public final class GenerateAll {
    private GenerateAll() {}

    public static void main(String[] args) throws Exception {
        PrefixToEnum.main(new String[]{});
        KeywordToEnum.main(new String[]{});
        PropertyToEnum.main(new String[]{});
        BrowserEnumGenerator.main(new String[]{});
        PrefixInfoClassGenerator.main(new String[]{});
    }
}
