package com.fsp.solative.index.service;

import com.fsp.solative.index.domain.Instrument;
import com.fsp.solative.index.dto.Statistics;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * This class is designed to implement requires index services. The scope this spring bean is singleton
 * so only one instance is created in the application.
 */
@Service
public class IndexService implements Runnable {

    /*
    the task schedule to run some operation and calculations periodically.
     */
    private final ThreadPoolTaskScheduler taskScheduler;
    // a Thread safe map to maintain instrument during the lifecycle of application
    private ConcurrentHashMap<String, Instrument> instruments;
    // Object to maintain the last aggregated index calculated;
    private Statistics statistics;

    public IndexService(ThreadPoolTaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    @PostConstruct
    public void init() {
        instruments = new ConcurrentHashMap<> ();
        taskScheduler.scheduleWithFixedDelay (this, 1000);
    }

    /**
     * Everytime a tick arrives this service method is called by Rest endpoint to store the tick in memory.
     * for every instrument added to memory there will be a scheduled task to delete old prices for it.
     *
     * @param instrumentCode: a financial instrument identifier
     * @param price:          current trade price of a financial instrument
     * @param timestamp       tick timestamp in milliseconds
     */
    public void tick(String instrumentCode, Double price, Long timestamp) {
        Instrument instrument = instruments.get (instrumentCode);
        if (instrument == null) {
            instrument = new Instrument (instrumentCode);
            taskScheduler.scheduleWithFixedDelay (instrument, 1000);
        }
        instrument.tick (price, timestamp);
        instruments.put (instrumentCode, instrument);
    }

    /**
     * calculates the statistics for the instrument identifier provided as input parameter.
     *
     * @param instrumentCode the instrument identifier
     * @return the calcualted statistics
     */
    public Statistics getStatistics(String instrumentCode) {
        Statistics instrumentStats;
        Instrument instrument = instruments.get (instrumentCode);
        instrumentStats = instrument.getStatistics ();
        return instrumentStats;
    }

    /**
     * returns the aggregated statistics which is calculated every one second within the system.
     *
     * @return as stated above
     */
    public Statistics getStatistics() {
        return statistics;
    }

    /**
     * This method calculates aggregated statistics for all instruments not older that 60 seconds
     * This method is scheduled to run every one second to keep the statistics up to date.
     */
    private void calculateAggregatedStats() {
        if (instruments != null && instruments.size () > 0) {
            statistics = new Statistics ();
            List<Statistics> statList = instruments.values ().parallelStream ().map (Instrument::getStatistics).
                    collect (Collectors.toList ());
            Double sum = 0.0;
            Double max = 0.0;
            Double min = 0.0;
            Long count = 0L;
            for (Statistics stat : statList) {
                sum += (stat.getAvg () * stat.getCount ());
                min = stat.getMin () < min || min == 0.0 ? stat.getMin () : min;
                max = stat.getMax () > max || max == 0.0 ? stat.getMax () : max;
                count += stat.getCount ();
            }
            statistics.setAvg (sum / count);
            statistics.setCount (count);
            statistics.setMax (max);
            statistics.setMin (min);
        }
    }

    /**
     * This method added just for testing purpose to get the expected results from running tests
     */
    public void reset() {
        instruments.clear ();
    }

    @Override
    public void run() {
        calculateAggregatedStats ();
    }
}
