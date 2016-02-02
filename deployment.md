Deployment
==========

Getting Started
---------------
If this is your first time doing this, read the full details at http://sfdc.co/omakase-deploy **first**.
Then you can use the tips here as a quick reminder.


Quick Tips
----------
1. ensure all relevant changes are committed and pushed
2. bump the version number in pom.xml, commit and push
2. tag the version number in git, and push the new tag
    1. > git tag -a vx.x.x (e.g., v0.6.0)
    2. > git push --tags <repo>
3. the jars must be deploy to **three** repositories:
    1. Internal aura maven repo
    2. External aura maven repo
    3. Internal sfdc maven repo
4. simplest way to do this is `omakase --deploy`
5. check in jars to p4
    1. run `blt --build update-repository-filter`
    2. add jars and relevant files to p4 changelist, and shelve

Version Numbering
-----------------
For the first commit in an sfdc release, change the second number to the release api version number.
e.g., for Summer '15 to Winter '16:

    0.34.0 to 0.35.0

For subsequent commits in an sfdc release/branch, increase the last number by one.
e.g., for a patch fix in Winter '16:
    
    0.35.0 to 0.35.1
    0.35.1 to 0.35.2
    0.35.9 to 0.35.10

Backwards-incompatable and API-breaking changes should only be done in the next major sfdc release, not in patch.
