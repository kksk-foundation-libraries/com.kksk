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

PRGDIR=`dirname "$PRG"`
BASEDIR=`cd "$PRGDIR/.." >/dev/null; pwd`
echo $BASEDIR

exec java $JAVA_OPTS -Dapp.pid="$$" -cp $BASEDIR/target/classes:$BASEDIR/target/dependency/* com.kumuluz.ee.EeApplication "$@"
