package com.company.csartain;

import java.net.URISyntaxException;
import java.util.Timer;
import java.util.TimerTask;

public class Main {
    private static final String CRYPTO_ENDPOINT = "wss://socket.polygon.io/crypto";
    private static final String BITCOIN_TICKER = "BTC-USD";
    private static final int THIRTY_SECONDS_IN_MILLIS = 30000;


    public static void main(String[] args) throws URISyntaxException, InterruptedException {
        PolygonWebSocketClient client = new PolygonWebSocketClient(CRYPTO_ENDPOINT);
        client.connectBlocking();
        // API key passed in via command line args
        client.authenticate(args[0]);
        client.subscribe(BITCOIN_TICKER);
        System.out.println("Collecting trade data...");

        // Task to print the most recent aggregate, and any that received out-of-order trades
        TimerTask printAggregatesTask = new TimerTask() {
            @Override
            public void run() {
                Aggregator aggregator = Aggregator.getInstance();
                aggregator.printAggregates();
                aggregator.createNewAggregate(BITCOIN_TICKER, System.currentTimeMillis());
            }
        };

        // Wait 30 seconds, then execute task and repeat every 30 seconds
        new Timer().schedule(printAggregatesTask, 0, THIRTY_SECONDS_IN_MILLIS);
    }
}
