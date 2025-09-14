package com.example.dd_ai_end.common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class EmotionServiceResult {
    private String contentAi;
    private LocalDateTime timestampAi;
    private String useTime;

    // 新增构造方法
    public static EmotionServiceResult fromMap(Map<String, Object> map) {
        System.out.println(map);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String timestampStr = (String) map.get("timestamp");
        System.out.println(timestampStr);
        String u_t = map.get("useTime")+"秒";

        return new EmotionServiceResult(
                (String) map.get("answer"),
                LocalDateTime.parse(timestampStr, formatter),
                u_t
        );
    }

    // 原构造函数改为私有
    private EmotionServiceResult(String contentAi, LocalDateTime timestampAi, String useTime) {
        this.contentAi = contentAi;
        this.timestampAi = timestampAi;
        this.useTime = useTime;
    }

    // Getter方法
    public String getContentAi() { return contentAi; }
    public LocalDateTime getTimestampAi() { return timestampAi; }
    public String getUseTime() { return useTime; }
}
