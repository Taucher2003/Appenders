#
#
#  Copyright 2022 Niklas van Schrick and the contributors of the Appenders Project
#
#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#
#  	http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.
#
#

function retry() {
  if eval "run_timed_command $@"; then
    return 0
  fi

  for i in 2 1; do
    sleep 3s
    echoinfo "Retrying $i..."
    if eval "run_timed_command $@"; then
      return 0
    fi
  done
  return 1
}

function run_timed_command() {
  local cmd="$@"
  local start=$(date +%s)

  echosuccess "\$ ${cmd}"
  eval "${cmd}"

  local ret=$?
  local end=$(date +%s)
  local runtime=$((end-start))
  local minutes=$((${runtime}/60))
  local seconds=$((${runtime}%60))
  local formattedRuntime=""
  if [[ $minutes != "0" ]]; then
    formattedRuntime="$minutes minutes and "
  fi
  formattedRuntime="$formattedRuntime$seconds seconds"

  if [[ $ret -eq 0 ]]; then
    echosuccess "==> '${cmd}' succeeded in ${formattedRuntime}."
    return 0
  else
    echoerror "==> '${cmd}' failed (${ret}) in ${formattedRuntime}."
    return $ret
  fi
}

function echoerror() {
  local header="${2}"

  if [ -n "${header}" ]; then
    printf "\n\033[0;31m** %s **\n\033[0m" "${1}" >&2;
  else
    printf "\033[0;31m%s\n\033[0m" "${1}" >&2;
  fi
}

function echoinfo() {
  local header="${2}"

  if [ -n "${header}" ]; then
    printf "\n\033[0;36m** %s **\n\033[0m" "${1}" >&2;
  else
    printf "\033[0;36m%s\n\033[0m" "${1}" >&2;
  fi
}

function echosuccess() {
  local header="${2}"

  if [ -n "${header}" ]; then
    printf "\n\033[0;32m** %s **\n\033[0m" "${1}" >&2;
  else
    printf "\033[0;32m%s\n\033[0m" "${1}" >&2;
  fi
}