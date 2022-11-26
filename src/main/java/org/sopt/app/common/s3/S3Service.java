package org.sopt.app.common.s3;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.sopt.app.common.ResponseCode;
import org.sopt.app.common.exception.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

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
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey); //accessKey 와 secretKey를 이용하여 자격증명 객체를 얻는다.
        return (AmazonS3Client) AmazonS3ClientBuilder.standard()
                .withRegion(region) // region 설정
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds)) // 자격증명을 통해 S3 Client를 가져온다.
                .build();
    }


    public List<String> upload(List<MultipartFile> multipartFile) {
        List<String> imgUrlList = new ArrayList<>();

        // forEach 구문을 통해 multipartFile로 넘어온 파일들 하나씩 fileNameList에 추가
        for (MultipartFile file : multipartFile) {
            String fileName = createFileName(file.getOriginalFilename());
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(file.getSize());
            objectMetadata.setContentType(file.getContentType());

            try(InputStream inputStream = file.getInputStream()) {
                s3Client.putObject(new PutObjectRequest(bucket+"/mainpage/makers-app", fileName, inputStream, objectMetadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));
                imgUrlList.add(s3Client.getUrl(bucket+"/mainpage/makers-app", fileName).toString());
            } catch(IOException e) {
                throw new ApiException(ResponseCode.INVALID_RESPONSE);
            }
        }
        return imgUrlList;
    }

    // 이미지파일명 중복 방지
    private String createFileName(String fileName) {
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    // 파일 유효성 검사
    private String getFileExtension(String fileName) {
        if (fileName.length() == 0) {
            throw new ApiException(ResponseCode.INVALID_RESPONSE);
        }
        ArrayList<String> fileValidate = new ArrayList<>();
        fileValidate.add(".jpg");
        fileValidate.add(".jpeg");
        fileValidate.add(".png");
        fileValidate.add(".JPG");
        fileValidate.add(".JPEG");
        fileValidate.add(".PNG");
        String idxFileName = fileName.substring(fileName.lastIndexOf("."));
        if (!fileValidate.contains(idxFileName)) {
            throw new ApiException(ResponseCode.INVALID_RESPONSE);
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }



//    @PostConstruct
//    public void setS3Client() {
//        AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey); //accessKey 와 secretKey를 이용하여 자격증명 객체를 얻는다.
//        s3Client = AmazonS3ClientBuilder.standard()
//                .withCredentials(new AWSStaticCredentialsProvider(credentials))  // 자격증명을 통해 S3 Client를 가져온다.
//                .withRegion(this.region) // region 설정
//                .build();
//    }
//
//    public String upload(MultipartFile file) throws IOException {
//        String fileName = file.getOriginalFilename();
//
//        // 업로드 하기 위해 사용되는 함수
//        s3Client.putObject(new PutObjectRequest(bucket, fileName, file.getInputStream(), null)
//                .withCannedAcl(CannedAccessControlList.PublicRead)); //외부에 공개해야 하므로 Public read 권한을 준다.
//        return s3Client.getUrl(bucket, fileName).toString();
//    }

}
