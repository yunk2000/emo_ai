package com.example.dd_ai_end.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class User implements Serializable {
    /**
     * 用户ID（主键，手动输入）
     */
    private Integer userId;

    /**
     * 用户名(唯一)
     */
    private String username;

    /**
     * 用户密码
     */
    private String password;

    /**
     * 注册时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * 用户电话号码(唯一)
     */
    private String phone;

}