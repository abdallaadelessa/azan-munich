#!/bin/bash
#================================>
## Constants
ANDROID_HOME=$ANDROID_HOME
EMULATOR_IMAGE="system-images;android-28;google_apis_playstore;x86_64"
#================================>
## Tools
SDKMANAGER=$ANDROID_HOME/tools/bin/sdkmanager
AVDMANAGER=$ANDROID_HOME/tools/bin/avdmanager
EMULATOR=$ANDROID_HOME/emulator/emulator
ADB=$ANDROID_HOME/platform-tools/adb
#================================>
## Functions
function reset_enviroment() {
  echo "=> Kill all emulators"
  ${ADB} devices | grep emulator | cut -f1 | while read line; do
    ${ADB} -s $line emu kill;
  done
  # Kill adb server
  ${ADB} kill-server
  # Start adb server
  ${ADB} start-server
}
function start_emulator() {
  if  [ -z "${1}" ] || [ -z "${2}" ] ;then
    echo "name or port is missing"
    return
  fi
  #==================>
  echo "=> Try to download the platform tools $EMULATOR_IMAGE"
  echo yes | ${SDKMANAGER} --verbose --update
  ${SDKMANAGER} --verbose 'platform-tools' $EMULATOR_IMAGE
  echo "=> Creating ${1} AVD (config:$EMULATOR_IMAGE)"
  ${AVDMANAGER} --verbose create avd -d 9 -k $EMULATOR_IMAGE -n ${1}
  #==================>
  echo "=> Starting Emulator (name:${1} , port:${2} , snapShot:${3})"
  if [ -z "${3}" ] ;then
    # Wipe data and start emulator
    ${EMULATOR} @${1} -wipe-data -netspeed full -port ${2}
  else
    # Load emulator snapshot
    ${EMULATOR} @${1} -port ${2} -snapshot ${3}
  fi
  echo "=> Waiting for Emulator (name:${1} , port:${2} , snapShot:${3})"
  # Wait for device
  ${ADB} wait-for-device shell shell 'while [[ -z $(getprop sys.boot_completed) ]]; do sleep 1; done; input keyevent 82' >/dev/null 2>&1
  echo "=> Emulator is ready"
}

function kill_emulator() {
  echo "=> Killing Emulator (port:${1})"
  ${ADB} -s emulator-${1} emu kill
}
#================================>
## Actions & Options
if  [ "${1}" = "--reset" ] || [ "${1}" = "-r" ] ;then
  reset_enviroment
elif [ "${1}" = "--kill" ] || [ "${1}" = "-k" ] ;then
  kill_emulator ${2}
elif [ "${1}" = "--start" ] || [ "${1}" = "-s" ] ;then
  start_emulator ${2} ${3} ${4}
else
  echo "Unknown Command"
fi
#================================>
