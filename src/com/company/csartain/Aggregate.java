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
     * @param ticker - Ticker symbol of the asset pair being aggregated
     * @param aggregateStartTimestamp - Timestamp of the start of the aggregate period
     */
    public Aggregate(String ticker, long aggregateStartTimestamp) {
        this.ticker = ticker;
        this.aggregateStartTimestamp = aggregateStartTimestamp;
        open = 0;
        close = 0;
        high = Double.MAX_VALUE;
        low = 0;
        volume = 0;
        latestTradeTimestamp = 0;
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

        if (open == 0 && close == 0) {
            return ticker + " - " + dateFromMillis + " - No Data";
        }

        String updatedText = updatedAfterPeriod ? " (Updated)" : "";
        return ticker + " - " + dateFromMillis + updatedText +
                " - open: $" + open +
                ", close: $" + close +
                ", high: $" + high +
                ", low: $" + low +
                ", volume: " + volume;
    }
}
