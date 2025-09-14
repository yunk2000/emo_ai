package com.example.dd_ai_end.service;

import com.example.dd_ai_end.entity.User;
import com.example.dd_ai_end.param.AccountLoginParam;

public interface LoginService {
    /**
     * 账号密码登录
     * @param param 登录参数
     * @return 状态码 (0=密码错误, 1=成功, 2=用户不存在)
     */
    int accountLogin(AccountLoginParam param);

    /**
     * 账号注册
     * @param user
     * @return 状态码（0=失败，1=成功）
     */
    int accountRegister(User user);

    /**
     * 查找是否存在相同用户名
     * @param username
     * @return 状态码（0=失败，没有该用户名，1=成功查到，有该用户名）
     */
    int findAccountUsername(String username);

    /**
     * 查找是否存在相同电话用户
     * @param phone
     * @return 状态码（0=失败，没有该电话，1=成功查到，有该电话）
     */
    int findAccountPhone(String phone);

}
