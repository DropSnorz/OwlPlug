ROOT=$(cd "$(dirname "$0")/.."; pwd)

# Resave jucer files
"$ROOT/build/bin/Projucer.exe" --resave "$ROOT/owlplug-host/src/main/juce/OwlPlugHost.jucer"

VS_WHERE="C:/Program Files (x86)/Microsoft Visual Studio/Installer/vswhere.exe"
  
MSBUILD_EXE=$("$VS_WHERE" -latest -requires Microsoft.Component.MSBuild -find "MSBuild\**\Bin\MSBuild.exe")
echo $MSBUILD_EXE

cd "$ROOT/owlplug-host/src/main/juce/Builds/VisualStudio2017"
"$MSBUILD_EXE" "owlplug-host.sln" "//p:VisualStudioVersion=17.0" "//m" "//t:Build" "//p:Configuration=Release" "//p:PlatformTarget=x64" "//p:PreferredToolArchitecture=x64"

