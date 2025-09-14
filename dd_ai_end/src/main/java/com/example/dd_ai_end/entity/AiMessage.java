package com.example.dd_ai_end.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class AiMessage implements Serializable {
    /**
     * ai消息ID（主键）
     */
    private Integer aiMessageId;

    /**
     * ai回答的消息内容
     */
    private String contentAi;

    /**
     * ai消息发送时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestampAi;

    /**
     * ai回答耗时
     */
    private String useTime;

}
