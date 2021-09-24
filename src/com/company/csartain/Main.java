package com.company.csartain;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

public class Main {
    public static void main(String[] args) throws URISyntaxException, InterruptedException {
        WebSocketClient mWs = new WebSocketClient( new URI( "wss://socket.polygon.io/crypto" )){
            @Override
            public void onMessage( String message ) {
                System.out.println( message );
            }
            @Override
            public void onOpen( ServerHandshake handshake ) {
                System.out.println( "opened connection" );
            }
            @Override
            public void onClose( int code, String reason, boolean remote ) {
                System.out.println( "closed connection" );
            }
            @Override
            public void onError( Exception ex ) {
                ex.printStackTrace();
            }
        };

        // wait for connection before auth/subscribe calls
        mWs.connectBlocking();

        // authenticate using API key passed through args
        String apiKey = args[0];
        JSONObject auther = new JSONObject();
        auther.put("action", "auth");
        auther.put("params", apiKey);
        String message = auther.toString();
        mWs.send(message);

        // subscribe to desired ticker
        JSONObject suber = new JSONObject();
        suber.put("action", "subscribe");
        suber.put("params", "XT.BTC-USD");
        message = suber.toString();
        mWs.send(message);
    }
}
