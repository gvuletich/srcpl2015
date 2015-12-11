#!/bin/bash

# SE EJECUTA EN /grafs Y SE LE PASA EL PATH DEL SCNXXX

path=$1
cd $path

echo "Estoy en:"$PWD

# Generar datos
nagen=$(head -n 1 ./info |tail -n 1 | awk '{print $1}')
#pruebas=$(head -n 2 ./infocorrida.txt |tail -n 1 | awk '{print $2}')
it=$(head -n 2 ./info |tail -n 1 | awk '{print $1}')
#stnum=$(head -n 4 ./infocorrida.txt |tail -n 1 | awk '{print $2}')
st=$(head -n 3 ./info |tail -n 1 | awk '{print $1}')

#ind=$((2+$nagen+$nagen+1));
ind=$(head -n 1 ./agente0 |tail -n 1 | wc -w)

echo "ind vale: "+$ind
echo "nagen vale: "+$nagen
echo "it vale: "+$it
echo "st vale: "+$st 

# Generar datos
file=grafica-utilidad


# for (( e=0; e<$pruebas; e=e+1 ))
# do

	rm -f $file.txt
	rondas=$(tail -n 1 ./agente0 |awk '{print $1}')
	op=$(head -n 1 ./rondas | awk '{print $2}')
	for (( r=1; r<$rondas+1; r=r+1 ))
	do
		sum=0
		echo -n "$r " >>$file.txt
		for (( itag=0; itag<$nagen; itag=itag+1 ))
		do
			uti=$(head -n $r "agente"$itag |tail -n 1 | awk -v myvar="$ind" '{print $myvar}')
			echo -n "$uti " >>$file.txt
			sum=$((sum+uti));
		done
		echo $sum $op >>$file.txt
	done
# done


gnuplot <<- EOF
	set terminal png
# 	set key outside
	set output "$file.png"
	set xrange [0:$rondas+1]
	set xlabel "Ronda de negociación"
	set ylabel "Utilidad"
	set yrange [0:1.1*$op]
	set style data lines
# 	set title "Todos los agentes"
	set grid
# 	filename(n) = sprintf("agente%d", n)
	title(n) = sprintf("Ag%d",n)
	plot for [i=1:$nagen] "$file.txt" using 1:i+1 title title(i),\
	"$file.txt" using 1:$nagen+2 lw 3 lt 2 title "BS", \
	"$file.txt" using 1:$nagen+3 lw 3 lt 5 title "BS Opt";
EOF
#
# xdg-open $dir
