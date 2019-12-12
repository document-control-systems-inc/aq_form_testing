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