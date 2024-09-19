package com.abhi.handler;


import org.json.JSONObject;

public class OrderHandler {

    private double triggerPrice;
    private double currentPrice;

    public OrderHandler(double triggerPrice) {
        this.triggerPrice = triggerPrice;
    }

    public void processMessage(String message) {
        // Extract price from the message (assuming JSON format)
        JSONObject jsonMessage = new JSONObject(message);
        if (jsonMessage.has("last_price")) {
            currentPrice = jsonMessage.getDouble("last_price");
            System.out.println("Current Price: " + currentPrice);
            checkPriceAndPrepareOrder();
        } else {
            System.out.println("Price information not found in the message.");
        }
    }

    private void checkPriceAndPrepareOrder() {
        if (currentPrice <= triggerPrice) {
            System.out.println("Trigger price hit or below. Preparing BUY order payload.");
            prepareBuyOrderPayload();
        } else if (currentPrice >= triggerPrice) {
            System.out.println("Trigger price hit or above. Preparing SELL order payload.");
            prepareSellOrderPayload();
        }
    }

    private void prepareBuyOrderPayload() {
        String symbol = "BTCUSDT";  // Example symbol, make this dynamic
        double quantity = 1.0;      // Order quantity, can be dynamic based on user input
        String payload = "{\"symbol\":\"" + symbol + "\",\"orderType\":\"BUY\",\"price\":" + currentPrice + ",\"quantity\":" + quantity + "}";
        System.out.println("Prepared BUY order payload: " + payload);
    }

    private void prepareSellOrderPayload() {
        String symbol = "BTCUSDT";  // Example symbol, make this dynamic
        double quantity = 1.0;      // Order quantity, can be dynamic based on user input
        String payload = "{\"symbol\":\"" + symbol + "\",\"orderType\":\"SELL\",\"price\":" + currentPrice + ",\"quantity\":" + quantity + "}";
        System.out.println("Prepared SELL order payload: " + payload);
    }
}
