instagram4j-realtime
===
MQTT Client in Java specifically for Instagram realtime protocol. Currently unstable with limited functionality and subject to drastic changes!

## Features
- Listen to direct message events

## Example
Example:
```java
// logged in IGClient
IGClient client = IGClient.builder().username("username").password("password").login();

// constructing IGRealtimeClient with one packet consumer (or listener)
IGRealtimeClient realtime = new IGRealtimeClient(client, (packet) - > {
    // a packet consumer (or listener) that listens for incoming packets and then acts on it
    try {
        // if packet is a publish packet
        if (packet instanceof PublishPacket) {
            // cast it
            final PublishPacket publishPacket = (PublishPacket) packet;
            // retrieve payload, unzip, then stringify
            final String payload = PacketUtil.stringify(ZipUtil.unzip(publishPacket.getPayload()));
            System.out.println(payload);
        }
    } catch (DataFormatException | JsonProcessingException ex) {
        System.out.println("Error while reading PublishPacket " + ex.getMessage());
    }
});

// connect the client to Instagram broker. This is a blocking operation
realtime.connect();
```
For an example application that simply replies to a direct message see [here.](https://github.com/instagram4j/instagram4j-realtime/blob/master/src/examples/java/IGRealtimeClientExample.java#L22)

**NOTE: There is little functionality currently. Only publish packets can be listened in the packet consumers.**

**NOTE: To see debugging logs, please configure the logger at DEBUG level**

Example log4j (log4j.properties file at the root of the project)
```
log4j.rootLogger=DEBUG, stdout
log4j.appender.stdout=org.apache.log4j.ConsoleAppender
log4j.appender.stdout.Target=System.out
log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
log4j.appender.stdout.layout.ConversionPattern=%d{yy/MM/dd HH:mm:ss} %p %c{2}: %m%n
```
## Instagram Realtime Protocol
Instagram's realtime protocol follows a modified version of [MQTT 3.1.1 Spec](https://docs.oasis-open.org/mqtt/mqtt/v3.1.1/os/mqtt-v3.1.1-os.html), called MQTToT. 
Much thanks to [Nerixyz's work](https://github.com/Nerixyz/instagram_mqtt).

Some notable changes are:
- CONNECT packet takes a zipped Thrift payload with parameters used to connect the client to the broker successfully
- CONNACK packet has a payload
