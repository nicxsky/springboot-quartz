package com.example.quartz.util;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class RestUtil {

    /**
     *
     * @param url
     * @param params
     * @param timeout ms
     */
    public static void doPost(String url, JSONObject params, int timeout) {
        log.info("RestUtil doPost start: " + url);
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setReadTimeout(timeout);
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        restTemplate.postForEntity(url, params, JSONObject.class).getBody();

        log.info("RestUtil doPost end");
    }

}
