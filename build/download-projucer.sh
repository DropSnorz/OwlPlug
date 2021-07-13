ROOT=$(cd "$(dirname "$0")/.."; pwd)
cd "$ROOT"
echo "$ROOT"

# Get the Projucer version
cd "$ROOT/owlplug-host/modules/JUCE"
JUCE_HASH=`git rev-parse HEAD`
JUCE_VERSION=`git tag --points-at HEAD`
echo "Juce Hash: $JUCE_HASH, Juce Version: $JUCE_VERSION"

# Download the Projucer
mkdir -p "$ROOT/build/bin"
cd "$ROOT/build/bin"

curl -v -L "https://github.com/juce-framework/JUCE/releases/download/$JUCE_VERSION/juce-$JUCE_VERSION-$OS.zip" --output "$ROOT/build/bin/JUCE.zip"
unzip JUCE.zip
