#!/bin/bash

width=900
height=700
time=600
seed="random" #other options?

host="127.0.0.1"
player1="bob"
player2="alice"
player3="fox"

java -Djava.library.path=repo/htw/ai/lenz/tiarait-server/2/lib/native/ -jar repo/htw/ai/lenz/tiarait-server/2/tiarait-server-2.jar ${width} ${height} ${time} &

java -jar target/TiaraitClient.jar ${host} ${player1} > /dev/null 2>&1 &
java -jar target/TiaraitClient.jar ${host} ${player2} > /dev/null 2>&1 &
java -jar target/TiaraitClient.jar ${host} ${player3} > /dev/null 2>&1 &
echo "dummy clients started"