#!/bin/bash
#$1 es el nombre de la base de datos a borrar
#Exportamos la variable MYSQL_PWD para evitar warnings al ejecutar las ordenes, aunque no es seguro

export MYSQL_PWD=$DB_KEY
cadenaDrop="DROP DATABASE IF EXISTS \`$1\`"
mysql -uroot -e "$cadenaDrop"