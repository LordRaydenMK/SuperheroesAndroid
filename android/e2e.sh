#!/bin/bash

# run maestro tests
# on CI this command is executed from the root folder so it stars with `android/`
maestro test android/app/src/maestro/flows/flow.yml
result=$?

# if tests fail, print the hierarchy and logcat
if [ $result -ne 0 ]; then
  echo "Printing hierarchy"
  maestro hierarchy
  echo "Printing logcat for app"
  adb logcat -d | grep "io.github.lordraydenmk.superheroesapp"
fi

# return the result of the first command as the result of the script
exit $result
