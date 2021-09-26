package com.company.csartain;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

public class Aggregator {
    private static Aggregator INSTANCE;
    private final List<Aggregate> aggregates;
    private final Set<Integer> aggregateIndicesToPrint = Collections.synchronizedSet(new HashSet<>());
    private volatile long earliestAggregateStartTimestamp = 0;

    private static final String PRICE_KEY = "p";
    private static final String TIMESTAMP_KEY = "t";
    private static final String VOLUME_KEY = "s";
    private static final String TYPE_KEY = "ev";
    private static final String TRADE_TYPE_VALUE = "XT";
    private static final long ONE_HOUR_MILLIS = 3600000;
    private static final long THIRTY_SECONDS_MILLIS = 30000;

    // For testing only
    Aggregator(List<Aggregate> aggregates) {
        this.aggregates = aggregates;
    }

    private Aggregator() {
        this(Collections.synchronizedList(new ArrayList<>()));
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
                    aggregateIndicesToPrint.add(indexToUpdate);
                }

                aggregates.get(indexToUpdate).updateAggregate(price, volume, timestamp, isOutOfOrder);
            }
        } catch (JSONException e) {
            System.out.println("Unable to parse trade JSON with exception= " + e.getMessage() + ", JSON=" + tradeJson);
            throw e;
        }
    }

    public void createNewAggregate(String ticker, long startTimeMillis) {
        aggregates.add(new Aggregate(ticker, startTimeMillis));
        aggregateIndicesToPrint.clear();
        aggregateIndicesToPrint.add(aggregates.size() - 1);

        if (earliestAggregateStartTimestamp == 0) {
            earliestAggregateStartTimestamp = startTimeMillis;
        } else if (startTimeMillis >= (earliestAggregateStartTimestamp + ONE_HOUR_MILLIS)) {
            // We don't need any aggregates more than one hour old
            // Increase the earliest timestamp and remove the expired aggregate
            earliestAggregateStartTimestamp = aggregates.get(1).getAggregateStartTimestamp();
            aggregates.remove(0);
        }
    }

    public void printAggregates() {
        for (Integer index : aggregateIndicesToPrint) {
            System.out.println(aggregates.get(index));
        }
    }

    public List<Aggregate> getAggregates() {
        return aggregates;
    }

    public Set<Integer> getAggregateIndicesToPrint() {
        return aggregateIndicesToPrint;
    }

    public long getEarliestAggregateStartTimestamp() {
        return earliestAggregateStartTimestamp;
    }

    void setEarliestAggregateStartTimestamp(long earliestAggregateStartTimestamp) {
        this.earliestAggregateStartTimestamp = earliestAggregateStartTimestamp;
    }
}
