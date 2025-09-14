package com.example.dd_ai_end.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class ChatHistory implements Serializable {
    /**
     * 聊天记录ID（主键）
     */
    private Integer messageId;

    /**
     * 用户ID（外键）
     */
    private Integer userId;

    /**
     * 情感记录表ID（外键）
     */
    private Integer sentimentId;

    /**
     * ai消息ID（外键）
     */
    private Integer aiMessageId;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 发送时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate timestamp;

    /**
     * 具体发送时间
     */
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime hss;

}
