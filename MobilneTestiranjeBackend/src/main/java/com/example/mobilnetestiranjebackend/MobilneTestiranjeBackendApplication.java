package com.example.mobilnetestiranjebackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MobilneTestiranjeBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(MobilneTestiranjeBackendApplication.class, args);

    }

}
