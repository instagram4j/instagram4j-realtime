package com.github.instagram4j.realtime.mqtt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLSocketFactory;
import org.apache.http.concurrent.FutureCallback;
import com.github.instagram4j.realtime.mqtt.packet.ConnackPacket;
import com.github.instagram4j.realtime.mqtt.packet.DisconnectPacket;
import com.github.instagram4j.realtime.mqtt.packet.MQTToTConnectPacket;
import com.github.instagram4j.realtime.mqtt.packet.Packet;
import com.github.instagram4j.realtime.mqtt.packet.PingReqPacket;
import com.github.instagram4j.realtime.mqtt.packet.PubackPacket;
import com.github.instagram4j.realtime.mqtt.packet.PublishPacket;
import com.github.instagram4j.realtime.mqtt.packet.PublishResponsePacket;
import com.github.instagram4j.realtime.utils.PacketUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MQTToTClient {
    private Socket socket;
    private InputStream incoming;
    private OutputStream outgoing;
    private final String host;
    private final int port;
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);
    private Future<?> ping_task, incoming_task;

    public MQTToTClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public boolean connect(byte[] connectPayload) throws UnknownHostException, IOException {
        socket = SSLSocketFactory.getDefault().createSocket(host, port);
        incoming = socket.getInputStream();
        outgoing = socket.getOutputStream();

        this.send(new MQTToTConnectPacket(connectPayload));
        ConnackPacket connack = this.await_connack();

        if (connack.getReturnCode() == 0) {
            log.info("Successfully connected to {}:{}", host, port);
            this.assign_tasks();
            
            return true;
        } else {
            log.error("CONNACK received code: {}", connack.getReturnCode());
            this.disconnect();
            return false;
        }
    }
    
    private void assign_tasks() {
        this.ping_task = scheduler.scheduleAtFixedRate(() -> {
            try {
                this.send(new PingReqPacket());
            } catch (Throwable e) {
                log.error("Exception occured during ping request", e);
                this.ping_task.cancel(true);
            }
        }, 19500, 19500, TimeUnit.MILLISECONDS);
        
        this.incoming_task = scheduler.scheduleWithFixedDelay(() -> {
            try {
                byte[] data = this.read();
                log.debug("Received (Length not encoded): {}", PacketUtil.hex_stringify(data));
                // TODO: Add proper MQTT spec handling and packet receive listeners
                PublishResponsePacket res_packet = new PublishResponsePacket(data);
                if (res_packet.getControlType() == PublishPacket.PUBLISH_PACKET_TYPE) {
                    log.debug("Received publish packet payload {}", res_packet.getJSONPayload());
                    if (res_packet.toByteArray()[0] == 0x32) {
                        // qos is 1 must send send PUBACK packet
                        this.send(new PubackPacket(res_packet.getIdentifier()));
                    }
                }
            } catch (Throwable e) {
                log.error("Exception occured when reading from stream", e);
                this.incoming_task.cancel(true);
            }
        }, 1, 1, TimeUnit.MILLISECONDS);
    }

    protected ConnackPacket await_connack() throws IOException {
        byte[] data = read();
        ConnackPacket packet = new ConnackPacket(data);
        log.debug("Received awaiting CONNACK {}", data);
        if (packet.getControlType() != ConnackPacket.CONNACK_PACKET_TYPE)
            throw new IllegalStateException("Expected CONNACK but received type " + packet.getControlType());

        return packet;
    }

    private byte[] read() throws IOException {
        byte[] packet_data = new byte[1];
        int data = this.incoming.read(packet_data);
        
        if (data == -1) throw new RuntimeException("End of stream");
        
        int multiplier = 1, length = 0;
        do {
            data = this.incoming.read();
            if (data == -1) throw new RuntimeException("Reached end of stream when reading length!");
            length += (data & 127) * multiplier;
            multiplier *= 128;
            if (multiplier > 2097152) {
                throw new RuntimeException("Malformed length");
            }
        } while ((data & 128) != 0);
        
        packet_data = Arrays.copyOf(packet_data, packet_data.length + length);
        this.incoming.read(packet_data, 1, length);

        return packet_data;
    }

    public void send(Packet packet) throws IOException {
        byte[] outgoing_byte_arr = packet.toByteArray();

        log.debug("Sent: {}", PacketUtil.hex_stringify(outgoing_byte_arr));

        outgoing.write(packet.toByteArray());
    }

    public void disconnect() throws IOException {
        this.send(new DisconnectPacket());
        this.incoming_task.cancel(true);
        this.ping_task.cancel(true);
        incoming.close();
        outgoing.close();
        socket.close();
    }
}
