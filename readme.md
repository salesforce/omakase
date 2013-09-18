Omakase
=======

Fast and slightly opinionated CSS parser.

Omakase (お任せ o-ma-ka-say) is a Japanese phrase that means "I'll leave it to you" (from Japanese "to entrust"). In Japan it can be used at any restaurant.

Features
--------

The two main goals of Omakase are speed and flexibility.

### Focus on speed

Omakase is written with runtime usage needs in mind. While most CSS tools are intended to be used on the command line or at build time, Omakase takes into consideration the additional sensitivities of runtime-level performance.

Part of the speed gains come from a 2-level parsing strategy. 2-level parsing accounts for various scenarios and use-cases. The first level separates the source code into selectors, declarations and at-rules only. The (optional or targeted) second level can be conditionally applied to specific selectors, declarations and at-rules or all of them depending on applicability. 

### Focus on flexibility

Omakase is a network of *plugins*. Plugins can subscribe to specific CSS syntax units to perform custom validation and/or rework any part of the CSS. The plugin framework is so flexible that much of library-provided functionality and APIs are written using the same plugin infrastructure (SyntaxTree generation, linting, validation, and more).

### Better error messaging

Because Omakase doesn't use a generic parser generator, the error messages are all CSS specific and often easier to understand.

### Awesome standard plugins

Omakase comes with some nifty plugins out of the box that mirror highly-touted _CSS Preprocessor_ functionality.

TODO add descriptions 

- mixins
- right-to-left swapping
- automatic vendor prefixing
- CSS annotations
- custom functions
- theme variables(?)
- url cache busting for images

Usage
-----

Omakase can be used as a parser, a preprocessor, a linter, or all three. When using Omakase it's useful to keep in mind the pluggable nature of its architecture.

### Basic usage

Parsing is simple. All parsing starts with the `Omakase` class. The CSS source is specified using the `#source(String)` method, then optional plugins are registered, and finally parsing begins with a call to `#process()`. An example of the most basic form of parsing is as follows

```java
Omakase.source(input).process();
```

However this is not very useful in of itself. Usually you will register various plugins according to your needs.

The most basic principle to understand when using Omakase is that nearly everything is organized into a set of _plugins_. Need a syntax tree? Add the `SyntaxTree` plugin. Need to output the processed code in compressed or dev mode format? Register a `StyleWriter` plugin. Want some validation or linting? Add the applicable plugins.

This loosely coupled architecture allows us to achieve better performance and caters to the pattern of parsing a CSS source over and over with different configurations as opposed to parsing just once (which other libraries might necessitate for performance cost reasons).

Note that only one instance of a plugin can be registered and that plugins will be executed in the order that they are registered. Thus **order can be extremely important** for rework operations.

### Output

Use the `StyleWriter` plugin to write the processed CSS.

```java
StyleWriter verbose = StyleWriter.verbose();

Omakase.source(input)
    .request(verbose)
    .process();

String out = verbose.write();
```

You can also write to an `Appendable`

```java
StyleWriter verbose = StyleWriter.verbose();

Omakase.source(input)
    .request(verbose)
    .process();

StringBuilder builder = new StringBuilder();
String out = verbose.write(builder);
```
joo
By default, CSS is written out in _inline_ mode. Other availables modes include _verbose_ and _compressed_. _Verbose_ mode will output newlines, spaces, comments, etc... _Inline_ mode will write each rule on a single line. _Compressed_ mode will eliminate as many characters as possible, including newlines, spaces, etc...

```java
StyleWriter verbose = StyleWriter.verbose();
StyleWriter inline = StyleWriter.inline();
StyleWriter compressed = StyleWriter.compressed();
```

In some cases you may want to write out an individual unit.

```java
Declaration declaration = new Declaration(Property.DISPLAY, KeywordValue.of(Keyword.NONE));
StyleWriter.compressed().writeSnippet(declaration);
```

You can override how any individual syntax unit is written. You could use this to prepend or append certain content, for example. For examples on how to do this see the "Custom writers" section below.

### Validation

In Omakase, _validation_ refers to both actual syntax validation (e.g., that the arguments to an `rgba` function are well-formed) as well as what is commonly known as _linting_ (e.g., that fonts are specified using relative units instead of pixels).

All validation is written and registered as plugins. When syntax validation is desired you usually want to enable every standard validation plugin as well as auto-refinement on everything

```java
Omakase.source(input)
    .request(new AutoRefiner().all())
    .request(Validation.normal())
    .process();
```

This auto-refines every selector and declaration (see the "Specifying common plugins" section below for more information) and registers the basic list of built-in validators.

In addition to syntax validation you can optionally register built-in linters as well:

- TODO add list of linters 

Validation methods will always occur after rework methods, but otherwise they will be executed in the order that the class was registered.

You can also add your own custom validators. For examples see the "Custom validation" section below.

### Registering plugins

When specifying plugins there are important details to keep in mind:

- Only one instance of a plugin can be registered to a single parse operation.
- Plugins methods of the same subscription type (preprocess, rework, or validate) will be executed in the order that the class is registered.
- `@PreProcess` annotated methods are executed first, then `@Rework` methods, and finally `@Validate` methods. Essentially this means that all rework will happen before validation, regardless of the order in which the plugins are registered. However if "Plugin A" is registered before "Plugin B" then "Plugin A"'s rework will execute before "Plugin B"'s, and likewise for validation.

There are **two** particular standard plugins that you need to be aware of, even if you don't create any custom plugins yourself.

#### SyntaxTree

The `SyntaxTree` plugin is responsible for, well creating the syntax tree. The phrase "syntax tree" refers to the data structure of the hierarchically and relationally organized list of objects representing the CSS source code. In basic form the syntax tree can be thought of like this:

- Stylesheet
    - Rule 1
        - Selector 1
        - Selector 2
        - Declaration Block
            - Declaration 1
            - Declaration 2
    - Rule 2
        - ...

Without the `SyntaxTree` plugin registered then processing will essentially just create a stream of `Selector`, `Declaration` and `AtRule` objects only. `Stylesheet` and `Rule` objects will not be created. `Selector` and `Declaration` objects will not be aware of their relationship or order with respect to other `Selector` and `Declaration` objects. In order to utilize this information a `SyntaxTree` plugin instance must be registered.

Plugins that depend on this information will register the dependency themselves and you will not have to worry about it. However in some cases you may want to get direct access to the tree itself

```java
SyntaxTree tree = new SyntaxTree();
List<Plugin> validation = Validation.normal();

Omakase.source(input)
    .request(validation)
    .request(tree)
    .process();

Stylesheet stylesheet = tree.stylesheet();
System.out.println("#statements = " + stylesheet.statements().size());
```

#### AutoRefiner

The `AutoRefiner` plugin is responsible for automatically refining `Refinable` objects. Currently this includes `Selector`, `Declaration`, and `AtRule`. _Refinement_ refers to the process of taking a generic syntax string (e.g., ".class > #id") and parsing out the individuals units (e.g., `ClassSelector`, `Combinator`, `IdSelector`). 

It is important to realize that unless refinement occurs, the unrefined syntax object may contain invalid CSS. For example a `Selector` may contain raw content consisting of ".class~!8391", but no errors will be thrown until `#refine()` is called on that selector instance. The `AutoRefiner` plugin can handle automatically calling the `#refine()` method on any and everything is refinable, thus enabling this additional level of syntax validation across the board. 

```java
AutoRefiner all = new AutoRefiner().all(); // refine everything
AutoRefiner selectors = new AutoRefiner().selectors(); // refine all selectors
AutoRefiner declarations = new AutoRefiner().declarations(); // refine all declarations
```

Many plugins will automatically register an `AutoRefiner` anyway, but it's important to register it yourself if you need to ensure that the unrefined objects are validated. 

Note that there are some cases when you don't need auto-refinement. For example, if you have already validate the CSS content during a build step, but now you are parsing the source code again in production to change a few dynamic values.

### Creating custom plugins

In addition to the standard library plugins, you can create and register your own custom plugins as well. Custom plugins allow you to rework the processed CSS or add your own custom validation and linting rules. 

Plugins are essentially plain java objects that implement one of the _plugin interfaces_ and define one or more _subscription methods_ to a particular AST object (e.g., `Selector` or `Declaration`). The subscription method does the actual rework or validation as appropriate.

Plugins are registered exactly the same as as any of the standard built-in plugins such as `StyleWriter` or `SyntaxTree`.

See the "Subscribable Syntax Units" section below for the definitive list of all subscribable AST objects.

#### Plugin interfaces

To get started, a plugin must first implement one of the _plugin interfaces_, listed as follows:

- **Plugin** - the most basic plugin; essentially just a marker interface
- **DependentPlugin** - for plugins that have dependencies on other plugins
- **BroadcastingPlugin** - for plugins that need access to a broadcaster
- **PreProcessingPlugin** - for plugins that need notification before and after _preprocessing_
- **PostProcessingPlugin** for plugins that need notification after all processing has completed

Most plugins will implement just the `Plugin` or `DependentPlugin` interface.

#### Custom rework

The term _rework_ refers to the process of changing the processed CSS source code, e.g., changing a class name, adding a declaration, removing a rule, etc... Omakase's rework capabilities allow you to easily change nearly any aspect of the CSS. You can add, change or remove selectors, add, change or remove declarations, add, change or remove rules, and... you get the picture. 

Each AST object contains setters and getters for working with its values. In addition there are several utility classes to make everything as convenient as possible. For Omakase, reworking the CSS tree is a first-class operation and is fully supported.

Here is an example of a simple rework operation to prefix every class with a class name called "myPrefix"

```java
public class PrefixAllSelectors implements Plugin {
    @Rework
    public void prefixClass(Selector selector) {
        // ensure the selector is segmented into the individual simple selector parts
        selector.refine();

        // create a new class simple selector
        ClassSelector prefix = new ClassSelector("myPrefix");

        // add the class simple selector to the beginning
        selector.parts().prepend(prefix);
    }
}
```

Notice the following details:

- It implements `Plugin`
- It has one subscription method with the `@Rework` annotation
- The subscription method has one argument, which is the AST object to be reworked

This is the general pattern of performing rework. The class adds as many methods as desired, each with the `@Rework` annotation and a single argument which is the type of syntax unit to rework. The method will be automatically invoked by the framework, allowing you to perform any operations on the given unit.

Actually this example is not quite complete. This plugin should actually be a `DependentPlugin` because it cares about the order of the selectors (it uses the `prepend` method). The class should look like this

```java
public class PrefixAllSelectors implements DependentPlugin {
    @Override
    public void dependencies(PluginRegistry registry) {
        registry.require(SyntaxTree.class);
        registry.require(AutoRefiner.class).selectors();
    }

    @Rework
    public void prefixClass(Selector selector) {
        // create and add a new class selector to the beginning
        selector.parts().prepend(new ClassSelector("myPrefix"));
    }
}
```

By implementing `DependentPlugin` instead, the `#dependencies` method will be called automatically, giving us a chance to declare any plugins that our code requires.

First we require a `SyntaxTree`, because without it our call to `#prepend` won't work. Second, since we require an `AutoRefiner` and have it automatically refine all `Selector` objects. We want to do this because we can't modify a `Selector` until it is refined anyway. 

See the "Dependent plugins" section below for more details on dependencies. For more examples on performing rework see the `ReworkTest.java` class. 

##### Dynamic AST creation and modification

Here are some code examples illustrating some common operations during rework

```java
// create a new selector
Selector selector = new Selector(new ClassSelector(".test"));

// another example
IdSelector theId = new IdSelector("main");
ClassSelector theClass = new ClassSelector("inner");
Selector selector = new Selector(theId, Combinator.descendant(), theClass);

// append a simple selector part to a selector
selector.parts().append(new ClassSelector("another-class"));

// append a selector to a rule
rule.selectors().append(myNewSelector);

// alternative to above
someSelector.append(myNewSelector);

// change a value on a class selector
myClassSelector.name("the-new-name");

// create a new declaration
Declaration declaration = new Declaration(Property.DISPLAY, KeywordValue.of(Keyword.NONE));

// another example
PropertyName name = PropertyName.using("new-prop");
Declaration declaration = new Declaration(name, KeywordValue.of(Keyword.NONE));

// another example
NumericalValue val1 = NumericalValue.of(1, "px");
NumericalValue val2 = NumericalValue.of(5, "px");
PropertyValue value = TermList.ofValues(TermOperator.SPACE, val1, val2);
Declaration declaration = new Declaration(Property.DISPLAY, value);

PropertyName prop = PropertyName.using(Property.BORDER_RADIUS).prefix(Prefix.WEBKIT);
Declaration declaration = new Declaration(prop, KeywordValue.of(Keyword.NONE));

// append a declaration to a rule
rule.declarations().append(myNewDeclaration);

// create a new rule
Rule rule = new Rule();
rule.selectors().append(new Selector(new ClassSelector("new")));
rule.declarations().append(new Declaration(Property.COLOR, HexColorValue.of("#fff")));
rule.declarations().append(new Declaration(Property.FONT_SIZE, NumericalValue.of(1).decimalValue(5).unit("em")));

// check if a declaration is for a specific property name
if (declaration.isProperty(Property.DISPLAY)) {...}
if (declaration.isProperty("new-prop")) {...}

// check if a declaration has a value
PropertyValue value = declaration.propertyValue();

Optional<KeywordValue> keyword = Value.asKeyword(value);
if (keyword.isPresent()) {...}

// use the method appropriate to what value you think it is (say, based on the property name)
Optional<HexColorValue> color = Value.asHexColor(value);
Optional<NumericalValue> number = Value.asNumerical(value);
Optional<StringValue> string = Value.asString(value);
Optional<TermList> termList = Value.asTermList(value);

// change a property value
Optional<KeywordValue> keyword = Value.asKeyword(declaration.propertyValue());
if (keyword.isPresent()) {
    keyword.get().keyword(Keyword.NONE);

}

// remove (detach) a selector, declaration, rule, etc...
someSelector.detach();
someDeclaration.detach();
someRule.detach();

// for more examples see the many unit tests
```
Keep in mind that dynamically created units will be automatically delivered to all `@Rework` subscriptions interested in the syntax unit's type, as well as to `@Validate` subscriptions later on. 

#### Custom validation

Besides rework, you can also register _subscription methods_ to perform validation and linting. Just like rework, you declare a method with the first parameter being the type of syntax unit you would like to validate. In addition there is a second parameter which is the `ErrorManager`.

Here is an example of a class with two validation subscription methods

```java
public class Validations implements Plugin {
    @Validate
    public void validateRelativeFonts(Declaration declaration, ErrorManager em) {
        if (declaration.isDetached()) return;
        if (!declaration.isProperty(Property.FONT_SIZE)) return;

        Optional<NumericalValue> number = Value.asNumerical(declaration.propertyValue());
        if (!number.isPresent()) return;

        if (number.get().unit().equals("px")) {
            em.report(ErrorLevel.FATAL, declaration, "Font sizes must use relative units");
        }
    }

    @Validate
    public void validateIncludesUnprefixed(Declaration declaration, ErrorManager em) {
        if (declaration.isDetached()) return;

        PropertyName propertyName = declaration.propertyName();

        if (propertyName.isPrefixed()) {
            boolean found = false;
            String expected = propertyName.unprefixedName();

            for (Declaration d : declaration.group().get()) {
                if (d.isProperty(expected)) {
                    found = true;
                }
            }

            if (!found) {
                em.report(ErrorLevel.WARNING, declaration, "Prefixed declaration without unprefixed version");
            }
        }
    }
}
```

Keep in mind that all validators runs after the rework phase has been completed. Validation methods should **not** change any aspect or content of the objects they are validating. They should only check values and reports errors as applicable.

#### Dependent plugins

As mentioned above, many plugins, especially ones with `@Rework` will need to register dependencies on other plugins. 

You must have a dependency on `SyntaxTree` if you are subscribing to `Rule` or `Stylesheet` objects. You also have this dependency if you do any kind of checking or operation that the relationship of units between one another, for example `prepend`, `append`, `isFirst`, `isLast`, and so on. In other words, all operations that utilize `SyntaxCollection` objects. 

You also will have a dependeny on `AutoRefiner` in many cases where you have subscriptions to syntax unit types more specific than `Selector` and `Declaration`. For example `ClassSelector`, `IdSeletor`, `HexColorValue`, and so on. All of these requirements are documented for each individual syntax unit type in the "Subscribable Syntax Units" section below. 

If your plugin only needs refined selector units then `AutoRefiner#selectors()` will suffice, or `AutoRefiner#declarations()` for declarations. To nab everything then you can use `AutoRefiner#all()`.

Here is an example of a plugin with dependencies

```java
public class Dependent implements DependentPlugin {
    @Override
    public void dependencies(PluginRegistry registry) {
        registry.require(SyntaxTree.class);
        registry.require(AutoRefiner.class).selectors();
    }
}
```

The `#require` method takes the class of the plugin. If the plugin is already registered then the registered instance is simply returned. Otherwise one is automatically created and added to the registry. You can then proceed to configure the plugin as necessary for your use case. 

You can also require your own custom plugins by using the `#require(Class, Suppier)` method.

There is another, more performant alternative to using `AutoRefiner`. You can instead subscribe to the high-level type, such as `Declaration`. You can check some information on the declaration first, such as `Declaration#isProperty` or check for an annotation on the declaration. If present then you can proceed to call `Declaration#refine`, which will automatically trigger any subscriptions to the more specific syntax unit types parsed within that declaration instance. This way you can avoid uncessarily refining declarations that you have no interest in.

#### Performing both rework and validation

Note that any particular plugin can have as many `@Rework` and `@Validate` annotated methods as it needs. That is, rework and validation does not need to be separated out in to multiple classes.

You can also subscribe to the exact same syntax type in multiple methods. However there is no guarantee to the execution order of subscription methods to the exact same type for the exact same operation (e.g., rework or validate). This means, for example, that if two `@Rework` methods subscribed to `ClassSelector` are needed, and that execution order is important, then these methods should be separated out into their own classes. The classes should then be registered in the intended execution order. 

#### Subscribable types

Not only can you subscribe to concrete types such as `ClassSelector` and `IdSelector`, you can also subscribe to higher-level interfaces such as `Statement`, `Refinable`, `SimpleSelector` or even the top-level `Syntax` interface.

Subscribing to an interface type will allow you to receive all instances of that type, which can be useful in certain scenarios. For example, `AutoRefiner` only has one subscription to `Refinable` instead of having to add a method for `Declaration`, `Selector`, etc...

Within a particular class, the more specifically-typed subscription will be delivered before the more generally-typed subscriptions. For example, in a class with subscriptions to `ClassSelector`, `SimpleSelector` and `Syntax`, the methods will always be invoked in that exact order. 

See the "Subscribable Syntax Units" section below for the definitive list of all subscribable AST objects.

#### Base plugin

You can extend the `BasePlugin` class, which comes with a predefined subscription method for each subscribable syntax unit type. Override the particular methods as appropriate for your use case. While this isn't the most preferred implementation option, you may find it easier to consume as a starting point for a custom plugin.

### Custom error handling

TODO

### Custom writers

TODO

### Repeated parsing

TODO

In some cases you may want to parse some CSS code, store it off, and conditionally parse it again (and again).

### Annotations and comments

TODO

### Using built-in preprocessor plugins

TODO

Subscribable Syntax Units
-------------------------

Following is the list of all supported syntax types that you can subscribe to in `@Rework`, `@Validate` and `@PreProcess` annotated methods. Keep in mind that many syntax units require _refinement_ or the `SyntaxTree` plugin before they will be delivered. More information on this is available in the Usage section above.

<pre>
    Name                        Description                                               Enablement / Dependency     Type
    -------------------------   -------------------------------------------------------   -------------------------   ---------------
01: Refinable                   raw syntax that can be further refined                    Automatic                   interface
02: Statement                   rule or at-rule                                           SyntaxTree                  interface
03: Syntax                      parent interface of all subscribable units                Under certain conditions*   interface
04: OrphanedComment             A comment unassociated with any syntax unit               Under certain conditions*   class
05: Rule                        (no description)                                          SyntaxTree                  class
06: Stylesheet                  (no description)                                          SyntaxTree                  class
07: AtRule                      (no description)                                          Automatic                   class
08: Declaration                 (no description)                                          Automatic                   class
09: PropertyValue               interface for all property values                         Declaration#refine          interface
10: Term                        a single segment of a property value                      Declaration#refine          interface
11: FunctionValue               individual function value                                 Declaration#refine          class
12: HexColorValue               individual hex color value                                Declaration#refine          class
13: KeywordValue                individual keyword value                                  Declaration#refine          class
14: NumericalValue              individual numerical value                                Declaration#refine          class
15: StringValue                 individual string value                                   Declaration#refine          class
16: TermList                    default, generic property value                           Declaration#refine          class
17: SelectorPart                group interface for all selector segments                 Selector#refine             interface
18: SimpleSelector              parent interface for simple selectors                     Selector#refine             interface
19: AttributeSelector           attribute selector segment                                Selector#refine             class
20: ClassSelector               class selector segment                                    Selector#refine             class
21: Combinator                  combinator segment                                        Selector#refine             class
22: IdSelector                  id selector segment                                       Selector#refine             class
23: PseudoClassSelector         pseudo class selector segment                             Selector#refine             class
24: PseudoElementSelector       pseudo element selector segment                           Selector#refine             class
25: Selector                    (no description)                                          Automatic                   class
26: TypeSelector                type/element selector segment                             Selector#refine             class
27: UniversalSelector           universal selector segment                                Selector#refine             class

Generated by SubscribableSyntaxTable.java
</pre>

**Notes:**

* A subscription to `Syntax` will depend on which concrete syntax classes are enabled. To get _every_ syntax unit then utilize `AutoRefiner#all`.
* Some orphaned comments will only be delivered if selectors and declarations are refined.

Development and Contribution
----------------------------

Before checking *anything* in, setup your IDE to conform to project standards. See and follow the instructions in the readme.md files inside of the `idea` or `eclipse` folders.

As of right now the (strongly) preferred IDE for contribution is intellij IDEA. This is mainly because the existing source code and style closely conforms to the idea settings included in the project. If you use eclipse or something else then be sure to following the existing coding conventions manually if need be.

Once you get everything set up, read the Project Architecture section (found below) so you can start to get an idea of how things work and are organized.

### Building

The project relies on the following technologies:

1. git (duh)
2. java 7 (make sure both the IDE and maven are setup to use it) 
3. maven 3+

run `mvn clean install` to get things going from the command line. It should build and run tests successfully. Afterwards you can import the maven project into your IDE and go from there.

### Dependencies

Non-test dependencies include Google's **Guava** library and **Logback** (used for logging). Dependencies shouldn't really increase beyond that as one of the goals is simplicity and self-containment.

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

There are several enums such as `Keyword.java` that contain values that will inevitably need to be updated. There are actually some tools under `com.salesforce.omakase.test.util.tool` that should be used to assist with this.

### Scripts

You can run some utility classes or perf tests by using the scripts under `bin`. For example

    bin/run.sh

or

    bin/perf.sh

Architecture
------------

Omakase is a CSS parser built from the ground up. Unlike other open-source CSS parsers it is not built on a parser generator such as JavaCC or Antlr. Instead, it relies on many small and simple java objects that know how to consume various parts of CSS syntax.

The main motivation behind building a new parser over utilizing an existing library include achieving better runtime performance and better support for dynamically reworking CSS trees.

The project requires _Java 7_, _git_ and _maven_. The general architecture of the project can be summarized as follows:

1. **Parsers** - Small, individual parser objects that process CSS source code.
2. **AST Objects** - Simple representations of various CSS syntax units.
3. **Plugins** - Observers that can subscribe to any AST object for rework or validation.
4. **Broadcasters and Emitter** - The bridge between parsers and plugins.
5. **Writers** - Outputs parsed CSS code.
6. **ErrorHandlers** - Manages errors encountered when parsing CSS source code.

### Parsers

2-level parsing.

### AST Objects

POJOs with getters and setters. In most cases no validation (validation should be done with plugins instead for greater flexibility).

### Plugins

### Broadcasters

### Writers

### ErrorHandlers

Omakase is not a thread-safe parser. That is, things will most definitely blow up if you try to reuse the same AST objects or SyntaxTree in multiple threads.

