package com.example.quartz.config;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.io.IOException;
import java.util.Properties;

@Configuration
@EnableScheduling
@Slf4j
public class JobSchedulerConfig {

    @Autowired
    private JobFactory jobFactory;

    @Bean(name = "SchedulerFactory")
    public SchedulerFactoryBean schedulerFactoryBean() throws IOException {
        log.info("SchedulerFactory init...");
        SchedulerFactoryBean factory = new SchedulerFactoryBean();

        //用于quartz集群,QuartzScheduler 启动时更新己存在的Job，这样就不用每次修改targetObject后删除qrtz_job_details表对应记录了
        //factory.setOverwriteExistingJobs(true);

        //QuartzScheduler 延时启动，应用启动完x秒后 QuartzScheduler 再启动
        factory.setStartupDelay(15);

        factory.setQuartzProperties(quartzProperties());  //作业及数据源配置信息

        // 自定义Job Factory，用于Spring注入  service,bin等
        factory.setJobFactory(jobFactory);

        return factory;
    }

    @Bean
    public Properties quartzProperties() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
        //在quartz.properties中的属性被读取并注入后再初始化对象
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }

    @Bean(name = "Scheduler")
    public Scheduler scheduler() throws IOException {
        log.info("Scheduler init...");
        return schedulerFactoryBean().getScheduler();
    }

}
