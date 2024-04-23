package org.sopt.app.application.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.joda.time.LocalDateTime;
import org.slf4j.LoggerFactory;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.common.response.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final ArrayList<String> imageFileExtension = new ArrayList<>(
            List.of(".jpg", ".jpeg", ".png", ".JPG", ".JPEG", ".PNG"));

    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Value("${cloud.aws.s3.uri}")
    private String baseURI;

    private final AmazonS3 amazonS3;


    @PostConstruct
    public AmazonS3 getAmazonS3() {
        val awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        return AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }


    public List<String> uploadDeprecated(List<MultipartFile> multipartFiles) {
        if (multipartFiles == null || multipartFiles.get(0).isEmpty()) {
            return new ArrayList<>();
        }

        return multipartFiles.stream().map(file -> {
            val fileName = createFileName(file.getOriginalFilename());
            val objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(file.getSize());
            objectMetadata.setContentType(file.getContentType());

            try (InputStream inputStream = file.getInputStream()) {
                amazonS3.putObject(
                        new PutObjectRequest(bucket + "/mainpage/makers-app", fileName, inputStream, objectMetadata)
                                .withCannedAcl(CannedAccessControlList.PublicRead));
                return amazonS3.getUrl(bucket + "/mainpage/makers-app", fileName).toString();
            } catch (IOException e) {
                throw new BadRequestException("요청이 처리 되지 않았습니다.");
            }
        }).toList();
    }

    private String createFileName(String fileName) {
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    private String getFileExtension(String fileName) {
        if (fileName.isEmpty()) {
            throw new BadRequestException("유효하지 않은 파일명입니다.");
        }

        val idxFileName = fileName.substring(fileName.lastIndexOf("."));
        if (!imageFileExtension.contains(idxFileName)) {
            throw new BadRequestException("유효하지 않은 확장자입니다.");
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }

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
            throw new BadRequestException(ErrorCode.PRE_SIGNED_URI_ERROR.getMessage());
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
