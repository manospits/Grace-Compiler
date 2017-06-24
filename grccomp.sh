#!/bin/bash

program="$1"
lib="$2"

filename=$(basename "$program")
filename="${filename%.*}"

java -jar ./compiler-1.0-SNAPSHOT.jar $program
gcc -m32 "$filename".s $lib -o $filename



