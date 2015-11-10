#!/bin/sh

PRG="$0"

while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done

# Get standard environment variables
BASE_DIR=`dirname "$PRG"`

berks vendor $BASE_DIR/cookbooks
chef-solo -c $BASE_DIR/solo.rb -j $BASE_DIR/node_openvpn.json
