package com.promptvault;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class PromptVaultApplication {

    public static void main(String[] args) {
        SpringApplication.run(PromptVaultApplication.class, args);
    }
}
