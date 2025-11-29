package com.vishtech.service;

import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.output.DetectedObjects;
import com.vishtech.model.EmbeddingsHolder;
import com.vishtech.model.ImageElement;
import com.vishtech.util.ImageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class FaceSearchService {

    @Autowired
    private FaceRecognitionService faceRecognitionService;

    public void findNoFaces(Path predictDir) throws Exception {
        if(!Files.isDirectory(predictDir)) throw new RuntimeException("1st argument is not a Predict directory");
        // Create subdirectory for images with no faces
        Path noFaceDir = predictDir.resolve("no_face");
        if (!Files.exists(noFaceDir)) {
            Files.createDirectories(noFaceDir);
            log.info("Created directory {}", noFaceDir);
        }
        Files.walk(predictDir)
                .filter(f->f.toString().matches(".*(jpg|JPG|jpeg|JPEG)"))
                .filter(f -> ImageUtil.isImage(f))
                .map(f -> {
                    try {
                        return new ImageElement(ImageUtil.getImage(f), f);
                    } catch (Exception e) {
                       log.error("Fail to read image {}, error: {}",f, e.getMessage() );
                        return null;  // skip this file
                    }
                }).filter(Objects::nonNull)
                .forEach(imageElement -> {
                    try {
                        log.info("Processing image {}", imageElement.getOriginalPath());
                        ImageElement processedImageElement = faceRecognitionService.detectFaces(imageElement);
                        boolean noFace = false;

                        if (processedImageElement == null) {
                            noFace = true;
                        } else {
                            DetectedObjects detectedObjects = processedImageElement.getDetectedFaces();
                            if (detectedObjects == null || detectedObjects.getNumberOfObjects() == 0) {
                                noFace = true;
                            }
                        }

                        if (noFace) {
                            log.info("No Face detected in {}", imageElement.getOriginalPath());

                            // Move file to no_face directory
                            Path source = imageElement.getOriginalPath();
                            Path target = noFaceDir.resolve(source.getFileName());

                            try {
                                Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
                                log.info("Moved {} → {}", source, target);
                            } catch (Exception e) {
                                log.error("Failed to move {} to {} due to {}", source, target, e.getMessage(), e);
                            }
                        }
                    }catch (Exception e){
                        log.error("Fail to process image ", e);
                    }
                });
        log.info("Done");
    }

    public void findMatchingFaces(Path inputDir, Path predictDir) throws Exception {
        if(!Files.isDirectory(inputDir)) throw new RuntimeException("1st argument is not a input directory");
        if(!Files.isDirectory(predictDir)) throw new RuntimeException("2nd argument is not a predict directory");
        // Create "not_matched" subfolder inside predictDir
        Path notMatchedDir = predictDir.resolve("not_matched");
        if (!Files.exists(notMatchedDir)) {
            Files.createDirectories(notMatchedDir);
        }
        EmbeddingsHolder trainEmbeddingsHolder = faceRecognitionService.generateEmbeddings(inputDir, false, false);

        Files.walk(predictDir)
                .filter(f->f.toString().matches(".*(jpg|JPG|jpeg|JPEG)"))
                .filter(f -> ImageUtil.isImage(f))
                .map(f -> {
                    try {
                        return new ImageElement(ImageUtil.getImage(f), f);
                    } catch (Exception e) {
                        log.error("Fail to read image {}, error: {}",f, e.getMessage() );
                        return null;  // skip this file
                    }
                })
                .filter(Objects::nonNull)
                .forEach(imageElement -> {
                    try {
                        log.info("Processing image {}", imageElement.getOriginalPath());
                        Image image = imageElement.getImage();
                        DetectedObjects detectedObjects = faceRecognitionService.predict(image, trainEmbeddingsHolder);
                        boolean matched = false;
                        if (Objects.nonNull(detectedObjects) && detectedObjects.getNumberOfObjects() > 0) {
                            for (int i = 0; i < detectedObjects.getNumberOfObjects(); i++) {
                                DetectedObjects.DetectedObject obj = detectedObjects.item(i);
                                if (obj.getProbability() > 0.75) {
                                    matched = true;
                                    break;
                                }
                            }
                            if (matched) {
                                log.info("Matched {}", imageElement.getOriginalPath());
                            } else {
                                log.info(" Faced Detected but not matched {}", imageElement.getOriginalPath());
                                moveToNotMatched(imageElement.getOriginalPath(), notMatchedDir);

                            }
                        } else {
                            log.info("Not Matched {}", imageElement.getOriginalPath());
                            moveToNotMatched(imageElement.getOriginalPath(), notMatchedDir);

                        }
                    }catch (Exception e){
                        log.error("Failed to Process image {}, Error: {}", imageElement.getOriginalPath(), e.getMessage());
                    }
                });

        log.info("Done");
    }
    private void moveToNotMatched(Path file, Path notMatchedDir) throws IOException {
        Path target = notMatchedDir.resolve(file.getFileName());
        Files.move(file, target, StandardCopyOption.REPLACE_EXISTING);
        log.info("Moved to NOT_MATCHED folder: {}", target);
    }
}