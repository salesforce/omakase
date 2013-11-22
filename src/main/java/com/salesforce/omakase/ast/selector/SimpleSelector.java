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

package com.salesforce.omakase.ast.selector;

import com.salesforce.omakase.broadcast.annotation.Description;
import com.salesforce.omakase.broadcast.annotation.Subscribable;

import static com.salesforce.omakase.broadcast.BroadcastRequirement.REFINED_SELECTOR;

/**
 * A simple selector, as defined by the Selectors Level 3 spec: "A simple selector is either a type selector, universal selector,
 * attribute selector, class selector, ID selector, or pseudo-class."
 * <p/>
 * Note that a {@link PseudoElementSelector} is not a simple selector.
 *
 * @author nmcwilliams
 */
@Subscribable
@Description(value = "parent interface for simple selectors", broadcasted = REFINED_SELECTOR)
public interface SimpleSelector extends SelectorPart {}
