#!/bin/bash
#export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-11.0.22.0.7-1.fc39.x86_64
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-17.0.9.0.9-3.fc39.x86_64
export JAVA_HOME=/home/linus/workspace/android-studio/jbr
./gradlew clean && ./gradlew --warning-mode all lint

