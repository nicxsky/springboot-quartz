package com.example.quartz.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class JobBaseParams {

    private int pageIndex;
    private int pageSize;
    private String keyWord;

}
