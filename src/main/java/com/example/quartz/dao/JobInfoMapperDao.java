package com.example.quartz.dao;

import com.example.quartz.model.JobParams;
import com.example.quartz.entity.JobInfo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobInfoMapperDao {
    List<JobInfo> getJobInfoList(JobParams queryParams);
}
