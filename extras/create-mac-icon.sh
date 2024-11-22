#!/bin/bash

################################################################
# Create ICNS file from PNG image with sips and iconutil
################################################################

# Find script dir and cd as working dir
SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
cd "$SCRIPT_DIR"
# Find and copy DeskStop-icon.png to current dir
find .. -type f -name 'DeskStop-icon.png' | xargs -I{} cp {} .
# Make iconset folder and generate all sizes
mkdir DeskStop.iconset
sips -z 16 16     DeskStop-icon.png --out DeskStop.iconset/icon_16x16.png
sips -z 32 32     DeskStop-icon.png --out DeskStop.iconset/icon_16x16@2x.png
sips -z 32 32     DeskStop-icon.png --out DeskStop.iconset/icon_32x32.png
sips -z 64 64     DeskStop-icon.png --out DeskStop.iconset/icon_32x32@2x.png
sips -z 128 128   DeskStop-icon.png --out DeskStop.iconset/icon_128x128.png
sips -z 256 256   DeskStop-icon.png --out DeskStop.iconset/icon_128x128@2x.png
sips -z 256 256   DeskStop-icon.png --out DeskStop.iconset/icon_256x256.png
sips -z 512 512   DeskStop-icon.png --out DeskStop.iconset/icon_256x256@2x.png
sips -z 512 512   DeskStop-icon.png --out DeskStop.iconset/icon_512x512.png
sips -z 1024 1024 DeskStop-icon.png --out DeskStop.iconset/icon_512x512@2x.png
# Convert all images in iconset to icns file
iconutil -c icns DeskStop.iconset
# Cleanup
rm -R DeskStop.iconset
rm DeskStop-icon.png
