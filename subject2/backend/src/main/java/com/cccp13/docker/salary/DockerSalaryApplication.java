package com.cccp13.docker.salary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class DockerSalaryApplication {
    public static void main(String[] args) {
        SpringApplication.run(DockerSalaryApplication.class, args);
    }
} 