#!/bin/bash
rondas=$(ls $1 | wc -l);

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

for ((i=0; i<$rondas; i=i+1))
do
	./grafica-utilidad.sh $1"SCN"$(tostring $i)"/";
done;
	
