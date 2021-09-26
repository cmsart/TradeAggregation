package com.company.csartain;

import org.json.JSONException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

class AggregatorTest {
    @Test
    public void testGetInstance() {
        Assertions.assertNotNull(Aggregator.getInstance());
    }

    @Test
    public void testProcessTrade_BadJson() {
        Assertions.assertThrows(JSONException.class, () -> {
            Aggregator.getInstance().processTrade("not json");
        });
    }

    @Test
    public void testProcessTrade_WrongResponseType() {
        String tradeJson = "[{\"ev\": \"status\", \"p\": 123, \"s\": 100, \"t\": " + System.currentTimeMillis() + "}]";
        List<Aggregate> aggregates = new ArrayList<>();
        Aggregate aggregate = Mockito.mock(Aggregate.class);
        aggregates.add(aggregate);

        Aggregator aggregator = new Aggregator(aggregates);
        aggregator.processTrade(tradeJson);
        // verify no updates were made
        Mockito.verifyNoInteractions(aggregate);
    }

    @Test
    public void testProcessTrade_InOrder() {
        long startTime = System.currentTimeMillis();
        long tradeTime = startTime + 40000;
        String tradeJson = "[{\"ev\": \"XT\", \"p\": 123, \"s\": 100, \"t\": " + tradeTime + "}]";

        List<Aggregate> aggregates = new ArrayList<>();
        Aggregate aggregate = Mockito.mock(Aggregate.class);
        Aggregate aggregate2 = Mockito.mock(Aggregate.class);
        aggregates.add(aggregate);
        aggregates.add(aggregate2);

        Aggregator aggregator = new Aggregator(aggregates);
        aggregator.setEarliestAggregateStartTimestamp(startTime);
        aggregator.processTrade(tradeJson);

        Mockito.verify(aggregate2).updateAggregate(123, 100, tradeTime, false);
    }

    @Test
    public void testProcessTrade_OutOfOrder() {
        long startTime = System.currentTimeMillis();
        // Should update first aggregate in list
        long tradeTime = startTime + 10000;
        String tradeJson = "[{\"ev\": \"XT\", \"p\": 123, \"s\": 100, \"t\": " + tradeTime + "}]";

        List<Aggregate> aggregates = new ArrayList<>();
        Aggregate aggregate = Mockito.mock(Aggregate.class);
        Aggregate aggregate2 = Mockito.mock(Aggregate.class);
        aggregates.add(aggregate);
        aggregates.add(aggregate2);

        Aggregator aggregator = new Aggregator(aggregates);
        aggregator.setEarliestAggregateStartTimestamp(startTime);
        aggregator.processTrade(tradeJson);

        Mockito.verify(aggregate).updateAggregate(123, 100, tradeTime, true);
        Assertions.assertEquals(1, aggregator.getAggregateIndicesToPrint().size());
        Assertions.assertTrue(aggregator.getAggregateIndicesToPrint().contains(0));
    }

    @Test
    public void testCreateNewAggregate() {
        long startTime = System.currentTimeMillis();
        Aggregator aggregator = new Aggregator(new ArrayList<>());
        aggregator.createNewAggregate("BTC-USD", startTime);
        aggregator.createNewAggregate("BTC-USD", startTime + 30000);

        Assertions.assertEquals(2, aggregator.getAggregates().size());
        Assertions.assertEquals(startTime, aggregator.getEarliestAggregateStartTimestamp());
    }

    @Test
    public void testCreateNewAggregate_OverOneHourFromStart() {
        long startTime = System.currentTimeMillis();
        Aggregator aggregator = new Aggregator(new ArrayList<>());
        aggregator.createNewAggregate("BTC-USD", startTime);
        // 30 sec later
        aggregator.createNewAggregate("BTC-USD", startTime + 30000);
        // 1 hour later
        aggregator.createNewAggregate("BTC-USD", startTime + 3600000);

        Assertions.assertEquals(2, aggregator.getAggregates().size());
        // first has been replaced by second
        Assertions.assertEquals(startTime + 30000, aggregator.getAggregates().get(0).getAggregateStartTimestamp());
        // earliest timestamp has been updated
        Assertions.assertEquals(startTime + 30000, aggregator.getEarliestAggregateStartTimestamp());
    }
}