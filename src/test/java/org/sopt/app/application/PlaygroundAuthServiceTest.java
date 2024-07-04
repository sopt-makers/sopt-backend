package org.sopt.app.application;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.sopt.app.common.fixtures.PokeFixture.GENERATION;
import static org.sopt.app.common.fixtures.PokeFixture.MBTI;
import static org.sopt.app.common.fixtures.PokeFixture.UNIVERSITY;
import static org.sopt.app.common.fixtures.PokeFixture.createSameMbtiPlaygroundProfileOfRecommendedFriend;
import static org.sopt.app.common.fixtures.PokeFixture.createSameUniversityPlaygroundProfileOfRecommendedFriend;

import io.jsonwebtoken.ExpiredJwtException;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.application.auth.PlaygroundAuthInfo;
import org.sopt.app.application.auth.PlaygroundAuthInfo.ActiveUserIds;
import org.sopt.app.application.auth.PlaygroundAuthInfo.MainView;
import org.sopt.app.application.auth.PlaygroundAuthInfo.MainViewUser;
import org.sopt.app.application.auth.PlaygroundAuthInfo.OwnPlaygroundProfile;
import org.sopt.app.application.auth.PlaygroundAuthInfo.PlaygroundMain;
import org.sopt.app.application.auth.PlaygroundAuthInfo.PlaygroundProfile;
import org.sopt.app.application.auth.PlaygroundAuthInfo.ActivityCardinalInfo;
import org.sopt.app.application.auth.PlaygroundAuthInfo.PlaygroundUserIds;
import org.sopt.app.application.auth.PlaygroundAuthInfo.RefreshedToken;
import org.sopt.app.application.auth.PlaygroundAuthInfo.UserActiveInfo;
import org.sopt.app.application.auth.PlaygroundAuthService;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.common.exception.UnauthorizedException;
import org.sopt.app.domain.enums.UserStatus;
import org.sopt.app.interfaces.external.PlaygroundClient;
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

    // getPlaygroundInfo
    @Test
    @DisplayName("SUCCESS_플레이그라운드 정보 조회")
    void SUCCESS_getPlaygroundInfo() {
        // given
        String token = "token";
        ActivityCardinalInfo activityCardinalInfo = new ActivityCardinalInfo("29,서버");
        PlaygroundAuthInfo.PlaygroundProfile playgroundProfile = PlaygroundProfile.builder()
                .activities(List.of(activityCardinalInfo))
                .profileImage("profileImage")
                .name("name")
                .build();
        PlaygroundMain playgroundMain = PlaygroundMain.builder().name("name").id(1L).build();

        // when
        when(playgroundClient.getPlaygroundMember(any())).thenReturn(playgroundMain);
        when(playgroundClient.getSinglePlaygroundMemberProfile(any(), anyLong())).thenReturn(List.of(playgroundProfile));
        PlaygroundMain result = playgroundAuthService.getPlaygroundInfo(token);

        // then
        assertEquals(token, result.getAccessToken());
        assertEquals(playgroundMain.getName(), result.getName());
        assertEquals(UserStatus.INACTIVE, result.getStatus());
    }

    @Test
    @DisplayName("FAIL_플레이그라운드 정보 조회 BadRequestException")
    void FAIL_getPlaygroundInfoBadRequestException() {
        when(playgroundClient.getPlaygroundMember(any())).thenThrow(BadRequest.class);

        Assertions.assertThrows(BadRequestException.class, () -> playgroundAuthService.getPlaygroundInfo(token));
    }

    @Test
    @DisplayName("FAIL_플레이그라운드 정보 조회 UnauthorizedException")
    void FAIL_getPlaygroundInfoExpiredJwtUnauthorizedException() {
        when(playgroundClient.getPlaygroundMember(any())).thenThrow(ExpiredJwtException.class);

        Assertions.assertThrows(UnauthorizedException.class, () -> playgroundAuthService.getPlaygroundInfo(token));
    }

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
        PlaygroundAuthInfo.PlaygroundProfile playgroundProfile = PlaygroundProfile.builder()
                .activities(List.of(activityCardinalInfo))
                .profileImage("profileImage")
                .name("name")
                .build();
        MainViewUser mainViewUser = MainViewUser.builder().name("name").profileImage("profileImage").build();
        MainView mainView = MainView.builder().user(mainViewUser).build();

        when(playgroundClient.getSinglePlaygroundMemberProfile(any(), anyLong())).thenReturn(List.of(playgroundProfile));

        MainView result = playgroundAuthService.getPlaygroundUserForMainView(token, 1L);
        assertEquals(mainView.getUser().getName(), result.getUser().getName());
    }

    @Test
    @DisplayName("SUCCESS_플레이그라운드 이미지 없는 유저 메인 뷰 조회")
    void SUCCESS_getPlaygroundUserForMainViewWithoutProfileImage() {
        ActivityCardinalInfo activityCardinalInfo = new ActivityCardinalInfo("1,서버");
        PlaygroundAuthInfo.PlaygroundProfile playgroundProfile = PlaygroundProfile.builder()
                .activities(List.of(activityCardinalInfo))
                .profileImage(null)
                .name("name")
                .build();
        MainViewUser mainViewUser = MainViewUser.builder().name("name").profileImage("").build();
        MainView mainView = MainView.builder().user(mainViewUser).build();

        when(playgroundClient.getSinglePlaygroundMemberProfile(any(), anyLong())).thenReturn(List.of(playgroundProfile));

        MainView result = playgroundAuthService.getPlaygroundUserForMainView(token, 1L);
        assertEquals(mainView.getUser().getName(), result.getUser().getName());
        assertEquals(mainView.getUser().getProfileImage(), result.getUser().getProfileImage());
    }

    // getPlaygroundUserActiveInfo
    @Test
    @DisplayName("SUCCESS_플레이그라운드 활동 유저 활동 정보 조회")
    void SUCCESS_getPlaygroundUserActiveInfoActive() {
        // given
        ActivityCardinalInfo activityCardinalInfo = new ActivityCardinalInfo("1,서버");
        PlaygroundAuthInfo.PlaygroundProfile playgroundProfile =
                PlaygroundProfile.builder().activities(List.of(activityCardinalInfo)).build();
        ReflectionTestUtils.setField(playgroundAuthService, "currentGeneration", 1L);

        // when
        when(playgroundClient.getSinglePlaygroundMemberProfile(any(), anyLong())).thenReturn(List.of(playgroundProfile));
        UserActiveInfo result = playgroundAuthService.getPlaygroundUserActiveInfo(token, 1L);

        // then
        assertEquals(UserStatus.ACTIVE, result.getStatus());
    }

    @Test
    @DisplayName("SUCCESS_플레이그라운드 비활동 유저 활동 정보 조회")
    void SUCCESS_getPlaygroundUserActiveInfoInactive() {
        PlaygroundAuthInfo.PlaygroundProfile playgroundProfile = PlaygroundProfile.builder().activities(List.of()).build();

        when(playgroundClient.getSinglePlaygroundMemberProfile(any(), anyLong())).thenReturn(List.of(playgroundProfile));

        UserActiveInfo result = playgroundAuthService.getPlaygroundUserActiveInfo(token, 1L);
        assertEquals(UserStatus.INACTIVE, result.getStatus());
    }

    @Test
    @DisplayName("SUCCESS_플레이그라운드 엠티 유저 활동 정보 조회")
    void SUCCESS_getPlaygroundUserActiveInfoEmptyList() {
        PlaygroundAuthInfo.PlaygroundProfile playgroundProfile =
                PlaygroundProfile.builder().activities(List.of()).build();

        when(playgroundClient.getSinglePlaygroundMemberProfile(any(), anyLong())).thenReturn(List.of(playgroundProfile));

        UserActiveInfo result = playgroundAuthService.getPlaygroundUserActiveInfo(token, 1L);
        assertEquals(UserStatus.INACTIVE, result.getStatus());
    }

    @Test
    @DisplayName("FAIL_플레이그라운드 프로필을 등록하지 않은 유저 활동 정보 조회 BadRequestException")
    void FAIL_getPlaygroundUserActiveInfoNotRegisteredBadRequestException() {
        when(playgroundClient.getSinglePlaygroundMemberProfile(any(), anyLong())).thenThrow(BadRequest.class);

        Assertions.assertThrows(BadRequestException.class,
                () -> playgroundAuthService.getPlaygroundUserActiveInfo(token, 1L));
    }

    @Test
    @DisplayName("FAIL_플레이그라운드 토큰이 만료된 유저 활동 정보 조회 UnauthorizedException")
    void FAIL_getPlaygroundUserActiveInfoExpiredJwtUnauthorizedException() {
        when(playgroundClient.getSinglePlaygroundMemberProfile(any(), anyLong())).thenThrow(ExpiredJwtException.class);

        Assertions.assertThrows(UnauthorizedException.class,
                () -> playgroundAuthService.getPlaygroundUserActiveInfo(token, 1L));
    }

    // getPlayGroundUserIds
    @Test
    @DisplayName("SUCCESS_플레이그라운드 유저 아이디 조회")
    void SUCCESS_getPlayGroundUserIds() {
        PlaygroundAuthInfo.ActiveUserIds userIds = ActiveUserIds.builder().userIds(List.of(1L)).build();

        when(playgroundClient.getPlaygroundUserIds(any(), any())).thenReturn(userIds);

        ActiveUserIds result = playgroundAuthService.getPlayGroundUserIds(token);
        assertEquals(1, result.getUserIds().size());
        assertEquals(1L, result.getUserIds().get(0));
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

        when(playgroundClient.getPlaygroundMemberProfiles(any(), any())).thenReturn(List.of(playgroundProfile));

        List<PlaygroundProfile> result = playgroundAuthService.getPlaygroundMemberProfiles(token, List.of());
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("name", result.get(0).getName());
    }

    @Test
    @DisplayName("FAIL_플레이그라운드 프로필을 등록하지 않은 유저 프로필 조회 BadRequestException")
    void FAIL_getPlaygroundMemberProfilesNotRegisteredBadRequestException() {
        List<Long> memberIds = List.of();

        when(playgroundClient.getPlaygroundMemberProfiles(any(), any())).thenThrow(BadRequest.class);

        Assertions.assertThrows(BadRequestException.class,
                () -> playgroundAuthService.getPlaygroundMemberProfiles(token, memberIds));
    }

    @Test
    @DisplayName("FAIL_플레이그라운드 토큰이 만료된 유저 프로필 조회 UnauthorizedException")
    void FAIL_getPlaygroundMemberProfilesExpiredJwtUnauthorizedException() {
        List<Long> memberIds = List.of();

        when(playgroundClient.getPlaygroundMemberProfiles(any(), any())).thenThrow(ExpiredJwtException.class);

        Assertions.assertThrows(UnauthorizedException.class,
                () -> playgroundAuthService.getPlaygroundMemberProfiles(token, memberIds));
    }

    @Test
    @DisplayName("SUCCESS_자신의 플레이그라운드 프로필 조회")
    void SUCCESS_getOwnPlaygroundProfile() {
        // given & when
        given(playgroundClient.getOwnPlaygroundProfile(any())).willReturn(new OwnPlaygroundProfile());

        // then
        assertDoesNotThrow(() -> playgroundAuthService.getOwnPlaygroundProfile(token));
    }

    @Test
    @DisplayName("SUCCESS_같은 기수의 플레이그라운드 프로필 조회")
    void SUCCESS_getPlaygroundIdsForSameGeneration() {
        // given & when

        given(playgroundClient.getPlaygroundUserIdsForSameRecommendType(any(), any()))
                .willReturn(PlaygroundUserIds.builder().build());

        // then
        assertDoesNotThrow(() -> playgroundAuthService.getPlaygroundIdsForSameGeneration(List.of(GENERATION)));
    }

    @Test
    @DisplayName("SUCCESS_같은 MBTI의 플레이그라운드 프로필 조회")
    void SUCCESS_getPlaygroundProfilesForSameMbtiAndGeneration() {
        // given & when
        given(playgroundClient.getPlaygroundUserIdsForSameRecommendType(any(), any()))
                .willReturn(PlaygroundUserIds.builder().build());

        // then
        assertDoesNotThrow(() -> playgroundAuthService.getPlaygroundIdsForSameMbti(GENERATION, MBTI));
    }

    @Test
    @DisplayName("SUCCESS_같은 대학교의 플레이그라운드 프로필 조회")
    void SUCCESS_getPlaygroundProfilesForSameUniversityAndGeneration() {
        // given & when
        given(playgroundClient.getPlaygroundUserIdsForSameRecommendType(any(), any()))
                .willReturn(PlaygroundUserIds.builder().build());

        // then
        assertDoesNotThrow(() -> playgroundAuthService.getPlaygroundIdsForSameUniversity(GENERATION, UNIVERSITY));
    }
}
