set AQUARIUS_REP=%~dp0
cd "%AQUARIUS_REP%source\web"
rmdir /Q /S node_modules
rmdir /Q /S dist
del package-lock.json
del portal.zip
echo "Compilando..."
npm install
