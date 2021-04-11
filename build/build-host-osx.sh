ROOT=$(cd "$(dirname "$0")/.."; pwd)

echo $JAVA_HOME

ls $JAVA_HOME/include/darwin

# Resave jucer files
"$ROOT/build/bin/Projucer.app/Contents/MacOS/Projucer" --resave "$ROOT/owlplug-host/src/main/juce/OwlPlugHost.jucer"

cd "$ROOT/owlplug-host/src/main/juce/Builds/MacOSX"
xcodebuild -configuration Release || exit 1
