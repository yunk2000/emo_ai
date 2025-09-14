package com.example.dd_ai_end.common;

import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class EmotionServiceClient {

    private static final String API_URL = "http://localhost:5000/api/chat";
    private final RestTemplate restTemplate = new RestTemplate();

    public EmotionServiceResult getEmotionResponse(String message) {
        try {
            // 构建请求体
            Map<String, String> request = new HashMap<>();
            request.put("message", message);

            // 发送请求
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    API_URL,
                    request,
                    Map.class
            );

            // 处理响应
            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> body = response.getBody();
                if (body != null && "success".equals(body.get("status"))) {
                    System.out.println(body);
//                    return (String) body.get("answer");
                    return EmotionServiceResult.fromMap(body);
                }
                throw new RuntimeException("API响应异常: " + body.get("message"));
            }
            throw new RuntimeException("HTTP错误: " + response.getStatusCode());

        } catch (Exception e) {
            throw new RuntimeException("请求失败: " + e.getMessage());
        }
    }

}
