package com.example.quartz.util;

import java.util.UUID;

public class JobUtil {

    public static String getUUID(){
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

}
