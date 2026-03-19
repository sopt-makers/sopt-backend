package org.sopt.app.facade;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.application.soptamp.SoptampUserService;
import org.sopt.app.application.stamp.ClapService;
import org.sopt.app.application.stamp.StampService;
import org.sopt.app.interfaces.postgres.ClapMilestoneGuard;

@ExtendWith(MockitoExtension.class)
class AdminSoptampFacadeTest {

    @Mock
    private StampService stampService;

    @Mock
    private SoptampUserService soptampUserService;

    @Mock
    private ClapService clapService;

    @Mock
    private ClapMilestoneGuard clapMilestoneGuard;

    @InjectMocks
    private AdminSoptampFacade adminSoptampFacade;

    @Test
    @DisplayName("SUCCESS_모든_데이터_삭제_플래그가_true일_때_모두_삭제")
    void SUCCESS_clearAllData() {
        // when
        adminSoptampFacade.clearSoptampData(true, true);

        // then
        verify(clapMilestoneGuard).deleteAll();
        verify(clapService).deleteAll();
        verify(stampService).deleteAllStampsWithImages();
        verify(soptampUserService).deleteAllSoptampUsers();
    }

    @Test
    @DisplayName("SUCCESS_스탬프만_삭제_플래그가_true일_때_스탬프와_박수만_삭제")
    void SUCCESS_clearOnlyStamps() {
        // when
        adminSoptampFacade.clearSoptampData(true, false);

        // then
        verify(clapMilestoneGuard).deleteAll();
        verify(clapService).deleteAll();
        verify(stampService).deleteAllStampsWithImages();
        verify(soptampUserService, never()).deleteAllSoptampUsers();
    }

    @Test
    @DisplayName("SUCCESS_유저만_삭제_플래그가_true일_때_유저만_삭제")
    void SUCCESS_clearOnlyUsers() {
        // when
        adminSoptampFacade.clearSoptampData(false, true);

        // then
        verify(clapMilestoneGuard, never()).deleteAll();
        verify(clapService, never()).deleteAll();
        verify(stampService, never()).deleteAllStampsWithImages();
        verify(soptampUserService).deleteAllSoptampUsers();
    }

    @Test
    @DisplayName("SUCCESS_아무것도_삭제하지_않음")
    void SUCCESS_clearNothing() {
        // when
        adminSoptampFacade.clearSoptampData(false, false);

        // then
        verify(clapMilestoneGuard, never()).deleteAll();
        verify(clapService, never()).deleteAll();
        verify(stampService, never()).deleteAllStampsWithImages();
        verify(soptampUserService, never()).deleteAllSoptampUsers();
    }
}
