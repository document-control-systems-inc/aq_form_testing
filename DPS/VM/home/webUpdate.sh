echo "Actualizando Aplicación Web"
sudo rm -rf /var/lib/tomcat7/webapps/ROOT/*
sudo unzip /home/ubuntu/vagrant/web/portal.zip -d /var/lib/tomcat7/webapps/ROOT/
echo "Iniciando servicios"
sudo service tomcat7 stop
sudo service tomcat7 start

