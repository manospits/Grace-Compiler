#!/bin/bash

for grprogr in $(find ../examples/ -iname '*.grace' -printf "%f\n"); do
    cat ../examples/$grprogr | java -jar ../target/compiler-1.0-SNAPSHOT.jar > ./example_CSTs/$grprogr.cst
done
