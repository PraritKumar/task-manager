package com.prarit.taskmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Task Manager API.
 *
 * @SpringBootApplication enables:
 *   - @Configuration     : marks this as a config class
 *   - @EnableAutoConfiguration : auto-configures Spring beans based on classpath
 *   - @ComponentScan     : scans all sub-packages for @Service, @Repository, @Controller
 */
@SpringBootApplication
public class TaskManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskManagerApplication.class, args);
    }

}
