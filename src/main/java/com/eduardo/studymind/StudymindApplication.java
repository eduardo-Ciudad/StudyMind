package com.eduardo.studymind;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class StudymindApplication implements AsyncConfigurer {

    public static void main(String[] args) {
        SpringApplication.run(StudymindApplication.class, args);
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) ->
                System.err.println("Erro assíncrono em " + method.getName() + ": " + ex.getMessage());
    }
}
