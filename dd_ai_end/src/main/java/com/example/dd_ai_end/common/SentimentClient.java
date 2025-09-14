package com.example.dd_ai_end.common;

import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

public class SentimentClient {

    private static final String API_URL = "http://localhost:5000/api/predict";

    public SentimentResult predictSentiment(String text) {
        RestTemplate restTemplate = new RestTemplate();

        Map<String, String> request = new HashMap<>();
        request.put("text", text);

        Map<String, Object> response = restTemplate.postForObject(
                API_URL,
                request,
                Map.class
        );

        return new SentimentResult(
                (Integer) response.get("label"),
                (Double) response.get("confidence")
        );
    }

//    public static void main(String[] args) {
//        SentimentResult result = predictSentiment("这个电影太好看了！");
//        System.out.println("标签：" + result.getLabel());
//        System.out.println("置信度：" + result.getConfidence());
//    }
}