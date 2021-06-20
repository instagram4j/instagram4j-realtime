instagram4j-realtime
===
MQTT Client in Java specifically for Instagram realtime protocol. Currently unstable with limited functionality and subject to drastic changes.

# Run it
Currently the process is
```java
IGClient client = IGClient.builder().username("username").password("password").login();
IGRealtimeClient realtime = new IGRealtimeClient(client);
realtime.connect();
```
**NOTE: There is little to no functionality currently. This only connects the client to the broker, subscribes to Instagram direct messages (called Iris), and logs outgoing and receiving packets**

**NOTE: To see logs, please configure the logger at DEBUG level**

Example log4j (log4j.properties file at the root of the project)
```
log4j.rootLogger=DEBUG, stdout
log4j.appender.stdout=org.apache.log4j.ConsoleAppender
log4j.appender.stdout.Target=System.out
log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
log4j.appender.stdout.layout.ConversionPattern=%d{yy/MM/dd HH:mm:ss} %p %c{2}: %m%n
```