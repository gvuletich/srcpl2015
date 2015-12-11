Pasos para ejecutar en Debian/Ubuntu

1) Instalar java:
	apt-get install openjdk-6-jdk
	

2) Instalar jade:
	
	a) Descargar desde http://jade.tilab.com/download/jade/ el archivo JADE-all-4.3.3.zip 
	b) Descomprimir las fuentes de JADE en el directorio donde se encuentran las fuentes de la Plataforma
	c) La estructura de directorios deberia quedar:
								
			/home/username/Platform_dir
								|
								|---> Plataform.java
								|---> Negotiator.java
								|............. *.java
								|
								|---> jaxb
								|---> lpsolve
								|---> lpsolve64
								|--->.class
								|---> xmls
								|---> jade
										|---> src
										|---> lib
										|---> demo
										|---> doc
										|---> classes
										
3) Instalar gnuplot:
	apt-get install gnuplot
	

4)  Editar el archivo /home/username/.bashrc y agregarle al final las siguientes lineas:

	LD_LIBRARY_PATH=/home/username/Platform_dir/lpsolve
	export LD_LIBRARY_PATH			

	y cerrar y volver a abrir el terminal para que recargue el .bashrc.
	
	
	NOTA: En caso de usar arquitectura 64 bits la primer linea debe ser:
			LD_LIBRARY_PATH=/home/username/Platform_dir/lpsolve64


	

