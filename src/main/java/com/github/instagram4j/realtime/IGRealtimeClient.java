package com.github.instagram4j.realtime;

import java.io.IOException;
import org.apache.thrift.TException;
import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.requests.direct.DirectInboxRequest;
import com.github.instagram4j.instagram4j.utils.IGUtils;
import com.github.instagram4j.realtime.mqtt.MQTToTClient;
import com.github.instagram4j.realtime.mqtt.packet.Packet;
import com.github.instagram4j.realtime.mqtt.packet.Payload;
import com.github.instagram4j.realtime.mqtt.packet.PublishPacket;
import com.github.instagram4j.realtime.payload.IGRealtimePayload;
import com.github.instagram4j.realtime.payload.IrisPayload;
import com.github.instagram4j.realtime.utils.RealtimeConstants;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IGRealtimeClient {
    private IGClient ig_client;
    private MQTToTClient mqtt_client;
    
    public IGRealtimeClient(IGClient igClient) {
        this.ig_client = igClient;
        this.mqtt_client = new MQTToTClient(RealtimeConstants.REALTIME_HOST_NAME, 443);
        IGUtils.getCookieValue(ig_client.getHttpClient().cookieJar(), "sessionid");
    }
    
    public void connect() {
        try {
            this.mqtt_client.connect(new IGRealtimePayload(this.ig_client).toThriftPayload());
            this.pub_iris();
        } catch (IllegalArgumentException | IllegalAccessException | IOException | TException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    private void pub_iris() throws IOException {
        Payload payload = new Payload();
        String json = IGUtils.objectToJson(new IrisPayload(this.ig_client.sendRequest(new DirectInboxRequest()).join()));
        payload.writeByteArray(json.getBytes());
        payload.compress();
        Packet iris = new PublishPacket("134", 1, payload);
        this.mqtt_client.send(iris);
    }
}
