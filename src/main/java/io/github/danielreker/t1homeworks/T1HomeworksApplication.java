package io.github.danielreker.t1homeworks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class T1HomeworksApplication {

    public static void main(String[] args) {
        SpringApplication.run(T1HomeworksApplication.class, args);
    }

}
