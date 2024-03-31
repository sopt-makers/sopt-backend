package org.sopt.app.application;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectResult;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.application.s3.S3Info;
import org.sopt.app.application.s3.S3Service;
import org.sopt.app.common.exception.BadRequestException;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
public class S3ServiceTest {

    @Mock
    private AmazonS3 s3Client;

    @InjectMocks
    private S3Service s3Service;

    @Test
    @DisplayName("SUCCESS_업로드(DEPRECATED) 파일 리스트 null일 때")
    void SUCCESS_uploadDeprecatedNull() {
        List<String> result = s3Service.uploadDeprecated(null);
        Assertions.assertEquals(0, result.size());
    }

    @Test
    @DisplayName("SUCCESS_업로드(DEPRECATED) 파일 리스트 첫 원소 empty일 때")
    void SUCCESS_uploadDeprecatedEmpty() {
        List<String> result = s3Service.uploadDeprecated(
                List.of(new MockMultipartFile("name", (byte[]) null))
        );
        Assertions.assertEquals(0, result.size());
    }

    @Test
    @DisplayName("SUCCESS_업로드(DEPRECATED) 파일 리스트 not null일 때")
    void SUCCESS_uploadDeprecatedNotNull() throws MalformedURLException {
        when(s3Client.putObject(any())).thenReturn(new PutObjectResult());
        when(s3Client.getUrl(any(), any())).thenReturn(new URL("http://url.com"));

        List<String> result = s3Service.uploadDeprecated(
                List.of(new MockMultipartFile("files", "image.jpg", "text/plain",
                        "1234".getBytes(StandardCharsets.UTF_8)))
        );
        Assertions.assertEquals(1, result.size());
    }

    @Test()
    @DisplayName("FAIL_업로드(DEPRECATED) 잘못된 파일명")
    void FAIL_uploadDeprecatedInvalidFilename() {
        Assertions.assertThrows(BadRequestException.class, () -> {
            s3Service.uploadDeprecated(
                    List.of(new MockMultipartFile("files", "", "text/plain",
                            "1234".getBytes(StandardCharsets.UTF_8))));
        });
    }

    @Test
    @DisplayName("FAIL_업로드(DEPRECATED) 잘못된 파일 확장자")
    void FAIL_uploadDeprecatedInvalidExtension() {
        Assertions.assertThrows(BadRequestException.class, () -> {
            s3Service.uploadDeprecated(
                    List.of(new MockMultipartFile("files", "file.csv", "text/plain",
                            "1234".getBytes(StandardCharsets.UTF_8))));
        });
    }

    @Test
    @DisplayName("SUCCESS_PreSignedUrl 조회 성공")
    void SUCCESS_getPreSignedUrl() throws MalformedURLException {
        when(s3Client.generatePresignedUrl(any(), any(), any(), any()))
                .thenReturn(new URL("http://url.com"));

        S3Info.PreSignedUrl result = s3Service.getPreSignedUrl("FolderName");
        Assertions.assertEquals("http://url.com", result.getPreSignedURL());
        Assertions.assertEquals("nullFolderName", result.getImageURL().substring(0, 14));
    }

    @Test
    @DisplayName("FAIL_PreSignedUrl 조회 중 NullPointerException 발생")
    void FAIL_getPreSignedUrlNullPointerException() {
        when(s3Client.generatePresignedUrl(any(), any(), any(), any()))
                .thenThrow(NullPointerException.class);

        Assertions.assertThrows(BadRequestException.class, () -> {
            s3Service.getPreSignedUrl("FolderName");
        });
    }

    @Test
    @DisplayName("SUCCESS_파일 삭제")
    void SUCCESS_deleteFiles() {
        doNothing().when(s3Client).deleteObject(any(), any());

        Assertions.assertDoesNotThrow(() ->
                s3Service.deleteFiles(List.of("https://url.com"), "folderName")
        );
    }

    @Test
    @DisplayName("FAIL_파일 삭제 중 AmazonServiceException 발생")
    void FAIL_deleteFilesAmazonServiceException() {
        doThrow(AmazonServiceException.class).when(s3Client).deleteObject(any(), any());

        s3Service.deleteFiles(List.of("https://url.com"), "folderName");
    }
}