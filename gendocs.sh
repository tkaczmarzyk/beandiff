#!/bin/bash

#rm -rf target/

echo -e "\n\n### overriding javadoc package ###\n"

docfile=$(ls target/*-javadoc.jar)
mv $docfile "$docfile.scala.jar"

sbt genjavadoc:package-doc

find target/sbt -type f -iname '*-javadoc.jar' -exec mv {} $docfile \;

if [ ! $? ]
then
	echo "unexpected error!"
	exit 1
fi

echo -e "\n\n### done ###\n"
