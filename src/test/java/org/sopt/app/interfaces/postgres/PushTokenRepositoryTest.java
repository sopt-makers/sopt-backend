// package org.sopt.app.interfaces.postgres;
//
// import java.util.List;
// import java.util.Optional;
// import org.assertj.core.api.Assertions;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.sopt.app.common.config.QuerydslConfiguration;
// import org.sopt.app.domain.entity.PushToken;
// import org.sopt.app.domain.enums.PushTokenPlatform;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
// import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
// import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
// import org.springframework.context.annotation.Import;
//
// @DataJpaTest
// @AutoConfigureTestDatabase(replace = Replace.NONE)
// @Import(QuerydslConfiguration.class)
// class PushTokenRepositoryTest {
//
//     @Autowired
//     private PushTokenRepository pushTokenRepository;
//
//     private PushToken pushToken1;
//     private PushToken pushToken2;
//
//     @BeforeEach
//     void beforeTest() {
//
//         pushToken1 = pushTokenRepository.save(
//                 PushToken.builder()
//                         .userId(1L)
//                         .playgroundId(1L)
//                         .token("token1")
//                         .platform(PushTokenPlatform.Android)
//                         .build()
//         );
//         pushToken2 = pushTokenRepository.save(
//                 PushToken.builder()
//                         .userId(1L)
//                         .playgroundId(1L)
//                         .token("token2")
//                         .platform(PushTokenPlatform.iOS)
//                         .build()
//         );
//     }
//
//     @Test
//     @DisplayName("SUCCESS_유저 아이디와 토큰으로 푸시 토큰 존재 여부 확인")
//     void SUCCESS_existsByUserIdAndToken() {
//         Assertions.assertThat(pushTokenRepository.existsByUserIdAndToken(pushToken1.getUserId(), pushToken1.getToken())).isTrue();
//     }
//
//
//     @Test
//     @DisplayName("SUCCESS_유저 아이디와 토큰으로 푸시 토큰 찾기")
//     void SUCCESS_findByUserIdAndToken() {
//         Assertions.assertThat(pushTokenRepository.findByUserIdAndToken(pushToken1.getUserId(), pushToken1.getToken()))
//                 .isEqualTo(Optional.of(pushToken1));
//     }
//
//     @Test
//     @DisplayName("SUCCESS_유저 아이디로 푸시 토큰 리스트 찾기")
//     void SUCCESS_findAllByUserId() {
//         Assertions.assertThat(pushTokenRepository.findAllByUserId(pushToken1.getUserId()))
//                 .containsAll(List.of(pushToken1, pushToken2));
//     }
// }