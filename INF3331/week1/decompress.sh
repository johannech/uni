#!/bin/bash
if [ -z "$1" ] || [ -n "$2" ] ; #Hvis argument 1 er noe, men ikke argument 2
then
	echo "Error! Wrong number of arguments. Type -h or -help"
elif [ "$1" = "-h" ] || [ "$1" = "-help" ] ;
then
	echo "Write a Pathname for decompressing .gz files"
	echo "Example: home/Documents   <-- Will decompress all files in this directory"
else
	find "$HOME/$1" -name "*.gz" -exec gunzip "{}" ";"
	#"tar med $HOME for at brukeren skal slippe å skrive inn 'Users/(brukernavn)' først"
	#'*' fordi vi kan ha alle mulige navn som slutter med .gz
	#-exec utfører gzip-kommandoen med pathfilen
fi