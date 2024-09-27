package com.github.java.wol;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.net.*;

@Slf4j
@ApplicationScoped
public class WakeOnLanService {

    @ConfigProperty(name = "broadcast-port", defaultValue = "9")
    Integer broadcastPort;

    @ConfigProperty(name = "broadcast-address", defaultValue = "255.255.255.255")
    String broadcastAddress;

    public void sendPacket(byte[] macBytes) {
        try {
            byte[] bytes = new byte[6 + 16 * macBytes.length];
            for (int i = 0; i < 6; i++) {
                bytes[i] = (byte) 0xff;
            }
            for (int i = 6; i < bytes.length; i += macBytes.length) {
                System.arraycopy(macBytes, 0, bytes, i, macBytes.length);
            }

            InetAddress address = InetAddress.getByName(broadcastAddress);
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, broadcastPort);
            DatagramSocket socket = new DatagramSocket();
            socket.send(packet);
            socket.close();

            log.info("Wake-on-LAN packet sent");
        } catch (Exception e) {
            log.error("Failed to send Wake-on-LAN packet", e);
            System.exit(1);
        }
    }
}