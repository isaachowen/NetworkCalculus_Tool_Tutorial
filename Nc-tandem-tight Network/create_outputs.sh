#!/bin/bash
for i in `seq 1 20`
do
	./nc-tandem-tight tandem${i}.txt output_${i}.lp
	echo ${i}
done
