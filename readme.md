Omakase
=======

Fast and slightly opinionated CSS parser.

Omakase (お任せ o-ma-ka-say) is a Japanese phrase that means "I'll leave it to you" (from Japanese "to entrust"). In Japan it can be used at any restaurant.

Features
--------

The two main goals of Omakase are speed and flexibility.

### Focus on speed

Omakase is written with runtime usage needs in mind. While most CSS tools are intended to be used on the command line or at build time, Omakase takes into consideration the additional needs of runtime-level usage.

2-level parsing accounts for various scenarios and use-cases. The first level separates the source code into selectors and declarations only. The (optional or targeted) second level can be conditionally applied to specific selectors and declarations or all of them depending on applicability. 

### Focus on flexibility

Omakase is a network of *plugins*. Plugins can subscribe to specific CSS syntax units to perform custom validation and/or rework any part of the CSS. The plugin framework is so flexible that much of library-provided functionality and APIs are written using the same plugin infrastructure (SyntaxTree generation, linting, validation, and more).

### Better error messaging

Because Omakase doesn't use a generic parser generator, the error messages are all CSS specific and sometimes easier to understand.

### Awesome standard plugins

Omakase comes with some nifty plugins out of the box that mirror the often-touted "CSS Preprocessor" functionality.

- Theme variables
- Automatic vendor prefixing
- right-to-left swapping
- CSS annotations
- mixins
- custom functions
- url cache busting for images

Usage
-----

Omakase can be used as a parser, a preprocessor, a linter, or all three. When using Omakase it's useful to keep in mind the pluggable nature of its architecture.

### Parsing basic CSS

TODO

### Output

TODO 

### Validation

TODO

### Specifying common plugins

TODO

### Creating custom plugins

TODO

See the "Subscribable Syntax Units" section below for the definitive list of all subscribable AST objects.

#### Custom rework

TODO

#### Custom validation

TODO

### Custom error handling

TODO

### Custom writers

TODO

### Repeated parsing

TODO

In some cases you may want to parse some CSS code, store it off, and conditionally parse it again (and again).

### Using built-in preprocessor plugins

TODO

Subscribable Syntax Units
-------------------------

<pre>
    Name                        Description                                               Enablement / Dependency     Type
    -------------------------   -------------------------------------------------------   -------------------------   ---------------
01: Refinable                   raw syntax that can be further refined                    Automatic                   interface
02: Statement                   rule or at-rule                                           SyntaxTree                  interface
03: Syntax                      parent interface of all subscribable units                Automatic                   interface
04: OrphanedComment             A comment unassociated with any syntax unit               Under certain conditions    class
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
17: SelectorPart                parent interface for all selector segments                Selector#refine             interface
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

Development and Contribution
----------------------------

Before checking *anything* in, setup your IDE to conform to project standards. See and follow the instructions in the readme.md files inside of the `idea` or `eclipse` folders.

As of right now the (strongly) preferred IDE for contribution is intellij IDEA. This is mainly because the existing source code and style closely conforms to the idea settings included in the project. If you use eclipse or something else then be sure to following the existing coding conventions manually if need be.

Once you get everything set up, the Project Architecture section (found below) so you can start to get an idea of how things work and are organized.

### Building

The project relies on the following technologies:

1. git (duh)
2. java 7 (make sure both the IDE and maven are setup to use it) 
3. maven 3+

run `mvn clean install` to get things going from the command line. It should build and run tests successfully. Afterwards you can import the maven projects into your IDE and go from there.

### Dependencies

Non-test dependencies include Google's Guava library and Logback (used for logging). Dependencies shouldn't really increase beyond that as one of the goals is simplicity and self-containment.

### Tests

Currently tests are built with junit 4 and [fest assertions](https://code.google.com/p/fest/#Fluent_Assertions). Junit should be self explanatory, but if you haven't used junit 4+ then keep in mind that it uses java _annotations_ instead of inheritance and method naming conventions.

Fest may be new to you, but it's quite simple and easy to get the hang of. If you have used hamcrest before then it's something like that. Basically it's a library of matchers and assertions. The actual assertions are fluent and look something like this:

    assertThat(someValue).isTrue();
    assertThat(someCollection).hasSize(3);
    assertThat(someCollection).containsExactly(value1, value2, value3);

This makes the tests more readable and also provides much more useful error messages out the box.

The important takeaway is that **all** unit tests must be written using fest assertions and not the junit/hamcrest ones. Just follow the patterns established in the existing tests. Particularly make sure you import the correct classes.

### Updating keywords, properties, etc...

There are several enums such as `Keyword.java` that contain values that will inevitably need to be updated. There are actually some tools under `com.salesforce.omakase.test.util.tool` that should be used to assist with this.

### Scripts

You can run some utilty classes or perf tests by using the scripts under `bin`. For example

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
6. **ErrorHandlers** - Managers errors encountered when parsing CSS source code.

### Parsers

2-level parsing.

### AST Objects

POJOs with getters and setters. In most cases no validation (validation should be done with plugins intead for greater flexibility).

### Plugins

### Broadcasters

### Writers

### ErrorHandlers

Omakase is not a thread-safe parser. That is, things will most definitely blow up if you try to reuse the same AST objects or SyntaxTree in multiple threads.

