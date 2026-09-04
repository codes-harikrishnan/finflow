package com.harikrishnan.finflow.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.concurrent.Executor;


@Configuration
@EnableAsync
public class AsyncConfig{

    @Bean(name = "taskExecutor")
    public Executor taskExecutor () {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setQueueCapacity(100);
        threadPoolTaskExecutor.setCorePoolSize(2);
        threadPoolTaskExecutor.setMaxPoolSize(5);
        threadPoolTaskExecutor.setThreadNamePrefix("async-thread-");
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }

}
