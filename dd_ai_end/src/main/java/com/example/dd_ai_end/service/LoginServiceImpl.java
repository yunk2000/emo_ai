package com.example.dd_ai_end.service;

import com.example.dd_ai_end.entity.User;
import com.example.dd_ai_end.mapper.UserMapper;
import com.example.dd_ai_end.param.AccountLoginParam;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final UserMapper userMapper;
//    private final PasswordEncoder passwordEncoder; // 密码加密器

    @Override
    public int accountLogin(AccountLoginParam param) {
        // 1. 根据用户名查询用户
        User user = userMapper.selectByUsername(param.getUsername());

        // 2. 用户不存在
        if (user == null) {
            return 2;
        }

        // 3. 校验密码
        if (!Objects.equals(param.getPassword(), user.getPassword())) {
            return 0;
        }

        // 4. 登录成功
        return 1;
    }

    @Override
    public int accountRegister(User user) {
        String username = user.getUsername();
        String password = user.getPassword();
        LocalDateTime createdAt = user.getCreatedAt();
        String phone = user.getPhone();

        System.out.println(createdAt);

        int i = userMapper.insertUser(username, password, createdAt, phone);
        return i;
    }

    @Override
    public int findAccountUsername(String username) {
        // 1. 根据用户名查询用户
        User user = userMapper.selectByUsername(username);

        // 2. 用户不存在
        if (user == null) {
            return 0;
        }

        return 1;
    }

    @Override
    public int findAccountPhone(String phone) {
        // 1. 根据电话查询用户
        User user = userMapper.selectByPhone(phone);

        // 2. 用户不存在
        if (user == null) {
            return 0;
        }

        return 1;
    }
}