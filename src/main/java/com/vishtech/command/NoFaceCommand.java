package com.vishtech.command;

import com.vishtech.service.FaceSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.*;

import java.nio.file.Path;
import java.util.concurrent.Callable;

@Component
@Command(name = "noface", description = "Verify images and move images without any face to sub dir", exitCodeOnExecutionException = 1)
@Slf4j
public class NoFaceCommand implements Callable<Integer> {

    @Autowired
    private FaceSearchService faceSearchService;
    @Option(names = { "-i", "--inputdir" }, description = "directory path for input images", required = true)
    private String inputDir;

    @Override
    public Integer call() throws Exception {
        try{
            Path inputDirPath = Path.of(inputDir);
            faceSearchService.findNoFaces(inputDirPath);
            return ExitCode.OK;
        }catch (Exception e){
            log.error("Fail to process images",e);
            System.err.println("Fail to process images, Error: "+e.toString());
            return ExitCode.SOFTWARE;
        }
    }
}
