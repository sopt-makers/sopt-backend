package org.sopt.app.application.s3;

import com.amazonaws.*;
import com.amazonaws.services.s3.*;
import java.io.*;
import java.net.*;
import java.util.*;
import lombok.*;
import org.joda.time.LocalDateTime;
import org.slf4j.LoggerFactory;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.common.response.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class S3Service {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.uri}")
    private String baseURI;

    private final AmazonS3 amazonS3;

    public S3Info.PreSignedUrl getPreSignedUrl(String folderName) {
        val now = LocalDateTime.now();
        val folderURI = bucket + "/mainpage/makers-app-img/" + folderName;
        val randomFileName = UUID.randomUUID();

        URI uri;
        try {
            // TODO: EC2와 자바의 시간이 UTC와 KST로 9시간이 차이나 있어 10시간을 더함, 추후 수정 필요
            uri = amazonS3.generatePresignedUrl(folderURI,
                    randomFileName.toString(), now.plusHours(10).toDate(), HttpMethod.PUT).toURI();
        } catch (NullPointerException | URISyntaxException e) {
            throw new BadRequestException(ErrorCode.PRE_SIGNED_URI_ERROR);
        }

        val preSignedURL = uri.toString();
        val imageURL = baseURI + folderName + "/" + randomFileName;

        return S3Info.PreSignedUrl.builder()
                .preSignedURL(preSignedURL)
                .imageURL(imageURL)
                .build();
    }

    @Async
    public void deleteFiles(List<String> fileUrls, String folderName) {
        val folderURI = bucket + "/mainpage/makers-app-img/" + folderName;
        val fileNameList = getFileNameList(fileUrls);
        fileNameList.forEach(file -> deleteFile(folderURI, file));
    }

    private List<String> getFileNameList(List<String> fileUrls) {
        return fileUrls.stream().map(url -> {
            val fileNameSplit = url.split("/");
            return fileNameSplit[fileNameSplit.length - 1];
        }).toList();
    }

    private void deleteFile(String folderURI, String fileName) {
        try {
            amazonS3.deleteObject(folderURI, fileName.replace(File.separatorChar, '/'));
        } catch (AmazonServiceException e) {
            LoggerFactory.getLogger(S3Service.class).error(e.getErrorMessage());
        }
    }
}
