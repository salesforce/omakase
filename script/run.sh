#!/bin/bash
if [ ! -d "target/dependency" ]; then
  echo "Please run 'mvn dependency:copy-dependencies' then try again."
  exit
else
  echo "If dependencies have been updated then please run 'mvn dependency:copy-dependencies' first"
fi

MAINCLASSES=$(grep " main *(" -R src/test/java/ | grep -v "\.git" | cut -d ':' -f 1 | sed 's+src/test/java/++;s/.java$//' | tr / . | sed 's/^.//')
select KLASS in ${MAINCLASSES}
do
  if [ -z "$KLASS" ]; then
    if [ "$REPLY" == "quit" ]; then
      break
    else
      echo "unknown option"
      continue
    fi
  fi

  java -Xms512m -Xmx4G -cp target/dependency/*:target/classes:target/test-classes $KLASS
  break
done



