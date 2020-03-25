package com.fsp.solative.index;

import com.fsp.solative.index.domain.Instrument;
import com.fsp.solative.index.dto.Statistics;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class IndexServiceControllerTest extends AbstractRestTest {
    private static final Logger logger = LoggerFactory.getLogger (IndexServiceControllerTest.class);


    @Override
    @Before
    public void setUp() {
        super.setUp ();
    }


    @Test
    public void testStatistics1() {
        long currentTimeStamp = System.currentTimeMillis ();
        try {

            // clearing up the data at server side
            mvc.perform (MockMvcRequestBuilders.get ("/reset")).andReturn ();

            String uri = "/ticks";
            Map<String, String> instrument = new HashMap<> ();
            instrument.put ("instrument", "testIN1");
            instrument.put ("price", "2.20");
            long timestamp = currentTimeStamp - Instrument.MAX_AGE * 1000 + 1000;
            instrument.put ("timestamp", Long.toString (timestamp));
            postRest (uri, instrument);

            instrument = new HashMap<> ();
            instrument.put ("instrument", "testIN2");
            instrument.put ("price", "10.20");
            timestamp = currentTimeStamp - Instrument.MAX_AGE * 1000 + 2000;
            instrument.put ("timestamp", Long.toString (timestamp));
            postRest (uri, instrument);

            instrument = new HashMap<> ();
            instrument.put ("instrument", "testIN3");
            instrument.put ("price", "8.5");
            timestamp = currentTimeStamp - Instrument.MAX_AGE * 1000 + 3000;
            instrument.put ("timestamp", Long.toString (timestamp));
            postRest (uri, instrument);

            instrument = new HashMap<> ();
            instrument.put ("instrument", "testIN4");
            instrument.put ("price", "14.5");
            timestamp = currentTimeStamp - Instrument.MAX_AGE * 1000 + 4000;
            instrument.put ("timestamp", Long.toString (timestamp));
            postRest (uri, instrument);

            Thread.sleep (2000);
            Statistics statistics = getRest ("/statistics");
            assertEquals (3, statistics.getCount ());
            assertEquals (11.066666666666668, statistics.getAvg ());
            assertEquals (8.5, statistics.getMin ());
            assertEquals (14.5, statistics.getMax ());
            // wait for 1 second
            Thread.sleep (1000);
            statistics = getRest ("/statistics");
            assertEquals (2, statistics.getCount ());
            assertEquals (11.5, statistics.getAvg ());
            assertEquals (8.5, statistics.getMin ());
            assertEquals (14.5, statistics.getMax ());
            // wait for 1 second
            Thread.sleep (1000);
            statistics = getRest ("/statistics");
            assertEquals (1, statistics.getCount ());
        } catch (Exception e) {
            logger.error (e.getMessage (), e);
        }
    }

    @Test
    public void testStatistics2() {

        long currentTimeStamp = System.currentTimeMillis ();
        try {
            // clearing up the data at server side
            mvc.perform (MockMvcRequestBuilders.get ("/reset")).andReturn ();

            String uri = "/ticks";
            Map<String, String> instrument = new HashMap<> ();
            instrument.put ("instrument", "testIN");
            instrument.put ("price", "2.20");
            long timestamp = currentTimeStamp - Instrument.MAX_AGE * 1000 + 1000;
            instrument.put ("timestamp", Long.toString (timestamp));
            postRest (uri, instrument);

            instrument = new HashMap<> ();
            instrument.put ("instrument", "testIN");
            instrument.put ("price", "10.20");
            timestamp = currentTimeStamp - Instrument.MAX_AGE * 1000 + 2000;
            instrument.put ("timestamp", Long.toString (timestamp));
            postRest (uri, instrument);

            instrument = new HashMap<> ();
            instrument.put ("instrument", "testIN");
            instrument.put ("price", "8.5");
            timestamp = currentTimeStamp - Instrument.MAX_AGE * 1000 + 3000;
            instrument.put ("timestamp", Long.toString (timestamp));
            postRest (uri, instrument);

            instrument = new HashMap<> ();
            instrument.put ("instrument", "testIN");
            instrument.put ("price", "14.5");
            timestamp = currentTimeStamp - Instrument.MAX_AGE * 1000 + 4000;
            instrument.put ("timestamp", Long.toString (timestamp));
            postRest (uri, instrument);

            Thread.sleep (1000);
            Statistics statistics = getRest ("/statistics/testIN");
            assertEquals (3, statistics.getCount ());
            assertEquals (11.066666666666668, statistics.getAvg ());
            assertEquals (8.5, statistics.getMin ());
            assertEquals (14.5, statistics.getMax ());
            // wait for 1 second
            Thread.sleep (1000);
            statistics = getRest ("/statistics/testIN");
            assertEquals (2, statistics.getCount ());
            assertEquals (11.5, statistics.getAvg ());
            assertEquals (8.5, statistics.getMin ());
            assertEquals (14.5, statistics.getMax ());
            // wait for 1 second
            Thread.sleep (1000);
            statistics = getRest ("/statistics/testIN");
            assertEquals (1, statistics.getCount ());
        } catch (Exception e) {
            logger.error (e.getMessage (), e);
        }
    }

    private void postRest(String uri, Object param) {
        try {
            String inputJson = super.mapToJson (param);
            logger.info ("inputJson=" + inputJson);
            MvcResult mvcResult = mvc.perform (MockMvcRequestBuilders.post (uri)
                    .contentType (MediaType.APPLICATION_JSON_VALUE).content (inputJson)).andReturn ();

            logger.info (mvcResult.getResponse ().getContentAsString ());
        } catch (Exception e) {
            logger.error (e.getMessage (), e);
        }
    }

    private Statistics getRest(String uri) {
        Statistics statistics = null;
        try {
            MvcResult mvcResult = mvc.perform (MockMvcRequestBuilders.get (uri)).andReturn ();
            statistics = mapFromJson (mvcResult.getResponse ().getContentAsString (),
                    Statistics.class);
            logger.info (mvcResult.getResponse ().getContentAsString ());
        } catch (Exception e) {
            logger.error (e.getMessage (), e);
        }
        return statistics;
    }

    @Test
    public void testTicks() {
        try {
            String uri = "/ticks";
            Map<String, String> instrument = new HashMap<> ();
            instrument.put ("instrument", "xxx");
            instrument.put ("price", "1400.0");
            long timestamp = System.currentTimeMillis () - Instrument.MAX_AGE * 1000 + 1000;
            instrument.put ("timestamp", Long.toString (timestamp));

            String inputJson = super.mapToJson (instrument);
            logger.info ("inputJson=" + inputJson);
            MvcResult mvcResult = mvc.perform (MockMvcRequestBuilders.post (uri)
                    .contentType (MediaType.APPLICATION_JSON_VALUE).content (inputJson)).andReturn ();

            logger.info (mvcResult.getResponse ().getContentAsString ());
            int status = mvcResult.getResponse ().getStatus ();

            System.out.println ("status=" + status);
            assertEquals (201, status);

        } catch (Exception e) {
            logger.error (e.getMessage (), e);
        }
    }

}
