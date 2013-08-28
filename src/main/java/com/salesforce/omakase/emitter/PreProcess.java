/**
 * ADD LICENSE
 */
package com.salesforce.omakase.emitter;

import java.lang.annotation.*;

import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.error.ErrorManager;
import com.salesforce.omakase.plugin.basic.AutoRefiner;
import com.salesforce.omakase.plugin.basic.SyntaxTree;

/**
 * Use this annotation to preprocess a particular {@link Syntax} unit.
 * 
 * <p>
 * Methods with this annotation will be called before any {@link Rework} or {@link Validate} methods are called.
 * 
 * <p>
 * The {@link PreProcess} annotation is mainly used to provide necessary metadata or setup that subsequent
 * {@link Rework} operations will rely on. For example, the {@link SyntaxTree} uses it to link related objects together,
 * and {@link AutoRefiner} uses it to automatically refine certain detailed syntax units, ensuring that they are
 * received by {@link Rework} plugins.
 * 
 * <p>
 * In most cases this is <b>not</b> what you want to use. Prefer {@link Rework} for operations that change content,
 * {@link Observe} for operations that do not change content, and {@link Validate} for validation that can result in
 * throwing an error (via an {@link ErrorManager}).
 * 
 * @author nmcwilliams
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PreProcess {
}
