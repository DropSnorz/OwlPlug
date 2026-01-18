echo "Preparing package"

owlplug_version=$1
target_platform=$2

echo "Exporting LICENSE File"

cp ../LICENSE ./input/LICENSE

echo "Copying owlplug-client-${owlplug_version}.jar to owlplug.jar"

cp ../owlplug-client/target/owlplug-client-${owlplug_version}.jar ./input/owlplug.jar

echo "Generating OwlPlug AppImage Install package"

jpackage --input ./input/ --type app-image --name OwlPlug --main-class org.springframework.boot.loader.launch.JarLauncher \
--main-jar owlplug.jar --dest ./output \
--app-version ${owlplug_version} --vendor OwlPlug

# Copy OwlPlug logo to AppDir
cp ./resources/owlplug.png ./OwlPlug.AppDir/owlplug.png

# Copy OwlPlug binaries to AppDir
cp -a ./output/OwlPlug/bin/. ./OwlPlug.AppDir/usr/bin
cp -a ./output/OwlPlug/lib/. ./OwlPlug.AppDir/usr/lib

echo "OwlPlug AppImage files generated"
