set AQUARIUS_REP=%~dp0
del "%AQUARIUS_REP%vagrant\provisioning\services\aquarius.war"
copy "%AQUARIUS_REP%source\servicios\aquarius\target\aquarius.war" "%AQUARIUS_REP%vagrant\provisioning\services\"
cd "%AQUARIUS_REP%vagrant"
vagrant provision
