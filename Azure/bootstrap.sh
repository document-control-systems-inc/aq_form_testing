#!/usr/bin/env bash

echo "Actualizando librerias"
sudo apt-get update
if [ ! -f /var/log/ldapinstall ];
then
	echo "Instalando LDAP"
	sudo debconf-set-selections <<< 'slapd slapd/password1 password adminpass'
	sudo debconf-set-selections <<< 'slapd slapd/password2 password adminpass'
	sudo apt-get -y install unzip tomcat7 postgresql postgresql-contrib python-software-properties slapd ldap-utils
	touch /var/log/ldapinstall
fi

if [ ! -f /var/log/ldap ];
then
	echo "Configuraciones LDAP"
	sudo ldapadd -x -D cn=admin,dc=f2m,dc=com,dc=mx -w adminpass -f /home/ubuntu/vagrant/ldap/default_ldap.ldif
	touch /var/log/ldap
fi

if [ ! -f /var/log/postgres ];
then
	echo "Configuraciones Postgres"
	sudo cp /home/ubuntu/vagrant/postgresql/pg_hba.conf /etc/postgresql/9.5/main/pg_hba.conf
	sudo cp /home/ubuntu/vagrant/postgresql/postgresql.conf /etc/postgresql/9.5/main/postgresql.conf
	sudo service postgresql stop
	sudo service postgresql start
	touch /var/log/postgres
fi

if [ ! -f /var/log/aquariusDefaultStorage ];
then
	sudo mkdir -p /opt/ecm
	sudo chown -R tomcat7:tomcat7 /opt/ecm
	touch /var/log/aquariusDefaultStorage
fi

if [ ! -f /var/log/aquariusSample ];
then
	sudo mkdir -p /opt/ecm/sample
	sudo cp /home/ubuntu/vagrant/services/sample/* /opt/ecm/sample/
	touch /var/log/aquariusSample
fi

echo "Actualizando Servicios Rest"
sudo rm -rf /var/lib/tomcat7/webapps/aquarius
sudo rm -rf /var/lib/tomcat7/webapps/aquarius.war
sudo cp /home/ubuntu/vagrant/services/aquarius.war /var/lib/tomcat7/webapps/

echo "Actualizando Aplicación Web"
sudo rm -rf /var/lib/tomcat7/webapps/ROOT/*
sudo unzip /home/ubuntu/vagrant/web/portal.zip -d /var/lib/tomcat7/webapps/ROOT/

echo "Iniciando servicios"
sudo service tomcat7 stop
sudo service tomcat7 start

if [ ! -f /var/log/aquariusDb ];
then
	echo "Configuraciones Postgres"
	sudo -i -u postgres psql -c "CREATE USER aquarius WITH PASSWORD 'aquarius01';"
	sudo -i -u postgres psql -c "CREATE DATABASE aquarius;"
	sudo -i -u postgres psql -d aquarius -a -f /home/ubuntu/vagrant/scripts/aquariusDB.sql
	touch /var/log/aquariusDb
fi
