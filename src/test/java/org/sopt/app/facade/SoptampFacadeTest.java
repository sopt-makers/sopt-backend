package org.sopt.app.facade;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.application.mission.MissionService;
import org.sopt.app.application.s3.S3Service;
import org.sopt.app.application.soptamp.SoptampPointService;
import org.sopt.app.application.soptamp.SoptampUserService;
import org.sopt.app.application.stamp.StampService;

@ExtendWith(MockitoExtension.class)
class SoptampFacadeTest {

    @Mock
    private StampService stampService;
    @Mock
    private S3Service s3Service;
    @Mock
    private MissionService missionService;
    @Mock
    private SoptampUserService soptampUserService;
    @Mock
    private SoptampPointService soptampPointService;

    @InjectMocks
    private SoptampFacade soptampFacade;

    /* TODO: 아래 메서드 구현
    @Test
    void uploadStampDeprecated() {
    }

    @Test
    void uploadStamp() {
    }

    @Test
    void deleteStamp() {
    }

    @Test
    void deleteStampAll() {
    }

    @Test
    void editSoptampUserNickname() {
    }

    @Test
    void editSoptampUserProfileMessage() {
    }

    @Test
    void getStampInfo() {
    }

    @Test
    void editStamp() {
    }

    */
}