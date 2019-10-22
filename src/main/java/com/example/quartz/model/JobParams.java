package com.example.quartz.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class JobParams extends JobBaseParams{

    private String jobName;
    private String jobGroup;
    private String jobDesc;
    private String cronExpression;

}
