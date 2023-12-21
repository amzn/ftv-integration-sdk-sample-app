#!/bin/sh

echo "Running pre-commit checks..."

JAVA_HOME=$(/usr/libexec/java_home -v 11)
export JAVA_HOME

./gradlew clean build --daemon

BUILD_STATUS_CODE=$?
ERROR_MESSAGE_COLOR='\033[0;31m'
SUCCESS_MESSAGE_COLOR='\033[0;32m'
NO_COLOR='\033[0m'

if [ ${BUILD_STATUS_CODE} -ne 0 ]; then
  echo "${ERROR_MESSAGE_COLOR}Pre-commit checks failed. Check output above to resolve the failure before committing again${NO_COLOR}"
  exit ${BUILD_STATUS_CODE}
else
  echo "${SUCCESS_MESSAGE_COLOR}Pre-commit checks passed!${NO_COLOR}"
fi
