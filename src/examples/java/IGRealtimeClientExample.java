import java.io.File;
import java.io.IOException;
import java.util.zip.DataFormatException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.requests.direct.DirectThreadsBroadcastRequest;
import com.github.instagram4j.instagram4j.requests.direct.DirectThreadsBroadcastRequest.BroadcastTextPayload;
import com.github.instagram4j.instagram4j.requests.direct.DirectThreadsMarkItemSeenRequest;
import com.github.instagram4j.instagram4j.utils.IGUtils;
import com.github.instagram4j.realtime.IGRealtimeClient;
import com.github.instagram4j.realtime.mqtt.packet.PublishPacket;
import com.github.instagram4j.realtime.utils.PacketUtil;
import com.github.instagram4j.realtime.utils.ZipUtil;

/**
 * Example IGRealtimeClient setup + listening to packet events
 *
 */
public class IGRealtimeClientExample {
    
    public static void main(String args[]) throws Exception {
        // Must have logged in IGClient
        final IGClient client = getClientFromSerialize("igclient.ser", "cookie.ser");
        
        // setting up realtime client + adding a packet Consumer to listen for packets
        final IGRealtimeClient realtime = new IGRealtimeClient(client, (packet) -> {
            try {
                if (packet instanceof PublishPacket) {
                    final PublishPacket publishPacket = (PublishPacket) packet;
                    final String payload = PacketUtil.stringify(ZipUtil.unzip(publishPacket.getPayload()));
                    
                    if (publishPacket.getTopicName().equals("146")) {
                        System.out.println("Received 146 topic: " + payload);
                        final JsonNode data = IGUtils.jsonToObject(payload, JsonNode.class).get(0).get("data").get(0);
                        final String thread_id = data.get("path").asText().substring(1).split("/")[2];
                        final JsonNode itemValue = IGUtils.jsonToObject(data.get("value").asText(), JsonNode.class);
                        if (data.get("op").asText().equals("add") && itemValue.get("user_id").asLong() != client.getSelfProfile().getPk()) {
                            client.sendRequest(new DirectThreadsMarkItemSeenRequest(thread_id, itemValue.get("item_id").asText())).join();
                            client.sendRequest(new DirectThreadsBroadcastRequest(new BroadcastTextPayload("Hello from me!", thread_id))).join();
                        }
                    }
                }
            } catch (DataFormatException | JsonProcessingException ex) {
                System.out.println("Error while reading PublishPacket " + ex.getMessage());
            }
        });
        
        // blocking operation + a Runnable that runs when the client is ready
        realtime.connect(() -> System.out.println("Client is ready!"));
    }
    
    public static IGClient getClientFromSerialize(String client, String cookie) throws ClassNotFoundException, IOException {
        File to = new File("src/examples/resources/" + client),
                cookFile = new File("src/examples/resources/" + cookie);
        
        return IGClient.deserialize(to, cookFile);
    }
}
