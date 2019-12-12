echo "Actualizando DataBase"
sudo -i -u postgres psql -d aquarius -a -f /home/ubuntu/vagrant/scripts/aquariusDB.sql
