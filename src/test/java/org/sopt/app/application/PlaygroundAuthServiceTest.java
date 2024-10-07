package org.sopt.app.application;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import io.jsonwebtoken.ExpiredJwtException;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.application.auth.dto.PlaygroundAuthTokenInfo.RefreshedToken;
import org.sopt.app.application.playground.dto.PlaygroundProfileInfo.*;
import org.sopt.app.application.playground.PlaygroundAuthService;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.common.exception.UnauthorizedException;
import org.sopt.app.domain.enums.UserStatus;
import org.sopt.app.application.playground.PlaygroundClient;
import org.sopt.app.presentation.auth.AppAuthRequest.AccessTokenRequest;
import org.sopt.app.presentation.auth.AppAuthRequest.CodeRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException.BadRequest;

@ExtendWith(MockitoExtension.class)
class PlaygroundAuthServiceTest {

    @Mock
    private PlaygroundClient playgroundClient;

    @InjectMocks
    private PlaygroundAuthService playgroundAuthService;

    private final String token = "header.payload.signature";

    // getPlaygroundAccessToken
    @Test
    @DisplayName("SUCCESS_플레이그라운드 어세스 토큰 발급")
    void SUCCESS_getPlaygroundAccessToken() {
        CodeRequest codeRequest = new CodeRequest("code");
        AccessTokenRequest accessTokenRequest = new AccessTokenRequest("accessToken");

        when(playgroundClient.getAccessToken(any(), any())).thenReturn(accessTokenRequest);

        AccessTokenRequest result = playgroundAuthService.getPlaygroundAccessToken(codeRequest);
        assertEquals(accessTokenRequest.getAccessToken(), result.getAccessToken());
    }

    @Test
    @DisplayName("FAIL_플레이그라운드 어세스 토큰 발급 BadRequestException")
    void FAIL_getPlaygroundAccessTokenBadRequestException() {
        CodeRequest codeRequest = new CodeRequest("code");

        when(playgroundClient.getAccessToken(any(), any())).thenThrow(BadRequest.class);

        Assertions.assertThrows(BadRequestException.class,
                () -> playgroundAuthService.getPlaygroundAccessToken(codeRequest));
    }

    // refreshPlaygroundToken
    @Test
    @DisplayName("SUCCESS_플레이그라운드 토큰 리프레시")
    void SUCCESS_refreshPlaygroundToken() {
        AccessTokenRequest accessTokenRequest = new AccessTokenRequest("accessToken");
        RefreshedToken refreshedToken = RefreshedToken.builder().accessToken("refreshedToken").build();

        when(playgroundClient.refreshPlaygroundToken(any(), any())).thenReturn(refreshedToken);

        RefreshedToken result = playgroundAuthService.refreshPlaygroundToken(accessTokenRequest);
        assertEquals(refreshedToken.getAccessToken(), result.getAccessToken());
    }

    @Test
    @DisplayName("FAIL_플레이그라운드 토큰 리프레시 UnauthorizedException")
    void FAIL_refreshPlaygroundTokenUnauthorizedException() {
        AccessTokenRequest accessTokenRequest = new AccessTokenRequest("accessToken");

        when(playgroundClient.refreshPlaygroundToken(any(), any())).thenThrow(BadRequest.class);

        Assertions.assertThrows(UnauthorizedException.class,
                () -> playgroundAuthService.refreshPlaygroundToken(accessTokenRequest));
    }

    @Test
    @DisplayName("FAIL_플레이그라운드 토큰 리프레시 UnauthorizedException")
    void FAIL_refreshPlaygroundTokenExpiredJwtUnauthorizedException() {
        AccessTokenRequest accessTokenRequest = new AccessTokenRequest("accessToken");

        when(playgroundClient.refreshPlaygroundToken(any(), any())).thenThrow(ExpiredJwtException.class);

        Assertions.assertThrows(UnauthorizedException.class,
                () -> playgroundAuthService.refreshPlaygroundToken(accessTokenRequest));
    }

    // getPlaygroundUserForMainView
    @Test
    @DisplayName("SUCCESS_플레이그라운드 이미지 있는 유저 메인 뷰 조회")
    void SUCCESS_getPlaygroundUserForMainViewWithProfileImage() {
        ActivityCardinalInfo activityCardinalInfo = new ActivityCardinalInfo("29,서버");
        PlaygroundProfile playgroundProfile = PlaygroundProfile.builder()
                .activities(List.of(activityCardinalInfo))
                .profileImage("profileImage")
                .name("name")
                .build();
        MainViewUser mainViewUser = MainViewUser.builder().name("name").profileImage("profileImage").build();
        MainView mainView = new MainView(mainViewUser);

        when(playgroundClient.getPlaygroundMemberProfiles(any(), anyLong())).thenReturn(List.of(playgroundProfile));

        MainView result = playgroundAuthService.getPlaygroundUserForMainView(token, 1L);
        assertEquals(mainView.user().getName(), result.user().getName());
    }

    @Test
    @DisplayName("SUCCESS_플레이그라운드 이미지 없는 유저 메인 뷰 조회")
    void SUCCESS_getPlaygroundUserForMainViewWithoutProfileImage() {
        ActivityCardinalInfo activityCardinalInfo = new ActivityCardinalInfo("1,서버");
        PlaygroundProfile playgroundProfile = PlaygroundProfile.builder()
                .activities(List.of(activityCardinalInfo))
                .profileImage(null)
                .name("name")
                .build();
        MainView mainView = new MainView(MainViewUser.builder()
                .name("name")
                .profileImage("")
                .generationList(List.of(1L))
                .build());

        when(playgroundClient.getPlaygroundMemberProfiles(any(), anyLong())).thenReturn(List.of(playgroundProfile));

        MainView result = playgroundAuthService.getPlaygroundUserForMainView(token, 1L);
        assertEquals(mainView.user().getName(), result.user().getName());
    }

    // getPlaygroundUserActiveInfo
    @Test
    @DisplayName("SUCCESS_플레이그라운드 활동 유저 활동 정보 조회")
    void SUCCESS_getPlaygroundUserActiveInfoActive() {
        // given
        ActivityCardinalInfo activityCardinalInfo = new ActivityCardinalInfo("1,서버");
        PlaygroundProfile playgroundProfile = PlaygroundProfile.builder()
                .activities(List.of(activityCardinalInfo)).build();
        ReflectionTestUtils.setField(playgroundAuthService, "currentGeneration", 1L);

        // when
        when(playgroundClient.getPlaygroundMemberProfiles(any(), anyLong())).thenReturn(List.of(playgroundProfile));
        UserActiveInfo result = playgroundAuthService.getPlaygroundUserActiveInfo(token, 1L);

        // then
        assertEquals(UserStatus.ACTIVE, result.status());
    }

    @Test
    @DisplayName("SUCCESS_플레이그라운드 비활동 유저 활동 정보 조회")
    void SUCCESS_getPlaygroundUserActiveInfoInactive() {
        PlaygroundProfile playgroundProfile = PlaygroundProfile.builder().activities(List.of()).build();

        when(playgroundClient.getPlaygroundMemberProfiles(any(), anyLong())).thenReturn(List.of(playgroundProfile));

        UserActiveInfo result = playgroundAuthService.getPlaygroundUserActiveInfo(token, 1L);
        assertEquals(UserStatus.INACTIVE, result.status());
    }

    @Test
    @DisplayName("SUCCESS_플레이그라운드 엠티 유저 활동 정보 조회")
    void SUCCESS_getPlaygroundUserActiveInfoEmptyList() {
        PlaygroundProfile playgroundProfile = PlaygroundProfile.builder().activities(List.of()).build();

        when(playgroundClient.getPlaygroundMemberProfiles(any(), anyLong())).thenReturn(List.of(playgroundProfile));

        UserActiveInfo result = playgroundAuthService.getPlaygroundUserActiveInfo(token, 1L);
        assertEquals(UserStatus.INACTIVE, result.status());
    }

    @Test
    @DisplayName("FAIL_플레이그라운드 프로필을 등록하지 않은 유저 활동 정보 조회 BadRequestException")
    void FAIL_getPlaygroundUserActiveInfoNotRegisteredBadRequestException() {
        when(playgroundClient.getPlaygroundMemberProfiles(any(), anyLong())).thenThrow(BadRequest.class);

        Assertions.assertThrows(BadRequestException.class,
                () -> playgroundAuthService.getPlaygroundUserActiveInfo(token, 1L));
    }

    @Test
    @DisplayName("FAIL_플레이그라운드 토큰이 만료된 유저 활동 정보 조회 UnauthorizedException")
    void FAIL_getPlaygroundUserActiveInfoExpiredJwtUnauthorizedException() {
        when(playgroundClient.getPlaygroundMemberProfiles(any(), anyLong())).thenThrow(ExpiredJwtException.class);

        Assertions.assertThrows(UnauthorizedException.class,
                () -> playgroundAuthService.getPlaygroundUserActiveInfo(token, 1L));
    }

    // getPlayGroundUserIds
    @Test
    @DisplayName("SUCCESS_플레이그라운드 유저 아이디 조회")
    void SUCCESS_getPlayGroundUserIds() {
        ActiveUserIds userIds = new ActiveUserIds(List.of(1L));

        when(playgroundClient.getPlaygroundUserIds(any(), any())).thenReturn(userIds);

        ActiveUserIds result = playgroundAuthService.getPlayGroundUserIds(token);
        assertEquals(1, result.userIds().size());
        assertEquals(1L, result.userIds().get(0));
    }

    @Test
    @DisplayName("FAIL_플레이그라운드 프로필을 등록하지 않은 유저 아이디 조회 BadRequestException")
    void FAIL_getPlayGroundUserIdsNotRegisteredBadRequestException() {
        when(playgroundClient.getPlaygroundUserIds(any(), any())).thenThrow(BadRequest.class);

        Assertions.assertThrows(BadRequestException.class, () -> playgroundAuthService.getPlayGroundUserIds(token));
    }

    @Test
    @DisplayName("FAIL_플레이그라운드 토큰이 만료된 유저 아이디 조회 UnauthorizedException")
    void FAIL_getPlayGroundUserIdsExpiredJwtUnauthorizedException() {
        when(playgroundClient.getPlaygroundUserIds(any(), any())).thenThrow(ExpiredJwtException.class);

        Assertions.assertThrows(UnauthorizedException.class, () -> playgroundAuthService.getPlayGroundUserIds(token));
    }

    // getPlaygroundMemberProfiles
    @Test
    @DisplayName("SUCCESS_플레이그라운드 멤버 프로필 조회")
    void SUCCESS_getPlaygroundMemberProfiles() {
        PlaygroundProfile playgroundProfile = PlaygroundProfile.builder().name("name").build();

        when(playgroundClient.getPlaygroundMemberProfiles(any(), anyString())).thenReturn(List.of(playgroundProfile));

        List<PlaygroundProfile> result = playgroundAuthService.getPlaygroundMemberProfiles(token, List.of());
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("name", result.get(0).getName());
    }

    @Test
    @DisplayName("FAIL_플레이그라운드 프로필을 등록하지 않은 유저 프로필 조회 BadRequestException")
    void FAIL_getPlaygroundMemberProfilesNotRegisteredBadRequestException() {
        List<Long> memberIds = List.of();

        when(playgroundClient.getPlaygroundMemberProfiles(any(), anyString())).thenThrow(BadRequest.class);

        Assertions.assertThrows(BadRequestException.class,
                () -> playgroundAuthService.getPlaygroundMemberProfiles(token, memberIds));
    }

    @Test
    @DisplayName("FAIL_플레이그라운드 토큰이 만료된 유저 프로필 조회 UnauthorizedException")
    void FAIL_getPlaygroundMemberProfilesExpiredJwtUnauthorizedException() {
        List<Long> memberIds = List.of();

        when(playgroundClient.getPlaygroundMemberProfiles(any(), anyString())).thenThrow(ExpiredJwtException.class);

        Assertions.assertThrows(UnauthorizedException.class,
                () -> playgroundAuthService.getPlaygroundMemberProfiles(token, memberIds));
    }

    @Test
    @DisplayName("SUCCESS_자신의 플레이그라운드 프로필 조회")
    void SUCCESS_getOwnPlaygroundProfile() {
        // given & when

        // then
        assertDoesNotThrow(() -> playgroundAuthService.getOwnPlaygroundProfile(token));
    }
}
