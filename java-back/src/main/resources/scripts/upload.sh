#!/bin/bash
#$1 es el nombre del zip

if 7z x "$1.zip" -aoa; #Descomprimir
then
	rm "$1.zip"
	cd $1
	if [ -f "pom.xml" ]
		then
			#Sacar el título
			echo "Extracting the title and storing in the following file: name.txt"
			titleName=`sed -n '/<name>/p' pom.xml | head -1` #Si hay varios resultados, se queda con el primero
			titleName=`echo $titleName | sed -e 's/^[[:space:]]*//'` #Elimina espacios en blanco delante y detras
        	
			#https://linuxhint.com/remove_characters_string_bash/
	       	name=`echo "$titleName" |  cut -c7- | rev | cut -c8- |rev` #Corta, gira el string, corta, y vuelve a girar
	       	echo $name >> name.txt
	        echo "The project name is: $name"

			#Sacamos la descripción
			descName=`sed -n '/<description>/p' pom.xml` #Extraemos la linea "<descripion>"
			descName=`echo $descName | sed -e 's/^[[:space:]]*//'` #Elimina espacios en blanco delante y detras
			desc=`echo "$descName" | cut -c14- | rev | cut -c15- | rev` #Cortamos, giramos y cortamos
			echo $desc >> desc.txt
			echo "The project description is: $desc"
		else
			echo "Not a Maven project. Deleting files..." 1>&2
			cd ..
			rm -rf "$1" 
			exit 2
		fi
else
	echo "Not a ZIP file. Deleting..." 1>&2
	rm -f "$1.zip"
	exit 1
fi

