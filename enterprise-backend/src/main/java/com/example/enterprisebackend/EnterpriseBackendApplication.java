package com.example.enterprisebackend;

import org.mybatis.spring.annotation.MapperScan; // 记得导包
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// 👇👇👇 加上这一行！括号里是你的 mapper 包的路径
@MapperScan("com.example.enterprisebackend.mapper")
public class EnterpriseBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(EnterpriseBackendApplication.class, args);
    }

}