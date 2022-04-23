ROOT=$(cd "$(dirname "$0")/.."; pwd)

"$ROOT/build/bin/JUCE/Projucer" --resave "$ROOT/owlplug-host/src/main/juce/OwlPlugHost.jucer"

cd "$ROOT/owlplug-host/src/main/juce/Builds/LinuxMakefile"
make CONFIG=Release
