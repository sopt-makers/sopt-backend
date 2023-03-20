package org.sopt.app.common.s3;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.common.ResponseCode;
import org.sopt.app.common.exception.ApiException;
import org.sopt.app.common.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final ArrayList<String> imageFileExtension = new ArrayList<>(
            List.of(".jpg", ".jpeg", ".png", ".JPG", ".JPEG", ".PNG"));

    private final AmazonS3 s3Client;

    @Value("${cloud.aws.credentials.accesskey}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secretkey}")
    private String secretKey;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;


    @PostConstruct
    public AmazonS3Client amazonS3Client() {
        val awsCredentials = new BasicAWSCredentials(accessKey, secretKey); //accessKey 와 secretKey를 이용하여 자격증명 객체를 얻는다.
        return (AmazonS3Client) AmazonS3ClientBuilder.standard()
                .withRegion(region) // region 설정
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials)) // 자격증명을 통해 S3 Client를 가져온다.
                .build();
    }


    public List<String> upload(List<MultipartFile> multipartFiles) {
        if (multipartFiles == null || multipartFiles.get(0).isEmpty()) {
            return new ArrayList<>();
        }

        return multipartFiles.stream().map(file -> {
            val fileName = createFileName(file.getOriginalFilename());
            val objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(file.getSize());
            objectMetadata.setContentType(file.getContentType());

            try (InputStream inputStream = file.getInputStream()) {
                s3Client.putObject(
                        new PutObjectRequest(bucket + "/mainpage/makers-app", fileName, inputStream, objectMetadata)
                                .withCannedAcl(CannedAccessControlList.PublicRead));
                return s3Client.getUrl(bucket + "/mainpage/makers-app", fileName).toString();
            } catch (IOException e) {
                throw new ApiException(ResponseCode.INVALID_RESPONSE);
            }
        }).collect(Collectors.toList());
    }

    // 이미지파일명 중복 방지
    private String createFileName(String fileName) {
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    // 파일 유효성 검사
    private String getFileExtension(String fileName) {
        if (fileName.length() == 0) {
            throw new BadRequestException(ResponseCode.INVALID_REQUEST);
        }

        val idxFileName = fileName.substring(fileName.lastIndexOf("."));
        if (!imageFileExtension.contains(idxFileName)) {
            throw new BadRequestException(ResponseCode.INVALID_REQUEST);
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }
}
