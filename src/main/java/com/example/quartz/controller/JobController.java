package com.example.quartz.controller;

import com.example.quartz.model.ExceptionMsg;
import com.example.quartz.model.JobParams;
import com.example.quartz.model.JobStatus;
import com.example.quartz.model.ResponseResult;
import com.example.quartz.service.JobInfoService;
import com.github.pagehelper.PageInfo;
import com.example.quartz.entity.JobInfo;
import com.example.quartz.job.JobBase;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/job")
@Slf4j
public class JobController {

    @Autowired
    @Qualifier("Scheduler")
    private Scheduler scheduler;

    @Autowired
    private JobInfoService jobInfoService;

    public static JobBase getJobClass(String clzName) throws Exception {
        Class<?> clz = Class.forName(clzName);
        return (JobBase) clz.newInstance();
    }

    /**
     * 新增job
     *
     * @param queryParams
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/add", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseResult jobAdd(@RequestBody(required = false) JobParams queryParams) throws Exception {
        if (queryParams == null || StringUtils.isBlank(queryParams.getJobName())
                || StringUtils.isBlank(queryParams.getJobGroup())
                || StringUtils.isBlank(queryParams.getCronExpression())
                || StringUtils.isBlank(queryParams.getJobDesc())) {
            return new ResponseResult(ExceptionMsg.FAILED.getCode(), "关键参数为空");
        }

        String jobClassName = queryParams.getJobName();
        String jobGroupName = queryParams.getJobGroup();
        String description = queryParams.getJobDesc();
        String cronExpression = queryParams.getCronExpression().trim();

        scheduler.start();
        JobDetail jobDetail = JobBuilder.newJob(getJobClass(jobClassName).getClass()).withIdentity(jobClassName, jobGroupName).withDescription(description).build();
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
        CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(jobClassName, jobGroupName).withSchedule(scheduleBuilder).build();

        try {
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            String errorMsg = "创建定时任务失败";
            log.error(errorMsg, e);
            return new ResponseResult(ExceptionMsg.FAILED.getCode(), errorMsg + ": " + e.getMessage());
        }

        log.info(String.format("添加定时任务成功: {jobName: %s, jobGroup: %s}", jobClassName, jobGroupName));

        return new ResponseResult(ExceptionMsg.SUCCESS, "添加定时任务成功");
    }

    /**
     * 删除job
     * <br>没运行的任务会运行至结束
     *
     * @param queryParams
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/del", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseResult jobDel(@RequestBody(required = false) JobParams queryParams) throws Exception {
        if (queryParams == null || StringUtils.isBlank(queryParams.getJobName())
                || StringUtils.isBlank(queryParams.getJobGroup())) {
            return new ResponseResult(ExceptionMsg.FAILED.getCode(), "关键参数为空");
        }

        String jobClassName = queryParams.getJobName();
        String jobGroupName = queryParams.getJobGroup();

        try {
            scheduler.pauseTrigger(TriggerKey.triggerKey(jobClassName, jobGroupName));
            scheduler.unscheduleJob(TriggerKey.triggerKey(jobClassName, jobGroupName));
            scheduler.deleteJob(JobKey.jobKey(jobClassName, jobGroupName));
        } catch (Exception e) {
            // 任务不存在也不会到Exception
            String errorMsg = "删除定时任务失败";
            log.error(errorMsg, e);
            return new ResponseResult(ExceptionMsg.FAILED.getCode(), errorMsg + ": " + e.getMessage());
        }

        log.info(String.format("删除定时任务成功: {jobName: %s, jobGroup: %s}", jobClassName, jobGroupName));

        return new ResponseResult(ExceptionMsg.SUCCESS, "删除定时任务成功");
    }

    /**
     * 更新job
     *
     * @param queryParams
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/update", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseResult jobUpdate(@RequestBody(required = false) JobParams queryParams) throws Exception {
        if (queryParams == null || StringUtils.isBlank(queryParams.getJobName())
                || StringUtils.isBlank(queryParams.getJobGroup())
                || StringUtils.isBlank(queryParams.getCronExpression())
                || StringUtils.isBlank(queryParams.getJobDesc())) {
            return new ResponseResult(ExceptionMsg.FAILED.getCode(), "关键参数为空");
        }

        //TODO Handling 1 trigger(s) that missed their scheduled fire-time.
        //如果程序没运行完，修改完之后就会不运行了
        String jobClassName = queryParams.getJobName();
        String jobGroupName = queryParams.getJobGroup();
        String description = queryParams.getJobDesc();
        String cronExpression = queryParams.getCronExpression();

        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(jobClassName, jobGroupName);
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).withDescription(description).startAt(new Date()).build();
            scheduler.rescheduleJob(triggerKey, trigger);
        } catch (SchedulerException e) {
            String errorMsg = "更新定时任务失败";
            log.error(errorMsg, e);
            return new ResponseResult(ExceptionMsg.FAILED.getCode(), errorMsg + ": " + e.getMessage());
        }

        log.info(String.format("更新定时任务成功: {jobName: %s, jobGroup: %s}", jobClassName, jobGroupName));
        return new ResponseResult(ExceptionMsg.SUCCESS, "更新定时任务成功");
    }

    /**
     * 停止job
     * <br>不是中断任务，没有执行完的任务在resume之后会继续执行
     * <br>被停止的job即使服务重启也不会继续执行，需要调用resume唤起
     *
     * @param queryParams
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/pause", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseResult jobPause(@RequestBody(required = false) JobParams queryParams) throws Exception {
        if (queryParams == null || StringUtils.isBlank(queryParams.getJobName())
                || StringUtils.isBlank(queryParams.getJobGroup())) {
            return new ResponseResult(ExceptionMsg.FAILED.getCode(), "关键参数为空");
        }

        String jobClassName = queryParams.getJobName();
        String jobGroupName = queryParams.getJobGroup();

        try {
            scheduler.pauseJob(JobKey.jobKey(jobClassName, jobGroupName));
        } catch (SchedulerException e) {
            String errorMsg = "暂停定时任务失败";
            log.error(errorMsg, e);
            return new ResponseResult(ExceptionMsg.FAILED.getCode(), errorMsg + ": " + e.getMessage());
        }

        log.info(String.format("停止定时任务成功: {jobName: %s, jobGroup: %s}", jobClassName, jobGroupName));
        return new ResponseResult(ExceptionMsg.SUCCESS, "停止定时任务成功");
    }

    /**
     * 恢复job
     * <br>会唤醒被中断的任务
     *
     * @param queryParams
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/resume", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseResult jobResume(@RequestBody(required = false) JobParams queryParams) throws Exception {
        if (queryParams == null || StringUtils.isBlank(queryParams.getJobName())
                || StringUtils.isBlank(queryParams.getJobGroup())) {
            return new ResponseResult(ExceptionMsg.FAILED.getCode(), "关键参数为空");
        }

        String jobClassName = queryParams.getJobName();
        String jobGroupName = queryParams.getJobGroup();

        try {
            scheduler.resumeJob(JobKey.jobKey(jobClassName, jobGroupName));
        } catch (SchedulerException e) {
            String errorMsg = "恢复定时任务失败";
            log.error(errorMsg, e);
            return new ResponseResult(ExceptionMsg.FAILED.getCode(), errorMsg + ": " + e.getMessage());
        }

        log.info(String.format("恢复定时任务成功: {jobName: %s, jobGroup: %s}", jobClassName, jobGroupName));
        return new ResponseResult(ExceptionMsg.SUCCESS, "恢复定时任务成功");
    }

    /**
     * 执行一次job
     *
     * @param queryParams
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/trigger", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseResult jobTrigger(@RequestBody(required = false) JobParams queryParams) throws Exception {
        if (queryParams == null || StringUtils.isBlank(queryParams.getJobName())
                || StringUtils.isBlank(queryParams.getJobGroup())) {
            return new ResponseResult(ExceptionMsg.FAILED.getCode(), "关键参数为空");
        }

        String jobClassName = queryParams.getJobName();
        String jobGroupName = queryParams.getJobGroup();

        try {
            scheduler.triggerJob(JobKey.jobKey(jobClassName, jobGroupName));
        } catch (SchedulerException e) {
            String errorMsg = "执行一次任务失败";
            log.error(errorMsg, e);
            return new ResponseResult(ExceptionMsg.FAILED.getCode(), errorMsg + ": " + e.getMessage());
        }

        log.info(String.format("执行一次任务成功: {jobName: %s, jobGroup: %s}", jobClassName, jobGroupName));
        return new ResponseResult(ExceptionMsg.SUCCESS, "执行一次任务成功");
    }

    /**
     * 获取job状态
     *
     * @param queryParams
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/status", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseResult jobStatus(@RequestBody(required = false) JobParams queryParams) throws Exception {
        if (queryParams == null || StringUtils.isBlank(queryParams.getJobName())
                || StringUtils.isBlank(queryParams.getJobGroup())) {
            return new ResponseResult(ExceptionMsg.FAILED.getCode(), "关键参数为空");
        }

        String jobClassName = queryParams.getJobName();
        String jobGroupName = queryParams.getJobGroup();
        Trigger.TriggerState triggerState = null;
        try {
            JobKey jobkey = new JobKey(jobClassName, jobGroupName);
            TriggerKey triggerKey = TriggerKey.triggerKey(jobkey.getName(), jobkey.getGroup());
            triggerState = scheduler.getTriggerState(triggerKey);
        } catch (Exception e) {
            String errorMsg = "获取任务状态失败";
            log.error(errorMsg, e);
            return new ResponseResult(ExceptionMsg.FAILED.getCode(), errorMsg + ": " + e.getMessage());
        }

        String status = JobStatus.getDescByCode(triggerState.name());

        log.info(String.format("获取定时任务状态成功: {jobName: %s, jobGroup: %s, status: %s}", jobClassName, jobGroupName, status));
        return new ResponseResult(ExceptionMsg.SUCCESS.getCode(), "获取任务状态成功", status);
    }

    /**
     * 获取Job列表
     *
     * @param queryParams
     * @return
     */
    @RequestMapping(value = "/query", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseResult<Map<String, Object>> query(@RequestBody(required = false) JobParams queryParams) {
        if (queryParams == null) {
            return new ResponseResult(ExceptionMsg.FAILED.getCode(), "关键参数为空", null);
        }
        PageInfo<JobInfo> jobInfos = jobInfoService.getJobInfos(queryParams);
        Map<String, Object> map = new HashMap<>();
        map.put("jobInfos", jobInfos);
        map.put("number", jobInfos.getTotal());
        return new ResponseResult(ExceptionMsg.SUCCESS.getCode(), "查询job成功", map);
    }
}
