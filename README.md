# tiarait

- server and dummy clients can be started using ./starter3
- path finding with dijkstra
- eraser picks out the enemy with max score
- cube sequentially processes the field
- pyramid relies on random generator to find target

## client
```
mvn clean package
java -jar target/TiaraitClient.jar <SERVER_IP> <TEAM_NAME>
eg: java -jar target/TiaraitClient.jar 127.0.0.1 winner
```