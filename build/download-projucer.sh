ROOT=$(cd "$(dirname "$0")/.."; pwd)
cd "$ROOT"
echo "$ROOT"

# Get the Projucer hash
cd "$ROOT/owlplug-host/modules/juce"
HASH=`git rev-parse HEAD`
echo "Hash: $HASH"

# Download the Projucer
mkdir -p "$ROOT/build/bin"
cd "$ROOT/build/bin"
while true
do
  PROJUCER_URL=$(curl -s -S "https://projucer.rabien.com/get_projucer.php?hash=$HASH&os=$OS")
  echo "Response: $PROJUCER_URL"
  if [[ $PROJUCER_URL == http* ]]; then
    curl -s -S $PROJUCER_URL -o "$ROOT/build/bin/Projucer.zip"
    unzip Projucer.zip
    break
  fi
  sleep 15
done