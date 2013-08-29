#!/bin/bash
svn up

ant jar > /dev/null
cp dist/Top20TargetGenerator.jar .

PWD=$(pwd)
LIBDIR=${PWD}/lib/
LIBFILES="."

for  FILE in $(find ${LIBDIR});
do
        LIBFILES=${LIBFILES}:${FILE}
done

#echo java  -Djava.library.path=${LIBFILES} -classpath ${LIBFILES} build.classes.keywordgenerator.main
java -jar  -Djava.library.path=${LIBFILES} -classpath ${LIBFILES} Top20TargetGenerator.jar

