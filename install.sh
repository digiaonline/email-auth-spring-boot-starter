#!/bin/bash

mvn package && mvn org.apache.maven.plugins:maven-install-plugin:2.5.2:install-file -Dfile=target/email-auth-spring-boot-starter-0.0.1-SNAPSHOT.jar.original -DpomFile=pom.xml
