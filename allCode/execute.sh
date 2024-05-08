#!/bin/bash

find Backend/app/src/main/resources/static/ -mindepth 1 -path Backend/app/src/main/resources/static/avatar -prune -o -type f -exec rm -f {} +
cd Backend/app/src/main/resources/static/
rm -rf assets

cd ../../../../../../Frontend

npm run build -- --configuration production

cp -r dist/frontend/* ../Backend/app/src/main/resources/static/

cd ../Backend/app

mvn package -DskipTests

# cd target

# java -jar "./app-0.0.1-SNAPSHOT.jar"