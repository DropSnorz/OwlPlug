echo "Preparing package"

owlplug_version=$1
target_platform=$2

echo "Exporting LICENSE File"

cp ../LICENSE ./input/LICENSE

echo "Copying owlplug-client-${owlplug_version}.jar to owlplug.jar"

cp ../owlplug-client/target/owlplug-client-${owlplug_version}.jar ./input/owlplug.jar

echo "Generating OwlPlug DMG Install package"

jpackage --input ./input/ --name OwlPlug --main-class org.springframework.boot.loader.launch.JarLauncher \
--main-jar owlplug.jar --license-file ./input/LICENSE --dest ./output \
--app-version ${owlplug_version} --icon ./resources/owlplug.icns --vendor OwlPlug \
--mac-package-identifier owlplug --mac-package-name OwlPlug

mv  ./output/OwlPlug-${owlplug_version}.dmg ./output/OwlPlug-${owlplug_version}-${target_platform}.dmg

echo "OwlPlug Dmg Install package generated"
