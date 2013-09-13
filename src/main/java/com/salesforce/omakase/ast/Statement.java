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

package com.salesforce.omakase.ast;

import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.collection.Groupable;
import com.salesforce.omakase.emitter.Description;
import com.salesforce.omakase.emitter.Subscribable;
import com.salesforce.omakase.plugin.basic.SyntaxTree;

import static com.salesforce.omakase.emitter.SubscribableRequirement.SYNTAX_TREE;

/**
 * TESTME
 * <p/>
 * A top-level {@link Syntax} unit, for example a {@link Rule} or {@link AtRule}.
 * <p/>
 * Note that {@link Statement}s are not be created unless the {@link SyntaxTree} plugin is enabled.
 *
 * @author nmcwilliams
 */
@Subscribable
@Description(value = "rule or at-rule", broadcasted = SYNTAX_TREE)
public interface Statement extends Syntax, Groupable<Statement> {
}
