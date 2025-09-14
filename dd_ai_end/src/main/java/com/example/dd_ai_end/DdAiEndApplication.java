package com.example.dd_ai_end;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.dd_ai_end.mapper") // 扫描 Mapper 接口
public class DdAiEndApplication {

    public static void main(String[] args) {
        SpringApplication.run(DdAiEndApplication.class, args);
    }

}
