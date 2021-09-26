package com.company.csartain;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

public class PolygonWebSocketClient extends WebSocketClient {
    public PolygonWebSocketClient(String uri) throws URISyntaxException {
        super(new URI(uri));
    }

    @Override
    public void onMessage(String message) {
        Aggregator.getInstance().processTrade(message);
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        System.out.println("Connected to " + getURI());
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Closed connection to " + getURI());
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

    public void authenticate(String apiKey) {
        JSONObject authRequest = new JSONObject();
        authRequest.put("action", "auth");
        authRequest.put("params", apiKey);
        String message = authRequest.toString();
        this.send(message);
    }

    public void subscribe(String ticker) {
        JSONObject subscribeRequest = new JSONObject();
        subscribeRequest.put("action", "subscribe");
        subscribeRequest.put("params", "XT." + ticker);
        String message = subscribeRequest.toString();
        this.send(message);
    }
}
