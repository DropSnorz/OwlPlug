echo ** Preparing package **

owlplugversion=$1

echo ** Exporting LICENSE File **

cp ../LICENSE ../owlplug-client/target/LICENSE

echo ** Copying owlplug-client-$owlplugversion.jar to owlplug.jar **

cp ../owlplug-client/target/owlplug-client-$owlplugversion.jar ../owlplug-client/target/owlplug.jar

echo ** Generating OwlPlug MSI Install package **

javapackager -deploy -native installer -appclass org.springframework.boot.loader.JarLauncher \
-srcdir ../owlplug-client/target/ -srcfiles owlplug.jar -outdir ./output -outfile owlplug-app \
-name OwlPlug -BappVersion=$owlplugversion -Bicon=resources/owlplug.ico

echo ** OwlPlug Dmg Install package generated **
