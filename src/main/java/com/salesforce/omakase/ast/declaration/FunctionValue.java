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

package com.salesforce.omakase.ast.declaration;

import com.salesforce.omakase.ast.Named;
import com.salesforce.omakase.broadcast.annotation.Description;
import com.salesforce.omakase.broadcast.annotation.Subscribable;

import static com.salesforce.omakase.broadcast.BroadcastRequirement.REFINED_DECLARATION;

/**
 * General interface for function term values, such as {@link GenericFunctionValue}.
 * <p/>
 * In contrast to {@link RawFunction}, this interface is implemented by all specific functions. {@link RawFunction}s on the other
 * hand represent functions that have yet to be refined into the specific function.
 * <p/>
 * In other words, subscribe to {@link FunctionValue} if you would like to receive all specific function value instances, and
 * subscribe to {@link RawFunction} if you would like to preprocess a raw function before it gets refined into the specific
 * function.
 *
 * @author nmcwilliams
 * @see GenericFunctionValue
 */
@Subscribable
@Description(value = "general interface for function terms", broadcasted = REFINED_DECLARATION)
public interface FunctionValue extends Term, Named {
}
