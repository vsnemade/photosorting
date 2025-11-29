package com.vishtech;


import com.vishtech.service.FaceSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.file.Path;
import java.util.List;

@SpringBootApplication
public class Main implements CommandLineRunner {

    @Autowired
    private FaceSearchService faceSearchService;

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        //For Local Testing
        //findNoFace();
        // matchFaces();
        //System.out.println("Done");
        //System.exit(0);
    }

    private void matchFaces() throws Exception {
        Path input = Path.of("C:\\Workspace\\Vishal\\java_projects\\PhotoSorting\\Input\\");
        Path predict = Path.of("C:\\Workspace\\Vishal\\java_projects\\PhotoSorting\\Predict\\");
        faceSearchService.findMatchingFaces(input, predict);
    }

    private void findNoFace() throws Exception {
        Path predict = Path.of("C:\\Workspace\\Vishal\\java_projects\\PhotoSorting\\Predict\\");
        faceSearchService.findNoFaces(predict);
    }

}
