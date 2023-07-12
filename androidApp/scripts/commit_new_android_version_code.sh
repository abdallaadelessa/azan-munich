#!/bin/bash

#===================> Functions

commit_new_android_version_code () {
  # Increment Version code
  versionCodeInString=$(grep "version_code=.*" androidApp/app_version.properties | sed 's|version_code=\([[:digit:]]*\)|\1|g')
  versionCode=$((versionCodeInString+1))
  sed -i -e "s|version_code=\([[:digit:]]*\)|version_code=$versionCode|g" androidApp/app_version.properties

  # Print version
  versionNameInString=$(grep "version_name=.*" androidApp/app_version.properties | sed 's|version_name=\([[:digit:]]*\)|\1|g')
  versionCodeInString=$(grep "version_code=.*" androidApp/app_version.properties | sed 's|version_code=\([[:digit:]]*\)|\1|g')
  echo "$versionNameInString.$versionCodeInString"
}

#===================> Script Start

newVersion=$(commit_new_android_version_code)
branchName=$(git rev-parse --abbrev-ref HEAD)
developBranchName="develop"

echo "Current branch $branchName"
echo "New version $newVersion"
echo "developBranchName $developBranchName"

git config --global user.email "ci-agent@circleCi.com"
git config --global user.name "CircleCi-Agent"

echo "Pulling branch $branchName ..."
git pull origin $branchName
git add androidApp/app_version.properties

echo "Committing and pushing version $newVersion ..."
git commit -m "Publish new app version $newVersion [ci skip]" || echo "No changes to commit"
git push --no-verify --set-upstream origin $branchName
git restore .
git clean -f -d

echo "Create and push tag v$newVersion ..."
git tag -a v$newVersion -m "Publish new tag v$newVersion"
git push origin --tags --no-verify

#echo "Merging $branchName into $developBranchName ..."
#git checkout $developBranchName
#git pull origin $developBranchName
#git merge $branchName
#git push --no-verify

#===================> Script End
