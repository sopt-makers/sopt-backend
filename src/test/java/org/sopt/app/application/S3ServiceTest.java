package org.sopt.app.application;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.amazonaws.services.s3.AmazonS3;
import java.net.MalformedURLException;
import java.net.URL;
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

@ExtendWith(MockitoExtension.class)
class S3ServiceTest {

    @Mock
    private AmazonS3 s3Client;

    @InjectMocks
    private S3Service s3Service;

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
}