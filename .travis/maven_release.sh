#!/bin/bash

set -e

git remote set-url origin git@github.com:mauro-midolo/WakeOnLanHTTPRest.git
git checkout master
git pull
mvn initialize
cat release.properties
echo "HEREEE"
mvn release:clean release:prepare release:perform -B --settings .travis/settings.xml -f WakeOnLanHTTPRest
git push
