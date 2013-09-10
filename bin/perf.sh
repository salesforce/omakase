#!/bin/bash
if [ ! -d "target/dependency" ]; then
  echo "Please run 'mvn dependency:copy-dependencies' then try again."
  exit
else
  echo "If dependencies have been updated then please run 'mvn dependency:copy-dependencies' first"
fi

java -Xms512m -Xmx4G -cp target/dependency/*:target/classes:target/test-classes com.salesforce.omakase.test.util.PerfTestLight
