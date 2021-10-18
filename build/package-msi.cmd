@echo off

set owlplug-version=%1
echo ** Preparing package **

echo ** Exporting LICENSE File **

copy "..\LICENSE" ".\input\LICENSE"

echo ** Copying owlplug-client-%owlplug-version%.jar to owlplug.jar **

copy "..\owlplug-client\target\owlplug-client-%owlplug-version%.jar" ".\input\owlplug.jar"

echo ** Generating OwlPlug MSI Install package **

jpackage --type msi --input ./input/ --name OwlPlug --main-class org.springframework.boot.loader.JarLauncher ^
--main-jar owlplug.jar --license-file .\input\LICENSE --dest ./output ^
--java-options "--add-opens=java.base/java.lang.reflect=ALL-UNNAMED" ^
--app-version %owlplug-version% --icon .\resources\owlplug.ico --vendor OwlPlug ^
--win-dir-chooser --win-menu --win-shortcut

echo ** OwlPlug MSI Install package generated**
