package com.example.quartz.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public interface JobBase extends Job {
    void execute(JobExecutionContext context) throws JobExecutionException;
}
