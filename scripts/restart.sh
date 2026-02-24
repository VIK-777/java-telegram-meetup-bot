#!/bin/bash

source /home/ubuntu/.profile

MEETUP_BOT_PATH="/home/ubuntu/meetup_calendar_bot"
MEETUP_BOT_BIN_PATH="$MEETUP_BOT_PATH/bin"
MEETUP_BOT_PROD_PATH="$MEETUP_BOT_PATH/prod"
MEETUP_BOT_JAR_NAME="meetupbot-0.0.1-SNAPSHOT.jar"

echo "Restarting application"
pid=$(pgrep -f "$MEETUP_BOT_JAR_NAME")
echo "Killing process $pid"
kill -9 "$pid"
nohup /home/ubuntu/.sdkman/candidates/java/current/bin/java -Xmx256M -DLOGS_FOLDER=$MEETUP_BOT_PROD_PATH/logs -jar $MEETUP_BOT_BIN_PATH/$MEETUP_BOT_JAR_NAME --spring.profiles.active=prod,prod-db-h2-ubuntu >/dev/null 2>&1 &
echo "Application restarted"
