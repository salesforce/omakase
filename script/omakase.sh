#!/bin/bash
if [ ! -d "target/dependency" ]; then
  echo "Building and copying maven dependencies..."
  mvn clean install dependency:copy-dependencies -DskipTests
  echo ""
fi


java -Xms512m -Xmx4G -cp target/dependency/*:target/classes:target/test-classes com.salesforce.omakase.test.util.tool.Run "$@"
