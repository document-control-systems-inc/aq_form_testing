echo "Actualizando DemoDashboard & DemoWebServices"
sudo rm -rf /var/lib/tomcat7/webapps/DemoDashboard
sudo rm -rf /var/lib/tomcat7/webapps/DemoDashboard.war
sudo rm -rf /var/lib/tomcat7/webapps/DemoWebServices
sudo rm -rf /var/lib/tomcat7/webapps/DemoWebServices.war
sudo cp /home/ubuntu/aig/DemoDashboard.war /var/lib/tomcat7/webapps/
sudo cp /home/ubuntu/aig/DemoWebServices.war /var/lib/tomcat7/webapps/
echo "Iniciando servicios"
sudo service tomcat7 stop
sudo service tomcat7 start
