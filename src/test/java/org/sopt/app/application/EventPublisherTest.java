// package org.sopt.app.application;
//
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.verify;
//
// import java.util.List;
// import org.junit.jupiter.api.Test;
// import org.sopt.app.application.fortune.FortuneService;
// import org.sopt.app.application.friend.FriendService;
// import org.sopt.app.application.notification.NotificationService;
// import org.sopt.app.application.notification.PushTokenService;
// import org.sopt.app.application.poke.PokeHistoryService;
// import org.sopt.app.application.s3.S3Service;
// import org.sopt.app.application.soptamp.SoptampUserService;
// import org.sopt.app.application.stamp.StampDeletedEvent;
// import org.sopt.app.application.stamp.StampService;
// import org.sopt.app.application.user.UserWithdrawEvent;
// import org.sopt.app.common.event.EventPublisher;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
//
// @SpringBootTest
// class EventPublisherTest {
//
//     @MockBean private PokeHistoryService pokeHistoryService;
//     @MockBean private PushTokenService pushTokenService;
//     @MockBean private FriendService friendService;
//     @MockBean private NotificationService notificationService;
//     @MockBean private StampService stampService;
//     @MockBean private FortuneService fortuneService;
//     @MockBean private SoptampUserService soptampUserService;
//     @MockBean private S3Service s3Service;
//
//     @Autowired private EventPublisher eventPublisher;
//
//     @Test
//     void SUCCESS_유저_탈퇴_이벤트_발행시_SoptampUser_삭제_메서드_실행() {
//         //given
//         final UserWithdrawEvent event = new UserWithdrawEvent(1L);
//
//         // when
//         eventPublisher.raise(event);
//
//         //then
//         verify(soptampUserService).handleUserWithdrawEvent(any());
//     }
//
//     @Test
//     void SUCCESS_유저_탈퇴_이벤트_발행시_Stamp_삭제_메서드_실행() {
//         //given
//         final UserWithdrawEvent event = new UserWithdrawEvent(1L);
//
//         // when
//         eventPublisher.raise(event);
//
//         //then
//         verify(stampService).handleUserWithdrawEvent(any());
//     }
//
//     @Test
//     void SUCCESS_유저_탈퇴_이벤트_발행시_UserFortune_삭제_메서드_실행() {
//         //given
//         final UserWithdrawEvent event = new UserWithdrawEvent(1L);
//
//         // when
//         eventPublisher.raise(event);
//
//         //then
//         verify(fortuneService).handleUserWithdrawEvent(any());
//     }
//
//     @Test
//     void SUCCESS_유저_탈퇴_이벤트_발행시_Notification_삭제_메서드_실행() {
//         //given
//         final UserWithdrawEvent event = new UserWithdrawEvent(1L);
//
//         // when
//         eventPublisher.raise(event);
//
//         //then
//         verify(notificationService).handleUserWithdrawEvent(any());
//     }
//
//     @Test
//     void SUCCESS_유저_탈퇴_이벤트_발행시_Friend_삭제_메서드_실행() {
//         //given
//         final UserWithdrawEvent event = new UserWithdrawEvent(1L);
//
//         // when
//         eventPublisher.raise(event);
//
//         //then
//         verify(friendService).handleUserWithdrawEvent(any());
//     }
//
//     @Test
//     void SUCCESS_유저_탈퇴_이벤트_발행시_PushToken_삭제_메서드_실행() {
//         //given
//         final UserWithdrawEvent event = new UserWithdrawEvent(1L);
//
//         // when
//         eventPublisher.raise(event);
//
//         //then
//         verify(pushTokenService).deleteAllDeviceTokenOf(any());
//     }
//
//     @Test
//     void SUCCESS_유저_탈퇴_이벤트_발행시_PokeHistory_삭제_메서드_실행() {
//         //given
//         final UserWithdrawEvent event = new UserWithdrawEvent(1L);
//
//         // when
//         eventPublisher.raise(event);
//
//         //then
//         verify(pokeHistoryService).handleUserWithdrawEvent(any());
//     }
//
//     @Test
//     void SUCCESS_스탬프_삭제_이벤트_발행시_스탬프_삭제_및_S3_이미지_삭제_메서드_실행() {
//         //given
//         final StampDeletedEvent event = new StampDeletedEvent(List.of("fileUrl"));
//
//         // when
//         eventPublisher.raise(event);
//
//         //then
//         verify(s3Service).handleStampDeletedEvent(any());
//     }
// }
