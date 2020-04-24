#!/usr/bin/env bash

echo "Actualizando librerias"
sudo apt-get update
if [ ! -f /var/log/ldapinstall ];
then
	echo "Instalando LDAP"
	debconf-set-selections <<< 'slapd slapd/password1 password adminpass'
	debconf-set-selections <<< 'slapd slapd/password2 password adminpass'
	sudo apt-get -y install unzip tomcat7 postgresql postgresql-contrib python-software-properties slapd ldap-utils
	sudo touch /var/log/ldapinstall
fi

if [ ! -f /var/log/ldap ];
then
	echo "Configuraciones LDAP"
	sudo ldapadd -x -D cn=admin,dc=f2m,dc=com,dc=mx -w adminpass -f /vagrant/provisioning/ldap/default_ldap.ldif
	sudo touch /var/log/ldap
fi

if [ ! -f /var/log/postgres ];
then
	echo "Configuraciones Postgres"
	sudo cp /vagrant/provisioning/postgresql/pg_hba.conf /etc/postgresql/9.5/main/pg_hba.conf
	sudo cp /vagrant/provisioning/postgresql/postgresql.conf /etc/postgresql/9.5/main/postgresql.conf
	sudo service postgresql stop
	sudo service postgresql start
	sudo touch /var/log/postgres
fi

if [ ! -f /var/log/tomcatssl ];
then
	echo "Configurando SSL en Tomcat"
	/usr/lib/jvm/java-1.8.0-openjdk-amd64/bin/keytool -genkey -alias aquarius -keyalg RSA -keystore /home/ubuntu/keystore.jks -dname "cn=aquarius,ou=f2m,C=MX" -storepass aquarius01 -keypass aquarius01 -deststoretype JKS -validity 5475 -keysize 2048
	sudo cp /vagrant/provisioning/ssl/server.xml /etc/tomcat7/server.xml
	sudo touch /var/log/tomcatssl
fi


if [ ! -f /var/log/aquariusDefaultStorage ];
then
	sudo mkdir -p /opt/ecm
	sudo chown -R tomcat7:tomcat7 /opt/ecm
	sudo touch /var/log/aquariusDefaultStorage
fi

if [ ! -f /var/log/aquariusSample ];
then
	sudo mkdir -p /opt/ecm/sample
	sudo cp /vagrant/provisioning/services/sample/* /opt/ecm/sample/
	sudo touch /var/log/aquariusSample
fi

if [ ! -f /var/log/aquariusThumbnail ];
then
	sudo mkdir -p /opt/ecm/thumbnail
	sudo cp /vagrant/provisioning/services/thumbnail/* /opt/ecm/thumbnail/
	sudo touch /var/log/aquariusThumbnail
fi

if [ ! -f /var/log/aquariusStamp ];
then
	sudo mkdir -p /opt/ecm/stamp
	sudo cp /vagrant/provisioning/services/stamp/* /opt/ecm/stamp/
	sudo touch /var/log/aquariusStamp
fi

echo "Actualizando Servicios Rest"
sudo rm -rf /var/lib/tomcat7/webapps/aquarius
sudo rm -rf /var/lib/tomcat7/webapps/aquarius.war
sudo cp /vagrant/provisioning/services/aquarius.war /var/lib/tomcat7/webapps/

echo "Actualizando Aplicación Web"
sudo rm -rf /var/lib/tomcat7/webapps/ROOT/*
sudo unzip /vagrant/provisioning/web/portal.zip -d /var/lib/tomcat7/webapps/ROOT/

echo "Iniciando servicios"
sudo service tomcat7 stop
sudo service tomcat7 start

if [ ! -f /var/log/aquariusDb ];
then
	echo "Configuraciones Postgres"
	sudo -i -u postgres psql -c "CREATE USER aquarius WITH PASSWORD 'aquarius01';"
	sudo -i -u postgres psql -c "CREATE DATABASE aquarius;"
	sudo -i -u postgres psql -d aquarius -a -f /vagrant/provisioning/scripts/aquariusDB.sql
	sudo touch /var/log/aquariusDb
fi

if [ ! -f /var/log/updateDB_20180519 ];
then
	sudo -i -u postgres psql -d aquarius -a -f /vagrant/provisioning/scripts/updateDB_20180519.sql
	sudo touch /var/log/updateDB_20180519
fi
if [ ! -f /var/log/updateDB_20180528 ];
then
	sudo -i -u postgres psql -d aquarius -a -f /vagrant/provisioning/scripts/updateDB_20180528.sql
	sudo touch /var/log/updateDB_20180528
fi

if [ ! -f /var/log/updateDB_20180529 ];
then
	sudo -i -u postgres psql -d aquarius -a -f /vagrant/provisioning/scripts/updateDB_20180529.sql
	sudo touch /var/log/updateDB_20180529
fi