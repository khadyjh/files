package com.example.demo.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.SubscribeRequest;
import com.amazonaws.util.IOUtils;
import com.example.demo.entity.Document;
import com.example.demo.entity.User;
import com.example.demo.repository.DocumentRepository;
import com.example.demo.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import java.net.URL;
import java.util.List;

@Service
@Slf4j
public class AWSStorageService {
    @Value("${application.bucket.name}")
    private String bucketName;

    @Autowired
    private AmazonS3 s3Client;

    @Autowired
    DocumentRepository documentRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AmazonSNSClient amazonSNSClient;

    private final String ARN_TOPIC="arn:aws:sns:eu-west-1:697682926753:khadyjh-java-test";


    public String uploadFile(MultipartFile file){
        User user= (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(file.getOriginalFilename().contains(".pdf")){
            java.io.File fileObj=convertMultiPartFileToFile(file);
            if(fileObj.length()<150000){
                String fileName=System.currentTimeMillis()+"_"+file.getOriginalFilename();
                s3Client.putObject(new PutObjectRequest(bucketName,fileName,fileObj));

                Document document=new Document();
                document.setName(fileName);
                URL url=s3Client.getUrl(bucketName,fileName);
                document.setLink(url.toString());
                document.setSize(fileObj.length());
                document.setUser(user);

                user.getFiles().add(document);

                userRepository.save(user);
                documentRepository.save(document);

                SubscribeRequest subscribeRequest=new SubscribeRequest(ARN_TOPIC,"email",user.getEmail());
                amazonSNSClient.subscribe(subscribeRequest);

                double total=totalSize(user);
                PublishRequest publishRequest=new PublishRequest(ARN_TOPIC,"file uploaded successfully with name " +
                        document.getName() + " and size " + document.getSize()+"byte " + "and total size of your files is : "+total
                        + "byte","HELLO FROM DEMO ");
                amazonSNSClient.publish(publishRequest);

                fileObj.delete();
                return "file uploaded " +fileName;

            }else {
                return "not allowed size /: ";
            }
//            String fileName=System.currentTimeMillis()+"_"+file.getOriginalFilename();
//            s3Client.putObject(new PutObjectRequest(bucketName,fileName,fileObj));
//
//            Document document=new Document();
//            document.setName(fileName);
//            URL url=s3Client.getUrl(bucketName,fileName);
//            document.setLink(url.toString());
//            document.setSize(fileObj.length());
//            document.setUser(user);
//
//            user.getFiles().add(document);
//
//            userRepository.save(user);
//            documentRepository.save(document);
//
//            SubscribeRequest subscribeRequest=new SubscribeRequest(ARN_TOPIC,"email",user.getEmail());
//            amazonSNSClient.subscribe(subscribeRequest);
//
//            double total=totalSize(user);
//            PublishRequest publishRequest=new PublishRequest(ARN_TOPIC,"file uploaded successfully with name " +
//                    document.getName() + " and size " + document.getSize()+"byte " + "and total size of your files is : "+total + "byte","HELLO FROM DEMO ");
//            amazonSNSClient.publish(publishRequest);
//
//            fileObj.delete();
//            return "file uploaded " +fileName;
            } else {
             return "uploadFile: not pdf file" ;
        }
    }

    public double totalSize(User user){
        double totalSize=0;
        List<Document> documentList=documentRepository.findByUser(user);
        for (Document doc :
                documentList) {
            totalSize=totalSize+doc.getSize();
        }

        return totalSize;
    }


    public byte[] downloadFile(String fileName){
        S3Object s3Object=s3Client.getObject(bucketName,fileName);
        S3ObjectInputStream inputStream=s3Object.getObjectContent();
        try {
           byte[] content= IOUtils.toByteArray(inputStream);
           return content;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String deleteFile(String fileName){
        s3Client.deleteObject(bucketName,fileName);
        return fileName + " is removed ... ";
    }



    private File convertMultiPartFileToFile(MultipartFile file) {
       final File convertedFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            log.error("Error converting multipartFile to file", e);
        }
        return convertedFile;
    }
}
