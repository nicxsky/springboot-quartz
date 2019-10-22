package com.example.quartz.model;

public enum JobStatus {

    NONE("NONE", "不存在"),
    NORMAL("NORMAL", "正常"),
    PAUSED("PAUSED", "暂停"),
    COMPLETE("COMPLETE", "完成"),
    ERROR("ERROR", "出错"),
    BLOCKED("BLOCKED", "阻塞");

    private String code;
    private String desc;

    JobStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static String getDescByCode(String code) {
        JobStatus[] jobStatuses = values();
        for (JobStatus jobStatus : jobStatuses) {
            if (jobStatus.getCode().equals(code)) {
                return jobStatus.getDesc();
            }
        }
        return null;
    }

}
