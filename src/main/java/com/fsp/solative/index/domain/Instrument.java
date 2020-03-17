package com.fsp.solative.index.domain;

import java.util.Date;
import java.util.concurrent.ConcurrentSkipListMap;


public class Instrument implements Runnable{
    public static final Long MAX_AGE=60L;
    private String abbreviation;
    private ConcurrentSkipListMap<Long,Double> prices;
    private Statistics statistics;

    public Instrument(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public void tick(Double price, Long timestamp){
        prices.put (timestamp,price);
    }

    public Statistics getStatistics(){
        if(statistics==null){
            calculateStats ();
        }
        return statistics;
    }
    @Override
    public void run() {
        calculateStats ();
    }
    private void calculateStats(){
       deleteOldPrices ();
       statistics = new Statistics ();
       statistics.setCount (prices.size ());

       statistics.setMin ( prices.values ().parallelStream ().
               mapToDouble (Double::doubleValue).min ().getAsDouble ());
       statistics.setMax ( prices.values ().parallelStream ().
               mapToDouble (Double::doubleValue).max ().getAsDouble ());;
       statistics.setAvg ( prices.values ().parallelStream ().
               mapToDouble (Double::doubleValue).average ().getAsDouble ());;
    }
    public Double getSum(){
        return prices.values ().parallelStream ().
                mapToDouble (Double::doubleValue).sum ();
    }
    public Double getMin(){
        return prices.values ().parallelStream ().
                mapToDouble (Double::doubleValue).min ().getAsDouble ();
    }
    public Double getMax(){
        return prices.values ().parallelStream ().
                mapToDouble (Double::doubleValue).max ().getAsDouble ();
    }
    public Integer getCount(){
        return prices.size ();
    }
    public void deleteOldPrices(){
        Long now = (new Date ()).getTime ();
        prices = (ConcurrentSkipListMap<Long, Double>) prices.tailMap (now-MAX_AGE*1000);
    }
}
