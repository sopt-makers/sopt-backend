 package org.sopt.app.interfaces.postgres;

 import java.util.List;
 import org.junit.jupiter.api.Assertions;
 import org.junit.jupiter.api.BeforeEach;
 import org.junit.jupiter.api.DisplayName;
 import org.junit.jupiter.api.Test;
 import org.sopt.app.common.fixtures.SoptampFixture;
 import org.sopt.app.domain.entity.User;
 import org.sopt.app.domain.entity.soptamp.Stamp;
 import org.sopt.app.support.IntegrationTestSupport;
 import org.springframework.beans.factory.annotation.Autowired;


 class StampRepositoryTest extends IntegrationTestSupport {

     private final User newUser = new User();
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
         Assertions.assertEquals(List.of(stamp1, stamp2), stampRepository.findAllByUserId(newUser.getId()));
     }

 //    @Test
 //    @DisplayName("SUCCESS_유저 아이디와 미션 아이디를 이용하여 유저의 스탬프 조회")
 //    void SUCCESS_findByUserIdAndMissionIdSuccess() {
 //        //given
 //        final Long missionId = 1L;
 //
 //        //when
 //        Stamp stamp = stampRepository.save(Stamp.builder().userId(newUser.getId()).missionId(missionId).contents("stampContents").build());
 //        Optional<Stamp> result = stampRepository.findByUserIdAndMissionId(newUser.getId(), missionId);
 //
 //        //then
 //        Assertions.assertEquals(Optional.of(stamp), result);
 //    }
 //
 //    @Test
 //    @DisplayName("SUCCESS_유저 아이디를 이용하여 유저의 스탬프 모두 삭제")
 //    void SUCCESS_deleteAllByUserIdSuccess() {
 //        //given
 //        stampRepository.save(Stamp.builder().userId(newUser.getId()).contents("stampContents").build());
 //        stampRepository.save(Stamp.builder().userId(newUser.getId()).contents("stampContents").build());
 //
 //        //when
 //        stampRepository.deleteAllByUserId(newUser.getId());
 //
 //        //then
 //        Assertions.assertEquals(List.of(), stampRepository.findAllByUserId(newUser.getId()));
 //    }

 }
