#!/usr/bin/env bash

#=======================> Kotlin

echo "Running kotlin detekt checks..."
OUTPUT="/tmp/tmp-$(date +%s)"
./gradlew detekt > $OUTPUT
EXIT_CODE=$?
if [ $EXIT_CODE -ne 0 ]; then
  cat $OUTPUT
  rm $OUTPUT
  echo "***********************************************"
  echo "                 Kotlin Detekt failed                 "
  echo " Please fix the above issues before committing "
  echo "***********************************************"
  exit $EXIT_CODE
fi
rm $OUTPUT

#=======================> Android

echo "Running android lint checks..."
OUTPUT="/tmp/tmp-$(date +%s)"
./gradlew lint > $OUTPUT
EXIT_CODE=$?
if [ $EXIT_CODE -ne 0 ]; then
  cat $OUTPUT
  rm $OUTPUT
  echo "***********************************************"
  echo "                 Android Lint failed                 "
  echo " Please fix the above issues before committing "
  echo "***********************************************"
  exit $EXIT_CODE
fi
rm $OUTPUT

#=======================> IOS

# echo "Running swift lint checks..."
# OUTPUT="/tmp/tmp-$(date +%s)"
# iosApp/Pods/SwiftLint/swiftlint > $OUTPUT
# EXIT_CODE=$?
# if [ $EXIT_CODE -ne 0 ]; then
#   cat $OUTPUT
#   rm $OUTPUT
#   echo "***********************************************"
#   echo "                 Swift Lint failed                 "
#   echo " Please fix the above issues before committing "
#   echo "***********************************************"
#   exit $EXIT_CODE
# fi
# rm $OUTPUT

#=======================> Unit tests

echo "Running shared unit tests..."
OUTPUT="/tmp/tmp-$(date +%s)"
./gradlew :shared:testDebugUnitTest > $OUTPUT
EXIT_CODE=$?
if [ $EXIT_CODE -ne 0 ]; then
  cat $OUTPUT
  rm $OUTPUT
  echo "***********************************************"
  echo "                 Shared unit tests failed                 "
  echo " Please fix the shared unit tests before committing "
  echo "***********************************************"
  exit $EXIT_CODE
fi
rm $OUTPUT
