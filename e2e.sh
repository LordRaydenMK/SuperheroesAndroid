#!/bin/bash

# run first command
maestro test app/src/maestro/flows/flow.yml
result=$?

# run second command if first one fails
if [ $result -ne 0 ]; then
  echo "Printing hierarchy"
  maestro hierarchy
  echo "Printing logcat for app"
  adb logcat -d | grep "io.github.lordraydenmk.superheroesapp"
fi

# return the result of the first command as the result of the script
exit $result
