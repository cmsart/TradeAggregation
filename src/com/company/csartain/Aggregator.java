package com.company.csartain;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

public class Aggregator {
    private static Aggregator INSTANCE;
    private final List<Aggregate> aggregates = Collections.synchronizedList(new ArrayList<>());
    private final Set<Integer> aggregateIndiciesToPrint = Collections.synchronizedSet(new HashSet<>());
    private volatile long earliestAggregateStartTimestamp = 0;

    private static final String PRICE_KEY = "p";
    private static final String TIMESTAMP_KEY = "t";
    private static final String VOLUME_KEY = "s";
    private static final String TYPE_KEY = "ev";
    private static final String TRADE_TYPE_VALUE = "XT";
    private static final long ONE_HOUR_MILLIS = 3600000;
    private static final long THIRTY_SECONDS_MILLIS = 30000;

    private Aggregator() {
        // singleton
    }

    public static Aggregator getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Aggregator();
        }

        return INSTANCE;
    }

    public void processTrade(String tradeJson) {
        try {
            JSONArray trades = new JSONArray(tradeJson);
            for (Object t : trades) {
                JSONObject trade = (JSONObject) t;
                // Don't process messages such as auth/sub status responses
                if (!TRADE_TYPE_VALUE.equals(trade.getString(TYPE_KEY))) {
                    return;
                }
                double price = trade.getDouble(PRICE_KEY);
                double volume = trade.getDouble(VOLUME_KEY);
                long timestamp = trade.getLong(TIMESTAMP_KEY);

                // Determine which aggregate to update, since they may be out or order
                int indexToUpdate = (int) (((double)(timestamp - earliestAggregateStartTimestamp)) / THIRTY_SECONDS_MILLIS);
                boolean isOutOfOrder = indexToUpdate != aggregates.size() - 1;
                if (isOutOfOrder) {
                    // Updated past aggregates will need to be printed again
                    aggregateIndiciesToPrint.add(indexToUpdate);
                }

                aggregates.get(indexToUpdate).updateAggregate(price, volume, timestamp, isOutOfOrder);
            }
        } catch (JSONException e) {
            System.out.println("Unable to parse trade JSON with exception= " + e.getMessage() + ", JSON=" + tradeJson);
        }
    }

    public void createNewAggregate(String ticker, long startTimeMillis) {
        aggregates.add(new Aggregate(ticker, startTimeMillis));
        aggregateIndiciesToPrint.clear();
        aggregateIndiciesToPrint.add(aggregates.size() - 1);

        if (earliestAggregateStartTimestamp == 0) {
            earliestAggregateStartTimestamp = startTimeMillis;
        } else if (startTimeMillis >= (startTimeMillis + ONE_HOUR_MILLIS)) {
            // We don't need any aggregates more than one hour old
            // Increase the earliest timestamp and remove the expired aggregate
            earliestAggregateStartTimestamp = aggregates.get(1).getAggregateStartTimestamp();
            aggregates.remove(0);
        }
    }

    public void printAggregates() {
        for (Integer index : aggregateIndiciesToPrint) {
            System.out.println(aggregates.get(index));
        }
    }
}
