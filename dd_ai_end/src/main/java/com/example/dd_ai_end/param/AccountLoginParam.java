package com.example.dd_ai_end.param;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AccountLoginParam {
    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;
}