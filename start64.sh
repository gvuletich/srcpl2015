#!/bin/bash

#javac -d .class/ -cp ./jade/lib/jade.jar:./lpsolve/lpsolve55j.jar:. src/*.java

tostring () {
	n=$1;
	if [ "$n" -lt 10 ]
	then
	   echo "00"$n;
	else 
		if [ "$n" -ge 10 -a "$n" -lt 100 ]
		then
	   		echo "0"$n;
		else
	   		echo $n;
		fi
	fi;
}

pass=""
rr=0
g=0

for arg in "$@"
do
    case "$arg" in
    n)	isn=true;    
        isg=false;
	iss=false;
	isd=false;
	isp=false;
	isr=false;
        ;;
    g)  isn=false;
        isg=true;
        iss=false;
        isd=false;
        isp=false;
	isr=false;
        ;;
    r)  isn=false;
        isg=false;
        iss=false;
        isd=false;
        isr=true;
        ;;
    s)  isn=false;
        isg=false;
        iss=true;
        isd=false;
        isp=false;
	isr=false;
        ;;
    d)  isn=false;
        isg=false;
        iss=false;
        isd=true;
        isp=false;
	isr=false;
    	;;
    p) 	isn=false;
	isg=false;
	iss=false;
	isd=false;
	isp=true;
	isr=false;
        ;;
    *)  if [ "$isn" = "true" ]
	then
	   	n=$arg;
		pass=$pass$n" "
		#echo "n vale "$arg;	
        else if [ "$isg" = "true" ]
        then
           	g=$arg;
		pass=$pass"g "$g" "
		#echo "g vale "$arg;
        else if [ "$iss" = "true" ]
        then
           	s=$arg;
		pass=$pass"s "$s" "
		#echo "s vale "$arg;
        else if [ "$isd" = "true" ]
        then
           	d=$arg;
		pass=$pass"d "$d" "
		#echo "d vale "$arg;
        else if [ "$isr" = "true" ]
        then
                rr=$arg;
                pass=$pass"r "$r" "
                #echo "r vale "$arg;

	else if [ "$isp" = "true" ]
        then
           	p=$arg;
		pass=$pass"p "$p" "
		#echo "p vale "$arg;
        fi;
	fi;
	fi;
	fi;
	fi;
	fi;
        ;;
    esac
done

echo "Estos son los parametros: "$pass;

if [ "$g" -gt 0 -a "$g" -lt 1000 ]
then
      java -cp ./jade/lib/jade.jar:./lpsolve64/lpsolve55j.jar:.class/  Platform $n "g" $g
fi; 

if [ "$rr" -gt 0 -a "$rr" -lt 1000 ]
then
	for (( i=0; i<$rr; i=i+1 ))
	do
		path=$pass"p ./xmls/SCN"$(tostring $i)"/"
		java -cp ./jade/lib/jade.jar:./lpsolve64/lpsolve55j.jar:.class/  Platform $path
	done
fi;
