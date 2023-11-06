package com.example;

import com.example.config.Config;
import com.example.service.impl.DiskServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.File;

@SpringBootApplication
public class InteractionWithYandexDiskApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(InteractionWithYandexDiskApiApplication.class, args);

        File file = new File("C:\\Users\\avdee\\IdeaProjects\\interaction-with-Yandex-disk-API\\src\\main\\java\\com\\example\\data\\testFile.txt");
        ApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
        DiskServiceImpl service = context.getBean(DiskServiceImpl.class);

        String uploadResult = service.upload(file);
        System.out.println("Upload result: " + uploadResult);
    }
}
