Deployment
==========

Getting Started
---------------
If this is your first time doing this, read the full details at http://sfdc.co/omakase-deploy **first**.
Then you can use the tips here as a quick reminder.


Quick Tips
----------
1. Ensure all relevant changes are committed and pushed.
2. If this is the first version in a major release, manually update the snapshot version (see versioning notes below).
2. Run `mvn release:clean release:prepare` to tag and bump version.
3. Ensure CI passes on github.com and everything looks good.
4. Run `mvn release:perform -P release` to push to sonatype nexus and maven central.
5. Update the dependency version in aura.
6. Update the dependency version in core.

Versioning Notes
----------------
We use [semantic versioning](https://semver.org/), but with some considerations for the Salesforce release cycle.

For the first commit in a Salesforce release it's recommended to manually increase the minor number (or the major number if the changes are not backwards compatible). This way, if later a change is needed in a Salesforce patch, but new work has already been done in the next release, the patch number can be updated and life will be easy.

You can do this by manually updating pom.xml, or using `mvn versions:set -DnewVersion=1.2.3`.

For example, if prod is on `222`, the Omakase version in prod is `1.4.4`, and the current pom has `1.4.5-SNAPSHOT`, when making the first commit for `224` then first change the version to `1.5.0-SNAPSHOT`. Emergency fixes in patch can continue to be made under `1.4.x`. Then you can continue with the above process as normal.

Backwards-incompatable and API-breaking changes should only be done in the next major Salesforce release, not in patch, and you must increment the major number manually.

Other Notes
-----------

In the configuration for the `nexus-staging-maven-plugin` in the pom.xml, if you change `autoReleaseAfterClose` to false then you can stage the change on sonatype nexus before actually releasing to maven central. More info can be found at http://sfdc.co/omakase-deploy.

Quick Links
-----------

- [Sonatype Release Docs](https://central.sonatype.org/pages/apache-maven.html)
- [Maven Central](https://search.maven.org/search?q=g:com.salesforce%20AND%20a:omakase)
- [Sonatype Nexus Repo Manager](https://oss.sonatype.org/#nexus-search;gav~~omakase~~~)
- [Published Versions](https://repo1.maven.org/maven2/com/salesforce/omakase/)
