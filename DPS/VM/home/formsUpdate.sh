echo "Actualizando AppForms"
sudo rm -rf /var/lib/tomcat7/webapps/FormsDPSApp
sudo rm -rf /var/lib/tomcat7/webapps/FormsDPSApp.war
sudo cp /home/ubuntu/forms/FormsDPSApp.war /var/lib/tomcat7/webapps/
echo "Iniciando AppForms"
sudo service tomcat7 stop
sudo service tomcat7 start
