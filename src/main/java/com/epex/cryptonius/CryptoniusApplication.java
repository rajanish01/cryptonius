package com.epex.cryptonius;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CryptoniusApplication {

    public static void main(String[] args) {
        SpringApplication.run(CryptoniusApplication.class, args);

    }

}
