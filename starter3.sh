#!/bin/bash

width=840
height=600
time=6000
#seed="random" #obstacle generation
seed="4669005409573044559" #obstacle generation
# 9045823041853235795

host="127.0.0.1"
player1="bob"
player2="alice"
player3="carol"

java -Djava.library.path=repo/htw/ai/lenz/tiarait-server/2/lib/native/ -jar repo/htw/ai/lenz/tiarait-server/2/tiarait-server-2.jar ${width} ${height} ${time} ${seed} &

java -jar target/TiaraitClient.jar ${host} ${player1} > /dev/null 2>&1 &
java -jar target/TiaraitClient.jar ${host} ${player2} > /dev/null 2>&1 &
java -jar target/TiaraitClient.jar ${host} ${player3} > /dev/null 2>&1 &
echo "dummy clients started"