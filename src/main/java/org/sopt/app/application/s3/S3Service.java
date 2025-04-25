package org.sopt.app.application.s3;


import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.slf4j.LoggerFactory;
import org.sopt.app.application.stamp.StampDeletedEvent;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.common.response.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class S3Service {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.uri}")
    private String baseURI;

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    public S3Info.PreSignedUrl getPreSignedUrl(String folderName) {
        String keyPrefix = "mainpage/makers-app-img/" + folderName + "/";
        String randomFileName = UUID.randomUUID().toString();
        String objectKey = keyPrefix + randomFileName;

        Duration expirationDuration = Duration.ofHours(1); // 1시간 유효기간

        try {
            PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(objectKey)
                    .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(expirationDuration)
                    .putObjectRequest(objectRequest)
                    .build();

            PresignedPutObjectRequest presignedPutObjectRequest = s3Presigner.presignPutObject(presignRequest);
            URI uri = presignedPutObjectRequest.url().toURI();
            String preSignedURL = uri.toString();
            String imageURL = baseURI + folderName + "/" + randomFileName;
            return S3Info.PreSignedUrl.builder()
                    .preSignedURL(preSignedURL)
                    .imageURL(imageURL)
                    .build();

        } catch (S3Exception | URISyntaxException e) {
            LoggerFactory.getLogger(S3Service.class).error("Error generating presigned URL: {}", e.getMessage());
            throw new BadRequestException(ErrorCode.PRE_SIGNED_URI_ERROR);
        } catch (SdkException e) {
            LoggerFactory.getLogger(S3Service.class).error("AWS SDK error generating presigned URL: {}", e.getMessage());
            throw new BadRequestException(ErrorCode.PRE_SIGNED_URI_ERROR);
        }
    }

    @EventListener(StampDeletedEvent.class)
    public void handleStampDeletedEvent(StampDeletedEvent event) {
        this.deleteFiles(event.getFileUrls(), "stamp");
    }

    private void deleteFiles(List<String> fileUrls, String folderName) {
        String keyPrefix = "mainpage/makers-app-img/" + folderName + "/";
        List<String> fileNameList = getFileNameList(fileUrls);
        fileNameList.forEach(fileName -> {
            String objectKey = keyPrefix + fileName;
            deleteFile(objectKey);
        });
    }

    private List<String> getFileNameList(List<String> fileUrls) {
        return fileUrls.stream().map(url -> {
            val fileNameSplit = url.split("/");
            return fileNameSplit[fileNameSplit.length - 1];
        }).toList();
    }

    private void deleteFile(String objectKey) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(objectKey) 
                    .build();
            s3Client.deleteObject(deleteObjectRequest);
            LoggerFactory.getLogger(S3Service.class).info("Successfully deleted S3 object: {}", objectKey);
        } catch (S3Exception e) {
            // SDK v2 예외 처리
            LoggerFactory.getLogger(S3Service.class).error("Error deleting S3 object {}: {}", objectKey, e.awsErrorDetails().errorMessage());
        } catch (SdkException e) {
            LoggerFactory.getLogger(S3Service.class).error("AWS SDK error deleting S3 object {}: {}", objectKey, e.getMessage());
        }
    }
}