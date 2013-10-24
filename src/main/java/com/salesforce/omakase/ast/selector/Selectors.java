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

import com.google.common.base.Optional;

/**
 * Utilities for working with {@link Selector}s and {@link SelectorPart}s.
 *
 * @author nmcwilliams
 */
public final class Selectors {
    /** do not construct */
    private Selectors() {}

    /**
     * Gets the given part as an instance of a {@link ClassSelector}, if it is one.
     *
     * @param part
     *     Check if this part is a {@link ClassSelector}.
     *
     * @return The class selector, or {@link Optional#absent()} if the part is a different type.
     */
    public static Optional<ClassSelector> asClassSelector(SelectorPart part) {
        return as(ClassSelector.class, part);
    }

    /**
     * Gets the given part as an instance of a {@link IdSelector}, if it is one.
     *
     * @param part
     *     Check if this part is a {@link IdSelector}.
     *
     * @return The id selector, or {@link Optional#absent()} if the part is a different type.
     */
    public static Optional<IdSelector> asIdSelector(SelectorPart part) {
        return as(IdSelector.class, part);
    }

    /**
     * Gets the given part as an instance of a {@link TypeSelector} if it is one.
     *
     * @param part
     *     Check if this part is a {@link TypeSelector}.
     *
     * @return The type selector, or {@link Optional#absent()} if the part is a different type.
     */
    public static Optional<TypeSelector> asTypeSelector(SelectorPart part) {
        return as(TypeSelector.class, part);
    }

    /**
     * Gets the given part as an instance of the given {@link SelectorPart} type.
     *
     * @param klass
     *     Check if the part is an instance of this class.
     * @param part
     *     The part to check.
     * @param <T>
     *     Check if the part is an instance of this class.
     *
     * @return the properly-typed instance, or {@link Optional#absent()} if it doesn't match.
     */
    public static <T extends SelectorPart> Optional<T> as(Class<T> klass, SelectorPart part) {
        return klass.isInstance(part) ? Optional.of(klass.cast(part)) : Optional.<T>absent();
    }

    /**
     * Checks the given {@link Selector} for a {@link ClassSelector} that matches the given name. If you don't actually need the
     * instance itself then you can use {@link #hasClassSelector(Selector, String)} instead.
     *
     * @param selector
     *     The selector to check.
     * @param name
     *     Check for a {@link ClassSelector} with this name.
     *
     * @return The class selector, or {@link Optional#absent()} if not found.
     */
    public static Optional<ClassSelector> findClassSelector(Selector selector, String name) {
        return findClassSelector(selector.parts(), name);
    }

    /**
     * Checks the given parts for the <em>first</em> {@link ClassSelector} that matches the given name. If you don't actually need
     * the instance itself then you can use {@link #hasClassSelector(Iterable, String)} instead.
     *
     * @param parts
     *     The parts to check.
     * @param name
     *     Check for a {@link ClassSelector} with this name.
     *
     * @return The class selector, or {@link Optional#absent()} if not found.
     */
    public static Optional<ClassSelector> findClassSelector(Iterable<SelectorPart> parts, String name) {
        for (SelectorPart part : parts) {
            Optional<ClassSelector> cs = asClassSelector(part);
            if (cs.isPresent() && cs.get().name().equals(name)) return cs;
        }
        return Optional.absent();
    }

    /**
     * Checks the given {@link Selector} for the <em>first</em> {@link IdSelector} that matches the given name. If you don't
     * actually need the instance itself then you can use {@link #hasIdSelector(Selector, String)} instead.
     *
     * @param selector
     *     The selector to check.
     * @param name
     *     Check for a {@link IdSelector} with this name.
     *
     * @return The class selector, or {@link Optional#absent()} if not found.
     */
    public static Optional<IdSelector> findIdSelector(Selector selector, String name) {
        return findIdSelector(selector.parts(), name);
    }

    /**
     * Checks the given parts for the <em>first</em> {@link IdSelector} that matches the given name. If you don't actually need
     * the instance itself then you can use {@link #hasIdSelector(Iterable, String)} instead.
     *
     * @param parts
     *     The parts to check.
     * @param name
     *     Check for a {@link IdSelector} with this name.
     *
     * @return The id selector, or {@link Optional#absent()} if not found.
     */
    public static Optional<IdSelector> findIdSelector(Iterable<SelectorPart> parts, String name) {
        for (SelectorPart part : parts) {
            Optional<IdSelector> id = asIdSelector(part);
            if (id.isPresent() && id.get().name().equals(name)) return id;
        }
        return Optional.absent();
    }

    /**
     * Checks the given {@link Selector} for the <em>first</em> {@link TypeSelector} that matches the given name. If you don't
     * actually need the instance itself then you can use {@link #hasTypeSelector(Selector, String)} instead.
     *
     * @param selector
     *     The selector to check.
     * @param name
     *     Check for a {@link TypeSelector} with this name.
     *
     * @return The class selector, or {@link Optional#absent()} if not found.
     */
    public static Optional<TypeSelector> findTypeSelector(Selector selector, String name) {
        return findTypeSelector(selector.parts(), name);
    }

    /**
     * Checks the given parts for the <em>first</em> {@link TypeSelector} that matches the given name. If you don't actually need
     * the instance itself then you can use {@link #hasTypeSelector(Iterable, String)} instead.
     *
     * @param parts
     *     The parts to check.
     * @param name
     *     Check for a {@link TypeSelector} with this name.
     *
     * @return The type selector, or {@link Optional#absent()} if not found.
     */
    public static Optional<TypeSelector> findTypeSelector(Iterable<SelectorPart> parts, String name) {
        for (SelectorPart part : parts) {
            Optional<TypeSelector> type = asTypeSelector(part);
            if (type.isPresent() && type.get().name().equals(name)) return type;
        }
        return Optional.absent();
    }

    /**
     * Checks the given parts for a {@link ClassSelector} that matches the given name.
     * <p/>
     * If you would like access to the found instance itself then use {@link #findClassSelector(Selector, String)} instead.
     *
     * @param selector
     *     The {@link Selector} to check.
     * @param name
     *     Check for a {@link ClassSelector} with this name.
     *
     * @return True if one of the parts is a {@link ClassSelector} with the given name.
     */
    public static boolean hasClassSelector(Selector selector, String name) {
        return hasClassSelector(selector.parts(), name);
    }

    /**
     * Checks the given parts for a {@link ClassSelector} that matches the given name.
     * <p/>
     * If you would like access to the found instance itself then use {@link #findClassSelector(Iterable, String)} instead.
     *
     * @param parts
     *     The parts to check.
     * @param name
     *     Check for a {@link ClassSelector} with this name.
     *
     * @return True if one of the parts is a {@link ClassSelector} with the given name.
     */
    public static boolean hasClassSelector(Iterable<SelectorPart> parts, String name) {
        return findClassSelector(parts, name).isPresent();
    }

    /**
     * Checks the given parts for a {@link IdSelector} that matches the given name.
     * <p/>
     * If you would like access to the found instance itself then use {@link #findIdSelector(Selector, String)} instead.
     *
     * @param selector
     *     The {@link Selector} to check.
     * @param name
     *     Check for a {@link IdSelector} with this name.
     *
     * @return True if one of the parts is a {@link IdSelector} with the given name.
     */
    public static boolean hasIdSelector(Selector selector, String name) {
        return hasIdSelector(selector.parts(), name);
    }

    /**
     * Checks the given parts for a {@link IdSelector} that matches the given name.
     * <p/>
     * If you would like access to the found instance itself then use {@link #findIdSelector(Iterable, String)} instead.
     *
     * @param parts
     *     The parts to check.
     * @param name
     *     Check for a {@link IdSelector} with this name.
     *
     * @return True if one of the parts is a {@link IdSelector} with the given name.
     */
    public static boolean hasIdSelector(Iterable<SelectorPart> parts, String name) {
        return findIdSelector(parts, name).isPresent();
    }

    /**
     * Checks the given parts for a {@link TypeSelector} that matches the given name.
     * <p/>
     * If you would like access to the found instance itself then use {@link #findTypeSelector(Selector, String)} instead.
     *
     * @param selector
     *     The {@link Selector} to check.
     * @param name
     *     Check for a {@link TypeSelector} with this name.
     *
     * @return True if one of the parts is a {@link TypeSelector} with the given name.
     */
    public static boolean hasTypeSelector(Selector selector, String name) {
        return hasTypeSelector(selector.parts(), name);
    }

    /**
     * Checks the given parts for a {@link TypeSelector} that matches the given name.
     * <p/>
     * If you would like access to the found instance itself then use {@link #findTypeSelector(Iterable, String)} instead.
     *
     * @param parts
     *     The parts to check.
     * @param name
     *     Check for a {@link TypeSelector} with this name.
     *
     * @return True if one of the parts is a {@link TypeSelector} with the given name.
     */
    public static boolean hasTypeSelector(Iterable<SelectorPart> parts, String name) {
        return findTypeSelector(parts, name).isPresent();
    }
}
