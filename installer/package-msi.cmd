@echo off

set owlplug-version=%1
echo ** Preparing package **

echo ** Exporting LICENSE File **

copy "..\LICENSE" "..\owlplug-client\target\LICENSE"

echo ** Copying owlplug-client-%owlplug-version%.jar to owlplug.jar **

copy "..\owlplug-client\target\owlplug-client-%owlplug-version%.jar" "..\owlplug-client\target\owlplug.jar"

echo ** Generating OwlPlug MSI Install package **

javapackager -deploy -native msi -appclass org.springframework.boot.loader.JarLauncher ^
-srcdir ../owlplug-client/target/ -srcfiles owlplug.jar;LICENSE -outdir ./output -outfile owlplug-app ^
-name OwlPlug -BappVersion=%owlplug-version% -Bicon=resources/owlplug.ico -BinstalldirChooser=true -BmenuHint=true ^
-BshortcutHint=true -Bvendor=OwlPlug -BlicenseFile=LICENSE

echo ** OwlPlug MSI Install package generated**
