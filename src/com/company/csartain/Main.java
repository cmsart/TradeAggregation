package com.company.csartain;

import java.net.URISyntaxException;

public class Main {
    private static final String CRYPTO_ENDPOINT = "wss://socket.polygon.io/crypto";
    private static final String BITCOIN_TICKER = "XT.BTC-USD";

    public static void main(String[] args) throws URISyntaxException, InterruptedException {
        PolygonWebSocketClient client = new PolygonWebSocketClient(CRYPTO_ENDPOINT);
        client.connectBlocking();
        // API key passed in via command line args
        client.authenticate(args[0]);
        client.subscribe(BITCOIN_TICKER);
    }
}
