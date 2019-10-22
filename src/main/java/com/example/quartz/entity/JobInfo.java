package com.example.quartz.entity;

import lombok.Data;

import java.math.BigInteger;

@Data
public class JobInfo {

    private String jobName;
    private String jobGroup;
    private String jobClassName;
    private String triggerName;
    private String triggerGroup;
    private BigInteger repeatInterval;
    private BigInteger timesTriggered;
    private String cronExpression;
    private String timeZoneId;
    private String jobDesc;
    private String jobStatus;

    private String startTime;
    private String endTime;
    private String preFireTime;
    private String nextFireTime;

}
