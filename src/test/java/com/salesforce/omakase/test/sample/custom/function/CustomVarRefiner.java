/*
 * Copyright (C) 2014 salesforce.com, inc.
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

package com.salesforce.omakase.test.sample.custom.function;

import com.salesforce.omakase.ast.declaration.RawFunction;
import com.salesforce.omakase.ast.declaration.Term;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.parser.ParserFactory;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.parser.refiner.FunctionRefiner;
import com.salesforce.omakase.parser.refiner.MasterRefiner;

import java.util.Map;

/**
 * The refiner handles converting the raw function into either the custom AST object ({@link CustomVarFunction}) or the
 * individual {@link Term}s.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class CustomVarRefiner implements FunctionRefiner {
    public enum Mode {PASSTHROUGH, RESOLVE}

    private final Mode mode;
    private final Map<String, String> vars;

    public CustomVarRefiner(Mode mode, Map<String, String> vars) {
        this.mode = mode;
        this.vars = vars;
    }

    @Override
    public boolean refine(RawFunction raw, Broadcaster broadcaster, MasterRefiner refiner) {
        // we only want to refine things with our name
        if (raw.name().equals(CustomVarFunction.NAME)) {

            // do some simple validation
            String arg = raw.args();
            if (!arg.matches("[a-zA-Z\\-]+")) {
                throw new RuntimeException("invalid custom-var arg: " + arg);
            } else if (!vars.containsKey(arg)) {
                throw new RuntimeException("unknown custom-var arg: " + arg);
            }

            if (mode == Mode.PASSTHROUGH) {
                // don't resolve, just convert to our custom AST object
                CustomVarFunction function = new CustomVarFunction(arg);

                // by broadcasting, it automatically gets added to the declaration since it is a Term. Also this is what
                // enables delivery to any subscription methods for the AST object.
                broadcaster.broadcast(function);
            } else {
                // parse the arg to real Term objects. Just by running the built-in parser, the terms will be automatically
                // broadcasted and thus associated with the declaration. Note that these terms are delivered to any applicable
                // subscription methods as normal.
                Source source = new Source(vars.get(arg), raw.line(), raw.column());
                ParserFactory.termSequenceParser().parse(source, broadcaster, refiner);
            }

            return true;
        }
        return false;
    }
}
