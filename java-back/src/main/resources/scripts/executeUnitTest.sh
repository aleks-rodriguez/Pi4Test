#!/bin/bash
version=`sed -n '/<java.version>/p' pom.xml` #Extraemos la linea "<java.version>"
version=`echo $version | sed 's/\r$//'` #Eliminamos el retorno de linea \r
version=`echo $version | sed 's/^[ \t]*//;s/[ \t]*$//'` #Eliminamos espacios en blanco y tabuladores \t
version=`echo $version | cut -c15- | rev | cut -c16- | rev` #Cortamos, giramos y cortamos

if [[ "$version" == 1.8 ]]
then
    JAVA_HOME=$HOME/JDK/jdk1.8.0_202 mvn -Dmaven.test.failure.ignore=true clean test
elif [[ "$version" == 8 ]]
then
    JAVA_HOME=$HOME/JDK/jdk1.8.0_202 mvn -Dmaven.test.failure.ignore=true clean test
elif [[ "$version" == 11 ]]
then
    JAVA_HOME=$HOME/JDK/jdk-11.0.13 mvn -Dmaven.test.failure.ignore=true clean test
else
    mvn -Dmaven.test.failure.ignore=true clean test
fi
