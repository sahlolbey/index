package com.fsp.solative.index.service;

import com.fsp.solative.index.domain.Instrument;
import com.fsp.solative.index.domain.Statistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class IndexService {

    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    ConcurrentHashMap<String, Instrument> instruments;

    @PostConstruct
    public void init(){
        instruments = new ConcurrentHashMap<> ();
    }
    public void tick(String instrumentCode, Double price,Long timestamp){
       Instrument instrument =  instruments.get (instrumentCode);
       if(instrument==null){
           instrument = new Instrument (instrumentCode);
           taskScheduler.scheduleWithFixedDelay (instrument,1000);
       }
       instrument.tick (price,timestamp);
       instruments.put (instrumentCode,instrument);
    }
    public Statistics getStatistics(String instrumentCode){
        Statistics statistics;
        if(instrumentCode!=null){
           Instrument instrument= instruments.get (instrumentCode);
           statistics = instrument.getStatistics ();
        }else {
            statistics = new Statistics ();
            instruments.values ().parallelStream ().forEach ((instrument -> instrument.deleteOldPrices ()));
            Double sum = instruments.values ().parallelStream ().mapToDouble (Instrument::getSum).sum ();
            Double min = instruments.values ().parallelStream ().mapToDouble (Instrument::getMin).min ().getAsDouble ();
            Double max = instruments.values ().parallelStream ().mapToDouble (Instrument::getMax).max ().getAsDouble ();
            Integer count = instruments.values ().parallelStream ().mapToInt (Instrument::getCount).sum ();
            statistics.setAvg (sum/count);
            statistics.setMax (max);
            statistics.setMin (min);
            statistics.setCount (count);

        }
        return statistics;
    }
}
