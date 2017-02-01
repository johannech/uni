#!/bin/bash
trap control_c SIGINT

control_c () {
	echo "Bye bye"
	exit 0
}

while true
do
	echo "$(date)"
done