#!/bin/bash
set -e -o pipefail
BASE_DIR=$( cd "$(dirname "${BASH_SOURCE[0]}")" && pwd);
JAR_FILE_NAME="redis-traffic-stats.jar"
JAR_FILE="$BASE_DIR/$JAR_FILE_NAME"
DOWNLOAD_URL="https://raw.githubusercontent.com/jiangyunpeng/redis-traffic-stats/master/bin/redis-traffic-stats.jar"
PCAP_FILE=$1
OPTIONS=$2

run(){
  if [ ! "$PCAP_FILE" ]; then
    echo "Error：需要指定 pcap 文件 。例如：redis-traffic-stats.sh redis.pcap"
    return
  fi
  java -jar $JAR_FILE $PCAP_FILE $OPTIONS
}

if [ -f "$JAR_FILE" ]; then
  run
else
  curl -sS -o $JAR_FILE_NAME  $DOWNLOAD_URL
  run
fi

