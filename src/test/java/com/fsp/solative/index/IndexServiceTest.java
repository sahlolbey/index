package com.fsp.solative.index;

import com.fsp.solative.index.dto.Statistics;
import com.fsp.solative.index.service.IndexService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;


@RunWith(SpringRunner.class)
@SpringBootTest
public class IndexServiceTest {
    private static final Logger logger = LoggerFactory.getLogger (IndexServiceTest.class);
    @Autowired
    IndexService indexService;

    @Test
    public void testTick() {
        long currentTimeStamp = new Date ().getTime ();
        try {
            indexService.tick ("testIN", 2.20, currentTimeStamp - 60000 + 1000);
            indexService.tick ("testIN", 10.2, currentTimeStamp - 60000 + 2000);
            indexService.tick ("testIN", 8.5, currentTimeStamp - 60000 + 3000);
            indexService.tick ("testIN", 14.5, currentTimeStamp - 60000 + 4000);
            // wait for 2 seconds
            Thread.sleep (2000);
            Statistics statistics = indexService.getStatistics ("testIN");
            assertEquals (2, statistics.getCount ());
            // wait for 1 second
            Thread.sleep (1000);
            statistics = indexService.getStatistics ("testIN");
            assertEquals (1, statistics.getCount ());
            // wait for 1 second
            Thread.sleep (1000);
            statistics = indexService.getStatistics ("testIN");
            assertEquals (0, statistics.getCount ());

        } catch (InterruptedException e) {
            logger.error (e.getMessage (), e);
        }
    }

    @Test
    public void testGetStatistics1() {
        indexService.reset ();
        long currentTimeStamp = new Date ().getTime ();
        try {
            indexService.tick ("testIN", 2.20, currentTimeStamp - 60000 + 1000);
            indexService.tick ("testIN", 10.2, currentTimeStamp - 60000 + 2000);
            indexService.tick ("testIN", 8.5, currentTimeStamp - 60000 + 3000);
            indexService.tick ("testIN", 14.5, currentTimeStamp - 60000 + 4000);

            Thread.sleep (2000);
            Statistics statistics = indexService.getStatistics ("testIN");
            assertEquals (2, statistics.getCount ());
            assertEquals (11.5, statistics.getAvg ());
            assertEquals (8.5, statistics.getMin ());
            assertEquals (14.5, statistics.getMax ());
            // wait for 1 second
            Thread.sleep (1000);
            statistics = indexService.getStatistics ("testIN");
            assertEquals (1, statistics.getCount ());
            assertEquals (14.5, statistics.getAvg ());
            assertEquals (14.5, statistics.getMin ());
            assertEquals (14.5, statistics.getMax ());
            // wait for 1 second
            Thread.sleep (1000);
            statistics = indexService.getStatistics ("testIN");
            assertEquals (0, statistics.getCount ());


        } catch (InterruptedException e) {
            logger.error (e.getMessage (), e);
        }
    }

    @Test
    public void testGetStatistics2() {
        indexService.reset ();
        long currentTimeStamp = new Date ().getTime ();
        try {

            indexService.tick ("testIN1", 2.20, currentTimeStamp - 60000 + 1000);
            indexService.tick ("testIN2", 10.2, currentTimeStamp - 60000 + 2000);
            indexService.tick ("testIN3", 8.5, currentTimeStamp - 60000 + 3000);
            indexService.tick ("testIN4", 14.5, currentTimeStamp - 60000 + 4000);

            Thread.sleep (2000);
            System.out.println ("took " + (System.currentTimeMillis () - currentTimeStamp) + " mili seconds");
            Statistics statistics = indexService.getStatistics ();
            assertEquals (3, statistics.getCount ());
            assertEquals (11.066666666666668, statistics.getAvg ());
            assertEquals (8.5, statistics.getMin ());
            assertEquals (14.5, statistics.getMax ());
            // wait for 1 second
            Thread.sleep (1000);
            statistics = indexService.getStatistics ();
            assertEquals (2, statistics.getCount ());
            assertEquals (11.5, statistics.getAvg ());
            assertEquals (8.5, statistics.getMin ());
            assertEquals (14.5, statistics.getMax ());
            // wait for 1 second
            Thread.sleep (1000);
            statistics = indexService.getStatistics ();
            assertEquals (1, statistics.getCount ());

        } catch (InterruptedException e) {
            logger.error (e.getMessage (), e);
        }
    }


}
