package io.github.danielreker.t1homeworks.service1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class T1HomeworksService1Application {

    public static void main(String[] args) {
        SpringApplication.run(T1HomeworksService1Application.class, args);
    }

}
