package com.example.quartz.service;

import com.example.quartz.model.JobParams;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.example.quartz.dao.JobInfoMapperDao;
import com.example.quartz.entity.JobInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobInfoService {

    @Autowired
    private JobInfoMapperDao jobInfoMapperDao;

    public PageInfo<JobInfo> getJobInfos(JobParams queryParams) {
        PageHelper.startPage(queryParams.getPageIndex(), queryParams.getPageSize());
        List<JobInfo> list = jobInfoMapperDao.getJobInfoList(queryParams);
        PageInfo<JobInfo> page = new PageInfo<>(list);
        return page;
    }
}
