package com.example.dd_ai_end.controller;

import com.example.dd_ai_end.entity.User;
import com.example.dd_ai_end.param.AccountLoginParam;
import com.example.dd_ai_end.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("login")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class userLoginController {

    private final LoginService loginService;

    /**
     * 1. 账号登录
     */

    @PostMapping("accountLogin")
    public int accountLogin(@RequestBody AccountLoginParam param) {
        return loginService.accountLogin(param);
    }

    /**
     * 2. 账号注册
     */
    @PostMapping("accountRegister")
    public int accountRegister(@RequestBody User user) {
//        System.out.println(user);
        return loginService.accountRegister(user);
    }

    /**
     * 3.查找是否以经有该用户名
     */
    @GetMapping("findAccountUsername")
    public int findAccountUsername(String username) {
        return loginService.findAccountUsername(username);
    }

    /**
     * 4.查找是否以有该电话账号
     */
    @GetMapping("findAccountPhone")
    public int findAccountPhone(String phone) {
        return loginService.findAccountPhone(phone);
    }
}
