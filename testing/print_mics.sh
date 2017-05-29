#!/bin/bash

for grprogr in $(find ../examples/ -iname '*.grace' -printf "%f\n"); do
    java -jar ../target/compiler-1.0-SNAPSHOT.jar ../examples/$grprogr > ./example_middlecodes/$grprogr.mic
done
