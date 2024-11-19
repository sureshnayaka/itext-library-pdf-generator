package com.monster.npd.governance.pdf.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
     CustomPhysicalNamingStrategy customPhysicalNamingStrategy() {
        return new CustomPhysicalNamingStrategy();
    }
}
