Omakase
=======

[![CircleCI](https://circleci.com/gh/salesforce/omakase.svg?style=svg)](https://circleci.com/gh/salesforce/omakase)
[![License](https://img.shields.io/badge/License-BSD%203--Clause-blue.svg)](https://opensource.org/licenses/BSD-3-Clause)

Fast, Java-based, plugin-oriented CSS3+ parser.

Omakase (お任せ o-_ma_-ka-say) has very few dependencies and doesn't need to execute Ruby or JavaScript code. It runs entirely in Java.

Features
--------

Omakase can work as a parser, preprocessor, linter, minifier or all four. It gives you an AST that can be modified and validated.

### Super fast
Omakase is engineered for speed. Compared to other Java-based, modern CSS parsers, Omakase parses ~300 lines of code up to 3x faster and ~1000 lines up to 5x faster. For very large files (over 10k lines) Omakase can potentially save hundreds of ms.

### Flexible
Omakase is plugin-oriented, which means you can create plugins to modify, add, remove, validate or lint any aspect of the CSS.

Plugins can also be used to extend the CSS grammar and syntax. This allows you to create common features like variables,  mixins and nesting using whatever format you like. In fact much of the built-in functionality uses plugins.

### Awesome bundled plugins
Omakase bundles several plugins, most notably:

- [automatic vendor prefixing](#prefixer) (supported by [caniuse.com](https://github.com/Fyrd/caniuse))
- [conditional blocks](#conditionals)
- [RTL direction flipping](#directionflip)

### Better error messaging
Omakase is built 100% solely for parsing CSS, which means that the error messages are often more specific and easier to understand than from other parsers created from generic parser generators.

### Easier programmatic usage
Omakase is focused on runtime usage, and provides special features to make runtime parsing even faster, namely a 2-level parsing strategy. The first level separates the source into selectors, declarations and at-rules only. The (optional) second level breaks each unit down further, for example into class selectors, type selectors, id selectors, etc... This allows you to do a full pass during build time, but during runtime only parse the aspects of CSS that need to be reworked. This speed improvement is on top of the general performance numbers mentioned above.

You can also parse snippets of CSS on-the-fly, such as a single selector or declaration value.

How to download
---------------

Build jars from source by cloning this project locally and running `mvn install` from the project root (must have Java and Maven installed).

Or you can download a jar from [releases]. Note that no dependencies are included in the release jars. The only main dependency is Guava. See the pom.xml file for more details.

Usage
-----

All parsing starts with the `Omakase` class. The CSS source is specified using the `#source(CharSequence)` method, optional plugins are then registered, and then parsing is performed with a call to `#process()`. An example of the most basic form of parsing is as follows:

```java
Omakase.source(input).process();
```

You will almost always include one or more plugins though. Plugins are used for output/minification, automatic vendor prefixing, modifications to the AST, custom linting, and more.

Unless you are specifically optimizing for performance, you should at least add the `StandardValidation` plugin, and it should be last, after other plugins.

Note that only one instance of a plugin can be registered per parsing operation.

### Output

Use the `StyleWriter` plugin to write the processed CSS:

```java
StyleWriter verbose = StyleWriter.verbose();
StandardValidation validation = new StandardValidation();
Omakase.source(input).use(verbose).use(validation).process();
String out = verbose.write();
```

You can also write to an `Appendable`

```java
StyleWriter verbose = StyleWriter.verbose();
StandardValidation validation = new StandardValidation();
Omakase.source(input).use(verbose).use(validation).process();
StringBuilder builder = new StringBuilder();
String out = verbose.writeTo(builder);
```

By default, CSS is written out in _inline_ mode. Other available modes include _verbose_ and _compressed_. Verbose mode will output newlines, spaces, comments, etc... Inline mode will write each rule on a single line. Compressed mode will eliminate as many characters as possible, including newlines, spaces, etc...

```java
StyleWriter verbose = StyleWriter.verbose();
StyleWriter inline = StyleWriter.inline();
StyleWriter compressed = StyleWriter.compressed();
```

In some cases you may want to write out an individual, stand-alone syntax unit:

```java
Declaration declaration = new Declaration(Property.DISPLAY, KeywordValue.of(Keyword.NONE));
String output = StyleWriter.inline().writeSingle(declaration);
```

You can also override how any individual syntax unit is written. For more information see the [Custom writers](#custom-writers) section below.

### Validation

In Omakase, _validation_ refers to both actual syntax validation (e.g., that the arguments to an `rgba` function are well-formed) as well as what is commonly known as _linting_ (e.g., that fonts are specified using relative units instead of pixels).

All validation is written and registered as plugins. To enable the standard validations, register an instance of the `StandardValidation` plugin:

```java
Omakase.source(input).use(new StandardValidation()).process();
```

This auto-refines every selector, declaration and at-rule (see the [AutoRefine](#autorefine) section below for more information on refinement) and registers the standard list of built-in validators.

Keep in mind that validation methods will always be invoked after rework methods, but otherwise they will be executed in the order that the plugin class was registered.

You can also add your own custom validators. For examples see the [Custom validation](#custom-validation) section below.

Note that basic CSS grammar is verified independently of any plugins as part of the core parser.

### Registering plugins

When registering plugins there are important details to keep in mind:

- Only one instance of a plugin can be registered.
- Subscription methods will be executed in the order that its plugin class was registered.
- All `@Rework` subscription methods will be executed before `@Validate`, regardless of the order in which the plugins were registered. Essentially this means validation always happens after rework modification is fully completed.

### Bundled plugins

#### SyntaxTree

The `SyntaxTree` plugin is an extremely simple plugin that only grabs and stores a reference to the parsed `Stylesheet` object. It's an easy way for you to get access to the `Stylesheet` object without having to write a custom plugin.

```java
SyntaxTree tree = new SyntaxTree();
Omakase.source(input).use(tree).process();
Stylesheet stylesheet = tree.stylesheet();
System.out.println("#statements = " + stylesheet.statements().size());
```

#### AutoRefine

The `AutoRefine` plugin is responsible for automatically refining all or certain `Refinable` objects. Currently this includes `Selector`, `Declaration`, `RawFunction` and `AtRule`. _Refinement_ refers to the process of taking a generic syntax string (e.g., ".class > #id") and parsing out the individuals units (e.g., `ClassSelector`, `Combinator`, `IdSelector`).

Unless refinement occurs the syntax object may contain invalid CSS. For example a `Selector` may contain raw content consisting of ".class~!8391", but no errors will be thrown until it is refined. The `AutoRefine` plugin can do this automatically:

```java
AutoRefine all = AutoRefine.everything()); // refine everything
AutoRefiner selectors = AutoRefine.only(Match.SELECTORS); // refine selectors
AutoRefiner declarations = AutoRefine.only(Match.DECLARATIONS); // refine declarations
```

Using `StandardValidation` automatically includes `AutoRefine` unless otherwise configured.

You may be wondering when you *wouldn't* want auto refinement. The main use-case is during runtime or other performance-sensitive environments.

You first parse the CSS with full refinement. This ensures you actually have valid CSS. You can store this preprocessed source code in memory or on the filesystem. During runtime, you can parse the CSS again to perform dynamic substitutions. However this time, since you have already ensured that the CSS is valid, there is no need to parse more than what is necessary to perform the dynamic substitions. You can simply refine only those selectors or declarations that you need and nothing more. This will result in faster parsing performance. For more information on this see the section on [conditional refinement](#conditional-refinement).

#### Conditionals

Conditionals allow you to vary the CSS output based on specific *true conditions*. Here's an example:

```css
.button {
  background: linear-gradient(#aaa, #ddd);
}

@if (ie7) {
  .button {
    background: #aaa;
  }
}
```

If "ie7" is passed in as a *true condition* then the block will be retained, otherwise it will be removed.

To use conditionals, register the `Conditionals` plugin:

```java
Conditionals conditionals = new Conditionals("ie7");
Omakase.source(input).use(conditionals).process();
```

You can manage the set of *true conditions* to print out variations of the CSS:

```java
Conditionals conditionals = new Conditionals();
StyleWriter writer = StyleWriter.compressed();
Omakase.source(input).use(conditionals).use(writer).process();

// ie7
conditionals.config().replaceTrueConditions("ie7");
String ie7 = writer.write();

// firefox
conditionals.config().replaceTrueConditions("firefox");
String firefox = writer.write();

// chrome
conditionals.config().replaceTrueConditions("webkit", "chrome");
String chrome = writer.write();
```
You can also use logical negation and logical or operators in the CSS:

```css
@if (!ie7) {
  .button {
    display: inline-block;
  }
}

@if (ie8 || ie9 || ie10) {
  .button {                                                                           
    margin: 7px;
  }
}
```

Finally, if you would like to enable conditionals and validate them but hold off on actually evaluating them, you can specify `passthroughMode` as true:

```java
new Conditionals("ie7").config().passthroughMode(true);
new Conditionals(true).config().addTrueConditions("ie7"); // same as above
```

Of course, any string can be used and referred to as a *true condition*, not just browsers. Finally, note that the default behavior is to automatically convert all conditions found in the input as well as the specified *true conditions* to **lower-case**. Thus, usage is not case-dependent.

##### Conditionals Collector

The `ConditionalsCollector` plugin can be used when you need to know what conditions were actually used in the input CSS, say, to determine what CSS variations you need to write.

```java
StyleWriter writer = StyleWriter.compressed();
Conditionals conditionals = new Conditionals();
ConditionalsCollector collector = new ConditionalsCollector();
Omakase.source(input).use(conditionals).use(collector).use(writer).process();

Map<String, String> variations = new HashMap<String, String>();
variations.put("default", writer.write());

for (String condition : collector.foundConditions()) {
    conditionals.config().replaceTrueConditions(condition);
    variations.put(condition, writer.write());
}
```

Note that `ConditionalsCollector` automatically registers an instance of the `Conditionals` plugin as well. If you are explicitly adding the `Conditionals` plugin, it must be registered *before* the `ConditionalsCollector` instance.

##### Conditionals Validator

The `ConditionalsValidator` plugin can be used to ensure only certain conditions can be used in the CSS:

```java
ConditionalsValidator validation = new ConditionalsValidator("ie8", "ie9", "ie10", "chrome", "firefox");
Omakase.source(input).use(validation).process();
```

Note that `ConditionalsValidator` automatically registers an instance of the `Conditionals` plugin as well. If you are explicitly adding the `Conditionals` plugin, it must be registered *before* the `ConditionalsValidator` instance.

#### UnquotedIEFilterPlugin

If you are in the unfortunate situation of using crappy legacy IE filters then the UnquotedIEFilterPlugin must be registered, otherwise syntax errors will occur.

```java
UnquotedIEFilterPlugin ieFilters = new UnquotedIEFilterPlugin();
Omakase.source(input).use(ieFilters).process();
```

Note that *quoted* IE filters do not require this plugin. An example of an unquoted IE filter:

```css
filter: progid:DXImageTransform.Microsoft.Shadow(color='#969696', Direction=145, Strength=3);
```

compared to quoted:

```css
-ms-filter: "progid:DXImageTransform.Microsoft.Shadow(color='#969696', Direction=145, Strength=3)";
```

This plugin must be registered before `StandardValidation` or `AutoRefine`.

#### Prefixer

The `Prefixer` plugin enables automatic vendor prefixing. It will analyze all prefixable selectors, properties, at-rules
function and keyword names, and automatically prepend prefixed-equivalents based on the specified level of browser support. For example:

```css
.class {
  border-radius: 3px;
}
```

gets transformed into:

```css
.class {
  -webkit-border-radius: 3px;
  -moz-border-radius: 3px;
  border-radius: 3px;
}
```

The `Prefixer` plugin determines which prefixes to actually use based on whether the browsers you'd like to support actually need them. It maintains a list of every browser version and which properties they require prefixed to accomplish this. The data is retrieved from the famous [caniuse.com](http://caniuse.com) website [database](https://github.com/fyrd/caniuse).

Here is how you would register the `Prefixer` plugin using the default browser level support:

```java
Prefixer prefixer = Prefixer.defaultBrowserSupport();
Omakase.source(input).use(prefixer).process();
```

The default browser version support includes the last six versions of iOS Safari, last five versions of Chrome and Firefox, last
three of Android, IE 7+, and the latest versions of Safari, IE Mobile and Opera Mini.

You can specify an alternative set of browsers to support as well:

```java
Prefixer prefixing = Prefixer.customBrowserSupport();
prefixing.support().all(Browser.IE);
prefixing.support().latest(Browser.FIREFOX);
prefixing.support().browser(Browser.SAFARI, 6.1);
prefixing.support().last(Browser.SAFARI, 2);
Omakase.source(input).use(prefixing).process();
```

This is cumulative, so you can also add extra support to the defaults instead.

To manually update the prefix data, see the [Scripts](#scripts) section below. Updating is a one-line shell command, and after an update the processed CSS automatically reflects any changes right away. This can be more efficient than using a mixin to handle vendor prefixes, as you would have to constantly check each prefixable property, selector, etc... to see if a prefix is still required.

Note that the `Prefixer` plugin will **not** trigger _refinement_ of a selector, declaration or at-rule just to check if a prefix is needed. This means you need to register `AutoRefine` (or `StandardValidation`) if you would like all selectors, declarations, etc... to be considered. See the [AutoRefine](#autorefine) section above for more information.

##### Pruning

The `Prefixer` plugin works well with existing CSS that is already littered with various vendor prefixes. By default, if a prefix is already present then it will be preserved as-is. That is, a duplicate prefix will not be added. This allows you to turn on and use the plugin right away without having to clean up your CSS file. It can even be a way for you to specify a value for the prefixed declaration that differs from the unprefixed one.

On the other hand, in many cases it will be more performant to actually have all unnecessary prefixes removed. You can do this with the `prune` method.

For example, take the following:

```css
.class {
    -webkit-border-radius: 3px;
    -moz-border-radius: 3px;
    -ms-border-radius: 3px;
    -o-border-radius: 3px;
    border-radius: 3px;
}
```

Many people still have CSS that looks like this, without realizing that border-radius has not required a prefix for `webkit` or `moz` for a very long time, and the `ms` and `o` prefixes were never actually needed at all.

Turn on pruning to have these prefixes automatically removed:

```java
Prefixer prefixer = Prefixer.defaultBrowserSupport().prune(true);
```

Then go back and update your CSS files to remove them as well at your leisure.

##### Rearranging

Similarly, sometimes people end up with CSS that looks like this:

```css
.class {
    -webkit-border-radius: 3px;
    border-radius: 3px;
    -moz-border-radius: 3px;
}
```

It's always best practice for the unprefixed version to be *last*, that way if the browser supports the property unprefixed that is the one it uses. You can turn on rearranging to ensure that any prefixes that exist in the source file are moved around to make this true:

```java
Prefixer prefixer = Prefixer.defaultBrowserSupport().rearrange(true);
```

##### Prefixer Support List

Here's a list of what's currently supported:

    At Rule
    ----------------------------
    keyframes

    Selector
    ----------------------------
    placeholder
    selection

    Property
    ----------------------------
    align-content
    align-items
    align-self
    animation
    animation-delay
    animation-direction
    animation-duration
    animation-fill-mode
    animation-iteration-count
    animation-name
    animation-play-state
    animation-timing-function
    appearance
    backface-visibility
    background-clip
    background-origin
    background-size
    border-bottom-left-radius
    border-bottom-right-radius
    border-image
    border-image-outset
    border-image-repeat
    border-image-slice
    border-image-source
    border-image-width
    border-radius
    border-top-left-radius
    border-top-right-radius
    box-shadow
    box-sizing
    column-count
    column-fill
    column-gap
    column-rule
    column-rule-color
    column-rule-style
    column-rule-width
    column-span
    column-width
    columns
    flex
    flex-basis
    flex-direction
    flex-flow
    flex-grow
    flex-shrink
    flex-wrap
    hyphens
    justify-content
    order
    perspective
    perspective-origin
    tab-size
    transform
    transform-origin
    transform-style
    transition
    transition-delay
    transition-duration
    transition-property
    transition-timing-function
    user-select

    Keyword
    ----------------------------
    flex
    inline-flex

    Function
    ----------------------------
    calc
    linear-gradient
    repeating-linear-gradient


You can view this yourself from the command line, as well as which of these will actually be auto-prefixed by default, by using the `omakase --prefixed-all` command. See the [Scripts](#scripts) section below.

#### PrefixCleaner

It's usually a good idea to add the `PrefixCleaner` plugin after the `Prefixer` plugin:

```java
Prefixer prefixer = Prefixer.defaultBrowserSupport();
PrefixCleaner cleaner = PrefixCleaner.mismatchedPrefixedUnits();
Omakase.source(input).use(prefixer).use(cleaner).process();
```

This will remove prefixed declarations inside of prefixed at rules, where the declaration's prefix doesn't match the at-rule's prefix.

#### DirectionFlip

This plugin will handle flipping certain property names and values from left-to-right to right-to-left.

```java
DirectionFlipPlugin rtl = new DirectionFlipPlugin();
Omakase.source(source).use(rtl).process();
```

It flips property names such as `left` and `border-left` to `right` and `border-right`. It also rearranges certain values like padding, margin and border-radius shorthand so that the right and left units are swapped.

If you need to prevent certain declarations from being flipped, you can use a CSS annotation like so:

```css
.button {
  /* @noflip */ padding-left: 10px;
}
```

Take note of the [CSS annotation format](#css-annotations). For example, if you use two asterisks to start the comment block instead of one then it will not be recognized.

### Creating custom plugins

In addition to the standard library plugins, you can create and register your own custom plugins. Custom plugins allow you to rework the processed CSS or add your own custom validation and linting rules. You can also use plugins to extend the CSS syntax and grammar.

Plugins are essentially plain java objects that implement one of the _plugin interfaces_ and define one or more _subscription methods_ to a particular AST object (e.g., `Selector` or `Declaration`). The subscription method does the actual rework or validation as appropriate.

Plugins are registered exactly the same as as any of the standard built-in plugins such as `StyleWriter` or `AutoRefine`.

See the [Subscribable Syntax Units](#subscribable-syntax-units) section below for the definitive list of all subscribable AST objects.

#### Plugin interfaces

To get started, a plugin must first implement one or more of the _plugin interfaces_, listed as follows:

- **Plugin** - the basic plugin.
- **DependentPlugin** - for plugins that have dependencies on other plugins.
- **GrammarPlugin** - for plugins that customize syntax and grammar.
- **ParserPlugin** - for plugins customize individual parser behavior.
- **PostProcessingPlugin** for plugins that need notification after all processing has completed.

Most plugins will implement just the `Plugin` or `DependentPlugin` interface.

#### Custom rework

The term _rework_ refers to the process of changing the processed CSS source code, e.g., changing a class name, adding a declaration, removing a rule, etc... Omakase allows you to easily change nearly any aspect of the CSS. You can add, change or remove selectors, add, change or remove declarations, add, change or remove rules, and... you get the picture.

Each AST object contains setters and getters for working with its values. In addition there are several utility classes.

Here is an example of a simple rework operation to prefix every selector with a class name called "myPrefix":

```java
public class PrefixAllSelectors implements Plugin {
    @Rework
    public void prefixClass(Selector selector) {
        // create a new simple selector
        ClassSelector prefix = new ClassSelector("myPrefix");

        // prepend the simple selector to the beginning of the selector
        selector.parts().prepend(prefix);
    }
}
```

Notice the following details:

- It implements `Plugin`
- It has one subscription method with the `@Rework` annotation
- The subscription method has one argument, which is the AST object to be reworked

For more advanced examples on performing rework see the [ReworkTest.java](src/test/java/com/salesforce/omakase/test/functional/ReworkTest.java) class.

##### Dynamic AST creation and modification

Here are some code examples illustrating some common operations during rework:

```java
// create a new selector
Selector selector = new Selector(new ClassSelector(".test"));

// another example
IdSelector theId = new IdSelector("main");
ClassSelector theClass = new ClassSelector("inner");
Selector selector = new Selector(theId, Combinator.descendant(), theClass);

// append a simple selector part to an existing selector
selector.parts().append(new ClassSelector("another-class"));

// append a selector to a rule
rule.selectors().append(myNewSelector);

// alternative to above
someSelector.append(myNewSelector);

// change a value on a class selector
myClassSelector.name("the-new-name");

// check if a selector has a particular (class|id|type) simple selector
if (Selectors.hasClassSelector(selector, "myClass")) {...}
if (Selectors.hasIdSelector(selector, "myId")) {...}
if (Selectors.hasTypeSelector(selector, "div")) {...}

// find the first matching (class|id|type) simple selector
Optional<ClassSelector> button = Selectors.findClassSelector(selector, "button");
if (button.isPresent()) {
    System.out.println(button.get());
}

// check if a 'div' type selector is adjoined to a class selector (e.g., "div.selector1")
if(Selectors.hasTypeSelector(Selectors.adjoining(selector1), "div")) {...}

// create a new declaration
Declaration declaration = new Declaration(Property.DISPLAY, KeywordValue.of(Keyword.NONE));

// another example
PropertyName name = PropertyName.of("new-prop");
Declaration declaration = new Declaration(name, KeywordValue.of("blah"));

// another example
NumericalValue val1 = NumericalValue.of(1, "px");
NumericalValue val2 = NumericalValue.of(5, "px");
PropertyValue value = PropertyValue.ofTerms(OperatorType.SPACE, val1, val2);
Declaration declaration = new Declaration(Property.MARGIN, value);

// another example
PropertyName prop = PropertyName.of(Property.BORDER_RADIUS).prefix(Prefix.WEBKIT);
Declaration declaration = new Declaration(prop, KeywordValue.of(Keyword.NONE));

// append a declaration to a rule
rule.declarations().append(myNewDeclaration);

// create a new rule
Rule rule = new Rule();
rule.selectors().append(new Selector(new ClassSelector("new")));
rule.declarations().append(new Declaration(Property.COLOR, HexColorValue.of("#fff")));
rule.declarations().append(new Declaration(Property.FONT_SIZE, NumericalValue.of(1.5).unit("em")));

// check if a declaration is for a specific property name
if (declaration.isProperty(Property.DISPLAY)) {...}
if (declaration.isProperty("new-prop")) {...}

// check if a declaration has a value
PropertyValue value = declaration.propertyValue();
Optional<KeywordValue> keyword = Values.asKeyword(value);
if (keyword.isPresent()) {...}

// use the method appropriate to what value you think it is (say, based on the property name)
Optional<HexColorValue> color = Values.asHexColor(value);
Optional<NumericalValue> number = Values.asNumerical(value);
Optional<StringValue> string = Values.asString(value);

// change a property value
Optional<KeywordValue> keyword = Values.asKeyword(declaration.propertyValue());
if (keyword.isPresent()) {
    keyword.get().keyword(Keyword.NONE);
}

// remove (destroy) a selector, declaration, rule, etc...
someSelector.destroy();
someDeclaration.destroy();
someRule.destroy();

// for more examples see the many unit tests
```

Keep in mind that dynamically created units will be automatically delivered to all `@Rework` subscription methods interested in the syntax unit's type, as well as to `@Validate` subscriptions later on. Thus, dynamically created CSS is fully integrated with all of your custom rework and validation plugins.

You can remove any unit from the tree by calling `#destroy`. Note that doing this will prevent that unit from being delivered to any subsequent subscription methods. Destroyed units cannot be added back to the tree, but they can be cloned with `#copy`.

There are other utilities for working with units in the following utility classes:

- Selectors.java
- Declarations.java
- Values.java
- Actions.java
- Parsers.java

See the `com.salesforce.omakase.util` package for more.

#### Custom validation

Besides rework, you can also register subscription methods to perform validation and linting. Just like rework, you declare a method with the first parameter being the type of syntax unit you would like to validate. In addition there is a second parameter which is the `ErrorManager` used to report any problems.

Here is an example of a class with two validation subscription methods:

```java
public class Validations implements Plugin {
    @Validate
    public void validateRelativeFonts(Declaration declaration, ErrorManager em) {
        if (!declaration.isProperty(Property.FONT_SIZE)) return; // only process font-size properties

        Optional<NumericalValue> number = Values.asNumerical(declaration.propertyValue());

        if (number.isPresent() && number.get().unit().isPresent() && number.get().unit().get().equals("px")) {
            em.report(ErrorLevel.FATAL, declaration, "Font sizes must use relative units");
        }
    }

    @Validate
    public void validateIncludesUnprefixed(Declaration declaration, ErrorManager em) {
        // check that all prefixed declarations include an unprefixed declaration as well.

        PropertyName propertyName = declaration.propertyName();

        if (propertyName.isPrefixed()) {
            boolean found = false;
            String expected = propertyName.unprefixed();

            // go through each declaration in the block, looking for one with the unprefixed name
            for (Declaration d : declaration.group() {
                if (d.isProperty(expected)) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                em.report(ErrorLevel.WARNING, declaration, "Prefixed declaration without unprefixed version");
            }
        }
    }
}
```

Keep in mind that all validators run after the rework phase has been completed. Validation methods should **not** change any aspect or content of the objects they are validating. They should only check values and reports errors as applicable.

#### Dependent plugins

As mentioned above, many plugins, especially ones with `@Rework`, will need to register dependencies on other plugins. Here is an example of a plugin with dependencies:

```java
public class Dependent implements DependentPlugin {
    @Override
    public void dependencies(PluginRegistry registry) {
        registry.require(SelectorPlugin.class);
        registry.require(MyPlugin.class, MyPlugin::new);
    }
}
```

The `#require` method takes the class of the plugin. If the plugin is already registered then the registered instance is simply returned. Otherwise one is automatically created and added to the registry. You can then proceed to configure the plugin as necessary for your use case.

You can also require your own custom plugins by using the `#require(Class, Supplier)` method.

#### Performing both rework and validation

Note that any particular plugin can have as many `@Rework` and `@Validate` annotated methods as it needs. That is, rework and validation do not need to be separated out in to multiple classes.

You can also subscribe to the exact same syntax type in multiple methods. However there is no guarantee to the execution order of subscription methods to the exact same syntax unit type for the exact same operation (rework or validate). This means, for example, that if two `@Rework` methods subscribed to `ClassSelector` are needed, and that execution order is important, then these methods should be separated out into their own classes. The classes should then be registered in the intended execution order.

You can also register anonymous inner classes as plugins too:

```java
Omakase.source(input)
    .add(new StandardValidation())
    .add(new Plugin() {
        @Rework
        public void rework(Declaration d) {
            ...
        }
    })
    .add(new Plugin() {
        @Validate
        public void validate(Declaration d, ErrorManager em) {
            ...
        }
    })
    .process();
```

#### Subscribing to interfaces

Not only can you subscribe to concrete types such as `ClassSelector` and `IdSelector`, you can also subscribe to higher-level interfaces such as `Statement`, `SimpleSelector` or even the top-level `Syntax` interface.

Subscribing to an interface type will allow you to receive all instances of that type, which can be useful in certain scenarios.

Within a particular class, the more specifically-typed subscription will be delivered before the more generally-typed subscriptions. For example, in a class with subscriptions to `ClassSelector`, `SimpleSelector` and `Syntax`, the methods will always be invoked in that exact order.

See the [Subscribable Syntax Units](#subscribable-syntax-units) section below for the definitive list of all subscribable AST objects.

#### Observe

Besides `@Rework` and `@Validate`, there is one another annotation that can be used to make a subscription method.

`@Observe` can be used in place of `@Rework` when your intention is to simply utilize information from the AST object and you do not intend to make any changes. In terms of execution order, `@Observe` and `@Rework` are equivalent. Currently the only difference `@Observe` makes is providing a better description of what the method intends to do.

#### Extending the CSS syntax

Omakase provides a powerful mechanism for extending the standard CSS syntax. You can easily augment CSS with your own:

- Custom functions
- Custom at-rules
- Custom selectors
- Custom declarations

Using the `@Refine` annotation, you can subscribe to any `Refinable` syntax unit:

```java
@Refine
public void refine(RawFunction function, Grammar grammar, Broadcaster broadcaster) {
}
```

`@Refine` methods must specify three parameters. The first is the unit to refine. The second is of type `Grammar`, which should be used to access internal parsers and tokens. The third is of type `Broadcaster`, which should be used to _broadcast_ refined syntax units.

Note that after a subscription method handles a unit it will not be sent to subsequent refiners, so plugins should be registered in appropriate order.

You can optionally specify a string to the `@Refine` annotation, and only units that match that name will be delivered. Here is an example of a custom function:

```java
@Refine("myFunction")
public void refine(RawFunction function, Grammar grammar, Broadcaster broadcaster) {
  String args = function.args().trim();

  // parse arguments, e.g., lookup a value, perform calculations...
  // ...

  // turn our parsed string into actual terms
  Source source = new Source(parsedArgs);
  grammar.parser().termSequenceParser().parse(source, grammar, broadcaster);
}
```

You can utilize the `Source` class as a parsing utility, and nearly all of the library parsing functionality can be used standalone. This includes parsing rules, declarations, selectors, and even specific selectors like a class selector. Utilize the methods on the `Grammar` instance and the `Parsers` class.

For a full example of a `FunctionRefiner` see `UrlPlugin`. For a full example of an `AtRuleRefiner` see `ConditionalsPlugin` and related classes. For more detailed examples see the [test samples](src/test/java/com/salesforce/omakase/sample/custom/).

Note that generally speaking, by simply utilizing an internal parser, all parsed units will be automatically broadcasted to the given broadcaster. This means that a custom function could simply parse a string for terms and operators using the term sequence parser and all encountered terms and operators will be automatically added to the declaration that the custom function is in, no further work required. To avoid this, just use your own broadcaster instance instead of passing through the one given to you.

### Conditional Refinement

As mentioned above, most of the time you want to include the `StandardValidation` or `AutoRefine` plugins to ensure that every AST object is refined and delivered to subscription methods. The alternative is to conditionally refine only the units that are necessary.

The easiest way to do this is with `AutoRefine`, where you can specify to only refine selectors, declarations, etc:

```java
// skip refinement of selectors
AutoRefine.only(Match.FUNCTIONS, Match.DECLARATIONS, Match.AT_RULES);
```

You can take this further with a custom `@Refine` method that checks the raw content and refines if appropriate:

```java
@Refine
public void observe(Selector selector, Grammar grammar, Broadcaster broadcaster) {
    if (selector.raw().get().content().contains(".foo")) {
        SelectorPlugin.delegateRefinement(selector, grammar, broadcaster);
    }
}
```

You can delegate refinement to the standard plugin (`SelectorPlugin`, `DeclarationPlugin`, `MediaPlugin`).

Using these methods you can eliminate unnecessary parsing for large sets of CSS in performance sensitive environments.

### Custom error handling

The default `ErrorManager` is `DefaultErrorManager`, which will rethrow some errors immediately and log others at the end of parsing.

You can alternatively specify your own `ErrorManager` implementation and provide it during parser setup:

```java
Omakase.source(input).use(myCustomErrorManager).process();
```

### Custom writers

Omakase allows you to hook into the writing process and override the output of any particular AST unit. This feature allows you to:

- Conditionally stop the output of the unit.
- Append or prepend something before or after the unit.
- Conditional apply logic or append content based on an annotation from a CSS comment associated with the unit.

However, it is not recommended to change the actual content of the unit using a custom writer, as this will bypass all rework and validation rules.

The first step is to create a new class that implements the `CustomWriter` interface. This interface is parameterized with the type of unit that it is overriding:

```java
public class MyCustomWriter implements CustomWriter<Selector> {
    @Override
    public boolean write(Selector selector, StyleWriter writer, StyleAppendable appendable) throws IOException {
        appendable.append("/*CUSTOM OUTPUT*/"); // arbitrary content
        writer.writeInner(selector, appendable, false);
        return true;
    }
}
```

Inside of the `#write` method you can append any content to the output by using the given `StyleAppendable` as seen above. If you would like to append the default output of the unit as well then call the `StyleWriter#writeInner` method, passing false for the last parameter. This method should return true if it has handled the unit, or false to allow subsequent custom writers or the default writer to handle it instead.

Afterwards, register this custom writer with the `StyleWriter` instance:

```java
StyleWriter writer = StyleWriter.compressed();
writer.addCustomWriter(Selector.class, new MyCustomWriter());
Omakase.source(".class{color:red}").use(writer).process();
```

### Comments and CSS annotations

AST objects are automatically associated with all comments that logically precede them. You can access the list of comments by calling the `Syntax#comments` method on the syntax unit. In other words, comments are linked to the AST object that directly follows them (with the exception of orphaned comments, as explained below). Take this example:

```css
/*0*/

/*1*/.class /*2*/ /*3*/.class/*4*/.class /*5*/, /*6*/ p#id /*7*/ { /*8*/
  /*9*/ border: /*10*/ 1px/*11*/ solid red /*12*/;/*13*/ /*14*/
  /*15*/ margin: 1px; /*16*/
  /*17*/
}

/*18*//*19*/
```

- **0, 1** - linked with the `Selector` starting with the content `.class...`
- **2, 3** - linked with the `ClassSelector` segment that follows them
- **4** - linked with the `ClassSelector` that follows it
- **5** - an orphaned comment on the `Selector`
- **6** - linked to the `Selector` starting with content `p#`...
- **7** - an orphaned comment attached to the `Selector`
- **8, 9** - linked to the `Declaration` starting with content `border`...
- **10** - linked to the term (`NumericalValue`) with content `1px`
- **11** - linked to the term (`KeywordValue`) with content `solid`
- **12** - an orphaned comment attached to the `Declaration`
- **13, 14, 15** - linked to the `Declaration` starting with content `margin`...
- **16, 17** - orphaned comments linked to the `Rule`
- **18, 19** - orphaned comments linked to the `Stylesheet`

Note that without refinement, the comments on the inner segments of the `Selector`s and `Declaration`s will not be known.

#### CSS Annotations

Comments can be used for _annotation directives_. For example:

```css
.class {
  width: 20px;
  margin-left: 20px;
  /* @noflip */ float: left;
}
```

In this example, the `@noflip` annotation is used to provide information to the `DirectionFlip` plugin regarding not flipping that particular declaration value.

CSS comment annotations start with `@` + the name of the annotation. They can also have arguments. There can be at most one annotation per CSS comment block, although multiple annotation comment blocks are allowed. For example:

```css
.class {
  /* @markerAnnotation */ color: red;
  /* @lang-switch ja eng */ font-family: Arial, sans-serif;
  /* @custom1 */ /* @custom2 */ display: block;
}
```

No other content is allowed in the comment with the annotation. If there's any content before the start of the annotation the annotation will not be recognized, with the exception of bang comments:

```css
.class {
  width: 20px;
  margin-left: 20px;
  /*! @noflip */ float: left;
}
```

Any content that follows the annotation will be considered arguments for the annotation.

Any number of custom annotations can be utilized by your plugins. To check for an annotation, there are convenience methods available on every syntax unit:

```java
public class MyPlugin implements Plugin {
    @Rework
    public void rework(Declaration declaration) {
        // read /* @markerAnnotation */
        if (declaration.hasAnnotation("markerAnnotation")) {
            // do something
        }

        // read /* @browser ie7 */
        Optional<CssAnnotation> annotation = declaration.annotation("browser");
        if (annotation.isPresent()) {
            System.out.println(annotation.get().rawArgs());
        }

        // read /* @bug b-123456, b-123457 */
        Optional<CssAnnotation> annotation = declaration.annotation("bug");
        if (annotation.isPresent()) {
            ImmutableList<String> bugNumbers = annotation.get().commaSeparatedArgs();
        }

        // read /* @bug b-123456 b-123457 */
        Optional<CssAnnotation> annotation = declaration.annotation("bug");
        if (annotation.isPresent()) {
            ImmutableList<String> bugNumbers = annotation.get().spaceSeparatedArgs();
        }

        // read /* @details author=nathan, since=2.0 */
        Optional<CssAnnotation> annotation = declaration.annotation("details");
        if (annotation.isPresent()) {
            ImmutableMap<String, String> map = annotation.get().keyValueArgs('=');
            String author = map.get("author");
        }

        // print all annotations
        for (CssAnnotation annotation : declaration.annotations()) {
            System.out.println(annotation.name());
            System.out.println(annotation.rawArgs());
        }
    }
}
```

The `CssAnnotation` class contains many powerful methods to parse various arg formats including space-delimited,
comma-delimited, key-value pairs and enum constants.

If you happen to have your hands on a specific `Comment` instance, it has convenience methods as well.

When an annotation is placed before a rule, it is associated with first selector instance in the rule, not the rule or the simple selector, as explained above. However for convenience, all of the `has*` and `get*` annotation methods will also check or include results from the first selector when called on a rule instance.

#### Orphaned comments

The term _orphaned comment_ refers to a comment that does not logically precede any particular AST unit. There are four places where orphaned comments can be found, which are at the end of a selector, at the end of a declaration (before the semi-colon), at the end of a rule, and at the end of a stylesheet. Here are some examples:

```css
.class1 .class2 > a:hover /* an orphaned comment */, #id a:hover /*another orphaned comment*/ {
    color: red;
    font-size: 1.3em /*an orphaned comment*/;
    /* an orphaned comment */
}

/* an orphaned comment */
```

Use the `#orphanedComments` method on a `Selector`, `Declaration`, `Rule` or `Stylesheet` to retrieve them.

Subscribable Syntax Units
-------------------------

Following is the list of all supported syntax types that you can subscribe to in `@Rework`, `@Validate` and `@Observe` annotated methods. Keep in mind that many syntax units require _refinement_ before they will be delivered. More information on this is available in the Usage section above.

<pre>
    Name                           Description                                               Enablement / Dependency     Type
    ----------------------------   -------------------------------------------------------   -------------------------   ---------------
01: Statement                      rule or at-rule                                           Automatic                   interface
02: Syntax                         top level interface for all units                         Under certain conditions*   interface
03: RawFunction                    a raw function before refinement                          Declaration refinement      class
04: Rule                           (no description)                                          Automatic                   class
05: Stylesheet                     (no description)                                          Automatic                   class
06: AtRule                         (no description)                                          Automatic                   class
07: FontDescriptor                 font descriptor within @font-face                         AtRule refinement           class
08: MediaQueryList                 full media query string                                   AtRule refinement           class
09: FunctionValue                  general interface for function terms                      Declaration refinement      interface
10: Term                           a single segment of a property value                      Declaration refinement      interface
11: Declaration                    (no description)                                          Automatic                   class
12: GenericFunctionValue           unknown function value                                    Declaration refinement      class
13: HexColorValue                  individual hex color value                                Declaration refinement      class
14: KeywordValue                   individual keyword value                                  Declaration refinement      class
15: LinearGradientFunctionValue    linear gradient function                                  Declaration refinement      class
16: NumericalValue                 individual numerical value                                Declaration refinement      class
17: PropertyValue                  interface for all property values                         Declaration refinement      class
18: StringValue                    individual string value                                   Declaration refinement      class
19: UnicodeRangeValue              unicode range value                                       Declaration refinement      class
20: UrlFunctionValue               url function                                              Declaration refinement      class
21: ConditionalAtRuleBlock         conditionals                                              AtRule refinement           class
22: UnquotedIEFilter               proprietary microsoft filter                              Declaration refinement      class
23: SelectorPart                   group interface for all selector segments                 Selector refinement         interface
24: SimpleSelector                 parent interface for simple selectors                     Selector refinement         interface
25: AttributeSelector              attribute selector segment                                Selector refinement         class
26: ClassSelector                  class selector segment                                    Selector refinement         class
27: IdSelector                     id selector segment                                       Selector refinement         class
28: PseudoClassSelector            pseudo class selector segment                             Selector refinement         class
29: PseudoElementSelector          pseudo element selector segment                           Selector refinement         class
30: Selector                       (no description)                                          Automatic                   class
31: TypeSelector                   type/element selector segment                             Selector refinement         class
32: UniversalSelector              universal selector segment                                Selector refinement         class

Generated by PrintSubscribableSyntaxTable.java
</pre>

**Notes:**

* A subscription to `Syntax` will depend on which concrete syntax classes are enabled. To get _every_ syntax unit then utilize `new StandardValidaion()` or `AutoRefine#everything`.
* Some orphaned comments will only be delivered if selectors and declarations are refined.

Interactive Shell
-----------------

This project comes with an interactive shell, which allows you to quickly see what Omakase will output when given specific input CSS, all in real-time.

To get started, you must first run the omakase setup script. Under the omakase project directory run this command from the terminal:

    script/setup.sh

This will enable the `omakase` command from within this project folder. See the [Scripts](#scripts) section below for more information. Once this is done, you can start up the interactive shell by typing in the terminal:

    omakase --interactive

This will display usage information that looks something like this:

    Omakase Interactive Shell
    enter ! on a new line to finish
    enter !c for continuous mode (ctrl+c to exit)
    enter !verbose for verbose output
    enter !inline for inline output
    enter !compressed for compressed output
    enter !prefix-current for default prefixing
    enter !prefix-all for all prefixing
    enter !prefix-prune to enable prefix pruning
    enter !prefix-rearrange to enable prefix rearranging
    enter !prefix-off to remove prefixing support
    enter !subl to use the sublime text editor (subl)
    enter !mate to use the textmate editor (mate)

Type your input CSS, with `!` on a blank line demarking the end of the input. For example:

    .test {
      color: red;
    }
    !

This will output something like this:

    ----------result----------

    .test {color:red}

For more involved scenarios, you might prefer to use the built in support for Sublime Text and Atom.

Sublime Text requires the `subl` command to be installed on your PATH:
https://www.sublimetext.com/docs/3/osx_command_line.html

You can test that this is working by simply typing `subl` in the terminal. It should open Sublime Text. If not, make sure ~/bin (or wherever you linked to) is on your path.

Atom requires the `atom` command. You can install this by going to Menu -> Install Shell Commands. Again you can test this if the `atom` command in terminal opens up Atom.

Development
-----------

Before checking anything in, setup your IDE to conform to project standards. See and follow the instructions in the readme.md files inside of the `idea` or `eclipse` folders.

As of right now the (strongly) preferred IDE for contribution is IntelliJ IDEA. This is mainly because the existing source code and style closely conforms to the idea settings included in the project. If you use eclipse or something else then be sure to following the existing coding conventions manually if need be.

### Building

The project relies on the following technologies:

1. git (duh)
2. java 8 (make sure both the IDE and maven are setup to use it)
3. maven 3+

run `mvn clean install` to get things going from the command line. It should build and run tests successfully. Afterwards you can import the maven project into your IDE (as an existing maven project) and go from there.

### Dependencies

Non-test dependencies include Google's **Guava** library. Dependencies shouldn't really increase beyond that as one of the goals is simplicity and self-containment.

### Tests

Currently tests are built with junit 4 and [fest assertions](https://code.google.com/p/fest/#Fluent_Assertions). Junit should be self explanatory, but if you haven't used junit 4+ then keep in mind that it uses java _annotations_ instead of inheritance and method naming conventions.

Fest may be new to you, but it's quite simple and easy to get the hang of. If you have used hamcrest before then it's something like that. Basically it's a library of matchers and assertions. The actual assertions are fluent and look something like this:

```java
assertThat(someValue).isTrue();
assertThat(someCollection).hasSize(3);
assertThat(someCollection).containsExactly(value1, value2, value3);
```

This makes the tests more readable and also provides much more useful error messages out the box.

The important takeaway is that **all** unit tests must be written using fest assertions and not the junit/hamcrest ones. Just follow the patterns established in the existing tests. Particularly make sure you import the correct classes.

### Updating keywords, properties, etc...

There are several enums such as `Keyword.java` and `Browser.java` that contain values that will inevitably need to be updated. Most of these are stored in data files under `src/test/resources/data/`.

You can use a [script](#scripts) to regenerate the java source files after updating, or you can directly run the main method on the classes.

### Scripts

The omakase CLI is a powerful tool for building the project, regenerating enum source files, running performance tests, and more.

To get started, under the project directory run this command from the shell:

    script/setup.sh

This will setup links to the omakase CLI script. Now you can run the `omakase` command from within the project root:

    ~/dev/omakase > omakase

    Usage: omakase [options]

    Options:

      -b (--build)                  build the project
      -d (--deploy)                 build and deploy jars (requires additional setup, see deploy.md)
      -h (--help)                   print this help message
      -i (--interactive, --shell)   interactive shell
      -l (--local-only)             only regenerate local data, no prefix data (used with -u option)
      -p (--perf) <args>            performance test
      -s (--syntax, --sub)          print the subscribable syntax table
      -u (--update)                 regenerate data enum, data class and prefixes source files
      -v (--prefixed-def)           print what is auto-prefixed by Prefixer.defaultBrowserSupport()
      -w (--prefixed-all)           print all properties, at-rules, etc...that are supported by Prefixer

For example, updating the prefix info:

    omakase --update

Printing the subscribable syntax table:

    omakase --syntax

Running the performance test:

    omakase -p

Architecture
------------

Omakase is a CSS parser built from the ground up. Unlike other open-source CSS parsers it is not built on a parser generator such as JavaCC or Antlr. Instead, it relies on many small and simple java objects that know how to consume various parts of CSS syntax, which also allows for easy extenstion of the syntax and grammar by consumers.

The project requires _Java 8_, _git_ and _maven_. The general architecture of the project can be summarized as follows:

1. **Parsers** - Small, individual parser objects that process CSS source code.
2. **AST Objects** - Simple representations of various CSS syntax units.
3. **Plugins** - Observers that can subscribe to any AST object for refinement, rework or validation.
4. **Broadcasters and Emitter** - The bridge between parsers and plugins.
5. **Writers** - Outputs parsed CSS code.
6. **ErrorHandlers** - Manages errors encountered when parsing CSS source code.

### Parsers
**Key Classes** `Token` `Tokens` `Source` `Parser` `BaseParserFactory`, `Grammar`

Parsers are simple java objects that know how to parse specific aspects of CSS syntax. Parsers do not maintain any state.

Parsers employ a 2-level parsing strategy. The first level will comb through the CSS source and extract the at-rules, selectors and declarations (and no more). Any errors at this grammatical level will be caught immediately (for example, a missing curly bracket to close a rule). However any errors at a more specific level (e.g., within a selector or within a declaration) will not be caught until that particular object is refined.

The second level occurs individually per instance, e.g., each particular `Selector` or `Declaration` instance.

It's important to understand that the second level may or may not be executed or may only be executed on certain instances.

This process allows us to be more specific about what actually gets parsed. For example, on a first parsing pass we may want to parse and validate everything, but on a second pass we may not care about fully parsing selectors anymore.

When a `Parser` has successfully parsed some content, it will construct the appropriate AST object and give it to the `Broadcaster`. Ultimately, the `Broadcaster` will pass the AST object to the registered subscription methods for that particular AST object type.

### AST Objects
**Key Classes** `Syntax` `Refinable` `Groubable` `SyntaxCollection` `Selector` `Declaration`

AST (Abstract Syntax Tree) objects are simple data objects representing various aspects o CSS syntax, e.g., selectors, declarations, etc... AST objects generally have getters and setters for various values. AST objects are also responsible for writing their own content out when a `StyleWriter` asks for it.

AST objects generally contain little to no validation logic. Most validation is written in the form of a `Plugin` that subscribes to the unit it is going to validate.

### Plugins
**Key Classes** `Plugin` `DependentPlugin`

A `Plugin` subscribes to one or more AST objects (one per method) to perform rework or validation. Methods on the plugin are annotated with `@Rework` or `@Validate` annotations as appropriate. These methods are known as subscription methods. Each subscription method is subscribed to one particular AST object type via its parameter. This parameter is how we know which methods to invoke when various AST objects are sent to be broadcasted.

Plugins are registered during parser setup via `Omakase#use`. Plugins can and often do have dependencies on each other, which can be registered by implementing the `DependentPlugin` interface.

The general Omakase philosophy is that much of the internal logic as well as all of the consumer logic is organized into a set of plugins.

### Broadcasters
**Key Classes** `Broadcaster` `Emitter` `AnnotationScanner` `Subscription`

A `Broadcaster` is something that handles broadcasts of various AST objects. You can think of this as the _observer_ pattern, or the _event listener_ pattern, where the AST object itself is the event, the plugins are the listeners, and the broadcasters are responsible for receiving the AST objects from the parsers and delivering it to all registered subscriptions.

However, broadcasters do no necessarily deliver the AST objects right away. The `Broadcaster` interface utilizes a _chain_ pattern. That is, broadcasters can be wrapped inside of each other, relaying broadcasters to the next broadcaster down the chain. Near the bottom of the chain is usually the `EmittingBroadcaster`, which is the one responsible for using an `Emitter` to actually invoke the subscription methods.

### Writers
**Key Classes** `Writable` `StyleWriter` `StyleAppendable`

Writers are fairly simple... they are responsible for taking the parsed `SyntaxTree` and writing it out as a CSS source code string.

### ErrorManagers
**Key Classes** `ErrorManager`

Error managers are responsible for dealing with errors during processing, including parser errors or errors generated from a validator plugin.

### Dependencies Graph

To view statistics on dependencies, run:

    mvn project-info-reports:dependencies
    open target/site/dependencies.html
