package org.sopt.app.application;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import io.jsonwebtoken.ExpiredJwtException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.sopt.app.application.auth.PlaygroundAuthInfo.MemberProfile;
import org.sopt.app.application.auth.PlaygroundAuthInfo.PlaygroundActivity;
import org.sopt.app.application.auth.PlaygroundAuthInfo.PlaygroundCardinalActivity;
import org.sopt.app.application.auth.PlaygroundAuthInfo.PlaygroundMain;
import org.sopt.app.application.auth.PlaygroundAuthInfo.PlaygroundProfile;
import org.sopt.app.application.auth.PlaygroundAuthInfo.RefreshedToken;
import org.sopt.app.application.auth.PlaygroundAuthInfo.UserActiveInfo;
import org.sopt.app.application.auth.PlaygroundAuthService;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.common.exception.UnauthorizedException;
import org.sopt.app.domain.enums.UserStatus;
import org.sopt.app.interfaces.external.PlaygroundClient;
import org.sopt.app.presentation.auth.AppAuthRequest.AccessTokenRequest;
import org.sopt.app.presentation.auth.AppAuthRequest.CodeRequest;
import org.springframework.web.client.HttpClientErrorException.BadRequest;

@ExtendWith(MockitoExtension.class)
public class PlaygroundAuthServiceTest {

    @Mock
    private PlaygroundClient playgroundClient;

    @InjectMocks
    private PlaygroundAuthService playgroundAuthService;
    private String token = "header.payload.signature";

    private Map<String, String> createDefaultHeader() {
        return new HashMap<>(Map.of("content-type", "application/json;charset=UTF-8"));
    }

    // getPlaygroundInfo
    @Test
    @DisplayName("SUCCESS_플레이그라운드 정보 조회")
    void SUCCESS_getPlaygroundInfo() {
        String token = "token";
        PlaygroundCardinalActivity playgroundCardinalActivity = new PlaygroundCardinalActivity();
        PlaygroundActivity playgroundActivity = new PlaygroundActivity();
        playgroundActivity.setCardinalActivities(List.of(playgroundCardinalActivity));
        PlaygroundAuthInfo.PlaygroundProfile playgroundProfile = new PlaygroundProfile();
        playgroundProfile.setActivities(List.of(playgroundActivity));
        playgroundProfile.setProfileImage("profileImage");
        playgroundProfile.setName("name");
        PlaygroundMain playgroundMain = new PlaygroundMain();
        playgroundMain.setName("name");

        when(playgroundClient.getPlaygroundMember(any())).thenReturn(playgroundMain);
        when(playgroundClient.getPlaygroundMemberProfile(any())).thenReturn(playgroundProfile);

        PlaygroundMain result = playgroundAuthService.getPlaygroundInfo(token);
        Assertions.assertEquals(token, result.getAccessToken());
        Assertions.assertEquals(playgroundMain.getName(), result.getName());
        Assertions.assertEquals(UserStatus.ACTIVE, result.getStatus());
    }

    @Test
    @DisplayName("FAIL_플레이그라운드 정보 조회 BadRequestException")
    void FAIL_getPlaygroundInfoBadRequestException() {
        when(playgroundClient.getPlaygroundMember(any())).thenThrow(BadRequest.class);

        Assertions.assertThrows(BadRequestException.class, () -> {
            playgroundAuthService.getPlaygroundInfo(token);
        });
    }

    @Test
    @DisplayName("FAIL_플레이그라운드 정보 조회 UnauthorizedException")
    void FAIL_getPlaygroundInfoExpiredJwtUnauthorizedException() {
        when(playgroundClient.getPlaygroundMember(any())).thenThrow(ExpiredJwtException.class);

        Assertions.assertThrows(UnauthorizedException.class, () -> {
            playgroundAuthService.getPlaygroundInfo(token);
        });
    }

    // getPlaygroundAccessToken
    @Test
    @DisplayName("SUCCESS_플레이그라운드 어세스 토큰 발급")
    void SUCCESS_getPlaygroundAccessToken() {
        CodeRequest codeRequest = new CodeRequest();
        codeRequest.setCode("code");
        AccessTokenRequest accessTokenRequest = new AccessTokenRequest();
        accessTokenRequest.setAccessToken("accessToken");

        when(playgroundClient.getAccessToken(any(), any())).thenReturn(accessTokenRequest);

        AccessTokenRequest result = playgroundAuthService.getPlaygroundAccessToken(codeRequest);
        Assertions.assertEquals(accessTokenRequest.getAccessToken(), result.getAccessToken());
    }

    @Test
    @DisplayName("FAIL_플레이그라운드 어세스 토큰 발급 BadRequestException")
    void FAIL_getPlaygroundAccessTokenBadRequestException() {
        CodeRequest codeRequest = new CodeRequest();

        when(playgroundClient.getAccessToken(any(), any())).thenThrow(BadRequest.class);

        Assertions.assertThrows(BadRequestException.class, () -> {
            playgroundAuthService.getPlaygroundAccessToken(codeRequest);
        });
    }

    // refreshPlaygroundToken
    @Test
    @DisplayName("SUCCESS_플레이그라운드 토큰 리프레시")
    void SUCCESS_refreshPlaygroundToken() {
        AccessTokenRequest accessTokenRequest = new AccessTokenRequest();
        accessTokenRequest.setAccessToken("accessToken");
        RefreshedToken refreshedToken = new RefreshedToken();
        refreshedToken.setAccessToken("refreshedToken");

        when(playgroundClient.refreshPlaygroundToken(any(), any())).thenReturn(refreshedToken);

        RefreshedToken result = playgroundAuthService.refreshPlaygroundToken(accessTokenRequest);
        Assertions.assertEquals(refreshedToken.getAccessToken(), result.getAccessToken());
    }

    @Test
    @DisplayName("FAIL_플레이그라운드 토큰 리프레시 UnauthorizedException")
    void FAIL_refreshPlaygroundTokenUnauthorizedException() {
        AccessTokenRequest accessTokenRequest = new AccessTokenRequest();

        when(playgroundClient.refreshPlaygroundToken(any(), any())).thenThrow(BadRequest.class);

        Assertions.assertThrows(UnauthorizedException.class, () -> {
            playgroundAuthService.refreshPlaygroundToken(accessTokenRequest);
        });
    }

    @Test
    @DisplayName("FAIL_플레이그라운드 토큰 리프레시 UnauthorizedException")
    void FAIL_refreshPlaygroundTokenExpiredJwtUnauthorizedException() {
        AccessTokenRequest accessTokenRequest = new AccessTokenRequest();

        when(playgroundClient.refreshPlaygroundToken(any(), any())).thenThrow(ExpiredJwtException.class);

        Assertions.assertThrows(UnauthorizedException.class, () -> {
            playgroundAuthService.refreshPlaygroundToken(accessTokenRequest);
        });
    }

    // getPlaygroundUserForMainView
    @Test
    @DisplayName("SUCCESS_플레이그라운드 이미지 있는 유저 메인 뷰 조회")
    void SUCCESS_getPlaygroundUserForMainViewWithProfileImage() {
        PlaygroundCardinalActivity playgroundCardinalActivity = new PlaygroundCardinalActivity();
        playgroundCardinalActivity.setGeneration(null);
        PlaygroundActivity playgroundActivity = new PlaygroundActivity();
        playgroundActivity.setCardinalActivities(List.of(playgroundCardinalActivity));
        PlaygroundAuthInfo.PlaygroundProfile playgroundProfile = new PlaygroundProfile();
        playgroundProfile.setActivities(List.of(playgroundActivity));
        playgroundProfile.setProfileImage("profileImage");
        playgroundProfile.setName("name");
        MainViewUser mainViewUser = MainViewUser.builder().name("name").profileImage("profileImage").build();
        MainView mainView = MainView.builder().user(mainViewUser).build();

        when(playgroundClient.getPlaygroundMemberProfile(any())).thenReturn(playgroundProfile);

        MainView result = playgroundAuthService.getPlaygroundUserForMainView(token);
        Assertions.assertEquals(mainView.getUser().getName(), result.getUser().getName());
    }

    @Test
    @DisplayName("SUCCESS_플레이그라운드 이미지 없는 유저 메인 뷰 조회")
    void SUCCESS_getPlaygroundUserForMainViewWithoutProfileImage() {
        PlaygroundCardinalActivity playgroundCardinalActivity = new PlaygroundCardinalActivity();
        playgroundCardinalActivity.setGeneration(null);
        PlaygroundActivity playgroundActivity = new PlaygroundActivity();
        playgroundActivity.setCardinalActivities(List.of(playgroundCardinalActivity));
        PlaygroundAuthInfo.PlaygroundProfile playgroundProfile = new PlaygroundProfile();
        playgroundProfile.setActivities(List.of(playgroundActivity));
        playgroundProfile.setProfileImage(null);
        playgroundProfile.setName("name");
        MainViewUser mainViewUser = MainViewUser.builder().name("name").profileImage("").build();
        MainView mainView = MainView.builder().user(mainViewUser).build();

        when(playgroundClient.getPlaygroundMemberProfile(any())).thenReturn(playgroundProfile);

        MainView result = playgroundAuthService.getPlaygroundUserForMainView(token);
        Assertions.assertEquals(mainView.getUser().getName(), result.getUser().getName());
        Assertions.assertEquals(mainView.getUser().getProfileImage(), result.getUser().getProfileImage());
    }

    // getPlaygroundUserActiveInfo
    @Test
    @DisplayName("SUCCESS_플레이그라운드 활동 유저 활동 정보 조회")
    void SUCCESS_getPlaygroundUserActiveInfoActive() {
        PlaygroundCardinalActivity playgroundCardinalActivity = new PlaygroundCardinalActivity();
        playgroundCardinalActivity.setGeneration(null);
        PlaygroundActivity playgroundActivity = new PlaygroundActivity();
        playgroundActivity.setCardinalActivities(List.of(playgroundCardinalActivity));
        PlaygroundAuthInfo.PlaygroundProfile playgroundProfile = new PlaygroundProfile();
        playgroundProfile.setActivities(List.of(playgroundActivity));

        when(playgroundClient.getPlaygroundMemberProfile(any())).thenReturn(playgroundProfile);

        UserActiveInfo result = playgroundAuthService.getPlaygroundUserActiveInfo(token);
        Assertions.assertEquals(UserStatus.ACTIVE, result.getStatus());
    }

    @Test
    @DisplayName("SUCCESS_플레이그라운드 비활동 유저 활동 정보 조회")
    void SUCCESS_getPlaygroundUserActiveInfoInactive() {
        PlaygroundCardinalActivity playgroundCardinalActivity = new PlaygroundCardinalActivity();
        playgroundCardinalActivity.setGeneration(0L);
        PlaygroundActivity playgroundActivity = new PlaygroundActivity();
        playgroundActivity.setCardinalActivities(List.of(playgroundCardinalActivity));
        PlaygroundAuthInfo.PlaygroundProfile playgroundProfile = new PlaygroundProfile();
        playgroundProfile.setActivities(List.of());

        when(playgroundClient.getPlaygroundMemberProfile(any())).thenReturn(playgroundProfile);

        UserActiveInfo result = playgroundAuthService.getPlaygroundUserActiveInfo(token);
        Assertions.assertEquals(UserStatus.INACTIVE, result.getStatus());
    }

    @Test
    @DisplayName("SUCCESS_플레이그라운드 엠티 유저 활동 정보 조회")
    void SUCCESS_getPlaygroundUserActiveInfoEmptyList() {
        PlaygroundActivity playgroundActivity = new PlaygroundActivity();
        playgroundActivity.setCardinalActivities(List.of());
        PlaygroundAuthInfo.PlaygroundProfile playgroundProfile = new PlaygroundProfile();
        playgroundProfile.setActivities(List.of());

        when(playgroundClient.getPlaygroundMemberProfile(any())).thenReturn(playgroundProfile);

        UserActiveInfo result = playgroundAuthService.getPlaygroundUserActiveInfo(token);
        Assertions.assertEquals(UserStatus.INACTIVE, result.getStatus());
    }

    @Test
    @DisplayName("FAIL_플레이그라운드 프로필을 등록하지 않은 유저 활동 정보 조회 BadRequestException")
    void FAIL_getPlaygroundUserActiveInfoNotRegisteredBadRequestException() {
        when(playgroundClient.getPlaygroundMemberProfile(any())).thenThrow(BadRequest.class);

        Assertions.assertThrows(BadRequestException.class, () -> {
            playgroundAuthService.getPlaygroundUserActiveInfo(token);
        });
    }

    @Test
    @DisplayName("FAIL_플레이그라운드 토큰이 만료된 유저 활동 정보 조회 UnauthorizedException")
    void FAIL_getPlaygroundUserActiveInfoExpiredJwtUnauthorizedException() {
        when(playgroundClient.getPlaygroundMemberProfile(any())).thenThrow(ExpiredJwtException.class);

        Assertions.assertThrows(UnauthorizedException.class, () -> {
            playgroundAuthService.getPlaygroundUserActiveInfo(token);
        });
    }

    // getPlayGroundUserIds
    @Test
    @DisplayName("SUCCESS_플레이그라운드 유저 아이디 조회")
    void SUCCESS_getPlayGroundUserIds() {
        PlaygroundAuthInfo.ActiveUserIds userIds = new ActiveUserIds();
        userIds.setUserIds(List.of(1L));

        when(playgroundClient.getPlaygroundUserIds(any(), any())).thenReturn(userIds);

        ActiveUserIds result = playgroundAuthService.getPlayGroundUserIds(token);
        Assertions.assertEquals(1, result.getUserIds().size());
        Assertions.assertEquals(1L, result.getUserIds().get(0));
    }

    @Test
    @DisplayName("FAIL_플레이그라운드 프로필을 등록하지 않은 유저 아이디 조회 BadRequestException")
    void FAIL_getPlayGroundUserIdsNotRegisteredBadRequestException() {
        PlaygroundAuthInfo.ActiveUserIds userIds = new ActiveUserIds();
        userIds.setUserIds(List.of(1L));

        when(playgroundClient.getPlaygroundUserIds(any(), any())).thenThrow(BadRequest.class);

        Assertions.assertThrows(BadRequestException.class, () -> {
            playgroundAuthService.getPlayGroundUserIds(token);
        });
    }

    @Test
    @DisplayName("FAIL_플레이그라운드 토큰이 만료된 유저 아이디 조회 UnauthorizedException")
    void FAIL_getPlayGroundUserIdsExpiredJwtUnauthorizedException() {
        PlaygroundAuthInfo.ActiveUserIds userIds = new ActiveUserIds();
        userIds.setUserIds(List.of(1L));

        when(playgroundClient.getPlaygroundUserIds(any(), any())).thenThrow(ExpiredJwtException.class);

        Assertions.assertThrows(UnauthorizedException.class, () -> {
            playgroundAuthService.getPlayGroundUserIds(token);
        });
    }

    // getPlaygroundMemberProfiles
    @Test
    @DisplayName("SUCCESS_플레이그라운드 멤버 프로필 조회")
    void SUCCESS_getPlaygroundMemberProfiles() {
        PlaygroundAuthInfo.MemberProfile memberProfile = MemberProfile.builder().name("name").build();

        when(playgroundClient.getMemberProfiles(any(), any())).thenReturn(List.of(memberProfile));

        List<MemberProfile> result = playgroundAuthService.getPlaygroundMemberProfiles(token, List.of());
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("name", result.get(0).getName());
    }

    @Test
    @DisplayName("FAIL_플레이그라운드 프로필을 등록하지 않은 유저 프로필 조회 BadRequestException")
    void FAIL_getPlaygroundMemberProfilesNotRegisteredBadRequestException() {
        when(playgroundClient.getMemberProfiles(any(), any())).thenThrow(BadRequest.class);

        Assertions.assertThrows(BadRequestException.class, () -> {
            playgroundAuthService.getPlaygroundMemberProfiles(token, List.of());
        });
    }

    @Test
    @DisplayName("FAIL_플레이그라운드 토큰이 만료된 유저 프로필 조회 UnauthorizedException")
    void FAIL_getPlaygroundMemberProfilesExpiredJwtUnauthorizedException() {
        when(playgroundClient.getMemberProfiles(any(), any())).thenThrow(ExpiredJwtException.class);

        Assertions.assertThrows(UnauthorizedException.class, () -> {
            playgroundAuthService.getPlaygroundMemberProfiles(token, List.of());
        });
    }
}
