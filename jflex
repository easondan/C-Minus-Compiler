#!/bin/bash
export JAVA_HOME="${JAVA_HOME:-/usr/local/opt/openjdk/libexec/openjdk.jdk/Contents/Home}"
exec "${JAVA_HOME}/bin/java"  -jar "/usr/local/Cellar/jflex/1.9.1/libexec/jflex-1.9.1.jar" "$@"
