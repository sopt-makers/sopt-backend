 package org.sopt.app.interfaces.postgres;

 import static org.assertj.core.api.Assertions.assertThat;

 import java.util.List;
 import java.util.Optional;
 import org.junit.jupiter.api.BeforeEach;
 import org.junit.jupiter.api.DisplayName;
 import org.junit.jupiter.api.Test;
 import org.sopt.app.common.fixtures.SoptampFixture;
 import org.sopt.app.common.fixtures.UserFixture;
 import org.sopt.app.domain.entity.User;
 import org.sopt.app.domain.entity.soptamp.Stamp;
 import org.sopt.app.support.IntegrationTestSupport;
 import org.springframework.beans.factory.annotation.Autowired;


 public class StampRepositoryTest extends IntegrationTestSupport {

     private final User newUser = UserFixture.createMyAppUser();
     @Autowired
     private UserRepository userRepository;
     @Autowired
     private StampRepository stampRepository;

     @BeforeEach
     public void beforeTest() {
         userRepository.save(newUser);
     }

     @Test
     @DisplayName("SUCCESS_유저 아이디를 이용하여 유저의 스탬프 모두 조회")
     void SUCCESS_findAllByUserId() {

         //given
         Stamp stamp1 = stampRepository.save(SoptampFixture.getStamp(newUser.getId()));
         Stamp stamp2 = stampRepository.save(SoptampFixture.getStamp(newUser.getId()));

         //when & then
         assertThat(List.of(stamp1, stamp2)).isEqualTo(stampRepository.findAllByUserId(newUser.getId()));
     }

     @Test
     @DisplayName("SUCCESS_유저 아이디와 미션 아이디를 이용하여 유저의 스탬프 조회")
     void SUCCESS_findByUserIdAndMissionIdSuccess() {
         //given
         final Long missionId = 1L;
         Stamp stamp = stampRepository.save(SoptampFixture.getStamp(newUser.getId(), missionId));

         //when
         Optional<Stamp> result = stampRepository.findByUserIdAndMissionId(newUser.getId(), missionId);

         //then
         assertThat(Optional.of(stamp)).isEqualTo(result);
     }

     @Test
     @DisplayName("SUCCESS_유저 아이디를 이용하여 유저의 스탬프 모두 삭제")
     void SUCCESS_deleteAllByUserIdSuccess() {
         //given
         stampRepository.save(SoptampFixture.getStamp(newUser.getId()));
         stampRepository.save(SoptampFixture.getStamp(newUser.getId()));
         assertThat(stampRepository.findAllByUserId(newUser.getId()).size()).isNotZero();

         //when
         stampRepository.deleteAllByUserId(newUser.getId());

         //then
         assertThat(stampRepository.findAllByUserId(newUser.getId())).isEmpty();
     }

 }
