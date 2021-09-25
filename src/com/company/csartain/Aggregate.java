package com.company.csartain;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Aggregate {
    private final String ticker;
    private final double open;
    private double close;
    private double high;
    private double low;
    private double volume;
    private final long aggregateStartTimestamp;
    private long latestTradeTimestamp;
    private boolean updatedAfterPeriod = false;

    /**
     * Aggregates are always initialized as a result of a trade being received from the API, so the initial trade
     * data will be used to set the starting Aggregate data
     * @param ticker - Ticker symbol of the asset pair for the trade
     * @param tradePrice - Price of the trade used to initialize the Aggregate
     * @param tradeVolume - Volume of the trade used to initialize the Aggregate
     * @param tradeTimestamp - Timestamp of the trade used to initialize the Aggregate
     */
    public Aggregate(String ticker, double tradePrice, double tradeVolume, long tradeTimestamp) {
        this.ticker = ticker;
        open = tradePrice;
        close = tradePrice;
        high = tradePrice;
        low = tradePrice;
        volume = tradeVolume;
        aggregateStartTimestamp = tradeTimestamp;
        latestTradeTimestamp = tradeTimestamp;
    }

    /**
     * Updates aggregate values using trade data
     * @param tradePrice - Price of the trade used to update the Aggregate
     * @param tradeVolume - Volume of the trade used to update the Aggregate
     * @param tradeTimestamp - Timestamp of the trade used to update the Aggregate
     */
    public void updateAggregate(double tradePrice, double tradeVolume, long tradeTimestamp, boolean afterPeriod) {
        if (tradePrice > high) {
            high = tradePrice;
        } else if (tradePrice < low) {
            low = tradePrice;
        }

        if (tradeTimestamp > latestTradeTimestamp) {
            latestTradeTimestamp = tradeTimestamp;
            close = tradePrice;
        }

        volume += tradeVolume;
        updatedAfterPeriod = afterPeriod;
    }

    @Override
    public String toString() {
        Format dateFormat = new SimpleDateFormat("HH.mm.ss");
        String dateFromMillis = dateFormat.format(new Date(aggregateStartTimestamp));
        String updatedText = updatedAfterPeriod ? " (Updated)" : "";
        return ticker + " - " + dateFromMillis + updatedText +
                " - open: $" + open +
                ", close: $" + close +
                ", high: $" + high +
                ", low: $" + low +
                ", volume: " + volume;
    }
}
