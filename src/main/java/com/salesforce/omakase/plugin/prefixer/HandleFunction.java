/*
 * Copyright (C) 2015 salesforce.com, inc.
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

package com.salesforce.omakase.plugin.prefixer;

import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.salesforce.omakase.SupportMatrix;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.declaration.FunctionValue;
import com.salesforce.omakase.ast.declaration.GenericFunctionValue;
import com.salesforce.omakase.ast.declaration.LinearGradientFunctionValue;
import com.salesforce.omakase.ast.declaration.NumericalValue;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.data.PrefixTablesUtil;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.parser.token.Tokens;
import com.salesforce.omakase.util.Equivalents;
import com.salesforce.omakase.util.Parsers;
import com.salesforce.omakase.util.Values;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Handles prefixing function names.
 *
 * @author nmcwilliams
 */
final class HandleFunction extends AbstractHandlerSimple<FunctionValue, Declaration> {
    private static final Map<String, String> DIR_FLIP = ImmutableMap.<String, String>builder()
        .put("to bottom", "top")
        .put("to top", "bottom")
        .put("to right", "left")
        .put("to left", "right")
        .put("to bottom right", "top left")
        .put("to bottom left", "top right")
        .put("to top right", "bottom left")
        .put("to top left", "bottom right")
        .build();

    @Override
    protected boolean applicable(FunctionValue instance, SupportMatrix support) {
        return instance.name().charAt(0) != '-' && PrefixTablesUtil.isPrefixableFunction(instance.name());
    }

    @Override
    protected Declaration subject(FunctionValue instance) {
        return instance.declaration();
    }

    @Override
    protected Set<Prefix> required(FunctionValue instance, SupportMatrix support) {
        return support.prefixesForFunction(instance.name());
    }

    @Override
    protected Multimap<Prefix, Declaration> equivalents(FunctionValue instance) {
        return Equivalents.prefixes(subject(instance), instance, Equivalents.FUNCTION_VALUES);
    }

    @Override
    protected void prefix(Declaration copied, Prefix prefix, SupportMatrix support) {
        // general functions
        for (GenericFunctionValue fv : Values.filter(GenericFunctionValue.class, copied.propertyValue())) {
            if (support.requiresPrefixForFunction(prefix, fv.name())) {
                fv.name(prefix + fv.name());
            }
        }

        // linear gradient special syntax
        for (LinearGradientFunctionValue fv : Values.filter(LinearGradientFunctionValue.class, copied.propertyValue())) {
            if (support.requiresPrefixForFunction(prefix, fv.unprefixedName())) {
                String newArgs = fv.args();

                char first = fv.args().charAt(0);
                if (first == 't') {
                    // "to" syntax -> "from" syntax
                    List<String> split = Lists.newArrayList(Splitter.on(",").limit(2).split(fv.args()));
                    String from = DIR_FLIP.get(split.get(0));
                    if (from != null) {
                        newArgs = from + "," + split.get(1);
                    }
                } else if (Tokens.DIGIT.matches(first) || first == '-') {
                    // convert angle http://www.sitepoint.com/using-unprefixed-css3-gradients-in-modern-browsers/
                    Source source = new Source(fv.args());
                    Optional<NumericalValue> numerical = Parsers.parseNumerical(source);
                    if (numerical.isPresent() && numerical.get().unit().isPresent()) {
                        int angle = Math.abs(numerical.get().intValue() - 450) % 360;
                        newArgs = angle + numerical.get().unit().get() + source.remaining();
                    }
                }

                fv.prefix(prefix);
                fv.args(newArgs);
            }
        }
    }
}