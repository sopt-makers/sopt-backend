package org.sopt.app.interfaces.postgres;

import org.junit.jupiter.api.BeforeEach;
import org.sopt.app.common.config.QuerydslConfiguration;
import org.sopt.app.domain.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;


@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import(QuerydslConfiguration.class)
class StampRepositoryTest {

    private final User newUser = new User();
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StampRepository stampRepository;

    @BeforeEach
    public void beforeTest() {
        userRepository.save(newUser);
    }

//    @Test
//    @DisplayName("SUCCESS_유저 아이디를 이용하여 유저의 스탬프 모두 조회")
//    void SUCCESS_findAllByUserId() {
//        //when
//        Stamp stamp1 = stampRepository.save(Stamp.builder().userId(newUser.getId()).contents("stampContents").build());
//        Stamp stamp2 = stampRepository.save(Stamp.builder().userId(newUser.getId()).contents("stampContents").build());
//
//        //then
//        Assertions.assertEquals(List.of(stamp1, stamp2), stampRepository.findAllByUserId(newUser.getId()));
//    }
//
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
