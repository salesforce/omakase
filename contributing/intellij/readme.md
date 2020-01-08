Steps
=====

_The following steps have been reviewed as of IntelliJ v. 15.0.2_

Plugins
-------
Ensure you have these plugins enabled:

1. Copyright
2. Maven Integration

Optionally (depending on what projects you are working with) you should also enable:

1. CSS Support
2. YAML
3. FreeMarker Support
4. JUnit
5. Git Integration


Project
-------

Import the project into IntelliJ:

1. Click Import Project
2. Select the top-level omakase folder
3. ok

Go through the prompts choosing the options as you see fit (most defaults are fine).
Choose the 1.8 SDK.

Ensure that your project is using Java 1.8 or above, e.g.,

1. Right-click on the omakase module in the Project view
2. Choose Module Settings
3. Select the Sources tab
4. Under Language Level, choose 8.0
5. Click on Project, under Project Settings in the left-most panel
6. Project SDK should be 1.8
7. Project language level should be SDK Default, or 8

Settings
--------

These settings will ensure you have the correct code style, formatting, indentation, inspections etc...

*IMPORTANT*: it's possible that importing these settings may change general options that are not specific to the Omakase module as well. This may be important to you if you use IntelliJ for other projects. Beware! (I've done my best to not export any settings that might do this).

1. file -> import settings -> choose contributing/intellij/settings.jar (restart editor)
2. preferences -> editor -> code style -> choose scheme 'Omakase'
3. ok

Alternatively, just go to editor -> code style -> import scheme and choose contributing/intellij/Omakase Code Style.xml.

If you don't import settings, the most important thing is that you use 4 spaces instead of tabs, and a right margin of 130. There are other settings for Java formatting, in which case you either need to manually duplicate or otherwise ensure any code you check in conforms with the existing code.

Inspections
-----------

1. preferences -> editor -> inspections -> manage -> import -> choose contributing/intellij/Omakase.xml
    1. ensure that 'Omakase' is chosen in the dropdown on left
2. a bunch of inspections in the list should be highlighted as blue
3. ok

Note: when running 'Inspect Code', ensure that the 'Omakase' inspection profile is chosen.

Copyright
---------
1. preferences -> editor -> copyright -> copyright profiles
2. click the '+' sign
3. enter 'Omakase' as the name
4. enter copyright text as follows:

Copyright (c) $today.year, salesforce.com, inc.
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided
that the following conditions are met:

   Redistributions of source code must retain the above copyright notice, this list of conditions and the
   following disclaimer.

   Redistributions in binary form must reproduce the above copyright notice, this list of conditions and
   the following disclaimer in the documentation and/or other materials provided with the distribution.

   Neither the name of salesforce.com, inc. nor the names of its contributors may be used to endorse or
   promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.

5. under 'Allow replacing copyright...' add 'Copyright'
6. click 'Apply'
7. preferences -> editor -> copyright
    1. under 'Default project copyright' choose 'Omakase'
    2. click 'Apply'
8. preferences -> editor -> copyright -> formatting
    1. 'Use block comment' *selected*
    2. 'Prefix each line' *selected*
    3. 'Before other comments' *selected*
    4. 'Add blank line after' *selected*
9. ok

Dictionaries
------------
1. preferences -> editor -> spelling -> dictionaries (tab)
2. click '+' -> choose idea/dictionaries
3. ok

File Encodings
--------------
1. File encodings should be UTF-8 (preferences -> editor -> file encodings)

TODOs
-----
1. settings -> TODO -> ensure you have all of the following (case insensitive):

\btodo\b.*
\bfixme\b.*
\bxxx\b.*
\btestme\b.*

Optional
--------
1. Remove file header (preferences -> editor -> File and Code Templates -> includes -> File Header -> clear content) OR change file header to something like this:
/**
 * TODO description
 *
 * @author yourname
 */
2. Useful to make projects automatically (settings -> compiler -> make project automatically)

Reload
------
Reload IDEA after making these changes.
