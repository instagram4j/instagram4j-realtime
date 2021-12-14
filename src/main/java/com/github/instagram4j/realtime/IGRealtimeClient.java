package com.github.instagram4j.realtime;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import org.apache.thrift.TException;
import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.requests.direct.DirectInboxRequest;
import com.github.instagram4j.instagram4j.utils.IGUtils;
import com.github.instagram4j.realtime.mqtt.MQTToTClient;
import com.github.instagram4j.realtime.mqtt.packet.Packet;
import com.github.instagram4j.realtime.mqtt.packet.Payload;
import com.github.instagram4j.realtime.mqtt.packet.PingReqPacket;
import com.github.instagram4j.realtime.mqtt.packet.PublishPacket;
import com.github.instagram4j.realtime.payload.IGRealtimePayload;
import com.github.instagram4j.realtime.payload.IrisPayload;
import com.github.instagram4j.realtime.utils.IGRealtimeConstants;
import com.github.instagram4j.realtime.utils.ZipUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IGRealtimeClient {
    private IGClient igClient;
    private MQTToTClient mqttClient;
    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private Future<?> pingTask;
    
    /**
     * Construct a MQTToT client that connects to the Instagram realtime broker
     * 
     * @param igClient The IGClient needed to login
     * @param packetListeners An array of packet consumers to listen on when a packet is received
     */
    @SafeVarargs
    public IGRealtimeClient(IGClient igClient, Consumer<Packet> ...packetListeners) {
        this.igClient = igClient;
        this.mqttClient = new MQTToTClient(IGRealtimeConstants.REALTIME_HOST_NAME, 443, packetListeners);
    }
    
    /**
     * Connects client to Instagram MQTT broker and blocks operation
     * 
     * Also, schedules a recurring task that sends a PingReq packet to
     * keep connection alive according to MQTT 3.1.1 specification.
     * 
     */
    public void connect() {
        connect(() -> {});
    }
    
    /**
     * Connects client to Instagram MQTT broker and blocks operation
     * 
     * Also, schedules a recurring task that sends a PingReq packet to
     * keep connection alive according to MQTT 3.1.1 specification.
     * 
     * @param onReady Runnable called when MQTToT client successfully connects
     */
    public void connect(final Runnable onReady) {
        try {
            this.mqttClient.connect(ZipUtil.zip(new IGRealtimePayload(this.igClient).toThriftPayload()), () -> {
                try {
                    this.sendPubIris();
                    this.startPingTask();
                    onReady.run();
                } catch (IOException exception) {
                    throw new UncheckedIOException(exception);
                }
            });
        } catch (IllegalArgumentException | IllegalAccessException | IOException | TException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Disconnects the client to the Instagram MQTT broker
     * 
     * @throws IOException
     */
    public void disconnect() throws IOException {
        this.pingTask.cancel(true);
        this.mqttClient.disconnect();
        this.mqttClient.close();
    }
    
    private void startPingTask() {
        this.pingTask = scheduler.scheduleAtFixedRate(() -> {
            try {
                this.mqttClient.send(new PingReqPacket());
            } catch (IOException ex) {
                log.error("Exception occured during ping request", ex);
            }
        }, 19500, 19500, TimeUnit.MILLISECONDS);
    }
    
    private void sendPubIris() throws IOException {
        Payload payload = new Payload();
        String json = IGUtils.objectToJson(new IrisPayload(this.igClient.sendRequest(new DirectInboxRequest()).join()));
        payload.writeByteArray(json.getBytes());
        payload.compress();
        Packet iris = new PublishPacket(false, (byte) 1, false, "134", (short) 5, payload.toByteArray());
        this.mqttClient.send(iris);
    }
}
