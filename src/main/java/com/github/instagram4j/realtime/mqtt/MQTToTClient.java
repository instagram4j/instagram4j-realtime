package com.github.instagram4j.realtime.mqtt;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import javax.net.ssl.SSLSocketFactory;
import com.github.instagram4j.realtime.mqtt.packet.ConnackPacket;
import com.github.instagram4j.realtime.mqtt.packet.DisconnectPacket;
import com.github.instagram4j.realtime.mqtt.packet.MQTToTConnectPacket;
import com.github.instagram4j.realtime.mqtt.packet.Packet;
import com.github.instagram4j.realtime.mqtt.packet.PubackPacket;
import com.github.instagram4j.realtime.mqtt.packet.PublishPacket;
import com.github.instagram4j.realtime.utils.PacketUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MQTToTClient implements Closeable {
    private Socket socket;
    private InputStream incoming;
    private OutputStream outgoing;
    private final String host;
    private final int port;
    private final List<Consumer<Packet>> packetListeners;
    
    /**
     * Construct a MQTToTClient to connect to the specified server host and port.
     * 
     * Optionally, add packet listeners to act on packet received events.
     * 
     * @param host Server Host name
     * @param port Server Port number
     * @param packetListeners An array of packet consumers to listen on when a packet is received
     */
    @SafeVarargs
    public MQTToTClient(String host, int port, Consumer<Packet> ...packetListeners) {
        this.host = host;
        this.port = port;
        this.packetListeners = Arrays.asList(packetListeners);
    }
    
    /**
     * Connects the MQTToT client and is a blocking operation.
     * 
     * @param connectPayload The MQTToT connect packet payload
     * @param onReady Runs upons successful connection
     * @throws UnknownHostException
     * @throws IOException
     */
    public void connect(byte[] connectPayload, Runnable onReady) throws UnknownHostException, IOException {
        socket = SSLSocketFactory.getDefault().createSocket(host, port);
        incoming = socket.getInputStream();
        outgoing = socket.getOutputStream();

        this.send(new MQTToTConnectPacket(connectPayload));
        ConnackPacket connack = this.awaitConnack();

        if (connack.getReturnCode() != 0) {
            log.error("CONNACK received code: {}", connack.getReturnCode());
            this.disconnect();
            return;
        }
        
        log.info("Successfully connected to {}:{}", host, port);
        
        onReady.run();
        
        while(true) {
            this.readPacket();
        }
    }
    
    private void readPacket() throws IOException {
        final byte[] data = this.read();
        log.debug("Received: {}", PacketUtil.hexStringify(data));
        final byte packetControlType = PacketUtil.getControlType(data[0]);
        if (packetControlType == PublishPacket.PUBLISH_PACKET_TYPE) {
            final PublishPacket packet = new PublishPacket(data);
            if (packet.getQoS() >= 1) {
                log.debug("Sending PUBACK for ID={} QoS={}", packet.getPacketIdentifier(), packet.getQoS());
                this.send(new PubackPacket(packet.getPacketIdentifier()));
            }
            packetListeners.forEach(consumer -> consumer.accept(packet));
        }
    }

    private ConnackPacket awaitConnack() throws IOException {
        byte[] data = this.read();
        ConnackPacket packet = new ConnackPacket(data);
        log.debug("Received CONNACK: {}", data);

        return packet;
    }
    
    /**
     * Reads from the socket according to MQTT 3.1.1 Specifications.
     * 
     * @return The packet data without the fixed header length. 
     *         The first byte is the fixed header parameter, and the 
     *         remaining bytes are the variable header and/or payload.
     * @throws IOException
     */
    private byte[] read() throws IOException {
        byte[] packet_data = new byte[1];
        int data = this.incoming.read(packet_data);
        
        if (data == -1) throw new IOException("End of stream");
        
        int multiplier = 1, length = 0;
        do {
            data = this.incoming.read();
            if (data == -1) throw new IOException("Reached end of stream when reading length!");
            length += (data & 127) * multiplier;
            multiplier *= 128;
            if (multiplier > 2097152) {
                throw new IOException("Malformed length");
            }
        } while ((data & 128) != 0);
        
        log.debug("Received fixed header {} and length is {} bytes.", PacketUtil.hexStringify(packet_data), length);
        
        packet_data = Arrays.copyOf(packet_data, packet_data.length + length);
        
        while (length > 0) {
            int readLength = this.incoming.read(packet_data, packet_data.length - length, length);
            log.debug("Read length {}", readLength);
            if (readLength == -1) {
                throw new IOException("Reached end of stream when reading payload!");
            }
            length -= readLength;
        }
        
        return packet_data;
    }
    
    /**
     * Sends a packet to the MQTT broker.
     * 
     * @param packet A MQTT/MQTToT 3.1.1 specified packet
     * @throws IOException
     */
    public void send(Packet packet) throws IOException {
        byte[] outgoing_byte_arr = packet.toByteArray();

        log.debug("Sent: {}", PacketUtil.hexStringify(outgoing_byte_arr));

        outgoing.write(packet.toByteArray());
    }
    
    /**
     * Disconnects the MQTT client and cleans up.
     * 
     * @throws IOException
     */
    public void disconnect() throws IOException {
        this.send(new DisconnectPacket());
        this.close();
    }

    @Override
    public void close() throws IOException {
        this.incoming.close();
        this.outgoing.close();
        this.socket.close();
    }
}
