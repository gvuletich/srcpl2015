#!/bin/bash

# Graficar la longitud de las negociaciones
# NOTA: 
#      gnuplot <<- EOF
#         Aqui poner cualquier archivo de gnuplot remplazando $ por \$
#      EOF

#!/bin/bash

# infocorrida.txt
# Num_tot_agentes $nagen
# Num_tot_pruebas $fin
# Num_de_pruebas $it
# Num_tot_estrategias $stnum
# Num_det_estrategia $st


# Generar datos
#nagen=$(head -n 1 ./0/infocorrida.txt |tail -n 1 | awk '{print $2}')
#pruebas=$(head -n 2 ./0/infocorrida.txt |tail -n 1 | awk '{print $2}')
#it=$(head -n 3 ./0/infocorrida.txt |tail -n 1 | awk '{print $2}')
#stnum=$(head -n 4 ./0/infocorrida.txt |tail -n 1 | awk '{print $2}')
#st=$(head -n 5 ./0/infocorrida.txt |tail -n 1 | awk '{print $2}')

path=$1
cd $path

echo "Estoy en: "$PWD

cd SCN000
ind=$(head -n 1 ./agente0 |tail -n 1 | wc -w)
cd ..
echo "ind vale: "$ind;



# Generar datos
file=incremento-sw2
rm -f file.txt

#cd ../salida
pruebas=$(ls | wc -l)
for e in *; do
	cd $e
	nagen=$(ls agente* | wc -l);
	u_i_min=$(head -n 1 "agente0" | awk -v myvar="$ind" '{print $myvar}');
	u_f_min=$(tail -n 1 "agente0" | awk -v myvar="$ind" '{print $myvar}');
	u_min=$(echo $u_f_min-$u_i_min|bc) 

	for (( itag=1; itag<$nagen; itag=itag+1 ))
	do
		u_i=$(head -n 1 "agente"$itag | awk -v myvar="$ind" '{print $myvar}');
		u_f=$(tail -n 1 "agente"$itag | awk -v myvar="$ind" '{print $myvar}');
		u=$(echo $u_f-$u_i|bc)
		
		if [ $u -lt $u_min ]; 
		then
			u_i_min=$u_i
			u_f_min=$u_f
			u_min=$u
		fi
	done;
	cd ..
	echo "scale=2;100*$u_f_min/$u_i_min" | bc >> "../grafs/"$file.txt
done


cd ../grafs
# Graficar
min_max_prom=$(awk 'NR == 1 { max=$1; min=$1; sum=0 } { if ($1>max) max=$1; if ($1<min) min=$1; sum+=$1;} END {printf "%d %d %.2f", min, max, sum/NR}' $file.txt)
min=$(echo $min_max_prom | awk '{print $1}')
max=$(echo $min_max_prom | awk '{print $2}')
prom=$(echo $min_max_prom | awk '{print $3}')
echo min $min, max $max, prom $prom

gnuplot <<- EOF
        set title  "Incremento del Beneficio Social(%), Estrategia $st"
        set xlabel "Incremento"
        set ylabel "Cantidad de negociaciones"
#         n=20 #number of intervals
#         width=($max-0.9*$min)/n #interval width
        width=25
   	set yrange [0:$pruebas]
        set parametric
   	set dummy t
        set trange [0:0.8*$pruebas]
    	set xrange [$min-0.5*width:$max+0.5*width]
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
