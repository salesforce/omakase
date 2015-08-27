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
import com.google.common.collect.Multimap;
import com.salesforce.omakase.SupportMatrix;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.data.PrefixTablesUtil;
import com.salesforce.omakase.data.Property;
import com.salesforce.omakase.util.Equivalents;

import java.util.Set;

/**
 * Handles prefixing property names.
 *
 * @author nmcwilliams
 */
class HandleProperty extends AbstractHandlerSimple<Declaration, Declaration> {
    @Override
    protected boolean applicable(Declaration instance, SupportMatrix support) {
        Optional<Property> property = instance.propertyName().asProperty();
        return property.isPresent() && PrefixTablesUtil.isPrefixableProperty(property.get());
    }

    @Override
    protected Declaration subject(Declaration instance) {
        return instance;
    }

    @Override
    protected Set<Prefix> required(Declaration instance, SupportMatrix support) {
        return support.prefixesForProperty(instance.propertyName().asProperty().get());
    }

    @Override
    protected Multimap<Prefix, Declaration> equivalents(Declaration instance) {
        return Equivalents.prefixes(subject(instance), instance, Equivalents.PROPERTIES);
    }

    @Override
    protected void prefix(Declaration copied, Prefix prefix, SupportMatrix support) {
        Optional<Property> property = copied.propertyName().asProperty();
        if (property.isPresent() && support.requiresPrefixForProperty(prefix, property.get())) {
            copied.propertyName().prefix(prefix);
        }
    }
}