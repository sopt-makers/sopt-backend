package org.sopt.app.application.s3;


import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.sopt.app.application.stamp.StampDeletedEvent;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.common.response.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsResponse;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
@RequiredArgsConstructor
public class S3Service {

    private static final int BATCH_SIZE = 1000;
    private static final String S3_BASE_PATH = "mainpage/makers-app-img/";

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.uri}")
    private String baseURI;

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    public S3Info.PreSignedUrl getPreSignedUrl(String folderName) {
        String keyPrefix = S3_BASE_PATH + folderName + "/";
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
        this.deleteFilesBatch(event.getFileUrls());
    }

    private void deleteFilesBatch(List<String> fileUrls) {
        if (fileUrls == null || fileUrls.isEmpty()) return;

        List<ObjectIdentifier> objectIdentifiers = fileUrls.stream()
                .map(url -> ObjectIdentifier.builder().key(extractObjectKey(url)).build())
                .toList();

        for (int i = 0; i < objectIdentifiers.size(); i += BATCH_SIZE) {
            int end = Math.min(objectIdentifiers.size(), i + BATCH_SIZE);
            List<ObjectIdentifier> batch = objectIdentifiers.subList(i, end);

            try {
                DeleteObjectsRequest deleteObjectsRequest = DeleteObjectsRequest.builder()
                        .bucket(bucket)
                        .delete(Delete.builder().objects(batch).build())
                        .build();

                DeleteObjectsResponse response = s3Client.deleteObjects(deleteObjectsRequest);
                LoggerFactory.getLogger(S3Service.class).info("Successfully deleted {} S3 objects", response.deleted().size());
                
                if (response.hasErrors() && !response.errors().isEmpty()) {
                    LoggerFactory.getLogger(S3Service.class).error("Failed to delete some S3 objects: {}", response.errors());
                }
            } catch (S3Exception e) {
                LoggerFactory.getLogger(S3Service.class).error("Error bulk deleting S3 objects: {}", e.getMessage());
            } catch (SdkException e) {
                LoggerFactory.getLogger(S3Service.class).error("AWS SDK error bulk deleting S3 objects: {}", e.getMessage());
            }
        }
    }

    private String extractObjectKey(String fileUrl) {
        String key = fileUrl.replace(baseURI, "");
        return key.startsWith(S3_BASE_PATH) ? key : S3_BASE_PATH + key;
    }
}
