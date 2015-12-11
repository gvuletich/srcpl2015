#!/bin/bash

# Generar datos
#nagen=$(head -n 1 ./info |tail -n 1 | awk '{print $1}')
#it=$(head -n 2 ./info |tail -n 1 | awk '{print $1}')
#st=$(head -n 3 ./info |tail -n 1 | awk '{print $1}')


path=$1
cd $path

echo "Estoy en: "$PWD


# Generar datos
file=longitud-de-negociacion
rm -f $file.txt

#cd ../salida
#for (( e=0; e<$pruebas; e=e+1 ))
for e in *; do
	echo -e -n $(tail -n 1 $e/agente0 |awk '{print $1}')' \n' >> "../grafs/"$file.txt
done

cd ../grafs
# Graficar
min_max_prom=$(awk 'NR == 1 { max=$1; min=$1; sum=0 } { if ($1>max) max=$1; if ($1<min) min=$1; sum+=$1;} END {printf "%d %d %.2f", min, max, sum/NR}' $file.txt)
min=$(echo $min_max_prom | awk '{print $1}')
max=$(echo $min_max_prom | awk '{print $2}')
prom=$(echo $min_max_prom | awk '{print $3}')
echo e $e min $min, max $max, prom $prom

gnuplot <<- EOF
        set title  "Longitud de las negociaciones , Estrategia $st"
        set xlabel "Longitud (cantidad de rondas)"
        set ylabel "Cantidad de negociaciones"
        n=20 #number of intervals
#         width=($max-$min)/n #interval width
        width=.5

    	set yrange [0:12]
     	set xrange [$min-1.5*width:$max+1.5*width]
    	set parametric
   	set dummy t
        set trange [0:12]
        set terminal png
        set key font ",20"
        set boxwidth width*0.8
        set style fill solid 0.5
        set tics out nomirror
        set output "$file.png"
	bin(x,width)=width*floor(x/width)
	plot '$file.txt' u (bin(\$1,width)):(1.0) smooth freq with boxes lw 3 lt rgb "black" notitle, \
      	      $prom, t lt 2 lc rgb "black" lw 1 title "Mean $prom"
EOF
