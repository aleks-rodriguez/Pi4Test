#!/bin/bash
#$1 es el nombre de la base de datos, $2 la localizacion del dump
#Exportamos la variable MYSQL_PWD para evitar warnings al ejecutar las ordenes, aunque no es seguro

export MYSQL_PWD=$DB_KEY
cadenaDrop="DROP DATABASE IF EXISTS \`$1\`"
cadenaCreate="CREATE DATABASE \`$1\`"
mysql -uroot -e "$cadenaDrop"
mysql -uroot  -e "$cadenaCreate"
mysql -uroot  $1 < $2
