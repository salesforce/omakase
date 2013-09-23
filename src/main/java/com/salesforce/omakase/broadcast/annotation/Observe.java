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

package com.salesforce.omakase.broadcast.annotation;

import com.salesforce.omakase.ast.Syntax;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use this annotation to subscribe to {@link Syntax} objects when the method <em>will not change</em> any aspect of the CSS or
 * object.
 * <p/>
 * The one an only parameter for methods with this annotation should be one of the {@link Syntax} types.
 * <p/>
 * Currently, this annotation is equivalent to {@link Rework}, with a clearer indication of the intended effect of the method.
 * This equivalence with {@link Rework} may change in the future, so take care to annotate properly.
 *
 * @author nmcwilliams
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Observe {
}
