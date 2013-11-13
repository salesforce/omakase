#!/bin/bash

NC='\033[0m'
WHITE='\033[1;37m'
BLACK='\033[0;30m'
BLUE='\033[0;34m'
LIGHT_BLUE='\033[1;34m'
GREEN='\033[0;32m'
LIGHT_GREEN='\033[1;32m'
CYAN='\033[0;36m'
LIGHT_CYAN='\033[1;36m'
RED='\033[0;31m'
LIGHT_RED='\033[1;31m'
PURPLE='\033[0;35m'
LIGHT_PURPLE='\033[1;35m'
BROWN='\033[0;33m'
YELLOW='\033[1;33m'
GRAY='\033[0;30m'
LIGHT_GRAY='\033[0;37m'

echo -e "Setting up the Omakase CLI"

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
FILE="/usr/local/bin/omakase"

if [ -f $FILE ];
then
    echo -e "\n'$FILE' is already present and will be replaced.\n"
    rm /usr/local/bin/omakase
fi


ln -s $DIR/omakase.sh /usr/local/bin/omakase

echo -e "You can now access the ${YELLOW}omakase${NC} command within this directory"
