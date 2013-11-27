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

import com.salesforce.omakase.Omakase;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.selector.PseudoClassSelector;
import com.salesforce.omakase.broadcast.annotation.Observe;
import com.salesforce.omakase.broadcast.annotation.Validate;
import com.salesforce.omakase.error.ErrorManager;
import com.salesforce.omakase.plugin.Plugin;
import com.salesforce.omakase.plugin.basic.SyntaxTree;
import com.salesforce.omakase.plugin.validator.StandardValidation;
import com.salesforce.omakase.test.util.EchoLogger;

/**
 * Omakase, full mode.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("UnusedParameters")
public final class PerfTestOmakaseFull implements PerfTestParser {
    @Override
    public char code() {
        return 'f';
    }

    @Override
    public String name() {
        return "Omakase Full";
    }

    @Override
    public void parse(String input) {
        Omakase.source(input)
            .request(new SyntaxTree())
            .request(new StandardValidation())
            .request(new EchoLogger())
            .request(new Plugin() {
                @Observe
                public void observe(Declaration d) {}
            })
            .request(new Plugin() {
                @Observe
                public void observe(Declaration d) {}
            })
            .request(new Plugin() {
                @Observe
                public void observe(Declaration d) {}
            })
            .request(new Plugin() {
                @Observe
                public void observe(Declaration d) {}
            })
            .request(new Plugin() {
                @Validate
                public void observe(Declaration d, ErrorManager em) {}
            })
            .request(new Plugin() {
                @Validate
                public void observe(PseudoClassSelector s, ErrorManager em) {}
            })
            .request(new Plugin() {
                @Validate
                public void observe(PseudoClassSelector s, ErrorManager em) {}
            })
            .request(new Plugin() {
                @Validate
                public void observe(AtRule a, ErrorManager em) {}
            })
            .process();
    }
}
