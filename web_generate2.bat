set AQUARIUS_REP=%~dp0
cd "%AQUARIUS_REP%source\web"
rmdir /Q /S dist
echo "Generando..."
ng build --prod