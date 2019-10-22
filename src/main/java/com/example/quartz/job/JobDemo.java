package com.example.quartz.job;

import com.example.quartz.service.JobDemoService;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.springframework.beans.factory.annotation.Autowired;

public class JobDemo implements JobBase {

    @Autowired
    private JobDemoService jobDemoService;

    @Override
    public void execute(JobExecutionContext context) {
        JobKey key = context.getJobDetail().getKey();
        jobDemoService.run(key);
    }
}
