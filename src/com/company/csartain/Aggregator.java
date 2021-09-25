package com.company.csartain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Aggregator {
    private static Aggregator INSTANCE;
    private final List<Aggregate> aggregates = Collections.synchronizedList(new ArrayList<>());
    private final List<Integer> aggregateIndiciesToPrint = Collections.synchronizedList(new ArrayList<>());

    private static final String PRICE_KEY = "p";
    private static final String TIMESTAMP_KEY = "t";
    private static final String VOLUME_KEY = "s";

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
        // parse json
        // aggregation logic
    }

    public void createNewAggregate(String ticker, long startTimeMillis) {
        aggregates.add(new Aggregate(ticker, startTimeMillis));
        aggregateIndiciesToPrint.clear();
    }

    public void printAggregates() {
        // always print the most recent aggregate
        System.out.println(aggregates.get(aggregates.size() - 1).toString());
        // also print any aggregates that had out of order trades during the last time period
        for (Integer index : aggregateIndiciesToPrint) {
            System.out.println(aggregates.get(index));
        }
    }
}
