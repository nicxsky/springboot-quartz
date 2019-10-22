package com.example.quartz.service;

import com.example.quartz.util.JobUtil;
import com.example.quartz.util.RestUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobKey;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JobDemoService extends JobBaseService {

    public void run(JobKey key) {

        String jobName = key.getName();
        String uuid = JobUtil.getUUID();
        log.info("Job name and group are: {" + key.getName() + ":" + key.getGroup() + "}");
        setJobID(uuid);
        setJobName(jobName);
        startJob();
        setJobTotal(1);

        log.info("######TEST######");

        //TODO RestUtil.doPost();

        setSuccessJob(jobName);
        endJob();
    }

}
