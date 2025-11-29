package com.vishtech.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import picocli.CommandLine;
import picocli.spring.PicocliSpringFactory;


@Configuration
public class PicocliConfig {

    @Autowired
    private ApplicationContext applicationContext;
    @Bean
    public CommandLine.IFactory picocliFactory() {
        return new PicocliSpringFactory(applicationContext);
    }
}