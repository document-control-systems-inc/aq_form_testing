set AQUARIUS_REP=%~dp0
echo "subiendo compilado..."
cd "%AQUARIUS_REP%Vagrant\provisioning\web"
del portal.zip
cd "%AQUARIUS_REP%source\web"
powershell -nologo -noprofile -command "& { Add-Type -A 'System.IO.Compression.FileSystem'; [IO.Compression.ZipFile]::CreateFromDirectory('dist', 'portal.zip'); }"
move portal.zip "%AQUARIUS_REP%Vagrant\provisioning\web\"
cd "%AQUARIUS_REP%Vagrant"
vagrant up
vagrant provision

