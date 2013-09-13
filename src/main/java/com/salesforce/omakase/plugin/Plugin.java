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

package com.salesforce.omakase.plugin;

/**
 * TODO Description
 * <p/>
 * not all subscriptions will be received automatically. sometimes auto refiner is needed.
 * <p/>
 * when you care about the relationships between syntax units, syntax tree required.
 * <p/>
 * a note about subscription order. PreProcess -> Rework/Observe -> Validate. In a hierarchy, the more specific type is received
 * before the more abstract type.
 *
 * @author nmcwilliams
 */
public interface Plugin {
}
