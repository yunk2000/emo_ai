package com.example.dd_ai_end.mapper;

import com.example.dd_ai_end.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;

@Mapper
public interface UserMapper {
    User selectByUsername(String username);

    User selectByPhone(String username);

    int insertUser(String username, String password, LocalDateTime createdAt, String phone);

    String findUsernameById(Integer userId);

}