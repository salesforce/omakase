Steps
=====

Plugins
-------
For some of the following instructions to work, ensure you have the following plugins enabled:

1. Copyright
2. Inspection Gadgets

Settings
--------
1. file -> import settings -> choose idea/settings.jar
2. settings -> code style -> choose scheme 'Omakase'
3. ok

Inspections
-----------
1. settings -> inspections -> import -> choose idea/Omakase.xml
    1. ensure that 'Omakase' is chosen in the dropdown on left
2. a bunch of inspections in the list should be highlighted as blue
3. ok

Note: when running 'Inspect Code', ensure that the 'Omakase' inspection profile is chosen.

Copyright
---------
1. preferences -> Copyright -> Copyright Profiles
2. click the '+' sign
3. enter 'Omakase' as the name
4. enter copyright text as follows:

Copyright (C) $today.year salesforce.com, inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

 5. Click 'Apply'
 6. settings -> Copyright
    1. under 'Default project copyright' choose 'Omakase'
 7. settings -> Copyright -> Formatting
    1. 'Use block comment' *selected*
    2. 'Prefix each line' *selected*
    3. 'Before other comments' *selected*
    4. 'Add blank line after' *selected*

Dictionaries
------------
1. settings -> spelling -> dictionaries (tab)
2. click '+' -> choose idea/dictionaries
3. ok

File Encodings
--------------
1. File encodings should be UTF-8 (preferences -> file encodings)

TODOs
-----
1. settings -> TODO -> ensure you have all of the following (case insensitive):

\btodo\b.*
\bfixme\b.*
\bxxx\b.*
\btestme\b.*

Optional
--------
1. Remove file header (preferences -> includes -> File Header -> clear content)
2. Useful to make projects automatically (preferences -> compiler -> make project automatcally)

