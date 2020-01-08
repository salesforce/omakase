Deployment
==========

For your first time, read the full details at http://sfdc.co/omakase-deploy.
Then use these tips as a quick reminder.

Quick Tips
----------
1. Ensure all relevant changes are committed and pushed.
2. If this is the first version in a major release, manually update the snapshot version (see versioning notes below).
2. Run `mvn release:clean release:prepare` to tag and bump version.
3. Ensure CI passes on github and everything looks good.
4. Run `mvn release:perform -P release` to push to sonatype nexus and maven central.
5. Update the dependency version in aura.
6. Update the dependency version in core.

Versioning Notes
----------------
We use [semantic versioning](https://semver.org/), but with some considerations for the Salesforce release cycle.

For the first commit in a Salesforce release it's recommended to manually increment the minor number (or the major number if the changes are not backwards compatible). This way, changes can be made in patch independently from main and life will be easy.

You can do this by manually updating pom.xml, or using `mvn versions:set -DnewVersion=1.2.3`.

For example let's say prod is on `222`, and the Omakase version in prod is `1.4.4`, and the current pom has `1.4.5-SNAPSHOT`. When making the first commit for `224`, first manually change the version to `1.5.0-SNAPSHOT`. Then you can continue with the above process as normal. Emergency fixes in patch can continue to be made under `1.4.x`.

Backwards-incompatable and API-breaking changes should only be done in the next major Salesforce release, not in patch, and you must increment the major number manually.

Quick Links
-----------

- [Sonatype Release Docs](https://central.sonatype.org/pages/apache-maven.html)
- [Sonatype Nexus Repo Manager](https://oss.sonatype.org/#nexus-search;gav~~omakase~~~)
- [Maven Central](https://search.maven.org/search?q=g:com.salesforce%20AND%20a:omakase)
- [Published Versions](https://repo1.maven.org/maven2/com/salesforce/omakase/)
