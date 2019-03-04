@echo off

set owlplug-version=%1

echo ** Generating OwlPlug MSI Install package **

javapackager -deploy -native msi -appclass org.springframework.boot.loader.JarLauncher ^
-srcdir ../target/ -srcfiles owlplug-%owlplug-version%.jar -outdir ./output -outfile owlplug-app ^
-name OwlPlug -BappVersion=%owlplug-version% -Bicon=resources/owlplug.ico -BinstalldirChooser=true -BmenuHint=true -BshortcutHint=true -Bvendor=OwlPlug

echo ** OwlPlug MSI Install package generated**
