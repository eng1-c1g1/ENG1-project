#!/bin/bash

# compile:
./gradlew lwjgl3:dist 

# run resulting jar:
java -jar lwjgl3/build/libs/Maze11-1.0.0.jar
