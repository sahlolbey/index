package com.fsp.solative.index.general;
import java.util.concurrent.TimeUnit;

import com.fsp.solative.index.service.IndexService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.PeriodicTrigger;

@Configuration
@ComponentScan(basePackages = "com.fsp.solative.index",
        basePackageClasses = { IndexService.class })
public class TaskSchedulerConfig {
    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(5);
        threadPoolTaskScheduler.setThreadNamePrefix("ThreadPoolTaskScheduler");
        return threadPoolTaskScheduler;
    }

    @Bean
    public PeriodicTrigger periodicTrigger() {
        PeriodicTrigger periodicTrigger = new PeriodicTrigger(50000, TimeUnit.MICROSECONDS);
        periodicTrigger.setFixedRate(true);
        periodicTrigger.setInitialDelay(1000);
        return periodicTrigger;
    }
}
