package com.abhi.websocket;

import com.abhi.handler.OrderHandler;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class CoinDCXWebSocketClient extends WebSocketClient {

    private static final Logger logger = LoggerFactory.getLogger(CoinDCXWebSocketClient.class);
    private static final String API_KEY = "96a1329b1294cf89cb7d5fb02690008e4fafda1436a9b79d";  // Replace with your actual API key
    private static final String SECRET = "61479bd2cba0326f9f335cfa933be86ca76517258731c8e1cb3d05d060b48cd1";  // Replace with your actual API secret
    private static final String CHANNEL = "coindcx";  // Example channel

    private final OrderHandler orderHandler;
    private boolean shouldReconnect = true;

    public CoinDCXWebSocketClient(OrderHandler orderHandler) throws Exception {
        super(new URI("wss://stream.coindcx.com"));
        this.orderHandler = orderHandler;
    }


    @Override
    public void onOpen(ServerHandshake handshakedata) {
        logger.info("Connected to WebSocket server");

        try {
            // Prepare body and signature for joining the channel
            Map<String, String> body = new HashMap<>();
            body.put("channel", CHANNEL);
            String jsonBody = "{\"channel\":\"" + CHANNEL + "\"}";
            String signature = HmacGenerator.generateSignature(SECRET, jsonBody);

            // Send the join request with authentication
            String joinMessage = "{\"channelName\":\"" + CHANNEL + "\",\"authSignature\":\"" + signature + "\",\"apiKey\":\"" + API_KEY + "\"}";
            send(joinMessage);
            logger.info("Joined the channel: " + CHANNEL);
        } catch (Exception e) {
            logger.error("Error generating signature or joining channel: ", e);
        }
    }

    @Override
    public void onMessage(String message) {
        logger.info("Received message: " + message);
        orderHandler.processMessage(message);  // Process message via OrderHandler
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        logger.info("Connection closed with exit code " + code + " additional info: " + reason);
        if (shouldReconnect) {
            new Thread(() -> {
                try {
                    Thread.sleep(5000); // Wait before retrying
                    reconnect();
                } catch (InterruptedException e) {
                    logger.error("Reconnection delay interrupted: ", e);
                }
            }).start();
        }
    }

    @Override
    public void onError(Exception ex) {
        logger.error("WebSocket error: ", ex);
    }

    // Method to leave the channel
    public void leaveChannel() {
        String leaveMessage = "{\"channelName\":\"" + CHANNEL + "\"}";
        send(leaveMessage);
        logger.info("Left the channel: " + CHANNEL);
    }

    // Method to stop reconnection attempts
    public void stopReconnecting() {
        this.shouldReconnect = false;
    }
}