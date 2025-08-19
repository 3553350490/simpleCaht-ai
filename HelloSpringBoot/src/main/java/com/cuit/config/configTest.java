package com.cuit.config;

import com.cuit.pojo.English;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class configTest {
    @Bean
    public English english(){
        return new English();
    }
}
