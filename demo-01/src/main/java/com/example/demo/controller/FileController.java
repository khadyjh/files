package com.example.demo.controller;

import com.amazonaws.services.sns.AmazonSNSClient;
import com.example.demo.service.AWSStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/files")
public class FileController {

    @Autowired
    private AWSStorageService service;

//    @Autowired
//    private AmazonSNSClient amazonSNSClient;
//
//    private final String arn_topic=": arn:aws:sns:eu-west-1:697682926753:khadyjh-java-test";

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam(value = "file")MultipartFile file){
        return new ResponseEntity<>(service.uploadFile(file), HttpStatus.OK);
    }

    @GetMapping("/download")
    public ResponseEntity<ByteArrayResource> downloadFile(@RequestParam String name){
        byte[] data=service.downloadFile(name);
        ByteArrayResource resource=new ByteArrayResource(data);
        return ResponseEntity
                .ok()
                .contentLength(data.length)
                .header("Content-Type","application/octet-stream")
                .header("Content-disposition","attachment; fileName=\""+name+"\"")
                .body(resource);
    }

    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<String> deleteFile(@PathVariable String fileName){
        return new ResponseEntity<>(service.deleteFile(fileName),HttpStatus.OK);
    }

}
