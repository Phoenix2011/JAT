package com.massage.spa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpaManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpaManagementApplication.class, args);
    }

}
