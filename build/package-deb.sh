echo "Preparing package"

owlplug_version=$1
target_platform=$2

echo "Exporting LICENSE File"

cp ../LICENSE ./input/LICENSE

echo "Copying owlplug-client-${owlplug_version}.jar to owlplug.jar"

cp ../owlplug-client/target/owlplug-client-${owlplug_version}.jar ./input/owlplug.jar

echo "Generating OwlPlug DEB Install package"

jpackage --input ./input/ --name OwlPlug --main-class org.springframework.boot.loader.launch.JarLauncher \
--main-jar owlplug.jar --license-file ./input/LICENSE --dest ./output \
--app-version ${owlplug_version} --icon ./resources/owlplug.png --vendor OwlPlug \
--linux-package-name owlplug --linux-deb-maintainer contact@owlplug.com \
--linux-menu-group "AudioVideo;Audio" --linux-shortcut

# Rename owlplug_${version]_${arch} to owlplug-${version}-${platform}
mv ./output/owlplug_${owlplug_version}_*.deb ./output/OwlPlug-${owlplug_version}-${target_platform}.deb

ls ./output

echo "OwlPlug DEB Install package generated"
