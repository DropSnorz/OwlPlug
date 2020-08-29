echo "Preparing package"

owlplugversion=$1

echo "Exporting LICENSE File"

cp ../LICENSE ./input/LICENSE

echo "Copying owlplug-client-$owlplugversion.jar to owlplug.jar"

cp ../owlplug-client/target/owlplug-client-$owlplugversion.jar ./input/owlplug.jar

echo "Generating OwlPlug MSI Install package"

jpackage --input ./input/ --name OwlPlug --main-class org.springframework.boot.loader.JarLauncher \
--main-jar owlplug.jar --license-file ./input/LICENSE --dest ./output \
--app-version $owlplugversion --icon ./resources/owlplug.icns --vendor OwlPlug \
--mac-package-identifier owlplug --mac-package-name OwlPlug

echo "OwlPlug Dmg Install package generated"
