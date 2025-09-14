package com.example.dd_ai_end.param;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ChatContentParam {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "用户问题不能为空")
    private String content;

    @NotBlank(message = "粗略问答时间不能为空")
    private LocalDate timestamp;

    @NotBlank(message = "具体问答时间不能为空")
    private LocalTime hss;

    @NotBlank(message = "ai回答的消息内容不能为空")
    private String contentAi;

    @NotBlank(message = "ai回答耗时不能为空")
    private String useTime;

}
