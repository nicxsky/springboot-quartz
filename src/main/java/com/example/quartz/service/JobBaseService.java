package com.example.quartz.service;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JobBaseService {

    @Setter
    private String jobID;
    @Setter
    private String jobName;

    private int failed = 0;
    private int success = 0;
    private int total = 0;

    public void startJob(){
        this.success = 0;
        this.failed = 0;
        this.total = 0;
        String msg = String.format("{jobID: %s jobName: %s status: start}", jobID, jobName);
        log.info(msg);
    }

    public void endJob(){
        String msg = String.format("{jobID: %s jobName: %s total: %s success: %s failed: %s status: end}", jobID, jobName, total, success, failed);
        log.info(msg);
    }

    public void setJobTotal(int tl) {
        this.total = tl;
    }

    public void setSuccessJob(String currentKey) {
        this.success ++;
        String msg = String.format("{jobID: %s jobName: %s key: %s status: success}", jobID, jobName, currentKey);
        log.info(msg);
    }

    public void setFailJob(String currentKey) {
        this.failed ++;
        String msg = String.format("{jobID: %s jobName: %s key: %s status: failed}", jobID, jobName, currentKey);
        log.error(msg);
    }

}
