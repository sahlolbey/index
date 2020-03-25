package com.fsp.solative.index.domain;

import com.fsp.solative.index.dto.Statistics;
import lombok.extern.slf4j.Slf4j;
import java.util.Date;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * This class keeps Instrument information including its abbreviated name plus prices receive for it during the
 * time. As required, the prices will no longer needed after 60 seconds so this class is defined as a Runnable
 * thread and scheduled to run every one second to clear up old prices.
 */
@Slf4j
public class Instrument implements Runnable {
    public static final Long MAX_AGE = 60L;

    private String abbreviation;
    private ConcurrentSkipListMap<Long, Double> prices;
    private Statistics statistics;

    public Instrument(String abbreviation) {

        this.abbreviation = abbreviation;
        prices = new ConcurrentSkipListMap<> ();
    }

    public void tick(Double price, Long timestamp) {
        prices.put (timestamp, price);
    }

    public Statistics getStatistics() {
        calculateStats ();
        return statistics;
    }

    @Override
    public void run() {
        deleteOldPrices ();
    }

    private void calculateStats() {
        statistics = new Statistics ();
        log.debug ("count=" + prices.size ());
        if (prices.size () > 0) {
            statistics.setCount ((long) prices.size ());

            statistics.setMin (prices.values ().parallelStream ().
                    mapToDouble (Double::doubleValue).min ().getAsDouble ());
            statistics.setMax (prices.values ().parallelStream ().
                    mapToDouble (Double::doubleValue).max ().getAsDouble ());
            statistics.setAvg (prices.values ().parallelStream ().
                    mapToDouble (Double::doubleValue).average ().getAsDouble ());

        }
    }

    /**
     * This method delete old prices and scheduled to run every 1 second.
     */
    private void deleteOldPrices() {
        if (prices != null || prices.size () > 0) {
            long now = (new Date ()).getTime ();
            prices = new ConcurrentSkipListMap<> (prices.tailMap (now - MAX_AGE * 1000));
        }
    }

}
