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

package com.salesforce.omakase.util.tool;

import com.salesforce.omakase.data.Keyword;

/**
 * Code generator for the {@link Keyword} enum.
 * <p/>
 * To modify the list of keywords, edit the 'src/test/resources/data/keywords.txt' file and execute the main method on this class
 * (also available via bin/run.sh).
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public final class KeywordToEnum {
    private KeywordToEnum() {}

    public static void main(String[] args) throws Exception {
        EnumWriter writer = new EnumWriter();

        writer.generator(KeywordToEnum.class);
        writer.enumClass(Keyword.class);
        writer.source("keywords.txt");
        writer.template("keyword-to-enum.ftl");

        writer.write();
    }
}
