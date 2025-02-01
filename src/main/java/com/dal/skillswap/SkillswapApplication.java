package com.dal.skillswap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.dal.skillswap.repository")
@EnableAsync
public class SkillswapApplication {

    public static void main(String[] args) {
        SpringApplication.run(SkillswapApplication.class, args);
    }

}
