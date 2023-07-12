#!/bin/bash

versionNameInString=$(grep "version_name=.*" androidApp/app_version.properties | sed 's|version_name=\([[:digit:]]*\)|\1|g')
versionCodeInString=$(grep "version_code=.*" androidApp/app_version.properties | sed 's|version_code=\([[:digit:]]*\)|\1|g')
newVersion="$versionNameInString.$versionCodeInString"
branchName=$(git rev-parse --abbrev-ref HEAD)

echo "Current branch $branchName"
echo "New version $newVersion"

git config --global user.email "ci-agent@circleCi.com"
git config --global user.name "CircleCi-Agent"

echo "Pulling branch $branchName ..."
git pull origin $branchName
git add androidApp/fastlane/metadata/android/

echo "Committing and pushing screen shots $newVersion ..."
git commit -m "Commit screenshots  $newVersion [ci skip]" || echo "No changes to commit"
git push --no-verify --set-upstream origin $branchName
