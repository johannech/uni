#!/bin/bash
if [ -z "$1" ] || [ -z "$2" ] || [ -n "$3" ] ;
then 
	echo "Error! Wrong number of arguments. Type -h or -help"
elif [ "$1" = "-h" ] || [ "$1" = "-help" ] ;
then
	echo "Write a Pathname and then a minimum size (Kb)"
	echo "Example: /Documents/INF3331 300000"
	echo "This will compress all files >= 300000Kb"
else
	find "$HOME/$1" -name "*" -type f -size "+$2k" -exec gzip "{}" ";"
	#"tar med $HOME for at brukeren skal slippe å skrive inn 'Users/(brukernavn)' først"
	#'*' fordi vi kan ha alle mulige navn
	#-type finner en ting for oss, og vi spesifiserer at den skal finne filer, med f-flagget
	#Har + før 2.argumentet for at alt over argumentets antall kilobytes
	#-exec utfører gzip-kommandoen med pathfilen
fi