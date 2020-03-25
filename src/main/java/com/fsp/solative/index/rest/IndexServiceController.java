package com.fsp.solative.index.rest;

import com.fsp.solative.index.domain.Instrument;
import com.fsp.solative.index.dto.Statistics;
import com.fsp.solative.index.dto.InstrumentDTO;
import com.fsp.solative.index.service.IndexService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * This is the Rest endpoint for the required services
 */
@Slf4j
@RestController
public class IndexServiceController {
    private final IndexService indexService;

    public IndexServiceController(IndexService indexService) {
        this.indexService = indexService;
    }

    /**
     * called every time we receive a tick. It is also the sole input of this rest API
     * @param instrument a financial instrument identifier
     * @return an empty body with a status code.The status code is 201 for successful tick and if the
     * price is older than 60 seconds the tick will not be successful and status will be 204.
     */
    @PostMapping("/ticks")
    public ResponseEntity<String> tick(@RequestBody InstrumentDTO instrument){
        log.debug ("instrument="+instrument.getInstrument());
        log.debug ("price="+instrument.getPrice());
        log.debug ("timestamp="+instrument.getTimestamp ());
        Long currentTimeStamp = System.currentTimeMillis ();
        int status = 201;

        try{
            if(currentTimeStamp-instrument.getTimestamp () <= Instrument.MAX_AGE*1000){
                indexService.tick (instrument.getInstrument (),instrument.getPrice (),instrument.getTimestamp ());
            }else {
                status = 204;
            }

        } catch (Exception e) {
            log.error (e.getMessage (),e);
        }
        return  ResponseEntity.status(status).body ("");
    }

    /**
     * returns the statistics based on the ticks of the last 60 seconds. if caller calls this with the uri
     * (/statistics) the aggregated statistics will be returned but if the caller calls it with (/statistics/instrument)
     * uri the statistics for that specific instrument will be returned.
     * @param instrument a financial instrument identifier
     * @return aggregated statistics or one instrument statistics as explained above.
     */
    @GetMapping({"/statistics", "/statistics/{instrument}"})
    public ResponseEntity<Statistics> getStatistics(@PathVariable(required = false) String instrument){
        Statistics response;
        if(instrument!=null)
            response = indexService.getStatistics (instrument);
        else response = indexService.getStatistics ();

        return ResponseEntity.ok ().body (response);
    }

    /**
     * this method written just to support tests
     */
    @GetMapping("/reset")
    public void reset(){
        indexService.reset ();
    }

}
