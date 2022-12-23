#!/bin/bash
	cd $1/target/site
	for val in {1..4}
	do
		sed -i '13d' surefire-report.html
	done
	#Tras la linea 12 añadimos el tag link
	sed -i '11 a <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.1/dist/css/bootstrap.min.css" rel="stylesheet">' surefire-report.html
	#Tras la linea 12 añadimos el tag script
	sed -i '12 a <script src="./addon.js"> </script>' surefire-report.html
	#Sustituimos el tag body original por uno customizado
	sed -i 's/<body class="composite">/<body class="composite" onload="init()">/g' surefire-report.html
 
