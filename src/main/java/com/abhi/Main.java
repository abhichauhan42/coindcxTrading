package com.abhi;

import com.abhi.websocket.CoinDCXWebSocketClient;
import com.abhi.handler.OrderHandler;

import java.util.Scanner;

public class Main {

    private static final int MAX_RETRIES = 5;
    private static final long RETRY_DELAY = 5000; // 5 seconds

    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter the trigger price: ");
            double triggerPrice = scanner.nextDouble();

            // Implement the OrderHandler interface to handle incoming messages
            OrderHandler orderHandler = new OrderHandler(triggerPrice);

            // Retry logic
            int retryCount = 0;
            boolean connected = false;

            while (retryCount < MAX_RETRIES && !connected) {
                try {
                    // Create WebSocket client and connect
                    CoinDCXWebSocketClient client = new CoinDCXWebSocketClient(orderHandler);
                    client.connectBlocking();  // Wait until connected

                    System.out.println("Connected to WebSocket.");
                    connected = true;

                    System.out.println("Press Enter to exit...");
                    scanner.nextLine();  // Wait for the user to exit

                    // Close connection and leave channel
                    client.leaveChannel();
                    client.stopReconnecting(); // Stop reconnection attempts
                    client.closeBlocking();
                } catch (Exception e) {
                    retryCount++;
                    System.err.println("Connection failed: " + e.getMessage());
                    if (retryCount < MAX_RETRIES) {
                        System.out.println("Retrying connection... (" + retryCount + "/" + MAX_RETRIES + ")");
                        Thread.sleep(RETRY_DELAY);  // Wait before retrying
                    } else {
                        System.err.println("Max retries reached. Could not connect to WebSocket.");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
