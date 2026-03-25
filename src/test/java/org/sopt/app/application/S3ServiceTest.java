package org.sopt.app.application;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.application.s3.S3Info;
import org.sopt.app.application.s3.S3Service;
import org.sopt.app.application.stamp.StampDeletedEvent;
import org.sopt.app.common.exception.BadRequestException;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;


@ExtendWith(MockitoExtension.class)
class S3ServiceTest {

    @Mock
    private S3Client s3Client;

    @Mock
    private S3Presigner s3Presigner;

    @Mock
    private PresignedPutObjectRequest presignedPutObjectRequest;

    @InjectMocks
    private S3Service s3Service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(s3Service, "bucket", "test-bucket");
        ReflectionTestUtils.setField(s3Service, "baseURI", "https://test-base-uri.com/");
    }

    @Test
    @DisplayName("SUCCESS_PreSignedUrl 조회 성공")
    void SUCCESS_getPreSignedUrl() throws MalformedURLException, URISyntaxException {
        String folderName = "stamp";
        URL testUrl = new URL("http://presigned-url.com");

        when(s3Presigner.presignPutObject(any(PutObjectPresignRequest.class)))
                .thenReturn(presignedPutObjectRequest);
        when(presignedPutObjectRequest.url()).thenReturn(testUrl);

        S3Info.PreSignedUrl result = s3Service.getPreSignedUrl(folderName);

        Assertions.assertEquals(testUrl.toString(), result.getPreSignedURL());
        Assertions.assertTrue(result.getImageURL().startsWith("https://test-base-uri.com/stamp/"));
    }

    @Test
    @DisplayName("FAIL_PreSignedUrl 생성 중 S3Exception 발생")
    void FAIL_getPreSignedUrlS3Exception() {
        String folderName = "stamp";

        when(s3Presigner.presignPutObject(any(PutObjectPresignRequest.class)))
                .thenThrow(S3Exception.builder().message("S3 Error").build());

        Assertions.assertThrows(BadRequestException.class, () -> {
            s3Service.getPreSignedUrl(folderName);
        });
    }

    @Test
    @DisplayName("SUCCESS_스탬프 삭제 이벤트 발생 시 S3 객체 대량 삭제 (1000개 이하)")
    void SUCCESS_deleteFilesOnStampDeletedEventBatchUnderLimit() {
        String folderName = "stamp";
        List<String> fileUrls = List.of(
                "https://test-base-uri.com/" + folderName + "/file1.jpg",
                "https://test-base-uri.com/" + folderName + "/file2.png"
        );
        StampDeletedEvent event = new StampDeletedEvent(fileUrls);

        DeleteObjectsResponse response = DeleteObjectsResponse.builder().deleted(new ArrayList<>()).build();
        when(s3Client.deleteObjects(any(DeleteObjectsRequest.class))).thenReturn(response);

        s3Service.handleStampDeletedEvent(event);

        verify(s3Client, times(1)).deleteObjects(any(DeleteObjectsRequest.class));
    }

    @Test
    @DisplayName("SUCCESS_스탬프 삭제 이벤트 발생 시 S3 객체 대량 삭제 (1000개 초과 시 분할 요청)")
    void SUCCESS_deleteFilesOnStampDeletedEventBatchOverLimit() {
        String folderName = "stamp";
        List<String> fileUrls = new ArrayList<>();
        for (int i = 0; i < 1500; i++) {
            fileUrls.add("https://test-base-uri.com/" + folderName + "/file" + i + ".jpg");
        }
        StampDeletedEvent event = new StampDeletedEvent(fileUrls);

        DeleteObjectsResponse response = DeleteObjectsResponse.builder().deleted(new ArrayList<>()).build();
        when(s3Client.deleteObjects(any(DeleteObjectsRequest.class))).thenReturn(response);

        s3Service.handleStampDeletedEvent(event);

        // 1500개이므로 1000개, 500개로 나누어 2번 호출되어야 함
        verify(s3Client, times(2)).deleteObjects(any(DeleteObjectsRequest.class));
    }

    @Test
    @DisplayName("SUCCESS_S3 삭제 중 에러가 발생해도 로그만 남기고 예외를 던지지 않음")
    void SUCCESS_deleteFilesWithS3Error() {
        String folderName = "stamp";
        List<String> fileUrls = List.of("https://test-base-uri.com/" + folderName + "/file1.jpg");
        StampDeletedEvent event = new StampDeletedEvent(fileUrls);

        when(s3Client.deleteObjects(any(DeleteObjectsRequest.class)))
                .thenThrow(S3Exception.builder().message("Forbidden").build());

        Assertions.assertDoesNotThrow(() -> s3Service.handleStampDeletedEvent(event));
        
        verify(s3Client, times(1)).deleteObjects(any(DeleteObjectsRequest.class));
    }
}
